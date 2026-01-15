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
        gridInventario.getChildren().clear();

        Utente utente = null;
        try {
            utente = controller.getUtente();
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        List<Oggetto> oggetti = oggettoDAO.ottieniTuttiOggetti(utente.getId());

        if (oggetti == null || oggetti.isEmpty()) {
            testoVuoto.setVisible(true);
            testoVuoto.setManaged(true);
            return;
        }

        testoVuoto.setVisible(false);
        testoVuoto.setManaged(false);

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

        card.setPrefWidth(260);
        card.setMinWidth(260);
        card.setMaxWidth(260);
        card.setMinHeight(340);
        card.setPrefHeight(javafx.scene.layout.Region.USE_COMPUTED_SIZE);
        card.setMaxHeight(Double.MAX_VALUE);

        ImageView imgView = new ImageView();
        imgView.setFitWidth(220);
        imgView.setFitHeight(180);
        imgView.setSmooth(true); // Attiva l'anti-aliasing di alta qualità
        imgView.setCache(true);  // Memorizza la versione renderizzata
        imgView.setCacheHint(javafx.scene.CacheHint.QUALITY); // Forza la GPU a dare priorità alla qualità
        imgView.setPreserveRatio(true);

        try {
            // Recupero del path relativo dal DB (es: "oggetti/file.jpg")
            String pathRelativo = (obj.getImmagini() != null && !obj.getImmagini().isEmpty()) ? obj.getImmagini().get(0) : null;

            if (pathRelativo != null) {
                if (pathRelativo.startsWith("file:") || pathRelativo.startsWith("http")) {
                    imgView.setImage(new Image(pathRelativo, 0, 0, true, true, true));
                }
                else {
                    // Costruzione del percorso assoluto puntando alla cartella dati_utenti
                    File fileImmagine = new File(System.getProperty("user.dir") + File.separator + "dati_utenti" + File.separator + pathRelativo);

                    if (fileImmagine.exists()) {
                        imgView.setImage(new Image(fileImmagine.toURI().toString(), 0, 0, true, true, false));                    } else {
                        System.err.println("Immagine non trovata: " + fileImmagine.getAbsolutePath());
                        // Fallback uniforme con HomePageBoundary
                        imgView.setImage(new Image(getClass().getResourceAsStream("/com/example/uninaswap/images/uninaLogo.png")));
                    }
                }
            } else {
                imgView.setImage(new Image(getClass().getResourceAsStream("/com/example/uninaswap/images/uninaLogo.png")));
            }
        } catch (Exception e) {
            System.err.println("Errore caricamento immagine: " + e.getMessage());
            imgView.setImage(new Image(getClass().getResourceAsStream("/com/example/uninaswap/images/uninaLogo.png")));
        }

        Text nome = new Text(obj.getNome());
        nome.getStyleClass().add("label");
        nome.setStyle("-fx-font-weight: bold; -fx-font-size: 18px;");
        nome.setWrappingWidth(230);
        nome.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);

        FlowPane badgeBox = new FlowPane();
        badgeBox.setAlignment(Pos.CENTER);
        badgeBox.setHgap(5);
        badgeBox.setVgap(5);
        badgeBox.setPrefWrapLength(230);

        String testoCondizione = String.valueOf(obj.getCondizione());
        Label badgeCondizione = new Label(testoCondizione);
        badgeCondizione.getStyleClass().addAll("badge", "badge-violet");
        badgeCondizione.setMinWidth(javafx.scene.layout.Region.USE_PREF_SIZE);

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
            FXMLLoader loader = new FXMLLoader(getClass().getResource(Costanti.pathAggiungiOggetto));
            Parent root = loader.load();

            AggiungiOggetto controllerAggiungi = loader.getController();
            controllerAggiungi.setOggettoDaModificare(obj);

            Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
            double larghezzaAttuale = stage.getScene().getWidth();
            double altezzaAttuale = stage.getScene().getHeight();

            Scene scene = new Scene(root, larghezzaAttuale, altezzaAttuale);
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