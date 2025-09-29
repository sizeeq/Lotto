package pl.lotto.Lotto.domain.numberreceiver;

import java.util.UUID;

public class UUIDTicketIdGenerator implements TicketIdGenerator {

    @Override
    public String generate() {
        return UUID.randomUUID().toString();
    }
}
