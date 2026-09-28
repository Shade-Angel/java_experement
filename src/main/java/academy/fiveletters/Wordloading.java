package academy.fiveletters;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.jspecify.annotations.Nullable;

public final class Wordloading{
    private Wordloading(){}

    public static List<String> load(String resPath){
        @Nullable InputStream stream = Wordloading.class.getResourceAsStream(resPath);
        if(stream == null){
            throw new IllegalArgumentException("Файл не найден по: " + resPath);
        }

        try(var reader = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8))){
            List<String> words = reader.lines()
                .map(String::strip)
                .filter(line -> !line.isEmpty() && !line.startsWith("#"))
                .toList();

            if(words.isEmpty()){
                throw new IllegalArgumentException("Файл пуст");
            }
            return words;
        }catch(IOException e){
            throw new UncheckedIOException("Файл не прочитан ", e);
        }
    }
}