package pl.lotto.Lotto.infrastructure.numberreceiver.controller;

import java.util.List;

public record InputNumberRequestDto(
        List<Integer> inputNumbers
) {
}
