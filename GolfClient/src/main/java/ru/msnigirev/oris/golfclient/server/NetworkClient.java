package ru.msnigirev.oris.golfclient.server;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.Getter;
import ru.msnigirev.oris.golfclient.Ball;
import ru.msnigirev.oris.golfclient.Game;
import ru.msnigirev.oris.golfclient.Hole;
import ru.msnigirev.oris.golfclient.Player;
import ru.msnigirev.oris.golfclient.obstactle.Wall;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.List;

public class NetworkClient {
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    @Getter
    private Game game;
    @Getter
    private volatile boolean isPlaying = false;

    public void connect(String host, int port, String playerName) {
        try {
            socket = new Socket(host, port);
            System.out.println("Connected to server.");

            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);

            sendMessage("CON;" + playerName);

            new Thread(() -> {
                try {
                    String line;
                    while ((line = in.readLine()) != null) {
                        handleServerMessage(line);
                    }
                } catch (IOException e) {
                    System.out.println("Connection lost.");
                }
            }).start();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void disconnect() {
        try {
            sendMessage("DIS");
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void shot(double mouseX, double mouseY, double power) {
        try {
            StringBuilder message = new StringBuilder("SHT;");
            message.append(mouseX)
                    .append(";")
                    .append(mouseY)
                    .append(";")
                    .append(power);
            sendMessage(message.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void handleServerMessage(String line) {
        try {
            String[] message = line.split(";");
            String type = message[0];

            switch (type) {
                case "STT" -> {
                    List<Player> players = game.getPlayers();
                    if (players.isEmpty()) {
                        for (int i = 0; i < message.length / 3; ++i) {
                            players.add(new Player());
                        }
                    }
                    for (int i = 1; i < message.length; i += 3) {
                        Player player = players.get(i / 3);
                        player.setName(message[i + 1]);
                        player.setScore(Integer.parseInt(message[i + 2]));
                    }
                    break;
                }
                case "BLC" -> {
                    List<Ball> balls = game.getBalls();
                    if (balls.isEmpty()) {
                        for (int i = 0; i < message.length / 2; ++i) {
                            balls.add(new Ball());
                        }
                    }
                    for (int i = 1; i < message.length; i += 2) {
                        balls.get(i / 2).setLocation(Double.parseDouble(message[i]),
                                Double.parseDouble(message[i + 1]));
                    }
                    break;
                }
                case "MAP" -> {

                    System.out.println("MAP" + line);
                    Hole hole = new Hole(Double.parseDouble(message[1]),
                            Double.parseDouble(message[2]),
                            Double.parseDouble(message[3]));
                    game.setHole(hole);
                    int obstaclesCount = Integer.parseInt(message[4]);
                    for (int i = 0; i < obstaclesCount; ++i) {
                        String obstacleLine;
                        if ((obstacleLine = in.readLine()) != null) {
                            System.out.println(obstacleLine);
                            String[] obstacleLineMessage = obstacleLine.split(";");
                            switch (obstacleLineMessage[0]) {
                                case "WLL": {
                                    Wall wall = new Wall(Double.parseDouble(obstacleLineMessage[1]),
                                            Double.parseDouble(obstacleLineMessage[2]),
                                            Double.parseDouble(obstacleLineMessage[3]),
                                            Double.parseDouble(obstacleLineMessage[4]));
                                    game.addObstacle(wall);
                                    break;
                                }
                            }
                        }
                    }
                    break;
                }
                case "CON" -> {
                    game = new Game();
                    int thisPlayerId = Integer.parseInt(message[1]);
                    game.setThisPlayerId(thisPlayerId);
                    break;
                }
                case "PLR" -> {
                    int currentPlayerId = Integer.parseInt(message[1]);
                    game.setCurrentPlayerId(currentPlayerId);
                    isPlaying = currentPlayerId == game.getThisPlayerId();
                    break;
                }
                case "END" -> {
                    game.setEnded(true);
                    game.setWinnerIndex(Integer.parseInt(message[1]));
                    break;
                }
                default -> System.out.println("unknown " + line);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void sendMessage(String msg) {
        if (out != null) {
            out.println(msg);
        }
    }

}
