package academy.fiveletters.console;

import academy.fiveletters.game.Game;
import academy.fiveletters.game.GuessRessult;
import academy.fiveletters.game.Statistic;
import academy.fiveletters.game.Status;
import academy.fiveletters.words.Dictionary;

public final class GameRunner {

    private static final int MAX_ATTEMPTS = 6;
    private static final int MAX_HINTS = 2;
    private static final String HINT_COMMAND = "?";
    private final Dictionary dictionary;
    private final UserInput input;
    private final UserOutput output;
    private final Statistic statistic = new Statistic();

    public GameRunner(Dictionary dictionary, UserInput input, UserOutput output) {
        this.dictionary = dictionary;
        this.input = input;
        this.output = output;
    }

    public void run() {
        output.printBanner();
        boolean running = true;
        while (running) {
            output.printMenu();
            if (!input.hasLine()) {
                break;
            }
            running = handleMenuChoice(input.readLine());
        }
        output.printFarewell();
        output.printStatistics(statistic);
    }

    private boolean handleMenuChoice(String choice) {
        return switch (choice) {
            case "1" -> {
                playGame();
                yield true;
            }
            case "2" -> false;
            case "3" -> {
                output.toggleColors();
                yield true;
            }
            default -> {
                output.printInvalidMenuChoice();
                yield true;
            }
        };
    }

    private void playGame() {
        long seed = System.currentTimeMillis();
        Game game = Game.startGame(dictionary, MAX_ATTEMPTS, seed);
        int hintsUsed = 0;

        output.printGameStart(seed, MAX_ATTEMPTS, MAX_HINTS);
        while (!game.isFinish()) {
            output.printAttemptPrompt(game.attemptsUsed() + 1);
            if (!input.hasLine()) {
                return;
            }
            hintsUsed = handleGuess(game, input.readLine(), hintsUsed);
        }

        recordOutcome(game);
        if (game.status() == Status.WIN) {
            output.printWin(game.attemptsUsed());
        } else {
            output.printLose(game.ans());
        }
    }

    private int handleGuess(Game game, String guess, int hintsUsed) {
        if (guess.isEmpty()) {
            output.printEmptyInput();
            return hintsUsed;
        }
        if (HINT_COMMAND.equals(guess)) {
            return handleHint(game, hintsUsed);
        }

        GuessRessult result = game.applyGuess(guess);
        if (!result.isValid()) {
            output.printInvalidGuess(game.attemptsRemaining());
            return hintsUsed;
        }
        output.printGuessResult(result.statuses(), guess.toLowerCase(), game.attemptsRemaining());
        return hintsUsed;
    }

    private int handleHint(Game game, int hintsUsed) {
        if (hintsUsed >= MAX_HINTS) {
            output.printHintExhausted();
            return hintsUsed;
        }
        char letter = game.ans().charAt(hintsUsed);
        output.printHint(letter, hintsUsed + 1);
        return hintsUsed + 1;
    }

    private void recordOutcome(Game game) {
        if (game.status() == Status.WIN) {
            statistic.recordWin(game.attemptsUsed());
        } else {
            statistic.recordLose(game.attemptsUsed());
        }
    }
}
