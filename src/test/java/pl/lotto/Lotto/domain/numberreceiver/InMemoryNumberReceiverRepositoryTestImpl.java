package pl.lotto.Lotto.domain.numberreceiver;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryNumberReceiverRepositoryTestImpl implements NumberReceiverRepository {

    Map<String, Ticket> inMemoryDatabase = new ConcurrentHashMap<>();

    @Override
    public Ticket save(Ticket ticket) {
        inMemoryDatabase.put(ticket.id(), ticket);
        return inMemoryDatabase.get(ticket.id());
    }

    @Override
    public List<Ticket> findAllTicketsByDrawDate(LocalDateTime drawDate) {
        return inMemoryDatabase.values().stream()
                .filter(ticket -> ticket.drawDate().equals(drawDate))
                .toList();
    }

    @Override
    public Optional<Ticket> findById(String id) {
        return Optional.ofNullable(inMemoryDatabase.get(id));
    }
}
