package com.example.uninaswap.boundary;
import javafx.scene.layout.FlowPane;
import com.example.uninaswap.Costanti;
import com.example.uninaswap.controller.ControllerUninaSwap;
import com.example.uninaswap.dao.OggettoDAO;
import com.example.uninaswap.entity.Oggetto;
import com.example.uninaswap.entity.Utente;
import com.example.uninaswap.interfaces.GestoreMessaggio;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.scene.control.Label;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class Inventario implements Initializable, GestoreMessaggio {

    @FXML private GridPane gridInventario;
    @FXML private Text testoVuoto;
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
            return;
        }

        // 3. Recupero Oggetti dal DB
        List<Oggetto> oggetti = oggettoDAO.ottieniTuttiOggetti(utente.getId());

        // --- FIX HERE: Check for NULL before checking isEmpty() ---
        if (oggetti == null || oggetti.isEmpty()) {
            testoVuoto.setVisible(true);
            testoVuoto.setManaged(true);
            return; // Important: Return here so the loop below doesn't run on null
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
        card.getStyleClass().add("inventory-card");

        // --- 1. DIMENSIONI DINAMICHE ---
        card.setPrefWidth(260);
        card.setMinWidth(260);
        card.setMaxWidth(260);

        card.setMinHeight(340);
        card.setPrefHeight(javafx.scene.layout.Region.USE_COMPUTED_SIZE);
        card.setMaxHeight(Double.MAX_VALUE);

        ImageView imgView = new ImageView();
        imgView.setFitWidth(220);
        imgView.setFitHeight(180);
        imgView.setPreserveRatio(true);

        try {
            // Prende la prima immagine se esiste, altrimenti null
            String path = (obj.getImmagini() != null && !obj.getImmagini().isEmpty()) ? obj.getImmagini().get(0) : null;

            if (path != null) {
                // Gestione file locali o URL web
                if (path.startsWith("file:") || path.startsWith("http")) {
                    imgView.setImage(new Image(path));
                } else {
                    imgView.setImage(new Image(new File(path).toURI().toString()));
                }
            } else {
                // Immagine di default se non ce ne sono
                imgView.setImage(new Image(getClass().getResourceAsStream("/images/uninaLogo.png")));
            }
        } catch (Exception e) {
            // Fallback in caso di errore nel caricamento
            imgView.setImage(new Image(getClass().getResourceAsStream("/images/uninaLogo.png")));
        }

        // --- NOME ---
        Text nome = new Text(obj.getNome());
        nome.getStyleClass().add("label");
        nome.setStyle("-fx-font-weight: bold; -fx-font-size: 18px;");
        nome.setWrappingWidth(230); // Impedisce al testo di uscire dalla card (260px - padding)
        nome.setTextAlignment(javafx.scene.text.TextAlignment.CENTER); // Mantiene il testo centrato se va a capo

        // --- 2. BADGES (FlowPane) ---
        FlowPane badgeBox = new FlowPane();
        badgeBox.setAlignment(Pos.CENTER);
        badgeBox.setHgap(5);
        badgeBox.setVgap(5);
        badgeBox.setPrefWrapLength(230);

        // Spilla Condizione
        String testoCondizione = String.valueOf(obj.getCondizione());
        Label badgeCondizione = new Label(testoCondizione);
        badgeCondizione.getStyleClass().addAll("badge", "badge-violet");
        badgeCondizione.setMinWidth(javafx.scene.layout.Region.USE_PREF_SIZE);

        // Spilla Stato
        String testoStato = String.valueOf(obj.getDisponibilita());
        Label badgeStato = new Label(testoStato);
        badgeStato.getStyleClass().add("badge");
        badgeStato.setMinWidth(javafx.scene.layout.Region.USE_PREF_SIZE);

        if ("DISPONIBILE".equalsIgnoreCase(testoStato)) {
            badgeStato.getStyleClass().add("badge-green");
        } else if ("SCAMBIATO".equalsIgnoreCase(testoStato) || "ELIMINATO".equalsIgnoreCase(testoStato)) {
            badgeStato.getStyleClass().add("badge-red");
        } else {
            badgeStato.getStyleClass().add("badge-orange");
        }

        badgeBox.getChildren().addAll(badgeCondizione, badgeStato);

        // --- BOTTONI ---
        HBox btnBox = new HBox(10);
        btnBox.setAlignment(Pos.CENTER);

        Button btnModifica = new Button("Modifica");
        btnModifica.getStyleClass().add("button-small");
        btnModifica.setOnAction(e -> onModificaOggetto(obj, e));

        Button btnElimina = new Button("Elimina");
        btnElimina.getStyleClass().addAll("button-small", "button-danger");
        btnElimina.setOnAction(e -> onEliminaOggetto(obj));

        btnBox.getChildren().addAll(btnModifica, btnElimina);

        card.getChildren().addAll(imgView, nome, badgeBox, btnBox);
        return card;
    }
    private void onModificaOggetto(Oggetto obj, ActionEvent event) {
        try {
            // Caricamento manuale per passare i dati al controller
            FXMLLoader loader = new FXMLLoader(getClass().getResource(Costanti.pathAggiungiOggetto));
            Parent root = loader.load();

            // Otteniamo il controller e passiamo l'oggetto
            AggiungiOggetto controllerAggiungi = loader.getController();
            controllerAggiungi.setOggettoDaModificare(obj);

            // 1. Recupero lo stage attuale
            Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();

            // 2. Salvo le dimensioni attuali della scena (larghezza e altezza)
            double larghezzaAttuale = stage.getScene().getWidth();
            double altezzaAttuale = stage.getScene().getHeight();

            // 3. Creo la nuova scena IMPONENDO le dimensioni vecchie
            Scene scene = new Scene(root, larghezzaAttuale, altezzaAttuale);

            // 4. Se usi CSS globali, ricordati di riaggiungerli (opzionale ma consigliato)
            // scene.getStylesheets().add(getClass().getResource("style.css").toExternalForm());

            stage.setScene(scene);
            stage.setTitle("Modifica Oggetto");
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            mostraMessaggioEsterno("Errore caricamento pagina modifica", Messaggio.TIPI.ERROR);
        }
    }

    private void onEliminaOggetto(Oggetto obj) {
        if (oggettoDAO.eliminaOggetto(obj.getId())) {
            mostraMessaggioEsterno("Oggetto eliminato!", Messaggio.TIPI.SUCCESS);
            caricaOggetti();
        } else {
            mostraMessaggioEsterno("Errore eliminazione.", Messaggio.TIPI.ERROR);
        }
    }

    @FXML
    public void onAggiungiNuovoClick(ActionEvent event) {
        // Qui usiamo GestoreScene normale perché non dobbiamo passare dati
        gestoreScene.CambiaScena(Costanti.pathAggiungiOggetto, "Aggiungi Oggetto", event);
    }

    @FXML public void onIndietroClick(ActionEvent event) {
        gestoreScene.CambiaScena(Costanti.pathHomePage, Costanti.homepage, event);
    }

    @Override
    public void mostraMessaggioEsterno(String testo, Messaggio.TIPI tipo) {
        if (notificaController != null) notificaController.mostraMessaggio(testo, tipo);
    }
}