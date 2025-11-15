package com.example.uninaswap;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.image.Image;

public class Main extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("HomePage.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        String iconPath = "/com/example/uninaswap/images/logo.jpg";
        Image icon = new Image(getClass().getResourceAsStream(iconPath));
        stage.getIcons().add(icon);
        stage.setTitle(Costanti.accedi);
        stage.setScene(scene);
        stage.setMaximized(true);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
