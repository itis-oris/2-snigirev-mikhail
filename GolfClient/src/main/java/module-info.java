module ru.msnigirev.oris.golfclient {
    requires javafx.controls;
    requires javafx.fxml;
    requires static lombok;
    requires com.fasterxml.jackson.databind;


    opens ru.msnigirev.oris.golfclient to javafx.fxml;
    exports ru.msnigirev.oris.golfclient;
    exports ru.msnigirev.oris.golfclient.server;
    opens ru.msnigirev.oris.golfclient.server to javafx.fxml;
    exports ru.msnigirev.oris.golfclient.controller;
    opens ru.msnigirev.oris.golfclient.controller to javafx.fxml;
}