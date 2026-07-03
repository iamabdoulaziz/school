package school.school.serviceface;

import school.school.dto.NoteReponseDTO;

import java.util.List;

public interface NoteServiceInterface {
    // L'étudiant connecté pourra uniquement appeler cette méthode pour voir ses propres notes
    List<NoteReponseDTO> getMesNotes();
}
