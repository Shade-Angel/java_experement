package academy.fiveletters.console;

import academy.fiveletters.game.LetterStatus;
import academy.fiveletters.game.Statistic;
import java.util.List;

public interface UserOutput {
    void printBanner();

    void printMenu();

    void printInvalidMenuChoice();

    void printGameStart(long seed, int maxAttempts, int maxHints);

    void printAttemptPrompt(int attemptsNumber);

    void printEmptyInput();

    void printHint(char letter, int possition);

    void printHintExhausted();

    void printInvalidGuess(int attemptsRemaining);

    void printGuessResult(List<LetterStatus> statuses, String guess, int attemptsRemaining);

    void printWin(int attemptsUsed);

    void printLose(String answer);

    void printFarewell();

    void printStatistics(Statistic statistic);

    void toggleColors();
}
