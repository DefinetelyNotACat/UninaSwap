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

    public void initData(Utente utente) {
        if (utente == null) return;

        txtTitolo.setText("Recensioni di " + utente.getUsername());
        List<Recensione> recensioni = utente.getRecensioniRicevute();

        if (recensioni == null || recensioni.isEmpty()) {
            mostraMessaggioVuoto();
        } else {
            calcolaEMostraMedia(recensioni);
            for (Recensione r : recensioni) {
                containerRecensioni.getChildren().add(creaCardRecensione(r));
            }
        }
    }

    private VBox creaCardRecensione(Recensione r) {
        VBox card = new VBox(10);
        card.getStyleClass().add("ad-card");
        card.setPadding(new Insets(15));
        card.setMaxWidth(600);

        HBox header = new HBox(10);
        header.setAlignment(Pos.CENTER_LEFT);

        // Ora il metodo getEmailRecensore() viene riconosciuto
        Label autore = new Label("Da: " + r.getEmailRecensore());
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

    private void calcolaEMostraMedia(List<Recensione> list) {
        // Calcolo della media matematica
        // $$media = \frac{\sum_{i=1}^{n} voto_i}{n}$$
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

            // Prendiamo lo stage e le dimensioni attuali prima di cambiare
            Stage stage = (Stage) txtTitolo.getScene().getWindow();
            double currentWidth = stage.getScene().getWidth();
            double currentHeight = stage.getScene().getHeight();

            // Settiamo la nuova scena mantenendo le misure
            stage.setScene(new Scene(root, currentWidth, currentHeight));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }}