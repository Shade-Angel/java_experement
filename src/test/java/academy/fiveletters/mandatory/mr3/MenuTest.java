package academy.fiveletters.mandatory.mr3;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import academy.fiveletters.Main;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/** Обязательные тесты: меню и режим автопроверки. */
@DisplayName("MR3. Меню и детерминированный режим")
class MenuTest {

    @Test
    @DisplayName("Некорректный пункт меню не роняет программу")
    void invalidMenuChoiceDoesNotCrash() {
        String input = "abc\n" + "2\n";
        InputStream originInput = System.in;
        PrintStream originOut = System.out;
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        try {
            System.setIn(new ByteArrayInputStream(input.getBytes()));
            System.setOut(new PrintStream(outputStream));

            assertDoesNotThrow(() -> Main.main(new String[] {}));

            String output = outputStream.toString();
            assertThat(output).containsIgnoringCase("некорректный");
            assertThat(output).containsIgnoringCase("спасибо за игру");
        } finally {
            System.setIn(originInput);
            System.setOut(originOut);
        }
    }

    @Test
    @DisplayName("Можно сыграть несколько партий подряд без перезапуска")
    void severalGamesInARow() {
        String input = "1\n" + "слово\nслово\nслово\nслово\nслово\nслово\n" + "2\n" + "1\n" + "озера\nозеро\n" + "2\n";

        InputStream originInput = System.in;
        PrintStream originOut = System.out;
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        try {
            System.setIn(new ByteArrayInputStream(input.getBytes()));
            System.setOut(new PrintStream(outputStream));

            assertDoesNotThrow(() -> Main.main(new String[] {}));
            String output = outputStream.toString();
            assertThat(output).containsIgnoringCase("новая игра началась").describedAs("Должна начаться первая игра");
            assertThat(output).containsIgnoringCase("Спасибо за игру");

        } finally {
            System.setIn(originInput);
            System.setOut(originOut);
        }
    }

    @Test
    @DisplayName("Детерминированный режим даёт предсказуемый вывод для автопроверки")
    void deterministicModeProducesPredictableOutput() {
        PrintStream originOut = System.out;
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        try {
            System.setOut(new PrintStream((outputStream)));
            Main.main(new String[] {"--check", "--answer", "озеро", "--guess", "арбуз"});

            String output = outputStream.toString();
            assertThat(output).contains("❌🟡❌❌🟡");
            assertThat(output).contains("STATUS: IN_PROGRESS");
            assertThat(output).contains("ANSWER: озеро");
        } finally {
            System.setOut(originOut);
        }
    }
}
