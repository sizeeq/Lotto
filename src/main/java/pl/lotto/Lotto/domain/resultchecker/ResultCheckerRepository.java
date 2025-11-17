package pl.lotto.Lotto.domain.resultchecker;

import org.jetbrains.annotations.NotNull;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ResultCheckerRepository extends MongoRepository<Result, String> {
    @NotNull Result save(Result result);

    @NotNull Optional<Result> findById(@NotNull String id);
}
