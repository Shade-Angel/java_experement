package academy.fiveletters.mandatory.mr2;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import academy.fiveletters.Game;
import academy.fiveletters.Status;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/** Обязательные тесты: завершение партии. */
@DisplayName("MR2. Победа и поражение")
class GameOutcomeTest {

    private final List<String> dict = List.of("озеро", "сорок", "арбуз", "книга");

    private Game newGame(String answer, int maxAttempts) {
        return Game.createWithAnswer(answer, maxAttempts, dict);
    }

    @Test
    @DisplayName("Угаданное слово переводит сессию в статус WIN")
    void correctGuessWinsTheGame() {
        Game game = newGame("озеро", 6);

        game.applyGuess("книга");
        game.applyGuess("озеро");

        assertThat(game.status()).isEqualTo(Status.WIN);
        assertThat(game.isFinish()).isTrue();
    }

    @Test
    @DisplayName("После 6 неудачных попыток сессия переходит в статус LOSE")
    void sixFailedAttemptsLoseTheGame() {
        Game game = newGame("озеро", 6);

        for (int i = 0; i < 6; i++) {
            game.applyGuess("книга");
        }

        assertThat(game.status()).isEqualTo(Status.LOSE);
        assertThat(game.isFinish()).isTrue();
        assertThat(game.attemptsUsed()).isEqualTo(6);
    }

    @Test
    @DisplayName("При поражении показывается загаданное слово")
    void answerIsRevealedOnLoss() {
        Game game = newGame("озеро", 1);

        game.applyGuess("книга");

        assertThat(game.status()).isEqualTo(Status.LOSE);
        assertThat(game.ans()).isEqualTo("озеро");
    }

    @Test
    @DisplayName("Завершённая партия больше не принимает попытки")
    void finishedGameRejectsFurtherGuesses() {
        Game game = newGame("озеро", 6);
        game.applyGuess("озеро");

        assertThatThrownBy(() -> game.applyGuess("книга")).isInstanceOf(IllegalStateException.class);
    }
}
