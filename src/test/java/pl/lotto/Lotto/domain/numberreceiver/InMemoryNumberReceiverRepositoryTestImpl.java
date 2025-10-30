package pl.lotto.Lotto.domain.numberreceiver;

import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.query.FluentQuery;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

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

    @Override
    public boolean existsById(String s) {
        return false;
    }

    @Override
    public <S extends Ticket> S insert(S entity) {
        return null;
    }

    @Override
    public <S extends Ticket> List<S> insert(Iterable<S> entities) {
        return List.of();
    }

    @Override
    public <S extends Ticket> Optional<S> findOne(Example<S> example) {
        return Optional.empty();
    }

    @Override
    public <S extends Ticket> List<S> findAll(Example<S> example) {
        return List.of();
    }

    @Override
    public <S extends Ticket> List<S> findAll(Example<S> example, Sort sort) {
        return List.of();
    }

    @Override
    public <S extends Ticket> Page<S> findAll(Example<S> example, Pageable pageable) {
        return null;
    }

    @Override
    public <S extends Ticket> long count(Example<S> example) {
        return 0;
    }

    @Override
    public <S extends Ticket> boolean exists(Example<S> example) {
        return false;
    }

    @Override
    public <S extends Ticket, R> R findBy(Example<S> example, Function<FluentQuery.FetchableFluentQuery<S>, R> queryFunction) {
        return null;
    }

    @Override
    public <S extends Ticket> List<S> saveAll(Iterable<S> entities) {
        return List.of();
    }

    @Override
    public List<Ticket> findAll() {
        return List.of();
    }

    @Override
    public List<Ticket> findAllById(Iterable<String> strings) {
        return List.of();
    }

    @Override
    public long count() {
        return 0;
    }

    @Override
    public void deleteById(String s) {

    }

    @Override
    public void delete(Ticket entity) {

    }

    @Override
    public void deleteAllById(Iterable<? extends String> strings) {

    }

    @Override
    public void deleteAll(Iterable<? extends Ticket> entities) {

    }

    @Override
    public void deleteAll() {

    }

    @Override
    public List<Ticket> findAll(Sort sort) {
        return List.of();
    }

    @Override
    public Page<Ticket> findAll(Pageable pageable) {
        return null;
    }
}
