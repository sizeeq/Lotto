package pl.lotto.Lotto.infrastructure.security.dto;

import lombok.Builder;

@Builder
public record JwtResponseDto(
        String username,
        String token
) {
}
