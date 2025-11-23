package boundary;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;


public class HomePageBoundary {
    @FXML private AnchorPane navbarComponent;
    @FXML private NavBarComponent navBarComponentController;

    @FXML private void initialize() {

        try {
            if (navBarComponentController != null) {
                navBarComponentController.initialize();
            }
        }catch (Exception e){
            System.err.println("Caricamento NavBar non avvento con successo: " + e.getMessage());
        }

    }

}
