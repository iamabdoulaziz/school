package school.school.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import school.school.dto.EtudiantInscriptionDTO;
import school.school.dto.EtudiantReponseDTO;
import school.school.serviceface.EtudiantServiceInterface;

import java.util.List;

@RestController
@RequestMapping("/api/etudiants")
public class EtudiantController {

    // Injection de notre Service
    private final EtudiantServiceInterface etudiantService;

    public EtudiantController (EtudiantServiceInterface etudiantService) {
        this.etudiantService = etudiantService;
    }

    // 1. Route pour s'inscrire (POST http://localhost:8080/api/etudiants/register)
    @PostMapping("register")
    public ResponseEntity<EtudiantReponseDTO> inscrireEtudiant(@RequestBody EtudiantInscriptionDTO dto){
        EtudiantReponseDTO resultat = etudiantService.register(dto);
        return new ResponseEntity<>(resultat, HttpStatus.CREATED);
    }

    // 2. Route pour tout récupérer (GET http://localhost:8080/api/etudiants)
    @GetMapping
    public ResponseEntity<List<EtudiantReponseDTO>> recupererToutLesEtudiant(){
        List<EtudiantReponseDTO> list = etudiantService.getAllEtudiant();
        return ResponseEntity.ok(list);
    }

    // 3. Route pour récupérer un étudiant par son ID (GET http://localhost:8080/api/etudiants/1)
    @GetMapping("/{id}")
    public ResponseEntity<EtudiantReponseDTO> recupererUnEtudiant(@PathVariable Long id){
        EtudiantReponseDTO dto = etudiantService.getEtudiantByID(id);
        return ResponseEntity.ok(dto);
    }

    // 4. Route pour supprimer un étudiant (DELETE http://localhost:8080/api/etudiants/1)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> supprimerEdutiant(@PathVariable Long id){
        etudiantService.deleteEtudiant(id);
        return ResponseEntity.noContent().build();
    }
}
