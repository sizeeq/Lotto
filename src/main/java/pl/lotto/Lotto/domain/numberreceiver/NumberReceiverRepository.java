package pl.lotto.Lotto.domain.numberreceiver;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface NumberReceiverRepository {
    Ticket save(Ticket ticket);

    List<Ticket> findAllTicketsByDrawDate(LocalDateTime drawDate);

    Optional<Ticket> findById(String id);
}
