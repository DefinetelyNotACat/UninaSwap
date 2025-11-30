package boundary;

import controller.ControllerUninaSwap;
import entity.Utente;
import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.shape.Circle; // Importante per il ritaglio rotondo
import java.io.File;

public class HomePageBoundary {

    // Recuperiamo l'istanza Singleton
    ControllerUninaSwap controllerUninaSwap = ControllerUninaSwap.getInstance();

    @FXML private AnchorPane navbarComponent;
    @FXML private NavBarComponent navBarComponentController;
    @FXML private ImageView fotoProfilo;

    @FXML
    private void initialize() {

            // Inizializza la navbar se necessario
            if (navBarComponentController != null) {
                navBarComponentController.initialize();
            }

        }
    }
