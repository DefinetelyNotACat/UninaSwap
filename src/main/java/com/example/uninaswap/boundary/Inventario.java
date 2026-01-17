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

    // Iniezione del controller della Navbar per la sincronizzazione
    @FXML private NavBarComponent navBarComponentController;

    private final ControllerUninaSwap controller = ControllerUninaSwap.getInstance();
    private final GestoreScene gestoreScene = new GestoreScene();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Sincronizza la navbar appena la pagina viene caricata
        if (navBarComponentController != null) {
            navBarComponentController.aggiornaFotoProfilo();
        }
        caricaOggetti();
    }

    /**
     * Recupera gli oggetti dell'utente dal Controller e popola la griglia.
     */
    private void caricaOggetti() {
        gridInventario.getChildren().clear();

        Utente utente;
        try {
            utente = controller.getUtente();
        } catch (Exception e) {
            mostraMessaggioEsterno("Errore: Utente non loggato.", Messaggio.TIPI.ERROR);
            return;
        }

        List<Oggetto> oggetti = controller.OttieniOggetti(utente);

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
            if (colonna == 3) { // Layout a 3 colonne
                colonna = 0;
                riga++;
            }
        }
    }

    /**
     * Costruisce graficamente la card dell'oggetto con immagini, badge e pulsanti.
     */
    private VBox creaCardOggetto(Oggetto obj) {
        VBox card = new VBox();
        card.setAlignment(Pos.CENTER);
        card.setSpacing(12);
        card.getStyleClass().add("inventory-card");
        card.setPrefWidth(260);
        card.setMinHeight(360);

        // 1. GESTIONE IMMAGINE
        ImageView imgView = new ImageView();
        imgView.setFitWidth(220);
        imgView.setFitHeight(160);
        imgView.setPreserveRatio(true);
        imgView.setSmooth(true);

        try {
            String pathRelativo = (obj.getImmagini() != null && !obj.getImmagini().isEmpty()) ? obj.getImmagini().get(0) : null;
            if (pathRelativo != null) {
                File fileImmagine = new File(System.getProperty("user.dir") + File.separator + "dati_utenti" + File.separator + pathRelativo);
                if (fileImmagine.exists()) {
                    imgView.setImage(new Image(fileImmagine.toURI().toString(), 0, 0, true, true, true));
                } else {
                    setDefaultImage(imgView);
                }
            } else {
                setDefaultImage(imgView);
            }
        } catch (Exception e) {
            setDefaultImage(imgView);
        }

        // 2. NOME OGGETTO
        Text nome = new Text(obj.getNome());
        nome.setStyle("-fx-font-weight: bold; -fx-font-size: 18px; -fx-fill: #2c3e50;");
        nome.setWrappingWidth(230);
        nome.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);

        // 3. SEZIONE BADGE (Condizione e Disponibilità)
        FlowPane badgeBox = new FlowPane();
        badgeBox.setAlignment(Pos.CENTER);
        badgeBox.setHgap(8);
        badgeBox.setVgap(8);

        Label badgeCondizione = new Label(obj.getCondizione().toString().replace("_", " "));
        badgeCondizione.getStyleClass().addAll("badge", "badge-violet");

        Label badgeStato = new Label(obj.getDisponibilita().toString());
        badgeStato.getStyleClass().add("badge");

        // Logica colori dinamica per lo stato
        String stato = obj.getDisponibilita().toString();
        if ("DISPONIBILE".equalsIgnoreCase(stato)) {
            badgeStato.getStyleClass().add("badge-green");
        } else if ("SCAMBIATO".equalsIgnoreCase(stato) || "VENDUTO".equalsIgnoreCase(stato)) {
            badgeStato.getStyleClass().add("badge-red");
        } else {
            badgeStato.getStyleClass().add("badge-orange"); // Es: OCCUPATO (in un annuncio)
        }

        badgeBox.getChildren().addAll(badgeCondizione, badgeStato);

        // 4. AREA PULSANTI
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

            // Passa l'oggetto esistente alla pagina di aggiunta (che farà da modifica)
            AggiungiOggetto controllerAggiungi = loader.getController();
            controllerAggiungi.setOggettoDaModificare(obj);

            Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root, stage.getScene().getWidth(), stage.getScene().getHeight()));
            stage.setTitle("Modifica Oggetto");
        } catch (IOException e) {
            e.printStackTrace();
            mostraMessaggioEsterno("Errore caricamento pagina modifica", Messaggio.TIPI.ERROR);
        }
    }

    private void onEliminaOggetto(Oggetto obj) {
        try {
            if (controller.EliminaOggetto(obj, controller.getUtente())) {
                mostraMessaggioEsterno("Oggetto eliminato con successo!", Messaggio.TIPI.SUCCESS);
                caricaOggetti(); // Refresh della griglia
            } else {
                mostraMessaggioEsterno("Errore: impossibile eliminare l'oggetto.", Messaggio.TIPI.ERROR);
            }
        } catch (Exception e) {
            mostraMessaggioEsterno("Sessione utente non valida.", Messaggio.TIPI.ERROR);
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