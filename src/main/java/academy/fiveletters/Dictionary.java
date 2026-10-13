package academy.fiveletters;

import java.util.List;
import java.util.Set;

public final class Dictionary {
    private final List<String> words;
    private final Set<String> wordSet;

    public Dictionary(List<String> words) {
        if (words == null) {
            throw new IllegalArgumentException("Dictionary | список слов равен null");
        }
        List<String> normalized = words.stream()
                .map(String::strip)
                .map(String::toLowerCase)
                .filter(word -> !word.isEmpty())
                .toList();

        if (normalized.isEmpty()) {
            throw new IllegalArgumentException("Dictionary | список слов пуст");
        }
        this.words = List.copyOf(normalized);
        this.wordSet = Set.copyOf(normalized);
    }

    public static Dictionary of(String... words) {
        return new Dictionary(List.of(words));
    }

    public boolean containsWord(String word) {
        return word != null && wordSet.contains(word);
    }

    public List<String> words() {
        return words;
    }

    public int size() {
        return words.size();
    }
}
