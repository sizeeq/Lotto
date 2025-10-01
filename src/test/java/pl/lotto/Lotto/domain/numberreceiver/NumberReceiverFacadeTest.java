package pl.lotto.Lotto.domain.numberreceiver;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import pl.lotto.Lotto.AdjustableClock;
import pl.lotto.Lotto.domain.numberreceiver.dto.NumberReceiverResultDto;
import pl.lotto.Lotto.domain.numberreceiver.dto.TicketDto;

import java.time.Clock;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static pl.lotto.Lotto.domain.numberreceiver.ValidationError.*;

class NumberReceiverFacadeTest {

    private final NumberReceiverRepository repository = new InMemoryNumberReceiverRepositoryTestImpl();
    Clock clock = Clock.systemUTC();

    @Test
    @DisplayName("Should return response success when user entered 6 numbers in range")
    public void should_return_success_when_user_gave_6_numbers_all_in_range() {
        //given
        TicketIdGeneratorTestImpl idGenerator = new TicketIdGeneratorTestImpl();
        SaturdayDrawDateProvider drawDateProvider = new SaturdayDrawDateProvider(clock);
        LocalDateTime nextDrawDate = drawDateProvider.nextDrawDate();
        NumberReceiverFacade numberReceiverFacade = new NumberReceiverConfiguration().createForTest(clock, repository, idGenerator);

        Set<Integer> numbersFromUser = Set.of(1, 2, 3, 4, 5, 6);

        TicketDto ticketDto = TicketDto.builder()
                .id(idGenerator.generate())
                .numbers(numbersFromUser)
                .drawDate(nextDrawDate)
                .build();


        //when
        NumberReceiverResultDto numberReceiverResultDto = numberReceiverFacade.inputNumbers(numbersFromUser);
        //then
        NumberReceiverResultDto expectedResult = NumberReceiverResultDto.builder()
                .success(true)
                .ticket(ticketDto)
                .errors(Collections.emptyList())
                .build();

        assertThat(numberReceiverResultDto).isEqualTo(expectedResult);
    }

    @Test
    @DisplayName("Should return response fail and number out of range when user entered 6 numbers with 1 out of range")
    public void should_return_fail_and_out_of_range_when_user_gave_6_numbers_with_1_out_of_range() {
        //given
        TicketIdGeneratorTestImpl idGenerator = new TicketIdGeneratorTestImpl();
        NumberReceiverFacade numberReceiverFacade = new NumberReceiverConfiguration().createForTest(clock, repository, idGenerator);

        Set<Integer> numbersFromUser = Set.of(100, 2, 3, 4, 5, 6);

        //when
        NumberReceiverResultDto numberReceiverResultDto = numberReceiverFacade.inputNumbers(numbersFromUser);

        //then
        NumberReceiverResultDto expectedResult = NumberReceiverResultDto.builder()
                .success(false)
                .errors(List.of(OUT_OF_RANGE.getMessage()))
                .build();

        assertThat(numberReceiverResultDto).isEqualTo(expectedResult);
    }

    @Test
    @DisplayName("Should return response fail and number out of range when user entered 6 numbers with 1 negative")
    public void should_return_fail_and_out_of_range_when_user_gave_6_numbers_with_1_negative() {
        //given
        TicketIdGeneratorTestImpl idGenerator = new TicketIdGeneratorTestImpl();
        NumberReceiverFacade numberReceiverFacade = new NumberReceiverConfiguration().createForTest(clock, repository, idGenerator);

        Set<Integer> numbersFromUser = Set.of(1, 2, 3, 4, -5, 6);

        //when
        NumberReceiverResultDto numberReceiverResultDto = numberReceiverFacade.inputNumbers(numbersFromUser);

        //then
        NumberReceiverResultDto expectedResult = NumberReceiverResultDto.builder()
                .success(false)
                .errors(List.of(OUT_OF_RANGE.getMessage()))
                .build();

        assertThat(numberReceiverResultDto).isEqualTo(expectedResult);
    }

