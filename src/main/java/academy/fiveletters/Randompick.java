package academy.fiveletters;

import java.util.List;
import java.util.Random;

public final class Randompick {
    private Randompick() {}

    public static String pick(List<String> text, long seed) {
        if (text == null || text.isEmpty()) {
            throw new IllegalArgumentException(" Randompick.java  |  Словарь пуст");
        }

        Random random = new Random(seed);
        int idx = random.nextInt(text.size());
        return text.get(idx);
    }
}
