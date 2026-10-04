package academy.fiveletters;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("MR1. Проверка модели сессии")
public class GameTest {

    @Test
    @DisplayName("Завершённая партия не принимает новые попытки")
    void finishedGameDoesNotAcceptNewAttempts() {
        List<String> dict = List.of("озеро", "сорок", "арбуз", "книга");
        Game game = Game.createWithAnswer("озеро", 6, dict);

        assertThat(game.status()).isEqualTo(Status.IN_PROGRESS);
        assertThat(game.attemptsUsed()).isZero();

        game.applyGuess("озеро");

        assertThat(game.status()).isEqualTo(Status.WIN);
        assertThat(game.isFinish()).isTrue();
        assertThatThrownBy(() -> game.applyGuess("книга")).isInstanceOf(IllegalStateException.class);
        assertThat(game.attemptsUsed()).isEqualTo(1);
        assertThat(game.history()).containsExactly("озеро");
    }
}
