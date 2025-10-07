package com.example.uninaswap;

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

public class NavBarController {

    @FXML private ChoiceBox<String> filtroBarraDiRicerca;
    @FXML private TextField barraDiRicerca;
    @FXML private ImageView profilePicture;
    @FXML private Button bottonePubblicaAnnuncio;
    @FXML private ImageView logo;

    private ContextMenu menuProfilo;
    private PauseTransition hideDelay;

    @FXML
    public void initialize() {
        filtroBarraDiRicerca.getItems().addAll("Articoli", "Utenti");
        filtroBarraDiRicerca.setValue("Articoli");

        try {
            Image logoImage = new Image(getClass().getResourceAsStream("/com/example/uninaswap/images/logo.png"));
            logo.setImage(logoImage);

            Image profilePictureImage = new Image(getClass().getResourceAsStream("/com/example/uninaswap/images/profile_picture.jpg"));
            profilePicture.setImage(profilePictureImage);
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

        profilePicture.setOnMouseEntered(event -> {
            if (hideDelay.getStatus() == javafx.animation.Animation.Status.RUNNING) {
                hideDelay.stop();
            }
            showmenuProfilo(event);
        });

        profilePicture.setOnMouseExited(event -> hideDelay.playFromStart());

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

        Point2D point = profilePicture.localToScreen(0, profilePicture.getBoundsInLocal().getHeight());
        if (point != null) {
            menuProfilo.show(profilePicture, point.getX(), point.getY());
        } else {
            menuProfilo.show(profilePicture, event.getScreenX(), event.getScreenY());
        }
    }
}
