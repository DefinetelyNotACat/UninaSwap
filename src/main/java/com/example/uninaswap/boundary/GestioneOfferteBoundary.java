package com.example.uninaswap.boundary;

import com.example.uninaswap.controller.ControllerUninaSwap;
import com.example.uninaswap.entity.Offerta;
import com.example.uninaswap.entity.OffertaVendita;
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
            // 1. Carica Offerte RICEVUTE (da altri utenti sui miei annunci)
            ArrayList<Offerta> ricevute = controller.OttieniOfferteRicevute();
            if (ricevute.isEmpty()) {
                boxRicevute.getChildren().add(new Label("Nessuna offerta ricevuta."));
            } else {
                for (Offerta o : ricevute) {
                    boxRicevute.getChildren().add(creaCardOfferta(o, true));
                }
            }

            // 2. Carica Offerte INVIATE (fatte da me su annunci di altri)
            ArrayList<Offerta> inviate = controller.OttieniLeMieOfferte();
            if (inviate.isEmpty()) {
                boxInviate.getChildren().add(new Label("Non hai inviato nessuna offerta."));
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
        VBox card = new VBox(8);
        card.setStyle("-fx-background-color: white; -fx-padding: 15; -fx-background-radius: 8; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 5, 0, 0, 0); -fx-border-color: #e0e0e0; -fx-border-radius: 8;");

        // Intestazione
        String headerText;
        if (isRicevuta) {
            headerText = "Offerta da: " + o.getUtente().getUsername() + " | Annuncio: " + o.getAnnuncio().getDescrizione();
        } else {
            headerText = "Inviata a: " + o.getAnnuncio().getId() + " (Annuncio) | Status";
        }
        Text titolo = new Text(headerText);
        titolo.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");

        // Dettagli specifici per tipo (Mostra Prezzo se vendita)
        String dettagliExtra = "";
        if (o instanceof OffertaVendita) {
            dettagliExtra = "Prezzo offerto: €" + ((OffertaVendita) o).getPrezzoOffertaVendita();
        } else {
            dettagliExtra = "Tipo: " + o.getClass().getSimpleName().replace("Offerta", "");
        }
        Label details = new Label(dettagliExtra);

        // Messaggio
        Text msg = new Text("Messaggio: \"" + o.getMessaggio() + "\"");
        msg.setStyle("-fx-font-style: italic;");

        // Stato Colorato
        Label statoLabel = new Label("Stato: " + o.getStato().toString());
        statoLabel.setStyle("-fx-font-weight: bold;");

        switch (o.getStato()) {
            case ACCETTATA:
                statoLabel.setStyle(statoLabel.getStyle() + " -fx-text-fill: green;");
                break;
            case RIFIUTATA:
                statoLabel.setStyle(statoLabel.getStyle() + " -fx-text-fill: red;");
                break;
            default:
                statoLabel.setStyle(statoLabel.getStyle() + " -fx-text-fill: orange;");
                break;
        }

        card.getChildren().addAll(titolo, details, msg, statoLabel);

        // Pulsanti (Solo se RICEVUTA e IN_ATTESA)
        if (isRicevuta && o.getStato() == Offerta.STATO_OFFERTA.IN_ATTESA) {
            HBox azioni = new HBox(15);
            azioni.setAlignment(Pos.CENTER_RIGHT);

            Button btnAccetta = new Button("Accetta");
            btnAccetta.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-cursor: hand;");

            Button btnRifiuta = new Button("Rifiuta");
            btnRifiuta.setStyle("-fx-background-color: #F44336; -fx-text-fill: white; -fx-cursor: hand;");

            btnAccetta.setOnAction(e -> cambiaStato(o, Offerta.STATO_OFFERTA.ACCETTATA));
            btnRifiuta.setOnAction(e -> cambiaStato(o, Offerta.STATO_OFFERTA.RIFIUTATA));

            azioni.getChildren().addAll(btnAccetta, btnRifiuta);
            card.getChildren().add(azioni);
        }

        return card;
    }

    private void cambiaStato(Offerta o, Offerta.STATO_OFFERTA nuovoStato) {
        boolean successo = controller.GestisciStatoOfferta(o, nuovoStato);
        if (successo) {
            System.out.println("Stato offerta aggiornato: " + nuovoStato);
            caricaOfferte(); // Ricarica la UI per mostrare il nuovo stato e rimuovere i pulsanti
        } else {
            System.out.println("Errore nell'aggiornamento stato");
        }
    }
}