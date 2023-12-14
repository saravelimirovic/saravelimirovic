package com.example.Backend.security;

import com.example.Backend.entity.User;
import com.example.Backend.entity.Token;
import com.example.Backend.repository.UserRepository;
import com.example.Backend.repository.TokenRepository;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.*;
import jakarta.servlet.http.*;
import lombok.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.security.authentication.*;
import org.springframework.security.core.*;
import org.springframework.security.core.authority.*;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.*;
import org.springframework.transaction.annotation.*;
import org.springframework.util.*;

import javax.crypto.*;
import java.nio.charset.*;
import java.util.*;
import java.util.concurrent.*;

import static com.example.Backend.utils.ContextUtils.getNullableUserDetails;

@Transactional
@Service
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class TokenAuthenticationService {

    private final UserRepository userRepository;
    private final TokenRepository tokenRepository;

    protected static final String TOKEN_PREFIX = "Bearer ";
    protected static final String AUTH_HEADER = "authorization";
    protected static final String ROLES = "roles";
    protected static final long EXPIRATION_TIME = TimeUnit.DAYS.toMillis(7);
    protected static final SecretKey key =
            Keys.hmacShaKeyFor("cd+Pr1js+w2qfT2BoCD+tPcYp9LbjpmhSMEJqUob1mcxZ7+Wmik4AYdjX+DlDjmE4yporzQ9tm7v3z/j+QbdYg==".getBytes(StandardCharsets.UTF_8));
    private static final ConcurrentHashMap<String, String> activeTokens = new ConcurrentHashMap<>();

    public Authentication getAuthentication(HttpServletRequest request,
                                            CustomUserDetailsService customUserDetailsService) {

        UsernamePasswordAuthenticationToken result = null;

        String token = parseToken(request);

        if (token != null) {

            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            String roles = claims.get(ROLES, String.class);

            String credentials = activeTokens.get(token);

            if (credentials != null) {

                UserDetails userDetails = loadUserDetails(customUserDetailsService, credentials);
                CustomUserDetails cud = (CustomUserDetails) userDetails;
                cud.setSessionIdentifier(cud.getCredentials());

                result = new UsernamePasswordAuthenticationToken(credentials, null,
                        AuthorityUtils.commaSeparatedStringToAuthorityList(roles));
                result.setDetails(userDetails);
            }

        }

        if (result == null) {
            throw new JwtException("Token is bad");
        }

        return result;
    }


    @Transactional
    public String createAuthenticationTokenWeb(CustomUserDetails userDetails,
                                               Collection<? extends GrantedAuthority> authorities) {
        String token = createAuthenticationTokenWeb(userDetails, authorities, EXPIRATION_TIME);
        User user = userRepository.findByEmail(userDetails.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("Invalid user"));
        user.getTokens().add(new Token(token, userDetails.getCredentials(), user));
        userRepository.save(user);
        return token;
    }

    private String createAuthenticationTokenWeb(CustomUserDetails userDetails,
                                                Collection<? extends GrantedAuthority> authorities,
                                                long expirationTime) {
        String roles = StringUtils.collectionToCommaDelimitedString(AuthorityUtils.authorityListToSet(authorities));

        String credentials = userDetails.getCredentials().toLowerCase();

        String token;
        String previousValue;
        do {
            token = Jwts.builder()
                    .setSubject(userDetails.getCredentials())
                    .claim(ROLES, roles)
                    .setId(UUID.randomUUID()
                            .toString())
                    .setExpiration(new Date(System.currentTimeMillis() + expirationTime))
                    .signWith(key)
                    .setIssuer(new Random(100000L).nextInt() + "")
                    .compact();

            previousValue = activeTokens.put(token, credentials);

        } while (previousValue != null);

        getNullableUserDetails().ifPresent(ud -> ud.setSessionIdentifier(credentials));

        return token;
    }

    protected static String parseToken(HttpServletRequest request) {
        String token = null;
        String authHeader = request.getHeader(AUTH_HEADER);
        if (authHeader != null && authHeader.startsWith(TOKEN_PREFIX)) {
            token = authHeader.substring(7);
        }
        return token;
    }

    private UserDetails loadUserDetails(CustomUserDetailsService customUserDetailsService, String credentials) {
        Optional<CustomUserDetails> maybeUser = getNullableUserDetails();
        boolean oldUser = maybeUser.isPresent() && maybeUser.get()
                .getCredentials()
                .equals(credentials);
        return oldUser ? maybeUser.get() : customUserDetailsService.loadUserByUsername(credentials);
    }

    @Transactional
    public void logoutUser(HttpServletRequest request) {
        String token = parseToken(request);
        activeTokens.remove(token);
        tokenRepository.deleteAllByToken(token);
    }

    public void setWebActiveTokens(Map<String, String> tokens) {
        tokens.remove(null);
        for (Map.Entry<String, String> token : tokens.entrySet()) {
            if (token.getKey() != null && token.getValue() != null) {
                activeTokens.put(token.getKey(), token.getValue().toLowerCase());
            }
        }
    }
}