    @Test
    @DisplayName("Should return response fail and not enough numbers when user entered 5 numbers with 1 negative")
    public void should_return_fail_and_not_enough_numbers_when_user_gave_5_numbers_with_1_negative() {
        //given
        TicketIdGeneratorTestImpl idGenerator = new TicketIdGeneratorTestImpl();
        NumberReceiverFacade numberReceiverFacade = new NumberReceiverConfiguration().createForTest(clock, repository, idGenerator);

        Set<Integer> numbersFromUser = Set.of(1, 2, 3, 4, -5);

        //when
        NumberReceiverResultDto numberReceiverResultDto = numberReceiverFacade.inputNumbers(numbersFromUser);

        //then
        NumberReceiverResultDto expectedResult = NumberReceiverResultDto.builder()
                .success(false)
                .errors(List.of(NOT_ENOUGH_NUMBERS.getMessage(), OUT_OF_RANGE.getMessage()))
                .build();

        assertThat(numberReceiverResultDto).isEqualTo(expectedResult);
    }

    @Test
    @DisplayName("Should return response fail and not enough numbers when user entered less than 6 numbers")
    public void should_return_fail_and_not_enough_numbers_when_user_gave_less_than_6_numbers() {
        //given
        TicketIdGeneratorTestImpl idGenerator = new TicketIdGeneratorTestImpl();
        NumberReceiverFacade numberReceiverFacade = new NumberReceiverConfiguration().createForTest(clock, repository, idGenerator);

        Set<Integer> numbersFromUser = Set.of(1, 2, 3, 4, 5);

        //when
        NumberReceiverResultDto numberReceiverResultDto = numberReceiverFacade.inputNumbers(numbersFromUser);

        //then
        NumberReceiverResultDto expectedResult = NumberReceiverResultDto.builder()
                .success(false)
                .errors(List.of(NOT_ENOUGH_NUMBERS.getMessage()))
                .build();

        assertThat(numberReceiverResultDto).isEqualTo(expectedResult);
    }

    @Test
    @DisplayName("Should return response fail and too many numbers when user entered more that 6 numbers")
    public void should_return_fail_and_too_many_numbers_when_user_gave_more_than_6_numbers() {
        //given
        TicketIdGeneratorTestImpl idGenerator = new TicketIdGeneratorTestImpl();
        NumberReceiverFacade numberReceiverFacade = new NumberReceiverConfiguration().createForTest(clock, repository, idGenerator);

        Set<Integer> numbersFromUser = Set.of(1, 2, 3, 4, 5, 6, 7);

        NumberReceiverResultDto expectedResult = NumberReceiverResultDto.builder()
                .success(false)
                .errors(List.of(TOO_MANY_NUMBERS.getMessage()))
                .build();

        //when
        NumberReceiverResultDto numberReceiverResultDto = numberReceiverFacade.inputNumbers(numbersFromUser);

        //then
        assertThat(numberReceiverResultDto).isEqualTo(expectedResult);
    }

    @Test
    @DisplayName("Should return response success and result with ticket and correct id when input is valid")
    public void should_generate_valid_ticket_id() {
        //given
        TicketIdGenerator idGenerator = new UUIDTicketIdGenerator();
        NumberReceiverFacade numberReceiverFacade = new NumberReceiverConfiguration().createForTest(clock, repository, idGenerator);

        Set<Integer> numbersFromUser = Set.of(1, 2, 3, 4, 5, 6);

        //when
        NumberReceiverResultDto numberReceiverResultDto = numberReceiverFacade.inputNumbers(numbersFromUser);

        //then
        String ticketId = numberReceiverResultDto.ticket().id();

        assertThat(numberReceiverResultDto.success()).isTrue();
        assertThat(ticketId).isNotNull();
        assertThat(ticketId).hasSize(36);
    }

    @Test
    @DisplayName("Should return response success and result with correct drawDate")
    public void should_return_success_and_result_with_correct_drawDate() {
        //given
        clock = Clock.fixed(
                LocalDateTime.of(2025, 9, 22, 8, 0)  // 22 (poniedziałek) wrzesień 2025, godzina 08:00
                        .toInstant(ZoneOffset.UTC),
                ZoneId.of("Europe/Warsaw")
        );
        TicketIdGenerator idGenerator = new UUIDTicketIdGenerator();
        NumberReceiverFacade numberReceiverFacade = new NumberReceiverConfiguration().createForTest(clock, repository, idGenerator);

        Set<Integer> numbersFromUser = Set.of(1, 2, 3, 4, 5, 6);

        //when
        NumberReceiverResultDto numberReceiverResultDto = numberReceiverFacade.inputNumbers(numbersFromUser);

        //then
        LocalDateTime expectedDrawDate = LocalDateTime.of(2025, 9, 27, 12, 0, 0); // 27 (sobota) wrzesień 2025, godzina 12:00
        LocalDateTime generatedDrawDate = numberReceiverResultDto.ticket().drawDate();

        assertThat(numberReceiverResultDto.success()).isTrue();
        assertThat(generatedDrawDate).isEqualTo(expectedDrawDate);
    }

