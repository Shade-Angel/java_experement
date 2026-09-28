package academy.fiveletters;

import java.util.ArrayList;
import java.util.List;

public final class Game {
    private final String ans;
    private final int maxAttemts;
    private int attempsUsed;
    private final List<String> history;

    private Status status;

    private Game(String ans, int maxAttemts) {
        this.ans = ans;
        this.maxAttemts = maxAttemts;
        this.attempsUsed = 0;
        this.history = new ArrayList<>();
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

    public int attempsUsed() {
        return attempsUsed;
    }

    public List<String> history() {
        return List.copyOf(history);
    }

    public Status status() {
        return status;
    }

    public boolean isFinish() {
        return status != Status.IN_PROGRESS;
    }

    public int attemRemain() {
        return maxAttemts - attempsUsed;
    }

    public void recordAttempt(String guess) {
        if (isFinish()) {
            throw new IllegalStateException("Игра закончилась.");
        }

        history.add(guess);
        attempsUsed++;

        if (guess.equals(ans)) {
            status = Status.WIN;
        } else if (attempsUsed >= maxAttemts) {
            status = Status.LOSE;
        }
    }
}
