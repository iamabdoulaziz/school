package school.school.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration // Dit à Spring que cette classe contient des configurations pour l'application
@EnableWebSecurity // Active la sécurité web personnalisée
@EnableMethodSecurity // Cette ligne pour activer la protection par méthode !
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter){
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean // Dit à Spring de créer cet objet une fois et de le rendre disponible partout pour l'injection
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    // C'est cette méthode (ce Bean) qui va dicter les règles d'accès à nos routes
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // 1. On désactive le CSRF (protection contre les failles CSRF) car on crée une API REST avec JWT
                .csrf(crsf -> crsf.disable())


                // IMPORTANT : On dit à Spring de ne pas créer de session sur le serveur (mode Stateless)
                // Chaque requête doit être indépendante et transporter son propre Token JWT
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // 2. On configure les règles d'accès aux URLs
                .authorizeHttpRequests(auth -> auth
                        // On dit à Spring : "Les routes /api/etudiants/register et api/etudiants/connexion sont publiques (inscription, connexion)"
                        .requestMatchers("/api/etudiants/register").permitAll()
                        .requestMatchers("/api/etudiants/connexion").permitAll()
                        // Pour n'importe quelle autre requête (comme voir les notes), il FAUT être authentifié
                        .anyRequest().authenticated()
                );
        // 3. ICI ON BRANCHE LE FILTRE :
        // On dit à Spring d'exécuter notre JwtAuthenticationFilter AVANT son filtre d'authentification par défaut
        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
    // 4. On expose le gestionnaire d'authentification standard de Spring.
    // Il nous servira dans la méthode de connexion pour vérifier les mots de passe.
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception{
        return configuration.getAuthenticationManager();
    }
}
