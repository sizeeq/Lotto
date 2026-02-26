package pl.lotto.domain.winningnumbersgenerator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.lotto.domain.numberreceiver.NumberReceiverFacade;
import pl.lotto.domain.numberreceiver.NumberValidator;
import pl.lotto.domain.numberreceiver.ValidationError;
import pl.lotto.domain.winningnumbersgenerator.dto.WinningNumbersDto;
import pl.lotto.domain.winningnumbersgenerator.exception.WinningNumbersNotFoundException;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class WinningNumbersGeneratorFacadeTest {

    @Mock
    private final NumberValidator numberValidator = new NumberValidator();
    private final WinningNumbersRepository repository = new InMemoryNumberGeneratorRepositoryTestImpl();
    private WinningNumbersGeneratorFacade winningNumbersGeneratorFacade;
    @Mock
    private WinningNumbersGenerator winningNumbersGenerator;

    @Mock
    private NumberReceiverFacade numberReceiverFacade;

    @BeforeEach
    void setUp() {
        winningNumbersGeneratorFacade = new WinningNumbersGeneratorFacade(winningNumbersGenerator, repository, numberValidator, numberReceiverFacade);
    }

    @Test
    @DisplayName("Should return winning numbers when generated numbers were valid")
    public void should_return_winning_numbers_when_generated_number_are_valid() {
        //given
        Set<Integer> generatedWinningNumbers = Set.of(1, 2, 3, 4, 5, 6);
        LocalDateTime expectedDrawDate = LocalDateTime.of(2025, 10, 4, 12, 0, 0); // 4 (sobota) październik 2025, godzina 12:00

        when(numberReceiverFacade.getNextDrawDate()).thenReturn(expectedDrawDate);
        when(winningNumbersGenerator.generate()).thenReturn(generatedWinningNumbers);

        //when
        WinningNumbersDto result = winningNumbersGeneratorFacade.generateWinningNumbers();

        //then
        assertThat(result.numbers()).isEqualTo(generatedWinningNumbers);
        assertThat(result.drawDate()).isEqualTo(expectedDrawDate);
    }

    @Test
    @DisplayName("Should throw an exception when generated numbers are not valid")
    public void should_throw_exception_when_generated_numbers_are_not_valid() {
        //given
        Set<Integer> generatedWinningNumbers = Set.of(100, 2, 3, 4, 5, 6);
        LocalDateTime expectedDrawDate = LocalDateTime.of(2025, 10, 4, 12, 0, 0); // 4 (sobota) październik 2025, godzina 12:00

        when(numberReceiverFacade.getNextDrawDate()).thenReturn(expectedDrawDate);
        when(winningNumbersGenerator.generate()).thenReturn(generatedWinningNumbers);
        when(numberValidator.validate(generatedWinningNumbers)).thenReturn(List.of(ValidationError.OUT_OF_RANGE));

        //when
        //then
        assertThatThrownBy(() -> winningNumbersGeneratorFacade.generateWinningNumbers())
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Generated numbers are not valid");
    }

    @Test
    @DisplayName("Should return winning numbers with correct draw date when numbers where given in edge cases of range 1-99")
    public void should_return_winning_numbers_with_draw_date_when_numbers_are_in_edge_cases() {
        //given
        Set<Integer> generatedWinningNumbers = Set.of(1, 2, 3, 4, 5, 99);
        LocalDateTime expectedDrawDate = LocalDateTime.of(2025, 10, 4, 12, 0, 0); // 4 (sobota) październik 2025, godzina 12:00

        when(numberReceiverFacade.getNextDrawDate()).thenReturn(expectedDrawDate);
        when(winningNumbersGenerator.generate()).thenReturn(generatedWinningNumbers);
        when(numberValidator.validate(generatedWinningNumbers)).thenReturn(Collections.emptyList());

        //when
        WinningNumbersDto result = winningNumbersGeneratorFacade.generateWinningNumbers();

        //then
        assertThat(result.numbers()).isEqualTo(generatedWinningNumbers);
        assertThat(result.drawDate()).isEqualTo(expectedDrawDate);
    }

    @Test
    @DisplayName("Should return WinningNumbersDto when numbers are found for given draw date")
    void should_return_winning_numbers_dto_when_numbers_found_for_given_draw_date() {
        //given
        Set<Integer> generatedWinningNumbers = Set.of(1, 2, 3, 4, 5, 6);
        LocalDateTime expectedDrawDate = LocalDateTime.of(2025, 10, 4, 12, 0, 0); // 4 (sobota) październik 2025, godzina 12:00

        WinningNumbers expectedEntity = WinningNumbers.builder()
                .numbers(generatedWinningNumbers)
                .drawDate(expectedDrawDate)
                .build();

        repository.save(expectedEntity);

        //when
        WinningNumbersDto result = winningNumbersGeneratorFacade.findWinningNumbersByDrawDate(expectedDrawDate);

        //then
        assertThat(result.numbers()).isEqualTo(expectedEntity.numbers());
        assertThat(result.drawDate()).isEqualTo(expectedEntity.drawDate());
    }

    @Test
    @DisplayName("Should throw WinningNumbersNotFoundException when numbers are not found for given draw date")
    void should_throw_winning_numbers_not_found_exception_when_numbers_not_found_for_given_draw_date() {
        //given
        LocalDateTime expectedDrawDate = LocalDateTime.of(2025, 10, 4, 12, 0, 0); // 4 (sobota) październik 2025, godzina 12:00

        //when
        //then
        assertThatThrownBy(() -> winningNumbersGeneratorFacade.findWinningNumbersByDrawDate(expectedDrawDate))
                .isInstanceOf(WinningNumbersNotFoundException.class)
                .hasMessage("Winning numbers were not found");
    }
}