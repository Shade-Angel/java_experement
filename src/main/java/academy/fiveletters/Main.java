package academy.fiveletters;

import academy.fiveletters.console.ConsoleInput;
import academy.fiveletters.console.ConsoleOutput;
import academy.fiveletters.console.FeedbackRender;
import academy.fiveletters.console.GameRunner;
import academy.fiveletters.game.Game;
import academy.fiveletters.game.GuessRessult;
import academy.fiveletters.words.Dictionary;
import academy.fiveletters.words.Wordloading;
import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.List;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class Main {

    private static final Logger LOG = LoggerFactory.getLogger(Main.class);
    private static final String WORD = "/dictionary.txt";
    private static final int MAX_ATTEMPTS = 6;

    @SuppressWarnings("SystemConsoleNull")
    private static final boolean INITIAL_COLOR_ENABLED =
            System.getenv("NO_COLOR") == null && System.getenv("CI") == null && System.console() != null;

    private Main() {}

    public static void main(String[] args) {
        LOG.debug("Аргументы запуска: {}", String.join(" ", args));

        if (args.length > 0 && "--check".equals(args[0])) {
            runCheck(args);
            return;
        }

        Dictionary dictionary;
        try {
            dictionary = new Dictionary(Wordloading.load(WORD));
        } catch (IllegalArgumentException | UncheckedIOException err) {
            System.err.println("Main.java | Ошибка загрузки словаря: " + err);
            System.err.flush();
            return;
        }

        ConsoleInput input = ConsoleInput.fromSystemIn();
        ConsoleOutput output = new ConsoleOutput(INITIAL_COLOR_ENABLED);
        GameRunner runner = new GameRunner(dictionary, input, output);

        runner.run();
        input.close();
    }

    private static void runCheck(String[] args) {
        CheckArgs parsed = parseCheckArgs(args);
        if (parsed == null) {
            return;
        }

        Game game = createCheckGame(parsed);
        if (game == null) {
            return;
        }

        for (String guess : parsed.guesses()) {
            GuessRessult res = game.applyGuess(guess);
            if (res.isValid()) {
                System.out.println(FeedbackRender.render(res.statuses()));
            } else {
                System.out.println("Неверный guess: " + guess);
            }
        }
        System.out.printf("STATUS: %s%n", game.status());
        System.out.printf("ANSWER: %s%n", game.ans());
        System.out.flush();
    }

    @Nullable
    private static Game createCheckGame(CheckArgs parsed) {
        String answer = parsed.answer();
        if (answer != null) {
            List<String> words = new ArrayList<>(parsed.guesses());
            words.add(answer);
            Dictionary dictionary = new Dictionary(words);
            return Game.createWithAnswer(answer, MAX_ATTEMPTS, dictionary);
        }

        Long seed = parsed.seed();
        if (seed == null) {
            return null;
        }

        Dictionary dictionary;
        try {
            dictionary = new Dictionary(Wordloading.load(WORD));
        } catch (IllegalArgumentException | UncheckedIOException e) {
            System.err.println("Main.java | Ошибка загрузки словаря: " + e);
            System.err.flush();
            return null;
        }
        return Game.startGame(dictionary, MAX_ATTEMPTS, seed);
    }

    @Nullable
    private static CheckArgs parseCheckArgs(String[] args) {
        String ans = null;
        Long seed = null;
        List<String> guesses = new ArrayList<>();

        int i = 1;
        while (i < args.length) {
            String arg = args[i];
            if ("--answer".equals(arg) && i + 1 < args.length) {
                ans = args[i + 1].toLowerCase();
                i += 2;
            } else if ("--guess".equals(arg) && i + 1 < args.length) {
                guesses.add(args[i + 1].toLowerCase());
                i += 2;
            } else if ("--seed".equals(arg) && i + 1 < args.length) {
                try {
                    seed = Long.parseLong(args[i + 1]);
                } catch (NumberFormatException e) {
                    System.err.println("Main.java | Неверное значение seed: " + args[i + 1]);
                    System.err.flush();
                    return null;
                }
                i += 2;
            } else {
                i++;
            }
        }

        if (guesses.isEmpty() || (ans == null && seed == null)) {
            System.err.println("Main.java | Использование: --check (--answer <слово> | --seed <число>) "
                    + "--guess <попытка> [--guess <попытка> ...]");
            System.err.flush();
            return null;
        }

        return new CheckArgs(ans, seed, List.copyOf(guesses));
    }

    private record CheckArgs(
            @Nullable String answer, @Nullable Long seed, List<String> guesses) {}
}
