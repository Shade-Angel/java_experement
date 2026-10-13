package academy.fiveletters.console;

import academy.fiveletters.game.LetterStatus;
import academy.fiveletters.game.Statistic;
import java.util.List;

public final class ConsoleOutput implements UserOutput {

    private static final int FOUR = 4;

    private static final String RESET = "\u001B[0m";
    private static final String BOLD = "\u001B[1m";
    private static final String GREEN = "\u001B[32m";
    private static final String YELLOW = "\u001B[33m";
    private static final String RED = "\u001B[31m";
    private static final String CYAN = "\u001B[36m";
    private static final String MAGENTA = "\u001B[35m";

    private boolean colorEnabled;

    public ConsoleOutput(boolean initialColorEnabled) {
        this.colorEnabled = initialColorEnabled;
    }

    @Override
    public void printBanner() {
        System.out.println(color(BOLD + CYAN, "Добро пожаловать в игру!"));
        System.out.printf("Нужно угадать слово из 5 букв за 6 попыток.%n");
        System.out.println();
        System.out.println(color(GREEN, "✅ - означает, что буква на своём месте"));
        System.out.println(color(YELLOW, "🟡 - означает, что буква есть, но не там"));
        System.out.println(color(RED, "❌ - означает, что буквы нет в слове"));
        System.out.println();
        System.out.flush();
    }

    @Override
    public void printMenu() {
        System.out.println(color(BOLD + CYAN, "Меню:"));
        System.out.println("1. Новая игра.");
        System.out.println("2. Выход из программы.");
        System.out.println("3. " + (colorEnabled ? "Выключить" : "Включить") + " цветной вывод.");
        System.out.print(color(CYAN, "Ваш выбор: "));
        System.out.flush();
    }

    @Override
    public void printInvalidMenuChoice() {
        System.out.println(color(RED, "Некорректный выбор."));
        System.out.println();
        System.out.flush();
    }

    @Override
    public void printGameStart(long seed, int maxAttempts, int maxHints) {
        System.out.println();
        System.out.println(color(BOLD + CYAN, "Новая игра началась!"));
        System.out.printf("У вас есть %d попыток.%n", maxAttempts);
        System.out.printf("Seed партии: %d (нужен для replay через --check --seed).%n", seed);
        System.out.printf(
                "Введите %s, чтобы получить подсказку (не более %d за игру).%n", color(YELLOW, "?"), maxHints);
        System.out.println();
        System.out.flush();
    }

    @Override
    public void printAttemptPrompt(int attemptNumber) {
        System.out.print(color(CYAN, "Попытка " + attemptNumber + ".") + " Введите слово: ");
        System.out.flush();
    }

    @Override
    public void printEmptyInput() {
        System.out.println(color(YELLOW, "Вы ничего не ввели! Попробуйте заново."));
        System.out.flush();
    }

    @Override
    public void printHint(char letter, int position) {
        System.out.println(color(YELLOW, "Подсказка: буква «" + letter + "» стоит на позиции " + position));
        System.out.flush();
    }

    @Override
    public void printHintExhausted() {
        System.out.println(color(YELLOW, "Подсказки закончились."));
        System.out.flush();
    }

    @Override
    public void printInvalidGuess(int attemptsRemaining) {
        System.out.println(color(RED, "Некорректный ввод. Слово должно состоять из 5 русских букв и быть в словаре."));
        System.out.println(color(YELLOW, "Осталось попыток: " + attemptsRemaining));
        System.out.println();
        System.out.flush();
    }

    @Override
    public void printGuessResult(List<LetterStatus> statuses, String guess, int attemptsRemaining) {
        System.out.println(FeedbackRender.render(statuses) + " " + color(BOLD, guess));
        System.out.println(color(YELLOW, "Осталось попыток: " + attemptsRemaining));
        System.out.println();
        System.out.flush();
    }

    @Override
    public void printWin(int attemptsUsed) {
        System.out.println(
                color(BOLD + GREEN, "Победа! Слово угадано за " + attemptsUsed + " " + getAttemptWord(attemptsUsed)));
        System.out.println();
        System.out.flush();
    }

    @Override
    public void printLose(String answer) {
        System.out.println(color(BOLD + RED, "Неудача. Загаданное слово: " + answer));
        System.out.println();
        System.out.flush();
    }

    @Override
    public void printFarewell() {
        System.out.println("Спасибо за игру!");
        System.out.flush();
    }

    @Override
    public void printStatistics(Statistic statistic) {
        if (statistic.games() == 0) {
            return;
        }
        System.out.println(color(BOLD + MAGENTA, "Статистика за запуск:"));
        System.out.printf("  Сыграно партий: %d%n", statistic.games());
        System.out.printf("  Побед: %s%n", color(GREEN, String.valueOf(statistic.wins())));
        System.out.printf("  Поражений: %s%n", color(RED, String.valueOf(statistic.losses())));
        System.out.printf("  Всего попыток: %d%n", statistic.totalGuesses());
        if (statistic.wins() > 0) {
            System.out.printf(
                    "  Лучшая победа: %s %s%n",
                    color(BOLD + GREEN, String.valueOf(statistic.bestWinAttempts())),
                    getAttemptWord(statistic.bestWinAttempts()));
        }
        System.out.flush();
    }

    @Override
    public void toggleColors() {
        colorEnabled = !colorEnabled;
        String state = colorEnabled ? color(BOLD + GREEN, "ВКЛЮЧЕНЫ") : color(BOLD + RED, "ВЫКЛЮЧЕНЫ");
        System.out.println();
        System.out.println("Цвета " + state + ".");
        System.out.println("Изменение применится к следующим выводам.");
        System.out.println();
        System.out.flush();
    }

    private String color(String code, String text) {
        return colorEnabled ? code + text + RESET : text;
    }

    private String getAttemptWord(int count) {
        if (count == 1) {
            return "попытку";
        }
        if (count >= 2 && count <= FOUR) {
            return "попытки";
        }
        return "попыток";
    }
}
