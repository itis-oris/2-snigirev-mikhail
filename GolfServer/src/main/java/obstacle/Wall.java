package obstacle;

import entity.Ball;

public class Wall implements Obstacle {
    private double x1, y1, x2, y2;

    public Wall(double x1, double y1, double x2, double y2) {
        this.x1 = x1;
        this.y1 = y1;
        this.x2 = x2;
        this.y2 = y2;
    }


    @Override
    public boolean checkCollision(Ball ball) {

        double distNew = distanceFromPointToLine(ball.getX(), ball.getY());
        if (distNew <= ball.getRadius()) {
            return true;
        }

        double distOld = distanceFromPointToLine(ball.getPreviousX(), ball.getPreviousY());
        if (distOld > ball.getRadius() && distNew > ball.getRadius()) {
            if (segmentsIntersect(
                    ball.getPreviousX(), ball.getPreviousY(),
                    ball.getX(),        ball.getY(),
                    x1, y1, x2, y2, ball.getRadius()
            )) {
                return true;
            }
        }

        return false;
    }

    @Override
    public void resolveCollision(Ball ball) {
        double[] normal = getNormalVector(ball);
        double nx = normal[0];
        double ny = normal[1];

        double dotProduct = ball.getVelocityX() * nx + ball.getVelocityY() * ny;
        double newVx = ball.getVelocityX() - 2 * dotProduct * nx;
        double newVy = ball.getVelocityY() - 2 * dotProduct * ny;
        ball.setVelocity(newVx, newVy);

        double[] closestPoint = getClosestPointOnSegment(ball.getX(), ball.getY());
        double dx = ball.getX() - closestPoint[0];
        double dy = ball.getY() - closestPoint[1];
        double distance = Math.sqrt(dx * dx + dy * dy);

        if (distance < ball.getRadius()) {
            double offset = ball.getRadius() - distance;
            ball.setPosition(
                    ball.getX() + offset * (dx / distance),
                    ball.getY() + offset * (dy / distance)
            );
        }
    }

    @Override
    public String getMessage() {
        return String.format("WLL;%s;%s;%s;%s;", x1, y1, x2, y2);
    }

    private double distanceFromPointToLine(double px, double py) {
        double A = px - x1;
        double B = py - y1;
        double C = x2 - x1;
        double D = y2 - y1;

        double dot = A * C + B * D;
        double len_sq = C * C + D * D;
        double param = (len_sq != 0) ? (dot / len_sq) : -1;

        double xx, yy;
        if (param < 0) {
            xx = x1;
            yy = y1;
        } else if (param > 1) {
            xx = x2;
            yy = y2;
        } else {
            xx = x1 + param * C;
            yy = y1 + param * D;
        }

        double dx = px - xx;
        double dy = py - yy;
        return Math.sqrt(dx * dx + dy * dy);
    }

    private double[] getNormalVector(Ball ball) {
        double[] closestPoint = getClosestPointOnSegment(ball.getX(), ball.getY());
        double dx = ball.getX() - closestPoint[0];
        double dy = ball.getY() - closestPoint[1];
        double magnitude = Math.sqrt(dx * dx + dy * dy);
        if (magnitude < 1e-8) {
            return new double[]{0, 1};
        }
        return new double[]{dx / magnitude, dy / magnitude};
    }

    private double[] getClosestPointOnSegment(double px, double py) {
        double dx = x2 - x1;
        double dy = y2 - y1;
        double t = ((px - x1) * dx + (py - y1) * dy) / (dx * dx + dy * dy);

        if (t < 0) {
            return new double[]{x1, y1};
        } else if (t > 1) {
            return new double[]{x2, y2};
        } else {
            return new double[]{x1 + t * dx, y1 + t * dy};
        }
    }
    private boolean segmentsIntersect(
            double x3, double y3, double x4, double y4,
            double x1, double y1, double x2, double y2,
            double ballRadius
    ) {
        if (!boundingBoxesIntersect(x3, y3, x4, y4, x1, y1, x2, y2, ballRadius)) {
            return false;
        }
        int o1 = orientation(x1, y1, x2, y2, x3, y3);
        int o2 = orientation(x1, y1, x2, y2, x4, y4);
        int o3 = orientation(x3, y3, x4, y4, x1, y1);
        int o4 = orientation(x3, y3, x4, y4, x2, y2);

        if (o1 != o2 && o3 != o4) {
            return true;
        }

        return false;
    }

    private boolean boundingBoxesIntersect(
            double x3, double y3, double x4, double y4,
            double x1, double y1, double x2, double y2,
            double radius
    ) {
        double minA_x = Math.min(x3, x4) - radius;
        double maxA_x = Math.max(x3, x4) + radius;
        double minA_y = Math.min(y3, y4) - radius;
        double maxA_y = Math.max(y3, y4) + radius;

        double minB_x = Math.min(x1, x2) - radius;
        double maxB_x = Math.max(x1, x2) + radius;
        double minB_y = Math.min(y1, y2) - radius;
        double maxB_y = Math.max(y1, y2) + radius;

        if (maxA_x < minB_x || minA_x > maxB_x) return false;
        if (maxA_y < minB_y || minA_y > maxB_y) return false;

        return true;
    }

    private int orientation(double x1, double y1,
                            double x2, double y2,
                            double x3, double y3) {
        double val = (y2 - y1) * (x3 - x2) - (x2 - x1) * (y3 - y2);
        if (Math.abs(val) < 1e-8) return 0;
        return (val > 0) ? 1 : -1;
    }
}
