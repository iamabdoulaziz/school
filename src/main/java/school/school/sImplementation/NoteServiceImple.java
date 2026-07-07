package school.school.sImplementation;

import org.aspectj.weaver.ast.Not;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import school.school.dto.NoteReponseDTO;
import school.school.model.Note;
import school.school.repository.NoteRepository;
import school.school.security.EtudiantPrincipal;
import school.school.serviceface.NoteServiceInterface;

import java.util.ArrayList;
import java.util.List;

@Service
public class NoteServiceImple implements NoteServiceInterface {
    // On injecte uniquement le NoteRepository car on n'a besoin que des notes
    private final NoteRepository noteRepository;

    public NoteServiceImple (NoteRepository noteRepository){
        this.noteRepository = noteRepository;
    }

    @Override
    public List<NoteReponseDTO> getMesNotes() {
        // SIMULATION : Pour l'instant, on fait comme si l'étudiant connecté avait l'ID 1.
        // Plus tard, Spring Security nous donnera dynamiquement l'ID du vrai connecté.
//        Long etudiantConnecteId = 1L;

        // On récupère l'adaptateur de l'étudiant actuellement connecté via le filtre JWT
        EtudiantPrincipal principal = (EtudiantPrincipal) SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal();
        // On extrait son VRAI ID depuis l'entité stockée dedans
        Long etudiantConnecteId = principal.getEtudiant().getId();
        // 1. On va chercher en BDD les notes de cet étudiant spécifique
        List<Note> notes = noteRepository.findByEtudiantId(etudiantConnecteId);

        // 2. On crée la liste vide qui accueillera nos DTO de réponse
        List<NoteReponseDTO> listDto = new ArrayList<>();

        // 3. On boucle sur nos entités "Note" pour les transformer en "NoteReponseDTO"
        for (Note note : notes){
            NoteReponseDTO dto = new NoteReponseDTO();
            dto.setId(note.getId());
            dto.setMatiere(note.getMatiere());
            dto.setMoyenne(note.getMoyenne());

            listDto.add(dto); // On l'ajoute à notre liste finale
        }
        // 4. On renvoie la liste propre au client
        return listDto;
    }
}
