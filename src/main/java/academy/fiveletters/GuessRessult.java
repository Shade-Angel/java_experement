package academy.fiveletters;

public record GuessRessult(String feedback, boolean isValid) {
    public static GuessRessult invalid() {
        return new GuessRessult("", false);
    }

    public static GuessRessult valid(String feedback) {
        return new GuessRessult(feedback, true);
    }
}
