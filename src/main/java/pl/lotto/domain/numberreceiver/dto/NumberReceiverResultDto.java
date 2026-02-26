package pl.lotto.domain.numberreceiver.dto;

import lombok.Builder;

import java.util.List;

@Builder
public record NumberReceiverResultDto(
        boolean success,
        TicketDto ticket,
        List<String> errors
) {
}
