package school.school.security;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component // Pour que Spring gère cette classe et l'injecte partout où on en a besoin
public class JwtUtils {
    // 1. Génération d'une clé secrète sécurisée
    private final SecretKey cleSecrete = Jwts.SIG.HS256.key().build();

    // Génère un jeton JWT crypté à partir de l'email de l'étudiant
    public String genererToken(String email){
        return Jwts.builder()
                .subject(email) // On stocke l'email au cœur du jeton (anciennement setSubject)
                .issuedAt(new Date()) // Date de création (aujourd'hui, maintenant)
                .expiration(new Date(System.currentTimeMillis() + 1000*60*60*24)) // Expire dans 24 heures
                .signWith(cleSecrete) // Signature électronique pour empêcher toute falsification
                .compact(); // Assemble le tout en une chaîne de caractères (le Token)
    }

    //Extrait l'email de l'étudiant caché dans le token reçu
    public String extraireEmail(String token){
        return Jwts.parser()
                .verifyWith(cleSecrete) // On vérifie d'abord si la signature est valide avec notre clé
                .build()
                .parseSignedClaims(token)// Décode le token (anciennement parseClaimsJws)
                .getPayload()  // Récupère les données (anciennement getBody)
                .getSubject();  // Extrait l'email
    }

    // Valide le token pour voir si la signature est bonne et s'il n'a pas expiré
    public boolean validerToken(String token){
        try {
            Jwts.parser()
                    .verifyWith(cleSecrete)
                    .build()
                    .parseSignedClaims(token);  // Si le token est expiré ou modifié, ça lève une exception direct
            return true; // Le token est valide
        } catch (JwtException e) {
            return false;  // Jeton invalide ou expiré, accès refusé
        }
    }
}
