package school.school.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
@Table(name = "notes")
public class Note {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String matiere;

    @Column(nullable = false)
    private Double moyenne;

    @ManyToOne
    @JoinColumn(name = "id_etudiant")
    private Etudiant etudiant;
}
