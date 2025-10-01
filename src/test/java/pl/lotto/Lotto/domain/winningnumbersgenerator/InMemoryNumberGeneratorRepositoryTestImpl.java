package pl.lotto.Lotto.domain.winningnumbersgenerator;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryNumberGeneratorRepositoryTestImpl implements WinningNumbersRepository {

    Map<LocalDateTime, WinningNumbers> inMemoryDatabase = new ConcurrentHashMap<>();

    @Override
    public WinningNumbers save(WinningNumbers winningNumbers) {
        return inMemoryDatabase.put(winningNumbers.drawDate(), winningNumbers);
    }

    @Override
    public Optional<WinningNumbers> findByDrawDate(LocalDateTime drawDate) {
        return Optional.ofNullable(inMemoryDatabase.get(drawDate));
    }
}
