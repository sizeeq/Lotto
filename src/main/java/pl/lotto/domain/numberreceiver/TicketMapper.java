package pl.lotto.domain.numberreceiver;

import pl.lotto.domain.numberreceiver.dto.TicketDto;

import java.util.List;

public class TicketMapper {

    public static TicketDto toDto(Ticket ticket) {
        return TicketDto.builder()
                .id(ticket.id())
                .numbers(ticket.numbers())
                .drawDate(ticket.drawDate())
                .build();
    }

    public static List<TicketDto> toDto(List<Ticket> tickets) {
        return tickets.stream()
                .map(TicketMapper::toDto)
                .toList();
    }

    public static Ticket toEntity(TicketDto ticketDto) {
        return Ticket.builder()
                .id(ticketDto.id())
                .numbers(ticketDto.numbers())
                .drawDate(ticketDto.drawDate())
                .build();
    }

    public static List<Ticket> toEntity(List<TicketDto> ticketDtos) {
        return ticketDtos.stream()
                .map(TicketMapper::toEntity)
                .toList();
    }
}
