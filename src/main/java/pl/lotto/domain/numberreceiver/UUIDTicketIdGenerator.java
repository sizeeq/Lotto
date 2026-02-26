package pl.lotto.domain.numberreceiver;

import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
class UUIDTicketIdGenerator implements TicketIdGenerator {

    @Override
    public String generate() {
        return UUID.randomUUID().toString();
    }
}
