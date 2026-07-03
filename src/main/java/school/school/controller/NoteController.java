package school.school.controller;


import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.school.dto.NoteReponseDTO;
import school.school.serviceface.NoteServiceInterface;

import java.util.List;

@RestController
@RequestMapping("/api/notes")// La racine de toutes nos routes concernant les notes
public class NoteController {
    // On injecte l'interface de notre service
    private final NoteServiceInterface noteService;

    // Constructeur pour l'injection de dépendance automatique par Spring
    public NoteController(NoteServiceInterface noteService){
        this.noteService = noteService;
    }
    // Route permettant à l'étudiant connecté de voir ses notes
    // GET http://localhost:8080/api/notes/mes-notes
    public ResponseEntity<List<NoteReponseDTO>> obtenirMesNotes(){
        // On demande au service d'aller récupérer les notes
        List<NoteReponseDTO> notes = noteService.getMesNotes();

        // On renvoie la liste avec un statut HTTP 200 OK
        return ResponseEntity.ok(notes);
    }
}
