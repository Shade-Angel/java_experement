package academy.fiveletters.mandatory.mr1;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import academy.fiveletters.Game;
import academy.fiveletters.Wordloading;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/** Обязательные тесты: словарь. */
@DisplayName("MR1. Словарь")
class DictionaryTest {

    @Test
    @DisplayName("Словарь содержит не меньше 50 слов")
    void dictionaryContainsAtLeastFiftyWords() {
        List<String> text = Wordloading.load("/dictionary.txt");
        assertThat(text).hasSizeGreaterThanOrEqualTo(50);
    }

    @Test
    @DisplayName("Все слова словаря состоят ровно из 5 букв")
    void allWordsAreExactlyFiveLettersLong() {
        List<String> words = Wordloading.load("/dictionary.txt");

        assertThat(words).allSatisfy(word -> assertThat(word).hasSize(5).matches("[а-я]{5}"));
    }

    @Test
    @DisplayName("Пустой словарь приводит к ошибке, а не к запуску игры без слова")
    void emptyDictionaryIsRejected() {
        assertThatThrownBy(() -> Game.startGame(List.of(), 6, 42L)).isInstanceOf(IllegalArgumentException.class);
    }
}
