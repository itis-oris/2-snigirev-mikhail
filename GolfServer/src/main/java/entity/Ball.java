package entity;

import lombok.Getter;
import lombok.Setter;

@Getter
public class Ball {
    private double x;
    private double y;

    private double previousX;
    private double previousY;

    @Setter
    private double velocityX;
    @Setter
    private double velocityY;

    @Setter
    private double radius = 5;

    public Ball(double x, double y) {
        this.x = x;
        this.y = y;
        this.velocityX = 0;
        this.velocityY = 0;
        this.previousX = x;
        this.previousY = y;
    }

    public Ball(double x, double y, double radius) {
        this(x, y);
        this.radius = radius;
    }

    public void updatePosition(double deltaTime) {
        this.previousX = this.x;
        this.previousY = this.y;

        this.x += velocityX * deltaTime;
        this.y += velocityY * deltaTime;

        double frictionPerSecond = 0.4;
        double frictionPerFrame = Math.pow(frictionPerSecond, deltaTime);
        velocityX *= frictionPerFrame;
        velocityY *= frictionPerFrame;

        if (Math.sqrt(velocityX * velocityX + velocityY * velocityY) < 0.01) {
            velocityX = 0;
            velocityY = 0;
        }
    }

    public void setPosition(double px, double py) {
        this.x = px;
        this.y = py;
    }
    public void setVelocity(double vx, double vy) {
        velocityX = vx;
        velocityY = vy;
    }
}