    @Test
    @DisplayName("Should return response success and result with next Saturday when the date is Saturday 12:00")
    public void should_return_success_and_result_with_next_Saturday_when_the_date_is_Saturday_midday() {
        //given
        clock = Clock.fixed(
                LocalDateTime.of(2025, 9, 27, 12, 0)  // 27 (sobota) wrzesień 2025, godzina 12:00
                        .toInstant(ZoneOffset.UTC),
                ZoneId.of("Europe/Warsaw")
        );
        TicketIdGenerator idGenerator = new UUIDTicketIdGenerator();
        NumberReceiverFacade numberReceiverFacade = new NumberReceiverConfiguration().createForTest(clock, repository, idGenerator);

        Set<Integer> numbersFromUser = Set.of(1, 2, 3, 4, 5, 6);

        //when
        NumberReceiverResultDto numberReceiverResultDto = numberReceiverFacade.inputNumbers(numbersFromUser);

        //then
        LocalDateTime expectedDrawDate = LocalDateTime.of(2025, 10, 4, 12, 0, 0); // 4 (sobota) październik 2025, godzina 12:00
        LocalDateTime generatedDrawDate = numberReceiverResultDto.ticket().drawDate();

        assertThat(numberReceiverResultDto.success()).isTrue();
        assertThat(generatedDrawDate).isEqualTo(expectedDrawDate);
    }

    @Test
    @DisplayName("Should return response success and result with next Saturday when the date is Saturday after 12:00")
    public void should_return_success_and_result_with_next_Saturday_when_the_date_is_Saturday_after_midday() {
        //given
        clock = Clock.fixed(
                LocalDateTime.of(2025, 9, 27, 13, 0)  // 27 (sobota) wrzesień 2025, godzina 13:00
                        .toInstant(ZoneOffset.UTC),
                ZoneId.of("Europe/Warsaw")
        );
        TicketIdGenerator idGenerator = new UUIDTicketIdGenerator();
        NumberReceiverFacade numberReceiverFacade = new NumberReceiverConfiguration().createForTest(clock, repository, idGenerator);

        Set<Integer> numbersFromUser = Set.of(1, 2, 3, 4, 5, 6);

        //when
        NumberReceiverResultDto numberReceiverResultDto = numberReceiverFacade.inputNumbers(numbersFromUser);

        //then
        LocalDateTime expectedDrawDate = LocalDateTime.of(2025, 10, 4, 12, 0, 0); // 4 (sobota) październik 2025, godzina 12:00
        LocalDateTime generatedDrawDate = numberReceiverResultDto.ticket().drawDate();

        assertThat(numberReceiverResultDto.success()).isTrue();
        assertThat(generatedDrawDate).isEqualTo(expectedDrawDate);
    }

    @Test
    @DisplayName("Should return correct tickets by drawDate")
    public void should_return_correct_tickets_by_next_drawDate() {
        //given
        AdjustableClock clock = new AdjustableClock(LocalDateTime.of(2025, 9, 26, 8, 0)  // 26 (piątek) wrzesień 2025, godzina 8:00
                .toInstant(ZoneOffset.UTC),
                ZoneId.of("Europe/Warsaw"));
        TicketIdGenerator idGenerator = new UUIDTicketIdGenerator();
        NumberReceiverFacade numberReceiverFacade = new NumberReceiverConfiguration().createForTest(clock, repository, idGenerator);
        LocalDateTime expectedDrawDate = numberReceiverFacade.getNextDrawDate();

        Set<Integer> numbersFromUser = Set.of(1, 2, 3, 4, 5, 6);

        //when
        NumberReceiverResultDto numberReceiverResultDto1 = numberReceiverFacade.inputNumbers(numbersFromUser);// 26 (piątek) wrzesień 2025, godzina 8:00
        clock.plusDays(1);
        NumberReceiverResultDto numberReceiverResultDto2 = numberReceiverFacade.inputNumbers(numbersFromUser);// 27 (sobota) wrzesień 2025, godzina 8:00
        clock.plusDays(1);
        NumberReceiverResultDto numberReceiverResultDto3 = numberReceiverFacade.inputNumbers(numbersFromUser);// 28 (niedziela) wrzesień 2025, godzina 8:00
        clock.plusDays(1);
        NumberReceiverResultDto numberReceiverResultDto4 = numberReceiverFacade.inputNumbers(numbersFromUser);// 29 (poniedziałek) wrzesień 2025, godzina 8:00

        TicketDto ticket1 = numberReceiverResultDto1.ticket();
        TicketDto ticket2 = numberReceiverResultDto2.ticket();

        List<TicketDto> ticketsByDrawDate = numberReceiverFacade.getTicketsByDrawDate(expectedDrawDate);

        //then
        assertThat(ticketsByDrawDate).containsOnly(ticket1, ticket2);
    }

