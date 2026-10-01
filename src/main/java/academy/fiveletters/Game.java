package academy.fiveletters;

import java.util.ArrayList;
import java.util.List;

public final class Game {
    private final String ans;
    private final int maxAttemts;
    private int attemptsUsed;
    private final List<String> attemptsHistory;

    private Status status;

    private Game(String ans, int maxAttemts) {
        this.ans = ans;
        this.maxAttemts = maxAttemts;
        this.attemptsUsed = 0;
        this.attemptsHistory = new ArrayList<>();
        this.status = Status.IN_PROGRESS;
    }

    public static Game startGame(List<String> dict, int maxAttemts, long seed) {
        if (dict == null || dict.isEmpty()) {
            throw new IllegalArgumentException(" Game.java  |  Словарь пуст!");
        }
        if (maxAttemts <= 0) {
            throw new IllegalArgumentException(" Game.java |   maxAttempts должен быть больше 0");
        }

        String ans = Randompick.pick(dict, seed);
        return new Game(ans, maxAttemts);
    }

    public String ans() {
        return ans;
    }

    public int maxAttemts() {
        return maxAttemts;
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

    public int attemRemain() {
        return maxAttemts - attemptsUsed;
    }

    public void recordAttempt(String guess) {
        if (isFinish()) {
            throw new IllegalStateException("Игра закончилась.");
        }

        attemptsHistory.add(guess);
        attemptsUsed++;

        if (guess.equals(ans)) {
            status = Status.WIN;
        } else if (attemptsUsed >= maxAttemts) {
            status = Status.LOSE;
        }
    }
}
