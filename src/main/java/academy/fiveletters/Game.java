package academy.fiveletters;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public final class Game {
    private static final int WORD_LEN = 5;
    private final String ans;
    private final int maxAttempts;
    private int attemptsUsed;
    private final List<String> attemptsHistory;

    private Status status;
    private final Set<String> diction;

    private Game(String ans, int maxAttemts, Set<String> diction) {
        this.ans = ans;
        this.maxAttempts = maxAttemts;
        this.attemptsUsed = 0;
        this.attemptsHistory = new ArrayList<>();
        this.status = Status.IN_PROGRESS;
        this.diction = diction;
    }

    public static Game startGame(List<String> dict, int maxAttemts, long seed) {
        if (dict == null || dict.isEmpty()) {
            throw new IllegalArgumentException(" Game.java  |  Словарь пуст!");
        }
        if (maxAttemts <= 0) {
            throw new IllegalArgumentException(" Game.java |   maxAttempts должен быть больше 0");
        }

        String ans = Randompick.pick(dict, seed).toLowerCase();
        Set<String> dictSet = dict.stream().map(String::toLowerCase).collect(Collectors.toSet());
        return new Game(ans, maxAttemts, dictSet);
    }

    public static Game createWithAnswer(String answer, int maxAttempts, List<String> dict) {
        Set<String> dictSet = dict.stream().map(String::toLowerCase).collect(Collectors.toSet());
        return new Game(answer, maxAttempts, dictSet);
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

        String normalGuess = guess.strip().toLowerCase();

        if (!isValidGuess(normalGuess)) {
            return GuessRessult.invalid();
        }

        String feedbackStr = calculateFeedback(normalGuess);
        recordValidGuess(normalGuess);
        return GuessRessult.valid(feedbackStr);
    }

    private boolean isValidGuess(String normalGuess) {
        return WordValidator.isGood(normalGuess) && diction.contains(normalGuess);
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

    private String calculateFeedback(String guess) {
        char[] ansChar = ans.toCharArray();
        char[] guessChar = guess.toCharArray();
        String[] feedback = new String[WORD_LEN];
        boolean[] used = new boolean[WORD_LEN];

        markExactMatches(guessChar, ansChar, feedback, used);
        markPartialMatches(guessChar, ansChar, feedback, used);

        return String.join("", feedback);
    }

    private void markExactMatches(char[] guessChar, char[] ansChar, String[] feedback, boolean[] used) {
        for (int i = 0; i < WORD_LEN; i++) {
            if (guessChar[i] == ansChar[i]) {
                feedback[i] = "✅";
                used[i] = true;
            }
        }
    }

    private void markPartialMatches(char[] guessChar, char[] ansChar, String[] feedback, boolean[] used) {
        for (int i = 0; i < WORD_LEN; i++) {
            if (feedback[i] == null) {
                feedback[i] = findPartialMatch(guessChar[i], ansChar, used);
            }
        }
    }

    private String findPartialMatch(char guessChar, char[] ansChar, boolean[] used) {
        for (int j = 0; j < WORD_LEN; j++) {
            if (!used[j] && guessChar == ansChar[j]) {
                used[j] = true;
                return "🟡";
            }
        }
        return "❌";
    }
}
