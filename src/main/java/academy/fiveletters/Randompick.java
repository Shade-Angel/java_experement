package academy.fiveletters;

import java.util.Random;

public final class Randompick {
    private Randompick() {}

    public static String pick(Dictionary dict, long seed) {
        Random random = new Random(seed);
        return dict.words().get(random.nextInt(dict.size()));
    }
}
