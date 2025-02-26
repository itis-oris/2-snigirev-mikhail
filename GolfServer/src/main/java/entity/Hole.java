package entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class Hole {
    private double centerX;
    private double centerY;
    private double radius;
    public boolean checkInHole(Ball ball) {
        double dx = ball.getX() - centerX;
        double dy = ball.getY() - centerY;
        double dist = Math.sqrt(dx*dx + dy*dy);
        return dist <= radius;
    }
}
