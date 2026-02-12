package pl.lotto.Lotto.infrastructure.numberreceiver.controller;

import pl.lotto.Lotto.domain.numberreceiver.dto.TicketDto;

public record NumberReceiverResponseDto(
        boolean success,
        TicketDto ticketDto
) {
}
