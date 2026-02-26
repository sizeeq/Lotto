package pl.lotto.infrastructure.security;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import pl.lotto.domain.user.UserFacade;
import pl.lotto.domain.user.dto.UserDto;

import java.util.List;

public class LoginUserDetailsService implements UserDetailsService {

    private final UserFacade userFacade;

    public LoginUserDetailsService(UserFacade userFacade) {
        this.userFacade = userFacade;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserDto userDto = userFacade.findUserByUsername(username);
        return getUser(userDto);
    }

    private UserDetails getUser(UserDto userDto) {
        List<SimpleGrantedAuthority> authorities = userDto.roles().stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role.name()))
                .toList();

        return org.springframework.security.core.userdetails.User.builder()
                .username(userDto.username())
                .password(userDto.password())
                .authorities(authorities)
                .build();
    }
}
