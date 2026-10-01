package academy.fiveletters;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("MR1. Проверка модели сессии")
public class GameTest {
    @Test
    @DisplayName("Завершенная партия не принимает новые попытки")
    void finshGame() {
        List<String> dict = List.of("клава", "телефон", "ноутбук", "javascript", "vite");
        Game game = Game.startGame(dict, 6, 23L);

        assertThat(game.status()).isEqualByComparingTo(Status.IN_PROGRESS);
        assertThat(game.attemptsUsed()).isZero();
        assertThat(game.history()).isEmpty();
        game.recordAttempt(game.ans());

        assertThat(game.status()).isEqualTo(Status.WIN);
        assertThat(game.isFinish()).isTrue();
        assertThatThrownBy(() -> game.recordAttempt("телефон")).isInstanceOf(IllegalStateException.class);
        assertThat(game.attemptsUsed()).isEqualTo(1);
        assertThat(game.history()).containsExactly(game.ans());
    }
}
