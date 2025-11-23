package boundary;

import com.example.uninaswap.Costanti;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class ControllerCambioBoundary {
    public void CambiaScena(String pathFxml, String TitoloScene, ActionEvent actionEvent){
        try{
            FXMLLoader loader = new FXMLLoader(getClass().getResource(pathFxml));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
            double larghezza = stage.getScene().getWidth();
            double lunghezza = stage.getScene().getHeight();
            stage.setTitle(TitoloScene);
            stage.setMaximized(true);
            Scene scene = new Scene(root, larghezza, lunghezza);
            stage.setScene(scene);
            stage.show();
        }
        catch (Exception e){
            System.out.println("Errore " + e.getMessage());
        }
    }
}
