package school.school.security;

import org.springframework.stereotype.Component;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.security.Key;
import java.time.Instant;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Date;

import io.jsonwebtoken.security.Keys;

@Component // Permet à Spring de gérer cette classe et de l'injecter là où on en aura besoin
public class JwtUtils {
    // 1. Une clé secrète ultra-sécurisée générée automatiquement par la bibliothèque

    private final Key cleSecrete = Keys.secretKeyFor(SignatureAlgorithm.HS256);
    
    // 2. La durée de validité du Token en millisecondes (ici 24 heures)
    private final Long tempExpiration = 24L;

    // Cette méthode prend l'email de l'étudiant et fabrique le jeton JWT crypté
    public String genererToken(String email){
        return Jwts.builder()
                    .setSubject(email)                          // On cache l'email au cœur du jeton
                    // On convertit au dernier moment en Date uniquement parce que JJWT l'exige
                    .setIssuedAt(new Date())         // Date de création du jeton
                    .setExpiration(new Date(System.currentTimeMillis() + 1000*60*60*24))       // Date d'expiration
                    .signWith(cleSecrete);                      // On signe le jeton avec notre clé secrète pour éviter la triche
                    .compact();                                 // On assemble le tout en une chaîne de caractères
    }

    public extraireEmail(String token){
        return JwtUtils.parserBuilder()
                    .setSigningKey(cleSecrete)       // On donne notre clé pour décoder le message
                    .build();
                    .parseClaimsJws(token);
                    .getBody();
                    .getSubject(); // On récupère l'email
 
    }

    // Cette méthode vérifie si le token est toujours valide.
    // Elle s'assure que la signature est bonne et que la date d'expiration n'est pas dépassée.
    public boolean validerToken(String token){
        try{
            Jwts.parserBuilder()
                    .setSigningKEY(cleSecrete);
                    .build()
                    .parseClaimsJws(token); // Si la signature est fausse ou le token expiré, une exception sera levée ici
            return true;         
        }
        catch(Exception e){
            return false; //le token,n'est pas fiable, on refuse l'accès

        }
    }
}
