package boundary;

import javafx.animation.PauseTransition;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.util.Duration;
import javafx.geometry.Point2D;

public class NavBarComponent {

    @FXML private ChoiceBox<String> filtroBarraDiRicerca;
    @FXML private TextField barraDiRicerca;
    @FXML private ImageView fotoProfilo;
    @FXML private Button bottonePubblicaAnnuncio;
    @FXML private ImageView logo;

    private ContextMenu menuProfilo;
    private PauseTransition hideDelay;

    @FXML
    public void initialize() {
        filtroBarraDiRicerca.getItems().addAll("Articoli", "Utenti");
        filtroBarraDiRicerca.setValue("Articoli");

        try {
            Image logoImage = new Image(getClass().getResourceAsStream("/com/example/uninaswap/images/uninaLogo.png"));
            logo.setImage(logoImage);

            Image fotoProfiloImage = new Image(getClass().getResourceAsStream("/com/example/uninaswap/images/profile_picture.jpg"));
            fotoProfilo.setImage(fotoProfiloImage);
        } catch (Exception e) {
            System.err.println("Nessun immagine trovata: " + e.getMessage());
        }

        menuProfilo = new ContextMenu();
        MenuItem leMieOfferte = new MenuItem("Mostra le mie offerte");
        MenuItem iMieiAnnunci = new MenuItem("Mostra i miei annunci");
        MenuItem ilMioInventario = new MenuItem("Mostra il mio inventario");
        MenuItem logout = new MenuItem("Logout");
        menuProfilo.getItems().addAll(leMieOfferte, iMieiAnnunci, ilMioInventario, new SeparatorMenuItem(), logout);

        hideDelay = new PauseTransition(Duration.millis(200));
        hideDelay.setOnFinished(e -> {
            if (menuProfilo.isShowing()) menuProfilo.hide();
        });

        fotoProfilo.setOnMouseEntered(event -> {
            if (hideDelay.getStatus() == javafx.animation.Animation.Status.RUNNING) {
                hideDelay.stop();
            }
            showmenuProfilo(event);
        });

        fotoProfilo.setOnMouseExited(event -> hideDelay.playFromStart());

        menuProfilo.setOnShown(e -> {
            Scene menuScene = menuProfilo.getScene();
            if (menuScene != null) {
                Node root = menuScene.getRoot();
                root.setOnMouseEntered(ev -> {
                    if (hideDelay.getStatus() == javafx.animation.Animation.Status.RUNNING) {
                        hideDelay.stop();
                    }
                });
                root.setOnMouseExited(ev -> hideDelay.playFromStart());
            }
        });

    }


    private void showmenuProfilo(MouseEvent event) {
        if (menuProfilo.isShowing()) return;

        Point2D point = fotoProfilo.localToScreen(0, fotoProfilo.getBoundsInLocal().getHeight());
        if (point != null) {
            menuProfilo.show(fotoProfilo, point.getX(), point.getY());
        } else {
            menuProfilo.show(fotoProfilo, event.getScreenX(), event.getScreenY());
        }
    }
}
