package academy.fiveletters;

import java.util.List;

public record GuessRessult(List<LetterStatus> statuses, boolean isValid) {
    public static GuessRessult invalid() {
        return new GuessRessult(List.of(), false);
    }

    public static GuessRessult valid(List<LetterStatus> statuses) {
        return new GuessRessult(List.copyOf(statuses), true);
    }
}
