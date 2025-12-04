package com.example.uninaswap.boundary;

import com.example.uninaswap.controller.ControllerUninaSwap;
import com.example.uninaswap.entity.Utente;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

public class ModificaProfilo implements Initializable {
    @FXML
    private ImageView profileImageView;
    private File immagineSelezionata;
    private Utente profilo;
    private ControllerUninaSwap controllerUninaSwap;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        controllerUninaSwap = ControllerUninaSwap.getInstance();
        try {
            profilo = controllerUninaSwap.getUtente();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        // Logica per il ritaglio circolare (Clip)
        if (profileImageView != null) {
            javafx.scene.shape.Circle clip = new javafx.scene.shape.Circle(
                    profileImageView.getFitWidth() / 2,
                    profileImageView.getFitHeight() / 2,
                    profileImageView.getFitWidth() / 2
            );
            profileImageView.setClip(clip);
        }
    }

    //Todo! evocare il controller per cambiare immagine pfp anche lato  DAO
    @FXML
    public void cambiaImmagineProfilo(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Seleziona Immagine Profilo");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg")
        );

        Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        File selectedFile = fileChooser.showOpenDialog(stage);

        if (selectedFile != null) {
            this.immagineSelezionata = selectedFile;
            Image originalImage = new Image(selectedFile.toURI().toString());

            if (profileImageView != null) {
                // Logica di ritaglio e centratura immagine
                double width = originalImage.getWidth();
                double height = originalImage.getHeight();
                double minDimension = Math.min(width, height);
                double x, y;
                if (width > height) {
                    x = (width - height) / 2;
                    y = 0;
                } else {
                    x = 0;
                    y = 0;
                }
                Rectangle2D cropArea = new Rectangle2D(x, y, minDimension, minDimension);
                profileImageView.setViewport(cropArea);
                profileImageView.setImage(originalImage);
                profileImageView.setSmooth(true);
                profileImageView.setCache(true);
                profileImageView.setPreserveRatio(true);
            }
        }
    }

    // Getter per permettere a SignBoundary di recuperare il file scelto
    public File getImmagineSelezionata() {
        return immagineSelezionata;
    }
}