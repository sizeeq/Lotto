package pl.lotto.infrastructure.security.dto;

import jakarta.validation.constraints.NotBlank;

public record TokenRequestDto(
        @NotBlank(message = "{username.validation.not.blank}")
        String username,

        @NotBlank(message = "{password.validation.not.blank}")
        String password
) {
}
