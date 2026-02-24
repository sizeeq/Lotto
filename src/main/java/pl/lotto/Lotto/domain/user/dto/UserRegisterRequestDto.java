package pl.lotto.Lotto.domain.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
public record UserRegisterRequestDto(
        @NotBlank(message = "${username.validation.not.blank}")
        @Size(message = "${username.validation.size.between.three.and.fifty}", min = 3, max = 50)
        String username,

        @NotBlank(message = "${password.validation.not.blank}")
        @Size(message = "${password.validation.size.between.six.and.one.hundred}", min = 6, max = 100)
        String password
) {
}
