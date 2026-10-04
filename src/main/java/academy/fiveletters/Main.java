package academy.fiveletters;

import java.util.List;
import java.util.Scanner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Точка входа в игру «5 букв». */
public final class Main {

    private static final Logger LOG = LoggerFactory.getLogger(Main.class);
    private static final String WORD = "/words.txt";
    private static final int MAX_ATTEMPTS = 6;

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
        } catch (Exception err) {
            System.err.println("Main.java | Ошибка загрузки файла в список: " + err);
            return;
        }

        Scanner scan = new Scanner(System.in);
        boolean isRun = true;

        System.out.println("Добро пожаловать в игру!");
        System.out.println(String.format("Нужно угадать слово из 5 букв за %d попыток.", MAX_ATTEMPTS));
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

            String in = scan.nextLine().trim();

            switch (in) {
                case "1":
                    playGame(scan, dict);
                    break;
                case "2":
                    isRun = false;
                    System.out.println("Спасибо за игру!");
                    break;
                default:
                    System.out.println("Некорректный выбор.");
                    System.out.println();
                    break;
            }
        }
        scan.close();
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
            System.out.println("Победа! Слово угадано за " + game.attemptsUsed()
                    + " " + getAttemptWord(game.attemptsUsed()));
        } else {
            System.out.println("Неудача. Загаданное слово: " + game.ans());
        }
        System.out.println();
    }

    private static String getAttemptWord(int cnt) {
        if (cnt == 1) {
            return "попытку";
        }
        if (cnt >= 2 && cnt <= 4) {
            return "попытки";
        }
        return "попыток";
    }

    private static void runCheck(String[] args) {
        String ans = null;
        String guess = null;

        for (int i = 1; i < args.length; i++) {
            if ("--answer".equals(args[i]) && i + 1 < args.length) {
                ans = args[++i].toLowerCase();
            } else if ("--guess".equals(args[i]) && i + 1 < args.length) {
                guess = args[++i].toLowerCase();
            } else if ("--seed".equals(args[i]) && i + 1 < args.length) {
                try {
                    Long.parseLong(args[++i]);
                } catch (NumberFormatException e) {
                    System.err.println("Main.java | Неверное значение seed: " + args[i]);
                    return;
                }
            }
        }

        if (ans == null || guess == null) {
            System.err.println("Main.java | Использование: --check --answer <слово> --guess <попытка> [--seed <число>]");
            return;
        }

        List<String> dict = ans.equals(guess) ? List.of(ans) : List.of(ans, guess);
        Game game = Game.createWithAnswer(ans, MAX_ATTEMPTS, dict);

        GuessRessult res = game.applyGuess(guess);

        if (!res.isValid()) {
            System.out.println("Неверный guess.");
        } else {
            System.out.println(res.feedback());
            System.out.println("STATUS: " + game.status());
            System.out.println("ANSWER: " + game.ans());
        }
    }
}