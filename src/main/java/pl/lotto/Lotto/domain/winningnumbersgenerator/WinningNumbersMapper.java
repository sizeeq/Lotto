package pl.lotto.Lotto.domain.winningnumbersgenerator;

import pl.lotto.Lotto.domain.winningnumbersgenerator.dto.WinningNumbersDto;

class WinningNumbersMapper {

    public static WinningNumbersDto toDto(WinningNumbers winningNumbers) {
        return WinningNumbersDto.builder()
                .numbers(winningNumbers.numbers())
                .drawDate(winningNumbers.drawDate())
                .build();
    }
}
