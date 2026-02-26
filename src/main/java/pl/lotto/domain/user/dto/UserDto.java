package pl.lotto.domain.user.dto;

import lombok.Builder;
import pl.lotto.domain.user.UserRole;

import java.util.Set;

@Builder
public record UserDto(
        String id,
        String username,
        String password,
        Set<UserRole> roles
) {
}
