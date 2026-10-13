package academy.fiveletters;

import java.util.List;

public final class FeedbackRender {
    private static final String EXACT_CHAR = "✅";
    private static final String PRESENT_CHAR = "🟡";
    private static final String ABSENT_CHAR = "❌";

    private FeedbackRender() {}

    public static String render(List<LetterStatus> statuses) {
        StringBuilder builder = new StringBuilder();
        for (LetterStatus status : statuses) {
            builder.append(singOf(status));
        }
        return builder.toString();
    }

    private static String singOf(LetterStatus status) {
        return switch (status) {
            case EXACT -> EXACT_CHAR;
            case PRESENT -> PRESENT_CHAR;
            case ABSENT -> ABSENT_CHAR;
        };
    }
}
