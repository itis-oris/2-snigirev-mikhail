package ru.msnigirev.oris.golfclient;

import lombok.Getter;
import lombok.Setter;
import ru.msnigirev.oris.golfclient.obstactle.Obstacle;

import java.util.ArrayList;
import java.util.List;

@Getter@Setter
public class Game {
    private int currentPlayerId;
    private int thisPlayerId;
    private boolean isEnded;
    private List<Obstacle> obstacles;
    private Hole hole;
    private List<Ball> balls;
    private List<Player> players;
    private int winnerIndex;

    public Game() {
        isEnded = false;
        obstacles = new ArrayList<>();
        balls = new ArrayList<>();
        players = new ArrayList<>();
        winnerIndex = -1;
    }
    public void addObstacle(Obstacle obstacle) {
        obstacles.add(obstacle);
    }
    public boolean isFull(){
        return players.size() == 2;
    }
}
