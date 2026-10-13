package academy.fiveletters;

import static org.assertj.core.api.Assertions.assertThat;

import academy.fiveletters.game.Game;
import academy.fiveletters.words.Dictionary;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("MR2 Игра помнит попытки игрока")
class GameSessionTest {

    @Test
    @DisplayName("История хранит попытки в том порядке, как их вводили")
    void remembersGuessHistory() {
        Game game = Game.createWithAnswer("озеро", 6, Dictionary.of("озеро", "книга", "арбуз"));

        game.applyGuess("книга");
        game.applyGuess("арбуз");

        assertThat(game.history()).containsExactly("книга", "арбуз");
        assertThat(game.attemptsRemaining()).isEqualTo(4);
    }
}
