package entity;

import lombok.Getter;

@Getter
public class Player {
    private int score;
    private String name;

    public Player(String name) {
        this.name = name;
        this.score = 0;
    }
    public void addScore(int plus){
        score += plus;
    }
}
