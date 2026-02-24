package pl.lotto.Lotto.domain.user;

import pl.lotto.Lotto.domain.user.dto.UserDto;
import pl.lotto.Lotto.domain.user.dto.UserRegisterResultDto;

public class UserMapper {

    public static UserDto toDto(User user) {
        return UserDto.builder()
                .id(user.id())
                .username(user.username())
                .password(user.password())
                .roles(user.roles())
                .build();
    }

    public static UserRegisterResultDto toUserRegisterResultDto(User user) {
        return UserRegisterResultDto.builder()
                .id(user.id())
                .username(user.username())
                .isCreated(true)
                .roles(user.roles())
                .build();
    }
}
