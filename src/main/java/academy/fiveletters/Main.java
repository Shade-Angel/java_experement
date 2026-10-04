package academy.fiveletters;

import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Scanner;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Точка входа в игру «5 букв». */
public final class Main {

    private static final Logger LOG = LoggerFactory.getLogger(Main.class);
    private static final String WORD = "/dictionary.txt";
    private static final int MAX_ATTEMPTS = 6;
    private static final int FOUR = 4;

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

        Scanner scan = new Scanner(System.in, StandardCharsets.UTF_8);
        boolean isRun = true;

        System.out.println("Добро пожаловать в игру!");
        System.out.println(String.format("Нужно угадать слово из 5 букв за %d попыток.", MAX_ATTEMPTS));
        System.out.flush();
        System.out.println();
        System.out.println("✅ - означает, что буква на своём месте");
        System.out.println("🟡 - означает, что буква есть, но не там");
        System.out.println("❌ - означает, что буквы нет в слове");
        System.out.println();

        while (isRun) {
            System.out.println("Меню:");
            System.out.println("1. Новая игра.");
            System.out.println("2. Выход из программы.");
            System.out.print("Ваш выбор: ");
            System.out.flush();

            if (!scan.hasNextLine()) {
                break;
            }

            try {
                String in = scan.nextLine().trim();
                isRun = handleMenuChoice(in, scan, dict);
            } catch (NoSuchElementException e) {
                break;
            }
        }
        scan.close();
    }

    private static boolean handleMenuChoice(String in, Scanner scan, List<String> dict) {
        return switch (in) {
            case "1" -> {
                playGame(scan, dict);
                yield true;
            }
            case "2" -> {
                System.out.println("Спасибо за игру!");
                System.out.flush();
                yield false;
            }
            default -> {
                System.out.println("Некорректный выбор.");
                System.out.println();
                System.out.flush();
                yield true;
            }
        };
    }

    private static void playGame(Scanner scan, List<String> diction) {
        long seed = System.currentTimeMillis();
        Game game = Game.startGame(diction, MAX_ATTEMPTS, seed);

        System.out.println();
        System.out.println("Новая игра началась!");
        System.out.println(String.format("У вас есть %d попыток.", MAX_ATTEMPTS));
        System.out.println();

        while (!game.isFinish()) {
            System.out.print("Попытка " + (game.attemptsUsed() + 1) + ". Введите слово: ");
            String input = scan.nextLine().trim();

            if (input.isEmpty()) {
                System.out.println("Вы ничего не ввели! Попробуйте заново.");
                continue;
            }

            GuessRessult result = game.applyGuess(input);

            if (!result.isValid()) {
                System.out.println("Некорректный ввод. Слово должно состоять из 5 русских букв и быть в словаре.");
                System.out.println("Осталось попыток: " + game.attemptsRemaining());
                System.out.println();
                continue;
            }

            System.out.println(result.feedback() + " " + input.toLowerCase());
            System.out.println("Осталось попыток: " + game.attemptsRemaining());
            System.out.println();
        }

        if (game.status() == Status.WIN) {
            System.out.println(
                    "Победа! Слово угадано за " + game.attemptsUsed() + " " + getAttemptWord(game.attemptsUsed()));
        } else {
            System.out.println("Неудача. Загаданное слово: " + game.ans());
        }
        System.out.println();
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

    private static void runCheck(String[] args) {
        CheckArgs parsed = parseCheckArgs(args);
        if (parsed == null) {
            return;
        }

        List<String> dict =
                parsed.answer.equals(parsed.guess) ? List.of(parsed.answer) : List.of(parsed.answer, parsed.guess);
        Game game = Game.createWithAnswer(parsed.answer, MAX_ATTEMPTS, dict);

        GuessRessult res = game.applyGuess(parsed.guess);

        if (!res.isValid()) {
            System.out.println("Неверный guess.");
        } else {
            System.out.println(res.feedback());
            System.out.printf("STATUS: %s%n", game.status());
            System.out.printf("ANSWER: %s%n", game.ans());
        }
        System.out.flush();
    }

    @Nullable
    private static CheckArgs parseCheckArgs(String[] args) {
        String ans = null;
        String guess = null;

        int i = 1;
        while (i < args.length) {
            String arg = args[i];
            if ("--answer".equals(arg) && i + 1 < args.length) {
                ans = args[i + 1].toLowerCase();
                i += 2;
            } else if ("--guess".equals(arg) && i + 1 < args.length) {
                guess = args[i + 1].toLowerCase();
                i += 2;
            } else if ("--seed".equals(arg) && i + 1 < args.length) {
                try {
                    Long.parseLong(args[i + 1]);
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

        if (ans == null || guess == null) {
            System.err.println(
                    "Main.java | Использование: --check --answer <слово> --guess <попытка> [--seed <число>]");
            System.err.flush();
            return null;
        }

        return new CheckArgs(ans, guess);
    }

    private record CheckArgs(String answer, String guess) {}
}
