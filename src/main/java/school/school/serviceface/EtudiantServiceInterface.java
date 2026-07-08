package school.school.serviceface;

import school.school.dto.EtudiantConnexionDTO;
import school.school.dto.EtudiantInscriptionDTO;
import school.school.dto.EtudiantReponseDTO;


import java.util.List;

public interface EtudiantServiceInterface {
    EtudiantReponseDTO register(EtudiantInscriptionDTO dto);
    List<EtudiantReponseDTO> getAllEtudiant();
    EtudiantReponseDTO getEtudiantByID(Long id);
    void deleteEtudiant(Long id);
    EtudiantReponseDTO connecter(EtudiantConnexionDTO dto);
}
