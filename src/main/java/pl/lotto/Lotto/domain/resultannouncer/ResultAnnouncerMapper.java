package pl.lotto.Lotto.domain.resultannouncer;

import pl.lotto.Lotto.domain.resultannouncer.dto.ResultAnnouncerDto;

public class ResultAnnouncerMapper {

    public static ResultAnnouncerDto toDto(ResultAnnouncer resultAnnouncer) {
        return ResultAnnouncerDto.builder()
                .ticketId(resultAnnouncer.ticketId())
                .userNumbers(resultAnnouncer.userNumbers())
                .winningNumbers(resultAnnouncer.winningNumbers())
                .drawDate(resultAnnouncer.drawDate())
                .status(resultAnnouncer.status())
                .message(resultAnnouncer.message())
                .build();
    }
}
