package school.school.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration // Dit à Spring que cette classe contient des configurations pour l'application
@EnableWebSecurity // Active la sécurité web personnalisée
@EnableMethodSecurity // Pour activer la protection par méthode !
public class SecurityConfig {

    @Bean // Dit à Spring de créer cet objet une fois et de le rendre disponible partout pour l'injection
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    // C'est cette méthode (ce Bean) qui va dicter les règles d'accès à nos routes
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception{
        http
        // 1. On désactive le CSRF (protection contre les failles CSRF) car on crée une API REST avec JWT
            .csrf(crsf -> crsf.disable())
            // 2. On configure les règles d'accès aux URLs
            .authorizeHttpRequests(auth -> auth
                // On dit à Spring : "Toutes les routes /api/etudiants/register et /api/etudiants/register sont publiques (inscription, connexion)"
                .requestMatchers("/api/etudiants/register").permitAll()
                .requestMatchers("/api/etudiants/connexion").permitAll()

                // Tout le reste (les DELETE, les GET des étudiants, les Notes) requiert une authentification
                .anyRequest().authenticated()
            );

        return http.build();
    }
    
}
