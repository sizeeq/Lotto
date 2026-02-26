package pl.lotto.infrastructure.numberreceiver.controller;

import pl.lotto.domain.numberreceiver.dto.TicketDto;

public record NumberReceiverResponseDto(
        boolean success,
        TicketDto ticketDto
) {
}
