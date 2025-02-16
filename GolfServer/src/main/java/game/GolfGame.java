package game;

import entity.Ball;
import entity.Player;
import lombok.Getter;
import obstacle.Obstacle;

import java.util.Arrays;

@Getter
public class GolfGame {
    private final static int size = 2;
    private Ball[] balls;
    private Player[] players;
    private GolfMap map;
    private boolean isEnded;
    private int currentRound;
    private final int maxRounds;
    private int currentPlayerIndex;
    private int winnerIndex;

    public GolfGame(GolfMap map, Ball[] balls, int maxRounds) {
        this.maxRounds = maxRounds;
        this.players = new Player[size];
        this.map = map;
        this.balls = balls;
        currentRound = 1;
        isEnded = false;
        currentPlayerIndex = (int) (Math.random() * size);
        winnerIndex = -1;
    }

    public boolean addPlayer(Player player) {
        for (int i = 0; i < size; ++i) {
            if (players[i] == null) {
                players[i] = player;
                return true;
            }
        }
        return false;
    }
    private void checkBoundsCollision(Ball ball, double width, double height) {
        if (ball.getX() - ball.getRadius() < 0) {
            ball.setPosition(ball.getRadius(), ball.getY());
            ball.setVelocity(-ball.getVelocityX(), ball.getVelocityY());
        } else if (ball.getX() + ball.getRadius() > width) {
            ball.setPosition(width - ball.getRadius(), ball.getY());
            ball.setVelocity(-ball.getVelocityX(), ball.getVelocityY());
        }
        if (ball.getY() - ball.getRadius() < 0) {
            ball.setPosition(ball.getX(), ball.getRadius());
            ball.setVelocity(ball.getVelocityX(), -ball.getVelocityY());
        } else if (ball.getY() + ball.getRadius() > height) {
            ball.setPosition(ball.getX(), height - ball.getRadius());
            ball.setVelocity(ball.getVelocityX(), -ball.getVelocityY());
        }
    }


    public boolean update(double deltaTime) {
        for (int i = 0; i < balls.length; i++) {

            Ball ball = balls[i];
            ball.updatePosition(deltaTime);

            checkBoundsCollision(ball, map.getWidth(), map.getHeight());

            for (Obstacle obs : map.getObstacles()) {
                if (obs.checkCollision(ball)) {
                    obs.resolveCollision(ball);
                }
                if (map.getHole().checkInHole(ball)) {
                    System.out.println("Игрок " + players[i].getName() + " попал в лунку!");
                    players[i].addScore(1);
                    currentRound++;
                    resetRound();
                    return true;
                }
            }
        }
        return false;
    }
    private void resetRound() {
        if (players[0].getScore() > maxRounds / 2 || players[1].getScore() > maxRounds / 2 || currentRound > maxRounds) {
            Player winner = Arrays.stream(players).max((p1, p2) -> p1.getScore() - p2.getScore()).get();
            for (int i = 0; i < players.length; ++i) {
                if (players[i] == winner) {
                    winnerIndex = i;
                    break;
                }
            }
            isEnded = true;
            System.out.println("Игра завершена!");
            return;
        }
        balls[0].setPosition(50, map.getHeight() / 2);
        balls[1].setPosition(70, map.getHeight() / 2);
        balls[0].setVelocity(0, 0);
        balls[1].setVelocity(0, 0);
    }
    public Ball getCurrentBall(){
        return balls[currentPlayerIndex];
    }

    public void nextTurn() {
        currentPlayerIndex = (currentPlayerIndex + 1) % size;
    }
}
