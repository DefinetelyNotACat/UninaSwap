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
    @FXML private Button btnLasciaRecensione;
    @FXML private Button btnMostraRicevute;
    @FXML private Button btnMostraDate;

    private Utente utenteTarget;
    private boolean visualizzandoRicevute = true;

    public void initData(Utente utente) {
        ControllerUninaSwap controller = ControllerUninaSwap.getInstance();
        if (utente == null) return;
        this.utenteTarget = utente;

        try {
            Utente utenteLoggato = controller.getUtente();
            if (utenteLoggato != null && utenteLoggato.getId() == utente.getId()) {
                if (btnLasciaRecensione != null) {
                    btnLasciaRecensione.setVisible(false);
                    btnLasciaRecensione.setManaged(false);
                }
                txtTitolo.setText("Le tue Recensioni");
            } else {
                if (btnLasciaRecensione != null) {
                    btnLasciaRecensione.setVisible(true);
                    btnLasciaRecensione.setManaged(true);
                }
                txtTitolo.setText("Recensioni di " + utente.getUsername());
            }
        } catch (Exception e) { e.printStackTrace(); }

        caricaRicevute(); // Default all'apertura
    }

    @FXML
    private void cliccaRicevute() {
        visualizzandoRicevute = true;
        btnMostraRicevute.getStyleClass().removeAll("button-outline");
        btnMostraRicevute.getStyleClass().add("button-primary"); // Evidenzia attivo
        btnMostraDate.getStyleClass().removeAll("button-primary");
        btnMostraDate.getStyleClass().add("button-outline");
        caricaRicevute();
    }

    @FXML
    private void cliccaDate() {
        visualizzandoRicevute = false;
        btnMostraDate.getStyleClass().removeAll("button-outline");
        btnMostraDate.getStyleClass().add("button-primary"); // Evidenzia attivo
        btnMostraRicevute.getStyleClass().removeAll("button-primary");
        btnMostraRicevute.getStyleClass().add("button-outline");
        caricaDate();
    }

    private void caricaRicevute() {
        ControllerUninaSwap controller = ControllerUninaSwap.getInstance();
        List<Recensione> recensioni = controller.OttieniRecensioniRicevuteUtente(utenteTarget);
        aggiornaLista(recensioni, "ricevute");
    }

    private void caricaDate() {
        ControllerUninaSwap controller = ControllerUninaSwap.getInstance();
        // Assicurati che questo metodo esista nel tuo Controller
        List<Recensione> recensioni = controller.OttieniRecensioniFatteUtente(utenteTarget);
        aggiornaLista(recensioni, "fatte");
    }

    private void aggiornaLista(List<Recensione> recensioni, String tipo) {
        containerRecensioni.getChildren().clear();
        if (recensioni == null || recensioni.isEmpty()) {
            mostraMessaggioVuoto(tipo);
        } else {
            calcolaEMostraMedia(recensioni);
            for (Recensione r : recensioni) {
                containerRecensioni.getChildren().add(creaCardRecensione(r));
            }
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

        String etichetta;
        String emailDaCercare;

        if (visualizzandoRicevute) {
            etichetta = "Da: ";
            emailDaCercare = r.getEmailRecensore();
        } else {
            etichetta = "A: ";
            emailDaCercare = r.getEmailRecensito();
        }

        Utente u = controller.ottieniUtenteDaEmail(emailDaCercare);
        String nome = (u != null) ? u.getUsername() : "Utente Anonimo";

        Label autore = new Label(etichetta + nome);
        autore.setStyle("-fx-font-weight: bold; -fx-text-fill: #003366;");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label badgeVoto = new Label("⭐ " + r.getVoto() + "/5");
        badgeVoto.getStyleClass().addAll("badge-base", "badge-vendita");

        header.getChildren().addAll(autore, spacer, badgeVoto);

        Text commento = new Text(r.getCommento() != null && !r.getCommento().isEmpty() ? r.getCommento() : "Nessun commento lasciato.");
        commento.setWrappingWidth(550);
        commento.setStyle("-fx-font-style: italic; -fx-fill: #555;");

        card.getChildren().addAll(header, commento);
        return card;
    }

    private void calcolaEMostraMedia(List<Recensione> list) {
        double somma = 0;
        for (Recensione r : list) somma += r.getVoto();
        double media = somma / list.size();
        txtMedia.setText(String.format("Valutazione Media: %.1f/5 (%d recensioni)", media, list.size()));
    }

    private void mostraMessaggioVuoto(String tipo) {
        txtMedia.setText("Nessuna valutazione");
        String msg = tipo.equals("ricevute") ? "Nessuna recensione ricevuta." : "Non hai ancora scritto recensioni.";
        Text t = new Text(msg);
        t.setStyle("-fx-fill: #aaa; -fx-font-size: 16px;");
        containerRecensioni.getChildren().add(t);
    }

    @FXML
    private void apriAggiungiRecensione() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(Costanti.pathAggiungiRecensione));
            Parent root = loader.load();
            AggiungiRecensione arb = loader.getController();
            arb.initData(utenteTarget);
            Stage stage = (Stage) containerRecensioni.getScene().getWindow();
            stage.setScene(new Scene(root, stage.getScene().getWidth(), stage.getScene().getHeight()));
        } catch (Exception e) { e.printStackTrace(); }
    }

    @FXML
    private void tornaHome() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource(Costanti.pathHomePage));
            Stage stage = (Stage) containerRecensioni.getScene().getWindow();
            stage.setScene(new Scene(root, stage.getScene().getWidth(), stage.getScene().getHeight()));
        } catch (Exception e) { e.printStackTrace(); }
    }
}