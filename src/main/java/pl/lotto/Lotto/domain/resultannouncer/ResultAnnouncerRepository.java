package pl.lotto.Lotto.domain.resultannouncer;

import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ResultAnnouncerRepository {
    ResultAnnouncer save(ResultAnnouncer resultAnnouncer);
    
    Optional<ResultAnnouncer> findById(String ticketId);
}
