package academy.fiveletters;

import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class Main {

    private static final Logger LOG = LoggerFactory.getLogger(Main.class);
    private static final String WORD = "/dictionary.txt";
    private static final int MAX_ATTEMPTS = 6;
    private static final int FOUR = 4;
    private static final int MAX_HINTS = 2;
    private static final String HINT_COMMAND = "?";

    private static final String RESET = "\u001B[0m";
    private static final String GREEN = "\u001B[32m";
    private static final String YELLOW = "\u001B[33m";
    private static final String RED = "\u001B[31m";
    private static final String CYAN = "\u001B[36m";
    private static final String MAGENTA = "\u001B[35m";
    private static final String BOLD = "\u001B[1m";

    @SuppressWarnings("SystemConsoleNull")
    private static boolean colorEnabled =
            System.getenv("NO_COLOR") == null && System.getenv("CI") == null && System.console() != null;

    private Main() {}

    public static void main(String[] args) {
        LOG.debug("Аргументы запуска: {}", String.join(" ", args));

        if (args.length > 0 && "--check".equals(args[0])) {
            runCheck(args);
            return;
        }

        List<String> dict;
        try {
            dict = Wordloading.load(WORD);
        } catch (IllegalArgumentException | UncheckedIOException err) {
            System.err.println("Main.java | Ошибка загрузки файла в список: " + err);
            System.err.flush();
            return;
        }

        Statistic stats = new Statistic();
        Scanner scan = new Scanner(System.in, StandardCharsets.UTF_8);
        printBanner();

        boolean isRun = true;
        while (isRun) {
            printMenu();
            if (!scan.hasNextLine()) {
                break;
            }
            String in = scan.nextLine().trim();
            isRun = handleMenuChoice(in, scan, dict, stats);
        }
        scan.close();

        System.out.println("Спасибо за игру!");
        printStatistics(stats);
        System.out.flush();
    }

    private static boolean handleMenuChoice(String in, Scanner scan, List<String> dict, Statistic stats) {
        return switch (in) {
            case "1" -> {
                playGame(scan, dict, stats);
                yield true;
            }
            case "2" -> false;
            case "3" -> {
                toggleColors();
                yield true;
            }
            default -> {
                System.out.println(color(RED, "Некорректный выбор."));
                System.out.println();
                System.out.flush();
                yield true;
            }
        };
    }

    private static void printBanner() {
        System.out.println(color(BOLD + CYAN, "Добро пожаловать в игру!"));
        System.out.printf("Нужно угадать слово из 5 букв за %d попыток.%n", MAX_ATTEMPTS);
        System.out.println();
        System.out.println(color(GREEN, "✅ - означает, что буква на своём месте"));
        System.out.println(color(YELLOW, "🟡 - означает, что буква есть, но не там"));
        System.out.println(color(RED, "❌ - означает, что буквы нет в слове"));
        System.out.println();
        System.out.flush();
    }

    private static void printMenu() {
        System.out.println(color(BOLD + CYAN, "Меню:"));
        System.out.println("1. Новая игра.");
        System.out.println("2. Выход из программы.");
        System.out.println("3. " + (colorEnabled ? "Выключить" : "Включить") + " цветной вывод.");
        System.out.print(color(CYAN, "Ваш выбор: "));
        System.out.flush();
    }

    private static void toggleColors() {
        colorEnabled = !colorEnabled;
        String state = colorEnabled ? color(BOLD + GREEN, "ВКЛЮЧЕНЫ") : color(BOLD + RED, "ВЫКЛЮЧЕНЫ");
        System.out.println();
        System.out.println("Цвета " + state + ".");
        System.out.println("Изменение применится к следующим выводам.");
        System.out.println();
        System.out.flush();
    }

    private static void playGame(Scanner scan, List<String> diction, Statistic stats) {
        long seed = System.currentTimeMillis();
        Game game = Game.startGame(diction, MAX_ATTEMPTS, seed);
        int hintsUsed = 0;

        System.out.println();
        System.out.println(color(BOLD + CYAN, "Новая игра началась!"));
        System.out.printf("У вас есть %d попыток.%n", MAX_ATTEMPTS);
        System.out.printf("Seed партии: %d (нужен для replay через --check --seed).%n", seed);
        System.out.printf(
                "Введите %s, чтобы получить подсказку (не более %d за игру).%n",
                color(YELLOW, HINT_COMMAND), MAX_HINTS);
        System.out.println();
        System.out.flush();

        while (!game.isFinish()) {
            System.out.printf("%s ", color(CYAN, "Попытка " + (game.attemptsUsed() + 1) + "."));
            System.out.print("Введите слово: ");
            System.out.flush();
            if (!scan.hasNextLine()) {
                return;
            }
            String input = scan.nextLine().trim();

            if (input.isEmpty()) {
                System.out.println(color(YELLOW, "Вы ничего не ввели! Попробуйте заново."));
                System.out.flush();
                continue;
            }

            if (HINT_COMMAND.equals(input)) {
                hintsUsed = printHint(game, hintsUsed);
                continue;
            }

            GuessRessult result = game.applyGuess(input);

            if (!result.isValid()) {
                System.out.println(
                        color(RED, "Некорректный ввод. Слово должно состоять из 5 русских букв и быть в словаре."));
                System.out.println(color(YELLOW, "Осталось попыток: " + game.attemptsRemaining()));
                System.out.println();
                System.out.flush();
                continue;
            }

            System.out.println(result.feedback() + " " + color(BOLD, input.toLowerCase()));
            System.out.println(color(YELLOW, "Осталось попыток: " + game.attemptsRemaining()));
            System.out.println();
            System.out.flush();
        }

        if (game.status() == Status.WIN) {
            stats.recordWin(game.attemptsUsed());
        } else {
            stats.recordLose(game.attemptsUsed());
        }
        printGameResult(game);
    }

    private static int printHint(Game game, int hintsUsed) {
        if (hintsUsed >= MAX_HINTS) {
            System.out.println("Подсказки закончились.");
            System.out.flush();
            return hintsUsed;
        }
        char letter = game.ans().charAt(hintsUsed);
        System.out.println(color(YELLOW, "Подсказка: буква «" + letter + "» стоит на позиции " + (hintsUsed + 1)));
        System.out.flush();
        return hintsUsed + 1;
    }

    private static void printGameResult(Game game) {
        if (game.status() == Status.WIN) {
            System.out.println(color(
                    BOLD + GREEN,
                    "Победа! Слово угадано за " + game.attemptsUsed() + " " + getAttemptWord(game.attemptsUsed())));
        } else {
            System.out.println(color(BOLD + RED, "Неудача. Загаданное слово: " + game.ans()));
        }
        System.out.println();
        System.out.flush();
    }

    private static String getAttemptWord(int cnt) {
        if (cnt == 1) {
            return "попытку";
        }
        if (cnt >= 2 && cnt <= FOUR) {
            return "попытки";
        }
        return "попыток";
    }

    private static String color(String code, String text) {
        return colorEnabled ? code + text + RESET : text;
    }

    private static void printStatistics(Statistic stats) {
        if (stats.games() == 0) {
            return;
        }
        System.out.println(color(BOLD + MAGENTA, "Статистика за запуск:"));
        System.out.printf("  Сыграно партий: %d%n", stats.games());
        System.out.printf("  Побед: %s%n", color(GREEN, String.valueOf(stats.wins()))); // ✅ %s вместо %d
        System.out.printf("  Поражений: %s%n", color(RED, String.valueOf(stats.losses()))); // ✅ %s вместо %d
        System.out.printf("  Всего попыток: %d%n", stats.totalGuesses());
        if (stats.wins() > 0) {
            System.out.printf(
                    "  Лучшая победа: %s %s%n",
                    color(BOLD + GREEN, String.valueOf(stats.bestWinAttempts())), // ✅ %s вместо %d
                    getAttemptWord(stats.bestWinAttempts()));
        }
        System.out.flush();
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
                System.out.println(res.feedback());
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
            List<String> dict = new ArrayList<>(parsed.guesses());
            if (!dict.contains(answer)) {
                dict.add(answer);
            }
            return Game.createWithAnswer(answer, MAX_ATTEMPTS, dict);
        }

        Long seed = parsed.seed();
        if (seed == null) {
            return null;
        }

        List<String> dict;
        try {
            dict = Wordloading.load(WORD);
        } catch (IllegalArgumentException | UncheckedIOException e) {
            System.err.println("Main.java | Ошибка загрузки словаря: " + e);
            System.err.flush();
            return null;
        }
        return Game.startGame(dict, MAX_ATTEMPTS, seed);
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
            System.err.println(
                    "Main.java | Использование: --check (--answer <слово> | --seed <число>) --guess <попытка> [--guess <попытка> ...]");
            System.err.flush();
            return null;
        }

        return new CheckArgs(ans, seed, List.copyOf(guesses));
    }

    private record CheckArgs(
            @Nullable String answer, @Nullable Long seed, List<String> guesses) {}
}
