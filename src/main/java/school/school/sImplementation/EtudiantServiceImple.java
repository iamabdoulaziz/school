package school.school.sImplementation;

import org.springframework.stereotype.Service;
import school.school.dto.EtudiantConnexionDTO;
import school.school.dto.EtudiantInscriptionDTO;
import school.school.dto.EtudiantReponseDTO;
import school.school.model.Etudiant;
import school.school.repository.EtudiantRepository;
import school.school.serviceface.EtudiantServiceInterface;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class EtudiantServiceImple implements EtudiantServiceInterface {

    private final EtudiantRepository etudiantRepository;

    public EtudiantServiceImple(EtudiantRepository etudiantRepository){
        this.etudiantRepository = etudiantRepository;
    }

    @Override
    public EtudiantReponseDTO register(EtudiantInscriptionDTO dto) {
        //A. Conversion : On prend les infos du DTO pour créer une entité Etudiant
        Etudiant etudiant = new Etudiant();
        etudiant.setNom(dto.getNom());
        etudiant.setEmail(dto.getEmail());
        etudiant.setPassword(dto.getPassword());

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

    @Override
    public EtudiantReponseDTO connecter(EtudiantConnexionDTO dto) {
        // 1. On cherche l'étudiant en base via son email
        Etudiant etudiant = etudiantRepository.findByEmail(dto.getEmail()).orElseThrow(() -> new RuntimeException("Email ou mot de passe incorrect !"));

        // 2. On vérifie si le mot de passe correspond

        if (!etudiant.getPassword().equals(dto.getPassword())){
            throw new RuntimeException("Email ou mot de passe incorrect !");
        }
        // 3. Si tout est correct, on convertit l'étudiant en DTO de réponse pour le connecter

        EtudiantReponseDTO reponse = new EtudiantReponseDTO();
        reponse.setId(etudiant.getId());
        reponse.setNom(etudiant.getNom());
        reponse.setEmail(etudiant.getEmail());
        return reponse;
    }
}
