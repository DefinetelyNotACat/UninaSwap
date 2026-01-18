package com.example.uninaswap.boundary;

import com.example.uninaswap.entity.*;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import java.io.File;

public class ArticoloCard {

    @FXML private ImageView imgAnnuncio;
    @FXML private Label titoloAnnuncio;
    @FXML private Label prezzoAnnuncio;

    public void setAnnuncioData(Annuncio annuncio) {
        titoloAnnuncio.setText(annuncio.getDescrizione());

        prezzoAnnuncio.getStyleClass().removeAll("badge-vendita", "badge-scambio", "badge-regalo");

        if (annuncio instanceof AnnuncioVendita av) {
            prezzoAnnuncio.setText(av.getPrezzoMedio() + " €");
            prezzoAnnuncio.getStyleClass().add("ad-extra-info"); // Usa lo stile blu/verde della home
        } else if (annuncio instanceof AnnuncioScambio) {
            prezzoAnnuncio.setText("🔄 SCAMBIO");
            prezzoAnnuncio.setStyle("-fx-text-fill: #007bff;"); // O usa una classe dedicata
        } else {
            prezzoAnnuncio.setText("🎁 REGALO");
            prezzoAnnuncio.setStyle("-fx-text-fill: #fd7e14;");
        }

        caricaImmagineAnnuncio(annuncio);
    }

    private void caricaImmagineAnnuncio(Annuncio annuncio) {
        try {
            // Verifica se ci sono oggetti e se il primo oggetto ha immagini (grazie alla JOIN)
            if (annuncio.getOggetti() != null && !annuncio.getOggetti().isEmpty()) {
                Oggetto primoOggetto = annuncio.getOggetti().get(0);

                if (primoOggetto.getImmagini() != null && !primoOggetto.getImmagini().isEmpty()) {
                    String pathRelativo = primoOggetto.getImmagini().get(0);

                    // Costruisce il percorso assoluto come fatto nelle altre boundary
                    File file = new File(System.getProperty("user.dir") + File.separator + "dati_utenti" + File.separator + pathRelativo);

                    if (file.exists()) {
                        imgAnnuncio.setImage(new Image(file.toURI().toString()));
                        return;
                    }
                }
            }
            // Immagine di fallback se non ci sono foto o il file non esiste
            imgAnnuncio.setImage(new Image(getClass().getResourceAsStream("/com/example/uninaswap/images/uninaLogo.png")));
        } catch (Exception exception) {
            System.err.println("Errore caricamento immagine card: " + exception.getMessage());
        }
    }
}