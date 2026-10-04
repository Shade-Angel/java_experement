package academy.fiveletters;

public final class WordValidator {
    private static final int WORD_LEN = 5;

    private WordValidator() {}

    public static String narmalsize(String check) {
        return check == null ? "" : check.strip().toLowerCase();
    }

    public static boolean isGood(String word) {
        if (word == null || word.length() != WORD_LEN) {
            return false;
        }
        for (int i = 0; i < WORD_LEN; i++) {
            char chr = word.charAt(i);
            if (chr < 'а' || (chr > 'я' && chr != 'ё')) {
                return false;
            }
        }
        return true;
    }
}
