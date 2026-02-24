package pl.lotto.Lotto.domain.user.dto;

import lombok.Builder;
import pl.lotto.Lotto.domain.user.UserRole;

import java.util.Set;

@Builder
public record UserDto(
        String id,
        String username,
        String password,
        Set<UserRole> roles
) {
}
