package com.example.uninaswap.boundary;

import com.example.uninaswap.Costanti;
import com.example.uninaswap.controller.ControllerUninaSwap;
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
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class Inventario implements Initializable, GestoreMessaggio {

    @FXML private GridPane gridInventario;
    @FXML private Text testoVuoto;
    @FXML private Messaggio notificaController;
    @FXML private NavBarComponent navBarComponentController;

    private final ControllerUninaSwap controller = ControllerUninaSwap.getInstance();
    private final GestoreScene gestoreScene = new GestoreScene();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        if (navBarComponentController != null) {
            navBarComponentController.aggiornaFotoProfilo();
        }
        caricaOggetti();
    }

    private void caricaOggetti() {
        gridInventario.getChildren().clear();

        Utente utenteLoggato;
        try {
            utenteLoggato = controller.getUtente();
        } catch (Exception e) {
            mostraMessaggioEsterno("Sessione scaduta. Effettua nuovamente il login.", Messaggio.TIPI.ERROR);
            return;
        }

        List<Oggetto> listaOggetti = controller.OttieniOggetti(utenteLoggato);

        if (listaOggetti == null || listaOggetti.isEmpty()) {
            testoVuoto.setVisible(true);
            testoVuoto.setManaged(true);
            return;
        }

        testoVuoto.setVisible(false);
        testoVuoto.setManaged(false);

        int colonna = 0;
        int riga = 0;

        for (Oggetto obj : listaOggetti) {
            VBox card = creaCardOggetto(obj);
            gridInventario.add(card, colonna, riga);

            colonna++;
            if (colonna == 3) { // Grid a 3 colonne per un layout pulito
                colonna = 0;
                riga++;
            }
        }
    }

    private VBox creaCardOggetto(Oggetto obj) {
        VBox card = new VBox();
        card.setAlignment(Pos.CENTER);
        card.setSpacing(15);
        card.getStyleClass().add("inventory-card");

        // Dimensioni fisse per mantenere la griglia allineata
        card.setPrefWidth(260);
        card.setMinWidth(260);
        card.setMinHeight(380);

        // 1. IMMAGINE (Caricamento HD e Smooth)
        ImageView imgView = new ImageView();
        imgView.setFitWidth(220);
        imgView.setFitHeight(180);
        imgView.setPreserveRatio(true);
        imgView.setSmooth(true);

        try {
            String pathRelativo = (obj.getImmagini() != null && !obj.getImmagini().isEmpty()) ? obj.getImmagini().get(0) : null;

            if (pathRelativo != null) {
                File fileImg = new File(System.getProperty("user.dir") + File.separator + "dati_utenti" + File.separator + pathRelativo);
                if (fileImg.exists()) {
                    // Caricamento con background loading e alta qualità
                    imgView.setImage(new Image(fileImg.toURI().toString(), 400, 400, true, true, true));
                } else {
                    setDefaultImage(imgView);
                }
            } else {
                setDefaultImage(imgView);
            }
        } catch (Exception e) {
            setDefaultImage(imgView);
        }

        // 2. NOME OGGETTO (Testo centrato e a capo)
        Text nome = new Text(obj.getNome());
        nome.setStyle("-fx-font-weight: bold; -fx-font-size: 18px; -fx-fill: #2d3436;");
        nome.setWrappingWidth(230);
        nome.setTextAlignment(TextAlignment.CENTER);

        // 3. SEZIONE BADGE (Pillole colorate)
        FlowPane badgeBox = new FlowPane();
        badgeBox.setAlignment(Pos.CENTER);
        badgeBox.setHgap(8);
        badgeBox.setVgap(8);

        Label badgeCondizione = new Label(obj.getCondizione().toString().replace("_", " "));
        badgeCondizione.getStyleClass().addAll("badge", "badge-violet");

        Label badgeStato = new Label(obj.getDisponibilita().toString());
        badgeStato.getStyleClass().add("badge");

        // Logica colori dinamica
        String statoStr = obj.getDisponibilita().toString();
        if ("DISPONIBILE".equalsIgnoreCase(statoStr)) {
            badgeStato.getStyleClass().add("badge-green");
        } else if ("SCAMBIATO".equalsIgnoreCase(statoStr) || "VENDUTO".equalsIgnoreCase(statoStr)) {
            badgeStato.getStyleClass().add("badge-red");
        } else {
            badgeStato.getStyleClass().add("badge-orange"); // Es: OCCUPATO (in annuncio)
        }

        badgeBox.getChildren().addAll(badgeCondizione, badgeStato);

        // 4. BOTTONI AZIONE
        HBox btnBox = new HBox(12);
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

    private void setDefaultImage(ImageView iv) {
        iv.setImage(new Image(getClass().getResourceAsStream("/com/example/uninaswap/images/uninaLogo.png")));
    }

    private void onModificaOggetto(Oggetto obj, ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(Costanti.pathAggiungiOggetto));
            Parent root = loader.load();

            // Passaggio dell'oggetto al controller di destinazione
            AggiungiOggetto controllerAggiungi = loader.getController();
            controllerAggiungi.setOggettoDaModificare(obj);

            Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root, stage.getScene().getWidth(), stage.getScene().getHeight());
            stage.setScene(scene);
            stage.setTitle("Modifica Oggetto - UninaSwap");
        } catch (IOException e) {
            e.printStackTrace();
            mostraMessaggioEsterno("Errore nel caricamento della pagina di modifica.", Messaggio.TIPI.ERROR);
        }
    }

    private void onEliminaOggetto(Oggetto obj) {
        try {
            if (controller.EliminaOggetto(obj, controller.getUtente())) {
                mostraMessaggioEsterno("Oggetto rimosso correttamente.", Messaggio.TIPI.SUCCESS);
                caricaOggetti(); // Refresh della visualizzazione
            } else {
                mostraMessaggioEsterno("Errore: Impossibile eliminare un oggetto associato a un annuncio attivo.", Messaggio.TIPI.ERROR);
            }
        } catch (Exception e) {
            mostraMessaggioEsterno("Errore durante l'eliminazione.", Messaggio.TIPI.ERROR);
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