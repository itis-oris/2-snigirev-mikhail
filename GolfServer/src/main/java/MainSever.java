import server.GolfServer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class MainSever {
    public static void main(String[] args) {
        GolfServer server = new GolfServer();
        try {
            server.start();
            System.out.println("Press ENTER to stop server...");
            new BufferedReader(new InputStreamReader(System.in)).readLine();
            server.stop();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
