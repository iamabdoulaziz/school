package school.school.security;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import school.school.model.Etudiant;
import school.school.repository.EtudiantRepository;

import java.util.Optional;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    private final EtudiantRepository etudiantRepository;
    // Injection de ton Repository pour pouvoir fouiller dans MySQL
    public CustomUserDetailsService(EtudiantRepository etudiantRepository){
        this.etudiantRepository = etudiantRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        // 1. On va chercher l'étudiant par son email dans la base de données
        Etudiant etudiant = etudiantRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Aucun étudiant trouvé avec l'email : " + email));

        // 2. On "emballe" notre étudiant dans l'adaptateur EtudiantPrincipal qu'on a créé
        return new EtudiantPrincipal(etudiant);
    }
}
