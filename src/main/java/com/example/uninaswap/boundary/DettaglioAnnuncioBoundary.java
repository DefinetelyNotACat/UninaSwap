package com.example.uninaswap.boundary;

import com.example.uninaswap.Costanti;
import com.example.uninaswap.controller.ControllerUninaSwap;
import com.example.uninaswap.entity.*;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
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
    @FXML private Text txtCondizioni;
    @FXML private Text txtVenditore;
    @FXML private Button btnFaiOfferta;

    private Annuncio annuncioCorrente;

    public void initData(Annuncio annuncio) {
        this.annuncioCorrente = annuncio;
        popolaCampi();
    }

    private void popolaCampi() {
        if (annuncioCorrente == null) return;

        // 1. Descrizione e Sede
        txtTitoloDescrizione.setText(annuncioCorrente.getDescrizione());
        txtSede.setText("📍 " + (annuncioCorrente.getSede() != null ? annuncioCorrente.getSede().getNomeSede() : "N/A"));

        // 2. Immagine (Logica riutilizzata dalla Home)
        caricaImmagine();

        // 3. Info Utente (Simulato, ideale avere username nell'annuncio o fare query)
        txtVenditore.setText("Pubblicato da utente ID: " + annuncioCorrente.getUtenteId());

        // 4. Gestione Polimorfica
        if (annuncioCorrente instanceof AnnuncioVendita av) {
            badgeTipo.setText("VENDITA");
            badgeTipo.getStyleClass().addAll("badge-base", "badge-vendita");
            txtDettagliSpecifici.setText("Prezzo richiesto: " + av.getPrezzoMedio() + "€");
        } else if (annuncioCorrente instanceof AnnuncioScambio as) {
            badgeTipo.setText("SCAMBIO");
            badgeTipo.getStyleClass().addAll("badge-base", "badge-scambio");
            txtDettagliSpecifici.setText("Cerca: " + as.getListaOggetti());
        } else {
            badgeTipo.setText("REGALO");
            badgeTipo.getStyleClass().addAll("badge-base", "badge-regalo");
            txtDettagliSpecifici.setText("Oggetto in regalo!");
        }

        // 5. Nascondi bottone offerta se sono io l'autore
        try {
            if (annuncioCorrente.getUtenteId() == ControllerUninaSwap.getInstance().getUtente().getId()) {
                btnFaiOfferta.setVisible(false);
            }
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void caricaImmagine() {
        try {
            if (annuncioCorrente.getOggetti() != null && !annuncioCorrente.getOggetti().isEmpty() &&
                    !annuncioCorrente.getOggetti().get(0).getImmagini().isEmpty()) {
                String path = annuncioCorrente.getOggetti().get(0).getImmagini().get(0);
                File file = new File(System.getProperty("user.dir") + File.separator + "dati_utenti" + File.separator + path);
                if (file.exists()) {
                    immagineAnnuncio.setImage(new Image(file.toURI().toString()));
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
            // CORREZIONE: Usa Costanti.pathEffettuaOfferta
            FXMLLoader loader = new FXMLLoader(getClass().getResource(Costanti.pathEffettuaOfferta));
            Parent root = loader.load();

            EffettuaOffertaBoundary controller = loader.getController();
            controller.initData(annuncioCorrente);

            Stage stage = (Stage) btnFaiOfferta.getScene().getWindow();
            stage.setScene(new Scene(root, stage.getScene().getWidth(), stage.getScene().getHeight()));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void tornaIndietro() {
        try {
            // CORREZIONE: Usa Costanti.pathHomePage
            FXMLLoader loader = new FXMLLoader(getClass().getResource(Costanti.pathHomePage));
            Parent root = loader.load();
            Stage stage = (Stage) btnFaiOfferta.getScene().getWindow();
            stage.setScene(new Scene(root, stage.getScene().getWidth(), stage.getScene().getHeight()));
        } catch (Exception e) { e.printStackTrace(); }
    }
}