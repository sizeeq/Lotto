package pl.lotto.infrastructure.user.controller;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import pl.lotto.domain.user.UserFacade;
import pl.lotto.domain.user.dto.UserRegisterRequestDto;
import pl.lotto.domain.user.dto.UserRegisterResultDto;

import java.net.URI;

@RestController
@RequestMapping("/register")
public class UserController {

    private final UserFacade userFacade;

    public UserController(UserFacade userFacade) {
        this.userFacade = userFacade;
    }

    @PostMapping
    public ResponseEntity<UserRegisterResultDto> registerUser(@RequestBody @Valid UserRegisterRequestDto registerRequestDto) {
        UserRegisterResultDto userRegisterResultDto = userFacade.registerUser(registerRequestDto);

        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("{id}")
                .buildAndExpand(userRegisterResultDto.id())
                .toUri();

        return ResponseEntity.created(location).body(userRegisterResultDto);
    }
}
