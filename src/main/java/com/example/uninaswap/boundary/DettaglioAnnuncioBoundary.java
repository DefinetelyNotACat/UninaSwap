package com.example.uninaswap.boundary;

import com.example.uninaswap.Costanti;
import com.example.uninaswap.controller.ControllerUninaSwap;
import com.example.uninaswap.entity.*;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Cursor;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import java.io.File;

public class DettaglioAnnuncioBoundary {

    @FXML private ImageView immagineAnnuncio;
    @FXML private Label badgeTipo;
    @FXML private Text txtSede;
    @FXML private Text txtTitoloDescrizione;
    @FXML private Text txtDettagliSpecifici;
    @FXML private Text txtVenditore;
    @FXML private Button btnFaiOfferta;

    // Inject del controller della navbar inclusa
    @FXML private NavBarComponent navBarComponentController;

    private Annuncio annuncioCorrente;

    @FXML
    public void initialize() {
        // Garantiamo i cursori a mano
        if (btnFaiOfferta != null) btnFaiOfferta.setCursor(Cursor.HAND);
    }

    public void initData(Annuncio annuncio) {
        this.annuncioCorrente = annuncio;
        popolaCampi();
    }

    private void popolaCampi() {
        if (annuncioCorrente == null) return;

        txtTitoloDescrizione.setText(annuncioCorrente.getDescrizione());
        txtSede.setText("📍 " + (annuncioCorrente.getSede() != null ? annuncioCorrente.getSede().getNomeSede() : "N/A"));

        caricaImmagineAltaQualita();

        // Recupero info venditore (Nome utente se disponibile, altrimenti ID)
        txtVenditore.setText("Utente #" + annuncioCorrente.getUtenteId());

        // Reset classi CSS badge per evitare sovrapposizioni
        badgeTipo.getStyleClass().removeAll("badge-vendita", "badge-scambio", "badge-regalo");

        if (annuncioCorrente instanceof AnnuncioVendita av) {
            badgeTipo.setText("VENDITA");
            badgeTipo.getStyleClass().add("badge-vendita");
            txtDettagliSpecifici.setText(av.getPrezzoMedio() + " €");
        } else if (annuncioCorrente instanceof AnnuncioScambio as) {
            badgeTipo.setText("SCAMBIO");
            badgeTipo.getStyleClass().add("badge-scambio");
            txtDettagliSpecifici.setText("Cerco: " + as.getListaOggetti());
        } else {
            badgeTipo.setText("REGALO");
            badgeTipo.getStyleClass().add("badge-regalo");
            txtDettagliSpecifici.setText("Gratuito");
        }

        // Nascondi pulsante se l'utente è il proprietario
        try {
            Utente loggato = ControllerUninaSwap.getInstance().getUtente();
            if (loggato != null && annuncioCorrente.getUtenteId() == loggato.getId()) {
                btnFaiOfferta.setVisible(false);
                btnFaiOfferta.setManaged(false);
            }
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void caricaImmagineAltaQualita() {
        try {
            if (annuncioCorrente.getOggetti() != null && !annuncioCorrente.getOggetti().isEmpty() &&
                    !annuncioCorrente.getOggetti().get(0).getImmagini().isEmpty()) {

                String path = annuncioCorrente.getOggetti().get(0).getImmagini().get(0);
                File file = new File(System.getProperty("user.dir") + File.separator + "dati_utenti" + File.separator + path);

                if (file.exists()) {
                    // Caricamento con smoothing e risoluzione corretta
                    immagineAnnuncio.setImage(new Image(file.toURI().toString(), 800, 800, true, true, true));
                } else {
                    setDefaultImage();
                }
            } else {
                setDefaultImage();
            }
        } catch (Exception e) {
            setDefaultImage();
        }
    }

    private void setDefaultImage() {
        immagineAnnuncio.setImage(new Image(getClass().getResourceAsStream("/com/example/uninaswap/images/uninaLogo.png")));
    }

    @FXML
    public void apriSchermataOfferta() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(Costanti.pathEffettuaOfferta));
            Parent root = loader.load();
            EffettuaOffertaBoundary controller = loader.getController();
            controller.initData(annuncioCorrente);

            Stage stage = (Stage) btnFaiOfferta.getScene().getWindow();
            stage.setScene(new Scene(root, stage.getScene().getWidth(), stage.getScene().getHeight()));
        } catch (Exception e) { e.printStackTrace(); }
    }

    @FXML
    public void tornaIndietro() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(Costanti.pathHomePage));
            Parent root = loader.load();
            Stage stage = (Stage) btnFaiOfferta.getScene().getWindow();
            stage.setScene(new Scene(root, stage.getScene().getWidth(), stage.getScene().getHeight()));
        } catch (Exception e) { e.printStackTrace(); }
    }
}