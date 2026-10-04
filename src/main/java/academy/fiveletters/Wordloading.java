package academy.fiveletters;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import org.jspecify.annotations.Nullable;

public final class Wordloading {
    private Wordloading() {}

    public static List<String> load(String resPath) {
        @Nullable InputStream istream = Wordloading.class.getResourceAsStream(resPath);
        if (istream == null) {
            throw new IllegalArgumentException("Wordloading | Файл не найден по: " + resPath);
        }

        try (var reader = new BufferedReader(new InputStreamReader(istream, StandardCharsets.UTF_8))) {
            List<String> words = reader.lines()
                    .map(String::strip)
                    .filter(line -> !line.isEmpty() && !line.startsWith("#"))
                    .toList();

            if (words.isEmpty()) {
                throw new IllegalArgumentException("Wordloading | Файл пуст");
            }
            return words;
        } catch (IOException e) {
            throw new UncheckedIOException("Wordloading | Файл не прочитан ", e);
        }
    }
}
