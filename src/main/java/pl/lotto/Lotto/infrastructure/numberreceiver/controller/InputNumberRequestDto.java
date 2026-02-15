package pl.lotto.Lotto.infrastructure.numberreceiver.controller;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record InputNumberRequestDto(

        @NotNull(message = "{inputNumber.not.null}")
        @NotEmpty(message = "{inputNumber.not.empty}")
        List<Integer> inputNumbers
) {
}