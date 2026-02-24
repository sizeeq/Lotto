package pl.lotto.Lotto.infrastructure.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import pl.lotto.Lotto.infrastructure.security.dto.JwtResponseDto;
import pl.lotto.Lotto.infrastructure.security.dto.TokenRequestDto;

import java.time.*;
import java.util.List;

@Component
public class JwtAuthenticator {

    private final AuthenticationManager authenticationManager;
    private final Clock clock;
    private final JwtConfigurationProperties properties;

    public JwtAuthenticator(AuthenticationManager authenticationManager, Clock clock, JwtConfigurationProperties properties) {
        this.authenticationManager = authenticationManager;
        this.clock = clock;
        this.properties = properties;
    }

    public JwtResponseDto authenticateAndGenerateToken(TokenRequestDto tokenRequestDto) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(tokenRequestDto.username(), tokenRequestDto.password())
        );

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        String token = createToken(userDetails);
        String username = userDetails.getUsername();

        return JwtResponseDto.builder()
                .token(token)
                .username(username)
                .build();
    }

    private String createToken(UserDetails userDetails) {
        String secretKey = properties.getSecret();
        Algorithm algorithm = Algorithm.HMAC256(secretKey);

        Instant now = LocalDateTime.now(clock).toInstant(ZoneOffset.UTC);
        Instant expiresAt = now.plus(Duration.ofDays(properties.getExpirationDays()));

        List<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toList();

        return JWT.create()
                .withSubject(userDetails.getUsername())
                .withIssuer(properties.getIssuer())
                .withIssuedAt(now)
                .withExpiresAt(expiresAt)
                .withClaim("roles", roles)
                .sign(algorithm);
    }
}
