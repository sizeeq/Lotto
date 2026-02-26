package pl.lotto.domain.user;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import pl.lotto.domain.user.dto.UserDto;
import pl.lotto.domain.user.dto.UserRegisterRequestDto;
import pl.lotto.domain.user.dto.UserRegisterResultDto;
import pl.lotto.domain.user.exception.UserAlreadyExistsException;

import java.util.Set;

import static pl.lotto.domain.user.UserMapper.toUserRegisterResultDto;

@Component
public class UserFacade {

    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;

    public UserFacade(UserRepository repository, PasswordEncoder passwordEncoder) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
    }

    public UserRegisterResultDto registerUser(UserRegisterRequestDto requestDto) {
        if (repository.existsByUsername(requestDto.username())) {
            throw new UserAlreadyExistsException("User with username " + requestDto.username() + " already exists");
        }

        String encodedPassword = passwordEncoder.encode(requestDto.password());

        User user = User.builder()
                .username(requestDto.username())
                .password(encodedPassword)
                .roles(Set.of(UserRole.USER))
                .build();

        User savedUser = repository.save(user);

        return toUserRegisterResultDto(savedUser);
    }

    public UserDto findUserByUsername(String username) {
        return repository.findUserByUsername(username)
                .map(UserMapper::toDto)
                .orElseThrow(() -> new BadCredentialsException("Username was not found"));
    }
}
