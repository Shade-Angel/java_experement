package academy.fiveletters;

public final class Statistic {

    private int wins;
    private int losses;
    private int totalGuesses;
    private int bestWinAttempts = Integer.MAX_VALUE;

    public void recordWin(int attempts) {
        wins++;
        totalGuesses += attempts;
        if (attempts < bestWinAttempts) {
            bestWinAttempts = attempts;
        }
    }

    public void recordLose(int attempts) {
        losses++;
        totalGuesses += attempts;
    }

    public int games() {
        return wins + losses;
    }

    public int wins() {
        return wins;
    }

    public int losses() {
        return losses;
    }

    public int totalGuesses() {
        return totalGuesses;
    }

    public int bestWinAttempts() {
        return bestWinAttempts;
    }
}
