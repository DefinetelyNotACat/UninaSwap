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
import javafx.scene.control.Button;
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

    // AGGIUNTO: fx:id deve corrispondere nel file recensioni.fxml
    @FXML private Button btnLasciaRecensione;

    private Utente utenteTarget; // Memorizziamo l'utente per passarlo alla prossima scena

    /**
     * Inizializza la pagina caricando le recensioni dell'utente passato come parametro.
     * Se l'utente è se stesso, il tasto per recensire viene rimosso.
     */
    public void initData(Utente utente) {
        ControllerUninaSwap controller = ControllerUninaSwap.getInstance();
        if (utente == null) return;
        this.utenteTarget = utente;

        // --- LOGICA DI CONTROLLO IDENTITÀ (PER CANCELLARE IL TASTO) ---
        try {
            Utente utenteLoggato = controller.getUtente();

            if (utenteLoggato != null && utenteLoggato.getId() == utente.getId()) {
                // Se sono io che guardo me stesso, il tasto deve sparire fottutamente!
                if (btnLasciaRecensione != null) {
                    btnLasciaRecensione.setVisible(false);
                    btnLasciaRecensione.setManaged(false);
                }
                txtTitolo.setText("Le tue Recensioni");
            } else {
                // Altrimenti mostriamo il tasto e il titolo standard
                if (btnLasciaRecensione != null) {
                    btnLasciaRecensione.setVisible(true);
                    btnLasciaRecensione.setManaged(true);
                }
                txtTitolo.setText("Recensioni di " + utente.getUsername());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        // -------------------------------------------------------------

        // Chiedi al controller di recuperarle dal DB (Fix per liste vuote in memoria)
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
            FXMLLoader loader = new FXMLLoader(getClass().getResource(Costanti.pathAggiungiRecensione));
            Parent root = loader.load();

            // Passiamo l'utente target al controller di "AggiungiRecensione"
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

    /**
     * Crea dinamicamente la card grafica per ogni recensione.
     */
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

    /**
     * Calcola la media matematica dei voti ricevuti.
     */
    private void calcolaEMostraMedia(List<Recensione> list) {
        if (list == null || list.isEmpty()) return;
        double somma = 0;
        for (Recensione r : list) {
            somma += r.getVoto();
        }
        double media = somma / list.size();
        txtMedia.setText(String.format("Valutazione Media: %.1f/5 (%d recensioni)", media, list.size()));
    }

    /**
     * Mostra un placeholder se l'utente non ha recensioni.
     */
    private void mostraMessaggioVuoto() {
        txtMedia.setText("Nessuna valutazione ricevuta");
        containerRecensioni.getChildren().clear();
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