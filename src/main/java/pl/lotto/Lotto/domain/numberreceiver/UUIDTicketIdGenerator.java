package pl.lotto.Lotto.domain.numberreceiver;

import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class UUIDTicketIdGenerator implements TicketIdGenerator {

    @Override
    public String generate() {
        return UUID.randomUUID().toString();
    }
}
