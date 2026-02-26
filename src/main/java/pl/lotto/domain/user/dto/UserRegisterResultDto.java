package pl.lotto.domain.user.dto;

import lombok.Builder;
import pl.lotto.domain.user.UserRole;

import java.util.Set;

@Builder
public record UserRegisterResultDto(
        String id,
        String username,
        boolean isCreated,
        Set<UserRole> roles
) {
}
