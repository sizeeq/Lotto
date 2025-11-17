package pl.lotto.Lotto.domain.resultannouncer;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ResultAnnouncerRepository extends MongoRepository<ResultAnnouncer, String> {
    ResultAnnouncer save(ResultAnnouncer resultAnnouncer);

    Optional<ResultAnnouncer> findById(String ticketId);
}
