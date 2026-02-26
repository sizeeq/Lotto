package pl.lotto.infrastructure.numberreceiver.controller;

import jakarta.validation.Valid;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import pl.lotto.domain.numberreceiver.NumberReceiverFacade;
import pl.lotto.domain.numberreceiver.dto.NumberReceiverResultDto;

import java.util.HashSet;
import java.util.Set;

@RestController
@Log4j2
public class InputNumberRestController {

    private final NumberReceiverFacade numberReceiverFacade;

    public InputNumberRestController(NumberReceiverFacade numberReceiverFacade) {
        this.numberReceiverFacade = numberReceiverFacade;
    }

    @PostMapping("/inputNumbers")
    public ResponseEntity<NumberReceiverResultDto> inputNumbers(@RequestBody @Valid InputNumberRequestDto requestDto) {
        Set<Integer> numbersFromUser = new HashSet<>(requestDto.inputNumbers());
        NumberReceiverResultDto resultDto = numberReceiverFacade.inputNumbers(numbersFromUser);
        return ResponseEntity.ok(resultDto);
    }
}
