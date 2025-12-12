package com.example.uninaswap;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.image.Image;
public class Main extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        System.out.println("Hello World!");
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("signIn.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        String iconPath = Costanti.pathLogo;
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
