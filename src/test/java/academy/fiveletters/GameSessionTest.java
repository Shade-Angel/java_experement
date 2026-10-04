package academy.fiveletters;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("MR2 Игра помнит попытки игрока")
class GameSessionTest {

    @Test
    @DisplayName("История хранит попытки в том порядке, как их вводили")
    void remembersGuessHistory() {
        Game game = Game.createWithAnswer("озеро", 6, List.of("озеро", "книга", "арбуз"));

        game.applyGuess("книга");
        game.applyGuess("арбуз");

        assertThat(game.history()).containsExactly("книга", "арбуз");
        assertThat(game.attemptsRemaining()).isEqualTo(4);
    }
}
