package pl.lotto.Lotto.infrastructure.security.controller;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import pl.lotto.Lotto.infrastructure.security.JwtAuthenticator;
import pl.lotto.Lotto.infrastructure.security.dto.JwtResponseDto;
import pl.lotto.Lotto.infrastructure.security.dto.TokenRequestDto;

@RestController
public class TokenController {

    private final JwtAuthenticator jwtAuthenticator;

    public TokenController(JwtAuthenticator jwtAuthenticator) {
        this.jwtAuthenticator = jwtAuthenticator;
    }

    @PostMapping("/token")
    public ResponseEntity<JwtResponseDto> authenticateAndGenerateToken(@Valid @RequestBody TokenRequestDto tokenRequestDto) {
        JwtResponseDto jwtResponseDto = jwtAuthenticator.authenticateAndGenerateToken(tokenRequestDto);
        return ResponseEntity.ok(jwtResponseDto);
    }
}
