package school.school.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import school.school.model.Note;

@Repository
public interface NoteRepository extends JpaRepository<Note, Long> {
}
