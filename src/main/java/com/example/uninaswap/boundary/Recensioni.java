package com.example.uninaswap.boundary;

import com.example.uninaswap.Costanti;
import com.example.uninaswap.controller.ControllerUninaSwap;
import com.example.uninaswap.entity.Recensione;
import com.example.uninaswap.entity.Utente;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.util.List;

public class Recensioni {

    @FXML private VBox containerRecensioni;
    @FXML private Text txtTitolo;
    @FXML private Text txtMedia;

    private Utente utenteTarget; // Memorizziamo l'utente per passarlo alla prossima scena

    public void initData(Utente utente) {
        ControllerUninaSwap controller = ControllerUninaSwap.getInstance();
        if (utente == null) return;
        this.utenteTarget = utente;

        txtTitolo.setText("Recensioni di " + utente.getUsername());

        // FIX: Non usare utente.getRecensioniRicevute() perché è una lista vuota in memoria!
        // Chiedi al controller di recuperarle dal DB
        List<Recensione> recensioni = controller.OttieniRecensioniRicevuteUtente(utente);

        if (recensioni == null || recensioni.isEmpty()) {
            mostraMessaggioVuoto();
        } else {
            calcolaEMostraMedia(recensioni);
            containerRecensioni.getChildren().clear(); // Pulisci prima di aggiungere
            for (Recensione r : recensioni) {
                containerRecensioni.getChildren().add(creaCardRecensione(r));
            }
        }
    }
    @FXML
    private void apriAggiungiRecensione() {
        ControllerUninaSwap controller = ControllerUninaSwap.getInstance();
        try {
            // Carichiamo il loader per accedere al controller
            FXMLLoader loader = new FXMLLoader(getClass().getResource(Costanti.pathAggiungiRecensione));
            Parent root = loader.load();

            // Passiamo l'utente target al controller di "AggiungiRecensione"
            // Assicurati che il controller di aggiungiRecensione abbia un metodo initData(Utente u)
            Object controllerAggiungi = loader.getController();
            if (controllerAggiungi instanceof AggiungiRecensione arb) {
                arb.initData(utenteTarget);
            }

            // Manteniamo le dimensioni correnti della finestra
            Stage stage = (Stage) containerRecensioni.getScene().getWindow();
            double width = stage.getScene().getWidth();
            double height = stage.getScene().getHeight();

            stage.setScene(new Scene(root, width, height));

        } catch (Exception e) {
            System.err.println("Errore nel caricamento di aggiungiRecensione: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private VBox creaCardRecensione(Recensione r) {
        ControllerUninaSwap controller = ControllerUninaSwap.getInstance();
        VBox card = new VBox(10);
        card.getStyleClass().add("ad-card");
        card.setPadding(new Insets(15));
        card.setMaxWidth(600);

        HBox header = new HBox(10);
        header.setAlignment(Pos.CENTER_LEFT);

        // --- RECUPERO USERNAME TRAMITE CONTROLLER ---
        String nomeDaVisualizzare = "Utente Anonimo";
        Utente recensore = controller.ottieniUtenteDaEmail(r.getEmailRecensore());

        if (recensore != null) {
            nomeDaVisualizzare = recensore.getUsername();
        }

        Label autore = new Label("Da: " + nomeDaVisualizzare);
        autore.setStyle("-fx-font-weight: bold; -fx-text-fill: #003366;");
        // --------------------------------------------

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label badgeVoto = new Label("⭐ " + r.getVoto() + "/5");
        badgeVoto.getStyleClass().addAll("badge-base", "badge-vendita");

        header.getChildren().addAll(autore, spacer, badgeVoto);

        Text commento = new Text(r.getCommento() != null && !r.getCommento().isEmpty()
                ? r.getCommento()
                : "Nessun commento lasciato.");
        commento.setWrappingWidth(550);
        commento.setStyle("-fx-font-style: italic; -fx-fill: #555;");

        card.getChildren().addAll(header, commento);
        return card;
    }
    private void calcolaEMostraMedia(List<Recensione> list) {
        double somma = 0;
        for (Recensione r : list) {
            somma += r.getVoto();
        }
        double media = somma / list.size();
        txtMedia.setText(String.format("Valutazione Media: %.1f/5 (%d recensioni)", media, list.size()));
    }

    private void mostraMessaggioVuoto() {
        txtMedia.setText("Nessuna valutazione ricevuta");
        Text t = new Text("Questo utente non è ancora stato recensito.");
        t.setStyle("-fx-fill: #aaa; -fx-font-size: 16px;");
        containerRecensioni.getChildren().add(t);
    }

    @FXML
    private void tornaHome() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource(Costanti.pathHomePage));
            Stage stage = (Stage) containerRecensioni.getScene().getWindow();
            double currentWidth = stage.getScene().getWidth();
            double currentHeight = stage.getScene().getHeight();
            stage.setScene(new Scene(root, currentWidth, currentHeight));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}