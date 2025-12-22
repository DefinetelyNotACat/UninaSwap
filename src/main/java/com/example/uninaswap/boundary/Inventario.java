package com.example.uninaswap.boundary;

import com.example.uninaswap.Costanti;
import com.example.uninaswap.controller.ControllerUninaSwap;
import com.example.uninaswap.dao.OggettoDAO;
import com.example.uninaswap.entity.Oggetto;
import com.example.uninaswap.entity.Utente;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class Inventario implements Initializable {

    @FXML private GridPane gridInventario;
    @FXML private Text testoVuoto;

    private ControllerUninaSwap controller = ControllerUninaSwap.getInstance();
    private OggettoDAO oggettoDAO = new OggettoDAO();
    private GestoreScene gestoreScene = new GestoreScene();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        caricaOggetti();
    }

    private void caricaOggetti() {
        gridInventario.getChildren().clear(); // Pulisce la griglia
        Utente utente = controller.getUtente();

        // Recuperiamo gli oggetti dell'utente (implementare questo metodo nel controller/DAO se manca)
        // Esempio: List<Oggetto> oggetti = controller.getOggettiUtente(utente.getId());
        // Qui uso un mock se non hai il metodo pronto, sostituisci con la chiamata reale:
        List<Oggetto> oggetti = oggettoDAO.ottieniTuttiOggetti(utente.getId());

        if (oggetti.isEmpty()) {
            testoVuoto.setVisible(true);
            testoVuoto.setManaged(true);
            return;
        }

        testoVuoto.setVisible(false);
        testoVuoto.setManaged(false);

        int colonna = 0;
        int riga = 1;

        for (Oggetto obj : oggetti) {
            VBox card = creaCardOggetto(obj);
            gridInventario.add(card, colonna, riga);

            colonna++;
            // Layout a 3 colonne, poi va a capo
            if (colonna == 3) {
                colonna = 0;
                riga++;
            }
        }
    }

    private VBox creaCardOggetto(Oggetto obj) {
        VBox card = new VBox();
        card.setAlignment(Pos.CENTER);
        card.setSpacing(10);
        card.getStyleClass().add("inventory-card"); // Assicurati di avere questa classe nel CSS
        card.setPrefWidth(250);
        card.setPrefHeight(300);

        // 1. Immagine
        ImageView imgView = new ImageView();
        imgView.setFitWidth(200);
        imgView.setFitHeight(180);
        imgView.setPreserveRatio(true);

        try {
            String path = (obj.getImmagini() != null && !obj.getImmagini().isEmpty())
                    ? obj.getImmagini().get(0)
                    : "@images/placeholder.png";
            // Gestione path file o risorsa
            if(path.startsWith("@") || path.startsWith("file:")) {
                imgView.setImage(new Image(path));
            } else {
                imgView.setImage(new Image("file:" + path));
            }
        } catch (Exception e) {
            // Immagine di fallback
            imgView.setImage(new Image(getClass().getResourceAsStream("/images/uninaLogo.png")));
        }

        // 2. Nome Oggetto
        Text nome = new Text(obj.getNome());
        nome.getStyleClass().add("label");
        nome.setStyle("-fx-font-weight: bold; -fx-font-size: 16px;");

        // 3. Pulsanti Azione (Modifica / Elimina)
        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(Pos.CENTER);

        Button btnModifica = new Button("Modifica");
        btnModifica.getStyleClass().add("button-small");
        btnModifica.setOnAction(e -> onModificaOggetto(obj));

        Button btnElimina = new Button("Elimina");
        btnElimina.getStyleClass().addAll("button-small", "button-danger"); // button-danger per colore rosso
        btnElimina.setOnAction(e -> onEliminaOggetto(obj));

        buttonBox.getChildren().addAll(btnModifica, btnElimina);

        card.getChildren().addAll(imgView, nome, buttonBox);
        return card;
    }

    private void onModificaOggetto(Oggetto obj) {
        System.out.println("Modifica oggetto: " + obj.getNome());
        // Qui dovresti navigare alla schermata "AggiungiOggetto" ma passandogli l'oggetto da modificare
        // Per ora stampiamo in console.
        // Esempio: gestoreScene.CambiaScenaConDati(..., obj);
    }

    private void onEliminaOggetto(Oggetto obj) {
        boolean eliminato = oggettoDAO.eliminaOggetto(obj.getId());
        if (eliminato) {
            System.out.println("Oggetto eliminato");
            caricaOggetti(); // Ricarica la griglia
        } else {
            System.err.println("Errore eliminazione");
        }
    }

    @FXML
    public void onAggiungiNuovoClick(ActionEvent event) {
        gestoreScene.CambiaScena(Costanti.pathAggiungiOggetto, "Aggiungi Oggetto", event);
    }

    @FXML
    public void onIndietroClick(ActionEvent event) {
        gestoreScene.CambiaScena(Costanti.pathHomePage, Costanti.homepage, event);
    }
}