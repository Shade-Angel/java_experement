package academy.fiveletters.mandatory.mr2;

import static org.assertj.core.api.Assertions.assertThat;

import academy.fiveletters.Game;
import academy.fiveletters.GuessRessult;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

/** Обязательные тесты: валидация ввода. */
@DisplayName("MR2. Валидация ввода")
class GuessValidationTest {

    private final List<String> dict = List.of("озеро", "сорок", "арбуз");

    private Game newGame() {
        return Game.createWithAnswer("озеро", 6, dict);
    }

    @ParameterizedTest
    @ValueSource(strings = {"дом", "домики", ""})
    @DisplayName("Слово не из 5 букв отклоняется: \"{0}\"")
    void wordOfWrongLengthIsRejected(String guess) {
        Game game = newGame();

        GuessRessult result = game.applyGuess(guess);

        assertThat(result.isValid()).isFalse();
        assertThat(game.attemptsUsed()).isZero();
    }

    @ParameterizedTest
    @ValueSource(strings = {"дом12", "дом!!", "до ма"})
    @DisplayName("Ввод с не-буквами отклоняется: \"{0}\"")
    void nonLetterInputIsRejected(String guess) {
        Game game = newGame();

        GuessRessult result = game.applyGuess(guess);

        assertThat(result.isValid()).isFalse();
        assertThat(game.attemptsUsed()).isZero();
    }

    @Test
    @DisplayName("Слово, которого нет в словаре, отклоняется")
    void wordOutsideDictionaryIsRejected() {
        Game game = newGame();

        GuessRessult result = game.applyGuess("книга");

        assertThat(result.isValid()).isFalse();
        assertThat(game.attemptsUsed()).isZero();
    }

    @Test
    @DisplayName("Некорректный ввод не тратит попытку")
    void invalidInputDoesNotConsumeAttempt() {
        Game game = newGame();

        game.applyGuess("дом");
        game.applyGuess("дом12");
        game.applyGuess("книга");
        assertThat(game.attemptsUsed()).isZero();

        game.applyGuess("арбуз");
        assertThat(game.attemptsUsed()).isEqualTo(1);
    }

    @Test
    @DisplayName("Ввод не зависит от регистра: \"ОЗЕРО\" и \"озеро\" обрабатываются одинаково")
    void inputIsCaseInsensitive() {
        Game upperGame = newGame();
        Game lowerGame = newGame();

        GuessRessult upper = upperGame.applyGuess("ОЗЕРО");
        GuessRessult lower = lowerGame.applyGuess("озеро");

        assertThat(upper.isValid()).isTrue();
        assertThat(upper.feedback()).isEqualTo(lower.feedback());
        assertThat(upperGame.status()).isEqualTo(lowerGame.status());
    }
}
