
package com.example.uninaswap.boundary;

import javafx.animation.PauseTransition;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.util.Duration;

public class Messaggio {
    @FXML
    private HBox bannerContainer;

    @FXML
    private Label bannerLabel;

    @FXML
    private Label bannerIcon;

    private PauseTransition delay;

    public void mostraMessaggio(String messaggio, String tipo) {
        bannerLabel.setText(messaggio);
        //resetto tutti gli stili
        bannerContainer.getStyleClass().removeAll("banner-success", "banner-error", "banner-info");
        // 3. Aggiunge la classe giusta in base al tipo
        switch (tipo) {
            case "SUCCESS":
                bannerContainer.getStyleClass().add("banner-success");
                bannerIcon.setText("✔");
                break;
            case "ERROR":
                bannerContainer.getStyleClass().add("banner-error");
                bannerIcon.setText("✘");
                break;
            case "INFO":
                bannerContainer.getStyleClass().add("banner-info");
                bannerIcon.setText("ⓘ");
                break;
        }

        // 4. Rende visibile il banner
        bannerContainer.setVisible(true);
        bannerContainer.setManaged(true);
        if (delay != null) {
            delay.stop();
        }
        delay = new PauseTransition(Duration.seconds(5));
        delay.setOnFinished(event -> chiudiBanner());
        delay.play();
    }
    @FXML
    public void chiudiBanner() {
        bannerContainer.setVisible(false);
        bannerContainer.setManaged(false);
    }
}
