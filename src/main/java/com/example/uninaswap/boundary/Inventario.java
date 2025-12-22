package com.example.uninaswap.boundary;

import com.example.uninaswap.Costanti;
import com.example.uninaswap.controller.ControllerUninaSwap;
import com.example.uninaswap.dao.OggettoDAO;
import com.example.uninaswap.entity.Oggetto;
import com.example.uninaswap.entity.Utente;
import com.example.uninaswap.interfaces.GestoreMessaggio; // Assumo che tu abbia questa interfaccia
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

import java.io.File;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class Inventario implements Initializable, GestoreMessaggio {

    @FXML private GridPane gridInventario;
    @FXML private Text testoVuoto;
    @FXML private Button btnAggiungiNuovo;

    // Riferimento al controller del banner incluso (fx:id="notifica")
    @FXML private Messaggio notificaController;

    private final ControllerUninaSwap controller = ControllerUninaSwap.getInstance();
    private final OggettoDAO oggettoDAO = new OggettoDAO();
    private final GestoreScene gestoreScene = new GestoreScene();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        caricaOggetti();
    }

    private void caricaOggetti() {
        // 1. Pulizia della griglia esistente
        gridInventario.getChildren().clear();

        // 2. Recupero Utente
        Utente utente = null;
        try {
            utente = controller.getUtente();
        } catch (Exception e) {
            e.printStackTrace();
            return; // Se non c'è utente, non carico nulla
        }

        // 3. Recupero Oggetti dal DB
        List<Oggetto> oggetti = oggettoDAO.ottieniTuttiOggetti(utente.getId());

        // 4. Gestione lista vuota
        if (oggetti.isEmpty()) {
            testoVuoto.setVisible(true);
            testoVuoto.setManaged(true);
            return;
        }

        testoVuoto.setVisible(false);
        testoVuoto.setManaged(false);

        // 5. Popolamento Griglia
        int colonna = 0;
        int riga = 0;

        for (Oggetto obj : oggetti) {
            VBox card = creaCardOggetto(obj);
            gridInventario.add(card, colonna, riga);

            colonna++;
            // Layout a 3 colonne (0, 1, 2)
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
        card.getStyleClass().add("inventory-card"); // Assicurati che questa classe esista nel CSS
        card.setPrefWidth(250);
        card.setPrefHeight(300);

        // --- 1. Immagine ---
        ImageView imgView = new ImageView();
        imgView.setFitWidth(200);
        imgView.setFitHeight(180);
        imgView.setPreserveRatio(true);

        try {
            String path = (obj.getImmagini() != null && !obj.getImmagini().isEmpty())
                    ? obj.getImmagini().get(0)
                    : null;

            if (path != null && !path.isEmpty()) {
                // Gestione path assoluto vs risorsa
                if (path.startsWith("http") || path.startsWith("file:")) {
                    imgView.setImage(new Image(path));
                } else {
                    // Se è un path locale del disco (es. C:\Users\...) bisogna aggiungere "file:"
                    File file = new File(path);
                    if (file.exists()) {
                        imgView.setImage(new Image(file.toURI().toString()));
                    } else {
                        // Fallback se il file non esiste più
                        imgView.setImage(new Image(getClass().getResourceAsStream("/images/uninaLogo.png")));
                    }
                }
            } else {
                // Placeholder se non ha immagini
                imgView.setImage(new Image(getClass().getResourceAsStream("/images/uninaLogo.png")));
            }
        } catch (Exception e) {
            System.err.println("Errore caricamento immagine per oggetto " + obj.getId());
            imgView.setImage(new Image(getClass().getResourceAsStream("/images/uninaLogo.png")));
        }

        // --- 2. Nome Oggetto ---
        Text nome = new Text(obj.getNome());
        nome.getStyleClass().add("label");
        nome.setStyle("-fx-font-weight: bold; -fx-font-size: 16px;");

        // --- 3. Pulsanti Azione ---
        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(Pos.CENTER);

        Button btnModifica = new Button("Modifica");
        btnModifica.getStyleClass().add("button-small");
        btnModifica.setOnAction(e -> onModificaOggetto(obj, e));

        Button btnElimina = new Button("Elimina");
        btnElimina.getStyleClass().addAll("button-small", "button-danger"); // Assumi di avere una classe rossa nel CSS
        btnElimina.setOnAction(e -> onEliminaOggetto(obj));

        buttonBox.getChildren().addAll(btnModifica, btnElimina);

        card.getChildren().addAll(imgView, nome, buttonBox);
        return card;
    }

    private void onModificaOggetto(Oggetto obj, ActionEvent event) {
        System.out.println("Modifica oggetto: " + obj.getNome());
        // TODO: Implementare passaggio dati.
        // Solitamente si usa un metodo nel controller di destinazione tipo "setOggetto(obj)"
        // gestoreScene.CambiaScenaConDati(Costanti.pathAggiungiOggetto, "Modifica Oggetto", event, obj);
        mostraMessaggioEsterno("Funzionalità modifica in arrivo", Messaggio.TIPI.INFO);
    }

    private void onEliminaOggetto(Oggetto obj) {
        boolean eliminato = oggettoDAO.eliminaOggetto(obj.getId());

        if (eliminato) {
            mostraMessaggioEsterno("Oggetto eliminato con successo", Messaggio.TIPI.SUCCESS);
            caricaOggetti(); // Ricarica la griglia per far sparire l'oggetto
        } else {
            mostraMessaggioEsterno("Errore durante l'eliminazione", Messaggio.TIPI.ERROR);
        }
    }

    @FXML
    public void onAggiungiNuovoClick(ActionEvent event) {
        gestoreScene.CambiaScena(Costanti.pathAggiungiOggetto, Costanti.aggiungiOggetto, event);
    }

    @FXML
    public void onIndietroClick(ActionEvent event) {
        gestoreScene.CambiaScena(Costanti.pathHomePage, Costanti.homepage, event);
    }

    // Implementazione interfaccia GestoreMessaggio per usare il banner incluso
    @Override
    public void mostraMessaggioEsterno(String testo, Messaggio.TIPI tipo) {
        if (notificaController != null) {
            notificaController.mostraMessaggio(testo, tipo);
        } else {
            System.out.println("Banner notifica non caricato: " + testo);
        }
    }
}