package pl.lotto.Lotto.domain.numberreceiver;

import org.springframework.stereotype.Component;
import pl.lotto.Lotto.domain.numberreceiver.dto.NumberReceiverResultDto;
import pl.lotto.Lotto.domain.numberreceiver.dto.TicketDto;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Set;

@Component
public class NumberReceiverFacade {

    private final NumberValidator numberValidator;
    private final NumberReceiverRepository repository;
    private final TicketIdGenerator idGenerator;
    private final DrawDateProvider drawDateProvider;

    public NumberReceiverFacade(NumberValidator numberValidator, NumberReceiverRepository repository, TicketIdGenerator idGenerator, DrawDateProvider drawDateProvider) {
        this.numberValidator = numberValidator;
        this.repository = repository;
        this.idGenerator = idGenerator;
        this.drawDateProvider = drawDateProvider;
    }

    public NumberReceiverResultDto inputNumbers(Set<Integer> numbersFromUser) {
        List<ValidationError> validationResult = numberValidator.validate(numbersFromUser);

        if (validationResult.isEmpty()) {
            String id = idGenerator.generate();
            LocalDateTime drawDate = drawDateProvider.nextDrawDate();

            Ticket generatedTicket = Ticket.builder()
                    .id(id)
                    .numbers(numbersFromUser)
                    .drawDate(drawDate)
                    .build();
            Ticket savedTicket = repository.save(generatedTicket);
            TicketDto ticketDto = TicketMapper.toDto(savedTicket);

            return NumberReceiverResultDto.builder()
                    .success(true)
                    .ticket(ticketDto)
                    .errors(Collections.emptyList())
                    .build();
        }
        List<String> validationMessages = validationResult.stream()
                .map(ValidationError::getMessage)
                .toList();

        return NumberReceiverResultDto.builder()
                .success(false)
                .errors(validationMessages)
                .build();
    }

    public List<TicketDto> getTicketsByDrawDate(LocalDateTime date) {
        LocalDateTime nextDrawDate = getNextDrawDate();

        if (date.isAfter(nextDrawDate)) {
            return Collections.emptyList();
        }

        List<Ticket> allTicketsByDrawDate = repository.findAllTicketsByDrawDate(date);
        return TicketMapper.toDto(allTicketsByDrawDate);
    }

    public TicketDto findById(String id) {
        return repository.findById(id)
                .map(TicketMapper::toDto)
                .orElse(TicketDto.builder().build());
    }

    public LocalDateTime getNextDrawDate() {
        return drawDateProvider.nextDrawDate();
    }
}
