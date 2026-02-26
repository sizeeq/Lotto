package pl.lotto.domain.resultannouncer;

import pl.lotto.domain.resultannouncer.dto.ResultDetailsDto;
import pl.lotto.domain.resultchecker.dto.ResultDto;

public class ResultAnnouncerMapper {

    public static ResultDetailsDto toDto(ResultAnnouncerEntity resultAnnouncerEntity) {
        return ResultDetailsDto.builder()
                .ticketId(resultAnnouncerEntity.ticketId())
                .userNumbers(resultAnnouncerEntity.userNumbers())
                .winningNumbers(resultAnnouncerEntity.winningNumbers())
                .matchedNumbers(resultAnnouncerEntity.matchedNumbers())
                .drawDate(resultAnnouncerEntity.drawDate())
                .status(resultAnnouncerEntity.status())
                .build();
    }

    public static ResultDetailsDto toDto(ResultDto resultDto) {
        return ResultDetailsDto.builder()
                .ticketId(resultDto.ticketId())
                .userNumbers(resultDto.userNumbers())
                .winningNumbers(resultDto.winningNumbers())
                .matchedNumbers(resultDto.matchedNumbers())
                .drawDate(resultDto.drawDate())
                .status(resultDto.status())
                .build();
    }

    public static ResultAnnouncerEntity toEntity(ResultDetailsDto resultDetailsDto) {
        return ResultAnnouncerEntity.builder()
                .ticketId(resultDetailsDto.ticketId())
                .userNumbers(resultDetailsDto.userNumbers())
                .winningNumbers(resultDetailsDto.winningNumbers())
                .matchedNumbers(resultDetailsDto.matchedNumbers())
                .drawDate(resultDetailsDto.drawDate())
                .status(resultDetailsDto.status())
                .build();
    }

    public static ResultAnnouncerEntity toEntity(ResultDto resultDto) {
        return ResultAnnouncerEntity.builder()
                .ticketId(resultDto.ticketId())
                .userNumbers(resultDto.userNumbers())
                .winningNumbers(resultDto.winningNumbers())
                .matchedNumbers(resultDto.matchedNumbers())
                .drawDate(resultDto.drawDate())
                .status(resultDto.status())
                .build();
    }
}
