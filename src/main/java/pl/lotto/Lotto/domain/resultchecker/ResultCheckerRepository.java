package pl.lotto.Lotto.domain.resultchecker;

import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ResultCheckerRepository {
    Result save(Result result);

    void saveAll(List<Result> results);

    Optional<Result> findById(String id);
}