    @Test
    @DisplayName("Should return empty list when there are no tickets for the drawDate")
    public void should_return_empty_list_when_there_are_no_ticket_for_the_drawDate() {
        //given
        TicketIdGenerator idGenerator = new TicketIdGeneratorTestImpl();
        NumberReceiverFacade numberReceiverFacade = new NumberReceiverConfiguration().createForTest(clock, repository, idGenerator);

        //when
        List<TicketDto> ticketsByDrawDate = numberReceiverFacade.getTicketsByDrawDate(LocalDateTime.now());

        //then
        assertThat(ticketsByDrawDate).isEmpty();
    }

    @Test
    @DisplayName("Should return empty list when the date argument is after drawDate")
    public void should_return_empty_list_when_given_date_argument_is_after_drawDate() {
        //given
        clock = Clock.fixed(
                LocalDateTime.of(2025, 9, 26, 13, 0)  // 26 (piątek) wrzesień 2025, godzina 13:00
                        .toInstant(ZoneOffset.UTC),
                ZoneId.of("Europe/Warsaw")
        );
        TicketIdGenerator idGenerator = new UUIDTicketIdGenerator();
        NumberReceiverFacade numberReceiverFacade = new NumberReceiverConfiguration().createForTest(clock, repository, idGenerator);

        Set<Integer> numbersFromUser = Set.of(1, 2, 3, 4, 5, 6);

        NumberReceiverResultDto numberReceiverResultDto = numberReceiverFacade.inputNumbers(numbersFromUser);
        LocalDateTime expectedDrawDate = numberReceiverResultDto.ticket().drawDate();
        //when
        List<TicketDto> ticketsByDrawDate = numberReceiverFacade.getTicketsByDrawDate(expectedDrawDate.plusDays(1L));

        //then
        assertThat(ticketsByDrawDate).isEmpty();
    }

    @Test
    @DisplayName("Should return correct next drawDate")
    public void should_return_correct_next_drawDate() {
        //given
        clock = Clock.fixed(
                LocalDateTime.of(2025, 9, 26, 13, 0)  // 26 (piątek) wrzesień 2025, godzina 13:00
                        .toInstant(ZoneOffset.UTC),
                ZoneId.of("Europe/Warsaw")
        );
        TicketIdGenerator idGenerator = new UUIDTicketIdGenerator();
        NumberReceiverFacade numberReceiverFacade = new NumberReceiverConfiguration().createForTest(clock, repository, idGenerator);

        //when
        LocalDateTime generatedDrawDate = numberReceiverFacade.getNextDrawDate();

        //then
        LocalDateTime expectedDrawDate = LocalDateTime.of(2025, 9, 27, 12, 0, 0);
        assertThat(generatedDrawDate).isEqualTo(expectedDrawDate);
    }

    @Test
    @DisplayName("Should return correct ticket from database after being validated and saved correctly")
    public void should_return_correct_ticket_from_database() {
        //given
        TicketIdGeneratorTestImpl idGenerator = new TicketIdGeneratorTestImpl("123-ticketId");
        NumberReceiverFacade numberReceiverFacade = new NumberReceiverConfiguration().createForTest(clock, repository, idGenerator);

        Set<Integer> numbersFromUser = Set.of(1, 2, 3, 4, 5, 6);

        NumberReceiverResultDto numberReceiverResultDto = numberReceiverFacade.inputNumbers(numbersFromUser);
        TicketDto expectedTicket = numberReceiverResultDto.ticket();

        //when
        TicketDto ticketById = numberReceiverFacade.findById("123-ticketId");

        //then
        assertThat(ticketById).isEqualTo(expectedTicket);
    }
}