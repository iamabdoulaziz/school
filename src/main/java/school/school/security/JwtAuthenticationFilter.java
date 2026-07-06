package school.school.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtUtils jwtUtils;
    private final CustomUserDetailsService userDetailsService;

    // Injection de nos outils
    public JwtAuthenticationFilter(JwtUtils jwtUtils, CustomUserDetailsService userDetailsService){
        this.jwtUtils = jwtUtils;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)throws ServletException, IOException{
        // 1. Récupérer l'entête "Authorization" de la requête HTTP
        String authHeader = request.getHeader("Authorization");
        String token = null;
        String email = null;

        // 2. Le token est TOUJOURS précédé du mot "Bearer " (Porteur du jeton)
        if(authHeader != null && authHeader.startsWith("Berear")){
            token = authHeader.substring(7); // On coupe les 7 premiers caractères pour garder UNIQUEMENT le token
            email = jwtUtils.extraireEmail(token); // On extrait l'email avec notre outil
        }

        // 3. Si on a trouvé un email et que l'utilisateur n'est pas déjà authentifié dans Spring
        if (email != null && SecurityContextHolder.getContext().getAuthentication() == null){
            // On charge l'utilisateur depuis MySQL via notre service
            UserDetails userDetails = userDetailsService.loadUserByUsername(email);

            // On vérifie si le token est mathématiquement valide
            if (jwtUtils.validerToken(token)){
                // Si tout est bon, on crée le "badge d'authentification officiel" de Spring Security
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities()
                );
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // On glisse ce badge dans le contexte de sécurité de Spring.
                // C'est ça qui dit à Spring : "C'est bon, cet utilisateur a le droit de passer !"
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }

        //4. On passe la main au filtre suivant (ou au Controller si c'était le dernier gardien)
        filterChain.doFilter(request, response);
    }
}
