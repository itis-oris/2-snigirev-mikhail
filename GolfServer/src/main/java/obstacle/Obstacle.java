package obstacle;

import entity.Ball;

public interface Obstacle {
    boolean checkCollision(Ball ball);
    void resolveCollision(Ball ball);
    String getMessage();
}
