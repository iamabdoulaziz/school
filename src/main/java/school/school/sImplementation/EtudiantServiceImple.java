package school.school.sImplementation;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import school.school.dto.EtudiantConnexionDTO;
import school.school.dto.EtudiantInscriptionDTO;
import school.school.dto.EtudiantReponseDTO;
import school.school.model.Etudiant;
import school.school.repository.EtudiantRepository;
import school.school.security.JwtUtils;
import school.school.serviceface.EtudiantServiceInterface;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class EtudiantServiceImple implements EtudiantServiceInterface {

    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;

    private final EtudiantRepository etudiantRepository;
    // 1. On déclare notre outil de hachage ici
    private final PasswordEncoder passwordEncoder;

    // 2. On l'ajoute au constructeur. Spring va voir qu'il a le Bean BCryptPasswordEncoder en mémoire et va l'injecter !
    public EtudiantServiceImple(EtudiantRepository etudiantRepository, PasswordEncoder passwordEncoder,
                                AuthenticationManager authenticationManager,
                                JwtUtils jwtUtils){
        this.etudiantRepository = etudiantRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtUtils = jwtUtils;
    }

    @Override
    public EtudiantReponseDTO register(EtudiantInscriptionDTO dto) {
        //A. Conversion : On prend les infos du DTO pour créer une entité Etudiant
        Etudiant etudiant = new Etudiant();
        etudiant.setNom(dto.getNom());
        etudiant.setEmail(dto.getEmail());

        // 3. C'EST ICI QUE CA CHANGE !
        // Au lieu de stocker "1234" en clair, on passe le mot de passe dans l'algorithme.
        String motDepasseHache = passwordEncoder.encode(dto.getPassword());
        etudiant.setPassword(motDepasseHache);

        // B. Sauvegarde : On demande au repository d'enregistrer l'étudiant dans MySQL
        // La méthode .save() renvoie l'étudiant avec son tout nouvel ID généré par MySQL
        Etudiant etudiantSauvegarder = etudiantRepository.save(etudiant);

        // C. Conversion inverse : On crée le DTO de réponse sans le mot de passe
        EtudiantReponseDTO reponse = new EtudiantReponseDTO();
        reponse.setId(etudiantSauvegarder.getId());
        reponse.setNom(etudiantSauvegarder.getNom());
        reponse.setEmail(etudiantSauvegarder.getEmail());

        return reponse;
    }

    @Override
    public List<EtudiantReponseDTO> getAllEtudiant() {
        // A. On récupère tous les étudiants de la base de données
        List<Etudiant> etudiants = etudiantRepository.findAll();

        // B. On crée une liste vide qui va contenir nos DTO de réponse
        List<EtudiantReponseDTO> listDto = new ArrayList<>();

        // C. On boucle sur chaque étudiant pour le transformer en DTO
        for (Etudiant etudiant : etudiants){
            EtudiantReponseDTO dto = new EtudiantReponseDTO();
            dto.setId(etudiant.getId());
            dto.setNom(etudiant.getNom());
            dto.setEmail(etudiant.getEmail());

            listDto.add(dto); // On l'ajoute à notre liste de sortie
        }

        return listDto;
    }

    @Override
    public EtudiantReponseDTO getEtudiantByID(Long id) {
        // On cherche l'étudiant. Si l'ID n'existe pas, on déclenche une erreur (Exception)
        Etudiant etudiant = etudiantRepository.findById(id).orElseThrow(() -> new RuntimeException("Etudiant introuvable avec l'ID : " + id));

        // Si on arrive ici, c'est que l'étudiant existe, on le convertit en DTO
        EtudiantReponseDTO dto = new EtudiantReponseDTO();
        dto.setId(etudiant.getId());
        dto.setNom(etudiant.getNom());
        dto.setEmail(etudiant.getEmail());
        return dto;
    }

    @Override
    public void deleteEtudiant(Long id) {

        // On vérifie si l'étudiant existe dans la base
        if (!etudiantRepository.existsById(id)){
            throw new RuntimeException("Etudiant introuvable avec l'ID : " + id);
        }
        // Si oui, on le supprime
        etudiantRepository.deleteById(id);
    }


    // Gère la connexion d'un étudiant et retourne un Token JWT si les identifiants sont bons
    @Override
    public EtudiantReponseDTO connecter(EtudiantConnexionDTO dto) {
       /*
       Dans la première version de mon code je faisais la verification de l'email et du mot de passe manuellement maintenant
       c'est spring Security qui gère raison pour laquelle cette partie de mon code est en commentaire!!

        // 1. On cherche l'étudiant en base via son email
        Etudiant etudiant = etudiantRepository.findByEmail(dto.getEmail()).orElseThrow(() -> new RuntimeException("Email ou mot de passe incorrect !"));

        // 2. On vérifie si le mot de passe correspond

        if (!etudiant.getPassword().equals(dto.getPassword())){
            throw new RuntimeException("Email ou mot de passe incorrect !");
        }
        // 3. Si tout est correct, on convertit l'étudiant en DTO de réponse pour le connecter

//        EtudiantReponseDTO reponse = new EtudiantReponseDTO();
//        reponse.setId(etudiant.getId());
//        reponse.setNom(etudiant.getNom());
//        reponse.setEmail(etudiant.getEmail());
        return null;
        */

        try {
            // 1. On demande à Spring Security de vérifier l'email et le mot de passe.
            // C'est cette ligne qui va chercher l'utilisateur en BDD et valider le mot de passe crypté avec BCrypt.
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(dto.getEmail(), dto.getPassword())
            );
        } catch (AuthenticationException e) {
            // Si le mot de passe est faux ou l'email inconnu, Spring lève une exception, et on la rattrape ici.
            throw new RuntimeException(" Email ou mot de passe incorrect ! " + e);
        }

        // 2. Si on arrive ici, c'est que l'authentification a réussi !
        // On va chercher l'étudiant en base pour récupérer son Id et son Nom afin de construire la réponse.
        Etudiant etudiant = etudiantRepository.findByEmail(dto.getEmail())
                .orElseThrow(() -> new RuntimeException("Erreur : Etudiant introuvable après authentification ! "));

        // 3. On génère son badge d'accès JWT personnalisé
        String monTokenJWT = jwtUtils.genererToken(etudiant.getEmail());

        // 4. On remplit le DTO de réponse avec ses vraies données ET le token généré
        EtudiantReponseDTO reponse = new EtudiantReponseDTO();
        reponse.setId(etudiant.getId());
        reponse.setNom(etudiant.getNom());
        reponse.setEmail(etudiant.getEmail());
        reponse.setToken(monTokenJWT); // On lui donne son token

        return reponse;
    }

}
