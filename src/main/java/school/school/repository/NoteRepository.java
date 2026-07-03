package school.school.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import school.school.model.Note;

import java.util.List;

@Repository
public interface NoteRepository extends JpaRepository<Note, Long> {
    // Spring comprend : "SELECT * FROM notes WHERE id_etudiant = ?"
    List<Note> findByEtudiantId(Long id);
}
