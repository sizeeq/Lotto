package pl.lotto.Lotto.domain.resultchecker;

import pl.lotto.Lotto.domain.resultchecker.dto.ResultDto;

import java.util.List;

public class ResultMapper {

    public static Result toEntity(ResultDto resultDto) {
        return Result.builder()
                .ticketId(resultDto.ticketId())
                .userNumbers(resultDto.userNumbers())
                .winningNumbers(resultDto.winningNumbers())
                .drawDate(resultDto.drawDate())
                .status(resultDto.status())
                .matchedNumbers(resultDto.matchedNumbers())
                .build();
    }

    public static ResultDto toDto(Result result) {
        return ResultDto.builder()
                .ticketId(result.ticketId())
                .userNumbers(result.userNumbers())
                .winningNumbers(result.winningNumbers())
                .drawDate(result.drawDate())
                .status(result.status())
                .matchedNumbers(result.matchedNumbers())
                .build();
    }

    public static List<ResultDto> toDto(List<Result> results) {
        return results.stream()
                .map(ResultMapper::toDto)
                .toList();
    }
}
