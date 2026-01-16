package com.example.uninaswap.boundary;

import com.example.uninaswap.controller.ControllerUninaSwap;
import com.example.uninaswap.dao.OffertaDAO;
import com.example.uninaswap.entity.Offerta;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
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
    private OffertaDAO offertaDAO = new OffertaDAO(); // Idealmente passare per controller

    @FXML
    public void initialize() {
        if (navBarComponentController != null) {
            // navBarComponentController.setHomePageBoundary(this); // Adatta l'interfaccia se necessario
        }
        caricaOfferte();
    }

    private void caricaOfferte() {
        boxRicevute.getChildren().clear();
        boxInviate.getChildren().clear();

        // NOTA: Qui dovresti implementare nel Controller/DAO i metodi:
        // getOfferteRicevute(idUtente) -> JOIN Offerta e Annuncio where annuncio.utente_id = me
        // getOfferteInviate(idUtente) -> SELECT * FROM Offerta where utente_id = me

        // ESEMPIO MOCKUP LOGICA VISIVA (da collegare ai dati veri):
        /*
        ArrayList<Offerta> ricevute = controller.OttieniOfferteRicevute();
        for(Offerta o : ricevute) {
            boxRicevute.getChildren().add(creaCardOfferta(o, true));
        }
        */

        // Messaggio placeholder se non implementato ancora il DAO backend
        Label placeholder = new Label("Funzionalità in fase di collegamento con il Backend.\nNecessari metodi nel Controller per filtrare INVIATE vs RICEVUTE.");
        boxRicevute.getChildren().add(placeholder);
    }

    private VBox creaCardOfferta(Offerta o, boolean isRicevuta) {
        VBox card = new VBox(10);
        card.setStyle("-fx-background-color: white; -fx-padding: 15; -fx-background-radius: 8; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 5, 0, 0, 0);");

        Text titolo = new Text(isRicevuta ? "Offerta da Utente " + o.getUtente().getUsername() : "Offerta per Annuncio ID " + o.getAnnuncio().getId());
        titolo.setStyle("-fx-font-weight: bold; -fx-font-size: 16px;");

        Label stato = new Label(o.getStato().toString());
        if(o.getStato() == Offerta.STATO_OFFERTA.ACCETTATA) stato.setStyle("-fx-text-fill: green;");
        else if(o.getStato() == Offerta.STATO_OFFERTA.RIFIUTATA) stato.setStyle("-fx-text-fill: red;");
        else stato.setStyle("-fx-text-fill: orange;");

        Text msg = new Text("Messaggio: " + o.getMessaggio());

        HBox azioni = new HBox(10);
        azioni.setAlignment(Pos.CENTER_RIGHT);

        if (isRicevuta && o.getStato() == Offerta.STATO_OFFERTA.IN_ATTESA) {
            Button accetta = new Button("Accetta");
            accetta.setStyle("-fx-background-color: #28a745; -fx-text-fill: white;");
            accetta.setOnAction(e -> gestisciStato(o, Offerta.STATO_OFFERTA.ACCETTATA));

            Button rifiuta = new Button("Rifiuta");
            rifiuta.setStyle("-fx-background-color: #dc3545; -fx-text-fill: white;");
            rifiuta.setOnAction(e -> gestisciStato(o, Offerta.STATO_OFFERTA.RIFIUTATA));

            azioni.getChildren().addAll(accetta, rifiuta);
        }

        card.getChildren().addAll(titolo, stato, msg, azioni);
        return card;
    }

    private void gestisciStato(Offerta o, Offerta.STATO_OFFERTA nuovoStato) {
        // Chiamata al DAO per update
        if(offertaDAO.modificaStatoOfferta(o.getId(), nuovoStato)){
            caricaOfferte(); // Ricarica UI
        }
    }
}