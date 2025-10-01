package pl.lotto.Lotto.domain.numberreceiver;

import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface NumberReceiverRepository {
    Ticket save(Ticket ticket);

    List<Ticket> findAllTicketsByDrawDate(LocalDateTime drawDate);

    Optional<Ticket> findById(String id);
}
