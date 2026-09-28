package academy.fiveletters.mandatory.mr1;

import static org.assertj.core.api.Assertions.assertThat;

import academy.fiveletters.Randompick;
import academy.fiveletters.Wordloading;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/** Обязательные тесты: одинаковый seed обязан давать одинаковое загаданное слово. */
@DisplayName("MR1. Воспроизводимость по seed")
class SeedTest {

    @Test
    @DisplayName("Одинаковый seed даёт одинаковое загаданное слово")
    void sameSeedProducesSameAnswer() {
        List<String> words = Wordloading.load("/dictionary.txt");

        String first = Randompick.pick(words, 42L);
        String second = Randompick.pick(words, 42L);
        assertThat(first).isEqualTo(second);
    }

    @Test
    @DisplayName("Разные seed'ы дают разные слова хотя бы иногда")
    void differentSeedsProduceDifferentAnswers() {
        List<String> words = Wordloading.load("/dictionary.txt");

        String first = Randompick.pick(words, 1L);
        boolean foundDiff = false;

        for (long seed = 2; seed <= 100; seed++) {
            String current = Randompick.pick(words, seed);

            if (!current.equals(first)) {
                foundDiff = true;
                break;
            }
        }

        assertThat(foundDiff).as("Дают разный реультат хоть иногда.").isTrue();
    }
}
