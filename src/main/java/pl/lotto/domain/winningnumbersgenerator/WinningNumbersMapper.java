package pl.lotto.domain.winningnumbersgenerator;

import pl.lotto.domain.winningnumbersgenerator.dto.WinningNumbersDto;

public class WinningNumbersMapper {

    public static WinningNumbersDto toDto(WinningNumbers winningNumbers) {
        return WinningNumbersDto.builder()
                .numbers(winningNumbers.numbers())
                .drawDate(winningNumbers.drawDate())
                .build();
    }
}
