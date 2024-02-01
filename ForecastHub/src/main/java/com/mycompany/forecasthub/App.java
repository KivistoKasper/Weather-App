package com.mycompany.forecasthub;

import java.io.IOException;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

/**
 * JavaFX App
 */
public class App extends Application {

    private static Scene scene;
    private BorderPane layout;
    private static String centerView = "mainView";

    @Override
    public void start(Stage stage) throws IOException {

        FXMLLoader sidePanelfxmlLoader = loadFXML("sidePanel");
        FXMLLoader mainViewfxmlLoader = loadFXML("mainView");

        Parent mainView = mainViewfxmlLoader.load();
        Parent sidePanel = sidePanelfxmlLoader.load();

        layout = new BorderPane();
        layout.setLeft(sidePanel);
        layout.setCenter(mainView);
        scene = new Scene(layout, 1800, 927);

        stage.setScene(scene);
        stage.setResizable(false);
        stage.setTitle("Weatherly");
        stage.show();
    }

    static void setRoot(String fxml) throws IOException {
        scene.setRoot(loadFXML(fxml).load());
    }

    // Other methods and initialization code...
    public static String getCenter() {
        return centerView;
    }

    static void setCenter(String fxml) throws IOException {
        Parent newCenter = loadFXML(fxml).load();
        ((BorderPane) scene.getRoot()).setCenter(newCenter);
        centerView = fxml;
    }

    private static FXMLLoader loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(fxml + ".fxml"));
        return fxmlLoader;
    }

    public static void main(String[] args) {

        launch();
    }

}
