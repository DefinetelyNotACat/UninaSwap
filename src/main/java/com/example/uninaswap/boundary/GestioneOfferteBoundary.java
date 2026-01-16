package com.example.uninaswap.boundary;

import com.example.uninaswap.controller.ControllerUninaSwap;
import com.example.uninaswap.entity.*;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import java.util.ArrayList;

public class GestioneOfferteBoundary {

    @FXML private VBox containerOfferte; // Singolo container dinamico
    @FXML private Button btnNavRicevute;
    @FXML private Button btnNavInviate;
    @FXML private NavBarComponent navBarComponentController;

    private ControllerUninaSwap controller = ControllerUninaSwap.getInstance();
    private boolean visualizzandoRicevute = true;

    @FXML
    public void initialize() {
        mostraRicevute(); // Caricamento iniziale
    }

    @FXML
    public void mostraRicevute() {
        visualizzandoRicevute = true;
        btnNavRicevute.getStyleClass().add("nav-btn-active");
        btnNavInviate.getStyleClass().remove("nav-btn-active");
        caricaOfferte();
    }

    @FXML
    public void mostraInviate() {
        visualizzandoRicevute = false;
        btnNavInviate.getStyleClass().add("nav-btn-active");
        btnNavRicevute.getStyleClass().remove("nav-btn-active");
        caricaOfferte();
    }

    private void caricaOfferte() {
        containerOfferte.getChildren().clear();
        try {
            ArrayList<Offerta> lista = visualizzandoRicevute
                    ? controller.OttieniOfferteRicevute()
                    : controller.OttieniLeMieOfferte();

            if (lista == null || lista.isEmpty()) {
                String msg = visualizzandoRicevute ? "Nessuna offerta ricevuta." : "Non hai inviato offerte.";
                containerOfferte.getChildren().add(creaLabelVuota(msg));
            } else {
                for (Offerta o : lista) {
                    containerOfferte.getChildren().add(creaCardOfferta(o, visualizzandoRicevute));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            containerOfferte.getChildren().add(new Label("Errore nel caricamento."));
        }
    }

    private VBox creaCardOfferta(Offerta o, boolean isRicevuta) {
        VBox card = new VBox(12);
        card.getStyleClass().add("offer-card");

        // Header
        String headerText = isRicevuta ? "Da: " + o.getUtente().getUsername() : "Annuncio #" + o.getAnnuncio().getId();
        Text titolo = new Text(headerText);
        titolo.getStyleClass().add("offer-card-title");

        // Stato con fix "IN ATTESA"
        Label statoLabel = new Label(o.getStato().toString().replace("_", " "));
        statoLabel.getStyleClass().add("status-badge");
        switch (o.getStato()) {
            case ACCETTATA: statoLabel.getStyleClass().add("status-accepted"); break;
            case RIFIUTATA: statoLabel.getStyleClass().add("status-rejected"); break;
            default: statoLabel.getStyleClass().add("status-pending"); break;
        }

        HBox topRow = new HBox();
        topRow.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(titolo, javafx.scene.layout.Priority.ALWAYS);
        topRow.getChildren().addAll(titolo, statoLabel);

        // Dettagli Proposta
        String dettagliExtra = "";
        if (o instanceof OffertaVendita) dettagliExtra = "Prezzo: " + ((OffertaVendita) o).getPrezzoOffertaVendita() + " €";
        else if (o instanceof OffertaScambio) dettagliExtra = "Tipo: Scambio";
        else dettagliExtra = "Tipo: Regalo";

        Label details = new Label(dettagliExtra);
        details.getStyleClass().add("offer-details-label");

        Text msg = new Text("\"" + o.getMessaggio() + "\"");
        msg.getStyleClass().add("offer-message-text");
        msg.setWrappingWidth(600);

        card.getChildren().addAll(topRow, details, msg);

        // Azioni
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