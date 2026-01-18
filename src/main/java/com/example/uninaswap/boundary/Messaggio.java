
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

    private String messaggio;

    private TIPI tipo;

    public enum TIPI{
        SUCCESS,
        ERROR,
        INFO
    };

    public void mostraMessaggio(String messaggio, TIPI tipo) {
        this.messaggio = messaggio;
        this.tipo = tipo;
        bannerLabel.setText(messaggio);
        bannerContainer.getStyleClass().removeAll("banner-success", "banner-error", "banner-info");
        switch (tipo) {
            case SUCCESS:
                bannerContainer.getStyleClass().add("banner-success");
                bannerIcon.setText("✔");
                break;
            case ERROR:
                bannerContainer.getStyleClass().add("banner-error");
                bannerIcon.setText("✘");
                break;
            case INFO:
                bannerContainer.getStyleClass().add("banner-info");
                bannerIcon.setText("ⓘ");
                break;
        }
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

    public TIPI getTipo() {
        return tipo;
    }
    public String getMessaggio() {
        return messaggio;
    }

}
