package academy.fiveletters.mandatory.mr2;

import static org.assertj.core.api.Assertions.assertThat;

import academy.fiveletters.Game;
import academy.fiveletters.GuessRessult;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/** Обязательные тесты: раскраска букв. */
@DisplayName("MR2. Проверка букв")
class LetterMatchingTest {

    private Game newGame(String answer, List<String> dict) {
        return Game.createWithAnswer(answer, 6, dict);
    }

    @Test
    @DisplayName("Базовый случай: загадано \"озеро\", ввод \"арбуз\" -> ❌❌❌")
    void basicCase() {
        Game game = newGame("озеро", List.of("озеро", "арбуз", "сорок", "книга"));

        GuessRessult result = game.applyGuess("арбуз");

        assertThat(result.isValid()).isTrue();
        assertThat(result.feedback()).isEqualTo("❌🟡❌❌🟡");
    }

    @Test
    @DisplayName("Полное совпадение: загадано \"озеро\", ввод \"озеро\" -> ✅✅✅✅✅")
    void exactMatch() {
        Game game = newGame("озеро", List.of("озеро", "арбуз", "сорок", "книга"));

        GuessRessult result = game.applyGuess("озеро");

        assertThat(result.isValid()).isTrue();
        assertThat(result.feedback()).isEqualTo("✅✅✅✅✅");
    }

    @Test
    @DisplayName("Повторяющиеся буквы: загадано \"сорок\", ввод \"оооом\" -> 🟡❌❌✅❌")
    void repeatedLettersAreNotDoubleCounted() {
        Game game = newGame("сорок", List.of("сорок", "оооом", "озеро", "арбуз"));

        GuessRessult result = game.applyGuess("оооом");

        assertThat(result.isValid()).isTrue();
        assertThat(result.feedback()).isEqualTo("❌✅❌✅❌");
    }

    @Test
    @DisplayName("Ни одна буква не подошла: все позиции ❌")
    void noMatchingLetters() {
        Game game = newGame("озеро", List.of("озеро", "арбуз", "сорок", "книга"));

        GuessRessult result = game.applyGuess("книга");

        assertThat(result.isValid()).isTrue();
        assertThat(result.feedback()).isEqualTo("❌❌❌❌❌");
    }
}
