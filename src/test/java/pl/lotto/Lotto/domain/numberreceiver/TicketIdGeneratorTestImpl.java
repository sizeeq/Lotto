package pl.lotto.Lotto.domain.numberreceiver;

public class TicketIdGeneratorTestImpl implements TicketIdGenerator {

    private final String hash;

    TicketIdGeneratorTestImpl(String hash) {
        this.hash = hash;
    }

    TicketIdGeneratorTestImpl() {
        hash = "123";
    }

    @Override
    public String generate() {
        return hash;
    }
}
