package server;

import entity.Ball;
import entity.Hole;
import entity.Player;
import game.GolfGame;
import game.GolfMap;
import lombok.Getter;
import obstacle.Obstacle;

import java.io.IOException;

public class GameSession {
    private final static int size = 2;
    private final GolfGame game;
    private final ClientHandler[] clients;
    @Getter
    private boolean closed;
    private final int subSteps = 100;

    public GameSession(GolfGame game){
        this.game = game;
        clients = new ClientHandler[size];
        closed = false;
    }

    public boolean addClient(ClientHandler client){
        for (int i = 0; i < clients.length; ++i) {
            if (clients[i] == null) {
                clients[i] = client;
                client.setClientId(i);
                return true;
            }
        }
        return false;
    }

    public void close() throws IOException {
        closed = true;
        for (ClientHandler client : clients) {
            if (client != null) {
                client.close();
            }
        }
    }
    public synchronized void sendBallsLocation() {
        Ball[] balls = game.getBalls();
        StringBuilder message = new StringBuilder("BLC;");
        for (Ball ball : balls) {
            message.append(ball.getX())
                    .append(";")
                    .append(ball.getY())
                    .append(";");
        }
        sendMessageForAllClients(message.toString());
    }

    public synchronized void sendPlayersStat() {
        StringBuilder message = new StringBuilder("STT;");
        Player[] players = game.getPlayers();
        for (int i = 0; i < players.length; ++i) {
            message.append(i)
                    .append(";")
                    .append(players[i].getName())
                    .append(";")
                    .append(players[i].getScore())
                    .append(";");
        }
        sendMessageForAllClients(message.toString());
    }

    public synchronized void handleMessage(String line, ClientHandler client) {
        System.out.println(line);
        String[] message = line.split(";");
        String type = message[0];
        switch (type) {
            case "CON": {
                handleConnect(message, client);
                break;
            }
            case "DIS": {
                handleDisconnect(client);
                break;
            }
            case "SHT": {
                handleShot(message, client);
                break;
            }

        }
    }

    private void handleConnect(String[] message, ClientHandler client) {
        System.out.println("Client connected " + client.getName());
        String name = message[1];
        Player player = new Player(name);
        client.setPlayer(player);
        game.addPlayer(player);
        client.sendMessage("CON;" + client.getClientId());
        if (isFull()) {
            sendMessageForAllClients("PLR;" + game.getCurrentPlayerIndex());
            sendPlayersStat();
            sendMap();
            start();
        }
    }

    private void handleDisconnect(ClientHandler client) {
        client.close();
    }

    private synchronized void handleShot(String[] message, ClientHandler client) {
        if (game.getCurrentPlayerIndex() != client.getClientId()) return;
        double mouseX = Double.parseDouble(message[1]);
        double mouseY = Double.parseDouble(message[2]);
        double power = Double.parseDouble(message[3]);
        Ball currentBall = game.getCurrentBall();
        System.out.println(mouseX + " " + currentBall.getX());
        System.out.println(mouseY + " " + currentBall.getY());
        System.out.println();
        System.out.println();

        double rad = Math.atan2(mouseY - currentBall.getY(), mouseX - currentBall.getX());
        System.out.println(rad);
        double vx = power * Math.cos(rad);
        double vy = power * Math.sin(rad);

        currentBall.setVelocity(vx, vy);

        game.nextTurn();
        sendMessageForAllClients("PLR;" + game.getCurrentPlayerIndex());
    }

    private void start(){
        new Thread(() -> {
            final int UPS = 30;
            final double DELTA_TIME = 1.0 / UPS;
            long lastTime = System.currentTimeMillis();

            while (!closed && !game.isEnded()) {
                long now = System.currentTimeMillis();
                long elapsed = now - lastTime;
                if (elapsed < 1000 / UPS) {
                    try {
                        Thread.sleep((1000 / UPS) - elapsed);
                    } catch (InterruptedException ignored) {}
                }
                lastTime = System.currentTimeMillis();
                for (int i = 0; i < subSteps; ++i) {
                    if (game.update(DELTA_TIME)) {
                        sendPlayersStat();
                        if (game.isEnded()) {
                            sendMessageForAllClients("END;" + game.getWinnerIndex());
                        }
                    }
                    break;
                }
                sendBallsLocation();
            }
        }).start();
    }
    private void sendMap(){
        GolfMap map = game.getMap();
        StringBuilder message = new StringBuilder("MAP;");
        Hole hole = map.getHole();

        message.append(hole.getCenterX())
                .append(";")
                .append(hole.getCenterY())
                .append(";")
                .append(hole.getRadius())
                .append(";");

        message.append(map.getObstacles().size())
                .append(";");
        for (Obstacle obstacle : map.getObstacles()) {
            message.append('\n').append(obstacle.getMessage());
        }
        sendMessageForAllClients(message.toString());
    }
    private void sendMessageForAllClients(String message) {
        if (!message.substring(0, 3).equals("BLC")) {

            System.out.println("Message: " + message);
        }
        for (ClientHandler client : clients) {
            if (client != null) {
                client.sendMessage(message);
            }
        }
    }
    private boolean isFull(){
        for (ClientHandler client : clients) {
            if (client == null) {
                return false;
            }
        }
        return true;
    }
}
