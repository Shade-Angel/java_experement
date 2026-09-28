package academy.fiveletters;

import java.util.ArrayList;
import java.util.List;



public class Game {
    private final String ans;
    private final int maxAttemts;
    private int attempsUsed;
    private final List<String> history;
    

    private enum GameStatus{
        IN_PROGRESS,
        WIN,
        LOSE
    };
    
    GameStatus status;

    private Game(String ans, int maxAttemts){
        this.ans = ans;
        this.maxAttemts = maxAttemts;
        this.attempsUsed = 0;
        this.history = new ArrayList<>();
        this.status = GameStatus.IN_PROGRESS;
    }

    public static Game startGame(List<String> dict, int maxAttemts, long seed){
        if(dict == null || dict.isEmpty()){
            System.out.println("Словарь пустой!");
            throw new IllegalArgumentException(" Game.java  |  Словарь пуст!");
        }
        if(maxAttemts <= 0){
            System.out.println("Количество попыток должно быть больше 0!");
            throw new IllegalArgumentException(" Game.java |   maxAttempts должен быть больше 0");
        }

        String ans = Randompick.pick(dict, seed);
        return new Game(ans, maxAttemts);
    }

    public String ans(){
        return ans;
    }

    public int maxAttemts(){
        return  maxAttemts;
    }

    public int attempsUsed(){
        return attempsUsed;
    }

    public List<String> history(){
        return history;
    }

    public GameStatus status(){
        return status;
    }

    public boolean isFinish(){
        return status != GameStatus.IN_PROGRESS;
    }

    public int attemRemain(){
        return maxAttemts - attempsUsed;
    }


    public void recordAttempt(String guess){
        if(isFinish()){
            System.out.println("Игра закончилась.");
            return ;
        }

        history.add(guess);
        attempsUsed++;

        if(guess.equals((ans))){
            status = GameStatus.WIN;
        }else if(attempsUsed >= maxAttemts){
            status = GameStatus.LOSE;
        }

    }
}
