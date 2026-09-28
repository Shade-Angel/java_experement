package academy.fiveletters;

import java.util.List;
import java.util.Random;

public class Randompick {
    private Randompick(){}

    public static String pick(List<String> words, long seed){
        if(words == null || words.isEmpty()){
            System.out.println("Ничего нет!");
            throw new IllegalArgumentException(" Randompick.java  |  Словарь пуст");
        }

        Random random = new Random(seed);
        int idx = random.nextInt(words.size());
        return words.get(idx);
    }
}
