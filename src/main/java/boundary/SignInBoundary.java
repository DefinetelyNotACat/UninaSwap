package boundary;

import com.example.uninaswap.Costanti;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;


import java.io.IOException;

public class SignInBoundary {

    public void onConfermaClick(ActionEvent actionEvent) {
        System.out.println("Login premuto!");
    }
    public void onRegistraClick(ActionEvent actionEvent) {
        System.out.println(actionEvent + "\n" + actionEvent.getSource().toString());
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/uninaswap/signUp.fxml"));
            Parent newRoot = loader.load();
            Scene currentScene = ((Node) actionEvent.getSource()).getScene();
            currentScene.setRoot(newRoot);
            Stage stage = (Stage) currentScene.getWindow();
            stage.setTitle(Costanti.registrati);

        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Errore nel caricamento di signUp.fxml: " + e.getMessage());
        }
    }
}