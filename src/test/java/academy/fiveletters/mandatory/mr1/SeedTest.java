package academy.fiveletters.mandatory.mr1;

import static org.assertj.core.api.Assertions.assertThat;

import academy.fiveletters.words.Dictionary;
import academy.fiveletters.words.Randompick;
import academy.fiveletters.words.Wordloading;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/** Обязательные тесты: одинаковый seed обязан давать одинаковое загаданное слово. */
@DisplayName("MR1. Воспроизводимость по seed")
class SeedTest {

    @Test
    @DisplayName("Одинаковый seed даёт одинаковое загаданное слово")
    void sameSeedProducesSameAnswer() {
        Dictionary words = new Dictionary(Wordloading.load("/dictionary.txt"));

        String first = Randompick.pick(words, 42L);
        String second = Randompick.pick(words, 42L);
        assertThat(first).isEqualTo(second);
    }

    @Test
    @DisplayName("Разные seed'ы дают разные слова хотя бы иногда")
    void differentSeedsProduceDifferentAnswers() {
        Dictionary words = new Dictionary(Wordloading.load("/dictionary.txt"));

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
