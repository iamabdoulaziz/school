package school.school.security;

import org.jspecify.annotations.Nullable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import school.school.model.Etudiant;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class EtudiantPrincipal implements UserDetails {
    // On stocke notre entité Etudiant à l'intérieur
    private final Etudiant etudiant;

    public EtudiantPrincipal(Etudiant etudiant){
        this.etudiant = etudiant;
    }

    // 1. Cette méthode gère les rôles (Habilitations).
    // Pour l'instant, on donne le rôle de base "ROLE_ETUDIANT" à tout le monde.
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(new SimpleGrantedAuthority("ROLE_ETUDIANT"));
    }

    // 2. Spring Security a besoin du mot de passe pour le comparer
    @Override
    public String getPassword() {
        return etudiant.getPassword();
    }

    // 3. L'identifiant unique dans notre cas, c'est l'email de l'étudiant
    @Override
    public String getUsername() {
        return etudiant.getEmail();
    }

    // 4. On passe ces 4 vérifications à 'true' pour dire que le compte est actif et valide
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    // Petite méthode utilitaire bien pratique pour récupérer l'étudiant complet plus tard
    public Etudiant getEtudiant(){
        return this.etudiant;
    }
}
