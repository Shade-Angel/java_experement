package academy.fiveletters.game;

import academy.fiveletters.words.Dictionary;
import academy.fiveletters.words.Randompick;
import academy.fiveletters.words.WordValidator;
import java.util.ArrayList;
import java.util.List;

public final class Game {
    private static final int WORD_LEN = 5;
    private final String ans;
    private final int maxAttempts;
    private int attemptsUsed;
    private final List<String> attemptsHistory;

    private Status status;
    private final Dictionary dictionary;

    private Game(String ans, int maxAttemts, Dictionary dictionary) {
        this.ans = ans;
        this.maxAttempts = maxAttemts;
        this.attemptsUsed = 0;
        this.attemptsHistory = new ArrayList<>();
        this.status = Status.IN_PROGRESS;
        this.dictionary = dictionary;
    }

    public static Game startGame(Dictionary dictionary, int maxAttemts, long seed) {
        if (maxAttemts <= 0) {
            throw new IllegalArgumentException(" Game.java |   maxAttempts должен быть больше 0");
        }

        String ans = Randompick.pick(dictionary, seed);
        return new Game(ans, maxAttemts, dictionary);
    }

    public static Game createWithAnswer(String answer, int maxAttempts, Dictionary dictionary) {
        if (maxAttempts <= 0) {
            throw new IllegalArgumentException(" Game.java |   maxAttempts должен быть больше 0");
        }
        if (!dictionary.containsWord(answer)) {
            throw new IllegalArgumentException("Game | ответ нет в словаре: " + answer);
        }
        return new Game(answer, maxAttempts, dictionary);
    }

    public String ans() {
        return ans;
    }

    public int maxAttempts() {
        return maxAttempts;
    }

    public int attemptsUsed() {
        return attemptsUsed;
    }

    public List<String> history() {
        return List.copyOf(attemptsHistory);
    }

    public Status status() {
        return status;
    }

    public boolean isFinish() {
        return status != Status.IN_PROGRESS;
    }

    public int attemptsRemaining() {
        return maxAttempts - attemptsUsed;
    }

    public GuessRessult applyGuess(String guess) {
        if (isFinish()) {
            throw new IllegalStateException("Игра закончилась.");
        }

        String normalGuess = WordValidator.narmalsize(guess);

        if (!isValidGuess(normalGuess)) {
            return GuessRessult.invalid();
        }

        List<LetterStatus> statuses = evaluate(normalGuess);
        recordValidGuess(normalGuess);
        return GuessRessult.valid(statuses);
    }

    private boolean isValidGuess(String normalGuess) {
        return WordValidator.isGood(normalGuess) && dictionary.containsWord(normalGuess);
    }

    private void recordValidGuess(String normalGuess) {
        attemptsHistory.add(normalGuess);
        attemptsUsed++;
        updateGameStatus(normalGuess);
    }

    private void updateGameStatus(String normalGuess) {
        if (normalGuess.equals(ans)) {
            status = Status.WIN;
        } else if (attemptsUsed >= maxAttempts) {
            status = Status.LOSE;
        }
    }

    private List<LetterStatus> evaluate(String guess) {
        LetterStatus[] statuses = new LetterStatus[WORD_LEN];
        boolean[] used = new boolean[WORD_LEN];
        markExactMatches(guess, statuses, used);
        markPartialMatches(guess, statuses, used);
        return List.of(statuses);
    }

    private void markExactMatches(String guess, LetterStatus[] statuses, boolean[] used) {
        for (int i = 0; i < WORD_LEN; i++) {
            if (guess.charAt(i) == ans.charAt(i)) {
                statuses[i] = LetterStatus.EXACT;
                used[i] = true;
            }
        }
    }

    private void markPartialMatches(String guess, LetterStatus[] statuses, boolean[] used) {
        for (int i = 0; i < WORD_LEN; i++) {
            if (statuses[i] == null) {
                statuses[i] = findPartialMatch(guess.charAt(i), used);
            }
        }
    }

    private LetterStatus findPartialMatch(char letter, boolean[] used) {
        for (int j = 0; j < WORD_LEN; j++) {
            if (!used[j] && letter == ans.charAt(j)) {
                used[j] = true;
                return LetterStatus.PRESENT;
            }
        }
        return LetterStatus.ABSENT;
    }
}
