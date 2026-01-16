package com.example.uninaswap.boundary;

import com.example.uninaswap.controller.ControllerUninaSwap;
import com.example.uninaswap.entity.Offerta;
import com.example.uninaswap.entity.OffertaVendita;
import com.example.uninaswap.entity.OffertaScambio;
import com.example.uninaswap.entity.OffertaRegalo;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import java.util.ArrayList;

public class GestioneOfferteBoundary {

    @FXML private VBox boxRicevute;
    @FXML private VBox boxInviate;
    @FXML private NavBarComponent navBarComponentController;

    private ControllerUninaSwap controller = ControllerUninaSwap.getInstance();

    @FXML
    public void initialize() {
        caricaOfferte();
    }

    private void caricaOfferte() {
        boxRicevute.getChildren().clear();
        boxInviate.getChildren().clear();

        try {
            // 1. Offerte RICEVUTE
            ArrayList<Offerta> ricevute = controller.OttieniOfferteRicevute();
            if (ricevute == null || ricevute.isEmpty()) {
                boxRicevute.getChildren().add(creaLabelVuota("Nessuna offerta ricevuta."));
            } else {
                for (Offerta o : ricevute) {
                    boxRicevute.getChildren().add(creaCardOfferta(o, true));
                }
            }

            // 2. Offerte INVIATE
            ArrayList<Offerta> inviate = controller.OttieniLeMieOfferte();
            if (inviate == null || inviate.isEmpty()) {
                boxInviate.getChildren().add(creaLabelVuota("Non hai inviato nessuna offerta."));
            } else {
                for (Offerta o : inviate) {
                    boxInviate.getChildren().add(creaCardOfferta(o, false));
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            boxRicevute.getChildren().add(new Label("Errore nel caricamento delle offerte."));
        }
    }

    private VBox creaCardOfferta(Offerta o, boolean isRicevuta) {
        VBox card = new VBox(12);
        card.getStyleClass().add("offer-card");

        // Intestazione
        String headerText = isRicevuta
                ? "Da: " + o.getUtente().getUsername()
                : "Annuncio #" + o.getAnnuncio().getId();

        Text titolo = new Text(headerText);
        titolo.getStyleClass().add("offer-card-title");

        Text descAnnuncio = new Text(o.getAnnuncio().getDescrizione());
        descAnnuncio.getStyleClass().add("offer-card-subtitle");

        // --- LOGICA DETERMINAZIONE TIPO CORRETTA ---
        String dettagliExtra = "";
        if (o instanceof OffertaVendita) {
            dettagliExtra = "Offerta: " + ((OffertaVendita) o).getPrezzoOffertaVendita() + " €";
        } else if (o instanceof OffertaScambio) {
            dettagliExtra = "Tipo: Scambio";
        } else if (o instanceof OffertaRegalo) {
            dettagliExtra = "Tipo: Regalo";
        }

        Label details = new Label(dettagliExtra);
        details.getStyleClass().add("offer-details-label");

        // Messaggio
        Text msg = new Text("\"" + o.getMessaggio() + "\"");
        msg.getStyleClass().add("offer-message-text");
        msg.setWrappingWidth(500);

        // FIX STATO: Rimosso Underscore
        String statoTesto = o.getStato().toString().replace("_", " ");
        Label statoLabel = new Label(statoTesto);
        statoLabel.getStyleClass().add("status-badge");

        // Colore dinamico dello stato
        switch (o.getStato()) {
            case ACCETTATA: statoLabel.getStyleClass().add("status-accepted"); break;
            case RIFIUTATA: statoLabel.getStyleClass().add("status-rejected"); break;
            default: statoLabel.getStyleClass().add("status-pending"); break;
        }

        HBox topRow = new HBox();
        topRow.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(titolo, javafx.scene.layout.Priority.ALWAYS);
        topRow.getChildren().addAll(titolo, statoLabel);

        card.getChildren().addAll(topRow, descAnnuncio, details, msg);

        // Pulsanti Azione (Solo se RICEVUTA e IN ATTESA)
        if (isRicevuta && o.getStato() == Offerta.STATO_OFFERTA.IN_ATTESA) {
            HBox azioni = new HBox(15);
            azioni.setAlignment(Pos.CENTER_RIGHT);

            Button btnAccetta = new Button("Accetta");
            btnAccetta.getStyleClass().add("btn-accept");

            Button btnRifiuta = new Button("Rifiuta");
            btnRifiuta.getStyleClass().add("btn-reject");

            btnAccetta.setOnAction(e -> cambiaStato(o, Offerta.STATO_OFFERTA.ACCETTATA));
            btnRifiuta.setOnAction(e -> cambiaStato(o, Offerta.STATO_OFFERTA.RIFIUTATA));

            azioni.getChildren().addAll(btnRifiuta, btnAccetta);
            card.getChildren().add(azioni);
        }

        return card;
    }

    private Label creaLabelVuota(String testo) {
        Label l = new Label(testo);
        l.setStyle("-fx-text-fill: #95a5a6; -fx-font-size: 16px; -fx-padding: 50 0 0 0;");
        return l;
    }

    private void cambiaStato(Offerta o, Offerta.STATO_OFFERTA nuovoStato) {
        if (controller.GestisciStatoOfferta(o, nuovoStato)) {
            caricaOfferte();
        }
    }
}