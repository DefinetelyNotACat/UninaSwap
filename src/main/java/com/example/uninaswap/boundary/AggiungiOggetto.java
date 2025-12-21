package com.example.uninaswap.boundary;

import com.example.uninaswap.Costanti;
import com.example.uninaswap.controller.ControllerUninaSwap;
import com.example.uninaswap.dao.OggettoDAO;
import com.example.uninaswap.entity.Categoria;
import com.example.uninaswap.entity.Oggetto;
import com.example.uninaswap.entity.Utente;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class AggiungiOggetto implements Initializable {

    @FXML private Text erroreNome;
    @FXML private TextField nomeOggettoField;
    @FXML private ComboBox<String> categoriaBox;
    @FXML private ComboBox<Oggetto.CONDIZIONE> condizioneBox;

    @FXML private HBox contenitoreImmagini;
    @FXML private Button caricaFotoButton;
    @FXML private Text erroreImmagini;
    @FXML private Button aggiungiButton;

    private ControllerUninaSwap controllerUninaSwap;
    private final OggettoDAO oggettoDAO = new OggettoDAO();
    private final List<File> immaginiSelezionate = new ArrayList<>();

    // =================================================================================
    // LOGICA DI VALIDAZIONE
    // =================================================================================

    private void controllaCampiValidi() {
        // Validazione Nome
        String testoNome = nomeOggettoField.getText();
        boolean nomeOk = false;
        if (testoNome != null) {
            String nomePulito = testoNome.replace(" ", "");
            if (nomePulito.length() >= 5 && testoNome.matches(Costanti.OGGETTO_FIELD_REGEX)) {
                nomeOk = true;
            }
        }

        // Validazione Categoria, Condizione e Immagini
        boolean categoriaOk = categoriaBox.getValue() != null;
        boolean condizioneOk = condizioneBox.getValue() != null;
        boolean immaginiOk = !immaginiSelezionate.isEmpty();

        if (erroreImmagini != null) {
            erroreImmagini.setVisible(!immaginiOk);
            erroreImmagini.setManaged(!immaginiOk);
        }

        if (aggiungiButton != null) {
            aggiungiButton.setDisable(!(nomeOk && categoriaOk && condizioneOk && immaginiOk));
        }
    }

    // =================================================================================
    // GESTIONE IMMAGINI
    // =================================================================================

    @FXML
    public void onCaricaFotoClick(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Seleziona Immagini Oggetto");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Immagini", "*.png", "*.jpg", "*.jpeg", "*.gif")
        );

        Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        List<File> files = fileChooser.showOpenMultipleDialog(stage);

        if (files != null) {
            immaginiSelezionate.addAll(files);
            aggiornaVisualizzazioneImmagini();
            controllaCampiValidi();
        }
    }

    private void aggiornaVisualizzazioneImmagini() {
        contenitoreImmagini.getChildren().clear();

        if (immaginiSelezionate.isEmpty()) {
            Text placeholder = new Text("Nessuna immagine caricata");
            placeholder.getStyleClass().add("placeholder-text");
            contenitoreImmagini.getChildren().add(placeholder);
        } else {
            for (File file : immaginiSelezionate) {
                creaMiniaturaImmagine(file);
            }
        }
    }

    private void creaMiniaturaImmagine(File file) {
        try {
            Image image = new Image(file.toURI().toString());
            ImageView imageView = new ImageView(image);

            imageView.setFitHeight(150);
            imageView.setFitWidth(150);
            imageView.setPreserveRatio(true);

            Button rimuoviBtn = new Button("X");
            rimuoviBtn.getStyleClass().add("button-remove");
            rimuoviBtn.setPrefSize(20, 20);

            rimuoviBtn.setOnAction(e -> {
                immaginiSelezionate.remove(file);
                aggiornaVisualizzazioneImmagini();
                controllaCampiValidi();
            });

            VBox boxSingolaFoto = new VBox(5);
            boxSingolaFoto.setAlignment(Pos.CENTER);
            boxSingolaFoto.getStyleClass().add("image-card");
            boxSingolaFoto.getChildren().addAll(imageView, rimuoviBtn);

            contenitoreImmagini.getChildren().add(boxSingolaFoto);

        } catch (Exception e) {
            System.err.println("Errore caricamento miniatura: " + file.getName());
        }
    }

    // =================================================================================
    // AZIONI UTENTE (PUBBLICA / ANNULLA)
    // =================================================================================

    public void onAnnullaClick(ActionEvent actionEvent) {
        GestoreScene gestoreScene = new GestoreScene();
        gestoreScene.CambiaScena(Costanti.pathHomePage, Costanti.homepage, actionEvent, "Operazione annullata", Messaggio.TIPI.INFO);
    }

    public void onPubblicaClick(ActionEvent actionEvent) {
        String nome = nomeOggettoField.getText();
        String nomeCategoria = categoriaBox.getValue();
        Oggetto.CONDIZIONE condizioneScelta = condizioneBox.getValue();

        Utente utenteCorrente = null;
        try {
            utenteCorrente = controllerUninaSwap.getUtente();
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        Oggetto nuovoOggetto = new Oggetto();
        nuovoOggetto.setNome(nome);
        nuovoOggetto.setDisponibilita(Oggetto.DISPONIBILITA.DISPONIBILE);
        nuovoOggetto.setCondizione(condizioneScelta);

        // Associazione categorie
        ArrayList<Categoria> listaCategorie = new ArrayList<>();
        listaCategorie.add(new Categoria(nomeCategoria));
        nuovoOggetto.setCategorie(listaCategorie);

        // Conversione percorsi immagini
        ArrayList<String> percorsiStringa = new ArrayList<>();
        for (File f : immaginiSelezionate) {
            percorsiStringa.add(f.getAbsolutePath());
        }
        nuovoOggetto.setImmagini(percorsiStringa);

        boolean esito = oggettoDAO.salvaOggetto(nuovoOggetto, utenteCorrente);

        if (esito) {
            GestoreScene gestoreScene = new GestoreScene();
            gestoreScene.CambiaScena(Costanti.pathHomePage, Costanti.homepage, actionEvent, "Oggetto pubblicato con successo!", Messaggio.TIPI.SUCCESS);
        } else {
            System.err.println("Errore salvataggio DB");
        }
    }

    // =================================================================================
    // INITIALIZE
    // =================================================================================

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        controllerUninaSwap = ControllerUninaSwap.getInstance();

        if (aggiungiButton != null) {
            aggiungiButton.setDisable(true);
        }

        // --- Configurazione Categorie ---
        ArrayList<Categoria> categorie = controllerUninaSwap.getCategorie();
        for (Categoria categoria : categorie) {
            categoriaBox.getItems().add(categoria.getNome());
        }

        categoriaBox.setCellFactory(lv -> new ListCell<String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                setText((empty || item == null) ? null : item);
                setCursor(empty ? Cursor.DEFAULT : Cursor.HAND);
            }
        });
        categoriaBox.setCursor(Cursor.HAND);
        categoriaBox.valueProperty().addListener((obs, oldVal, newVal) -> controllaCampiValidi());

        // --- Configurazione Condizioni ---
        condizioneBox.getItems().setAll(controllerUninaSwap.getCondizioni());

        // 1. Visualizzazione nella lista dropdown (Sostituisce _ con spazio)
        condizioneBox.setCellFactory(lv -> new ListCell<Oggetto.CONDIZIONE>() {
            @Override
            protected void updateItem(Oggetto.CONDIZIONE item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setCursor(Cursor.DEFAULT);
                } else {
                    // Trasforma "COME_NUOVO" in "COME NUOVO" per l'utente
                    setText(item.toString().replace("_", " "));
                    setCursor(Cursor.HAND);
                }
            }
        });

        // 2. Visualizzazione del valore selezionato (il bottone)
        condizioneBox.setButtonCell(new ListCell<Oggetto.CONDIZIONE>() {
            @Override
            protected void updateItem(Oggetto.CONDIZIONE item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.toString().replace("_", " "));
                }
            }
        });

        condizioneBox.setCursor(Cursor.HAND);
        condizioneBox.valueProperty().addListener((obs, oldVal, newVal) -> controllaCampiValidi());

        // --- Listener Validazione Nome ---
        if (nomeOggettoField != null) {
            nomeOggettoField.textProperty().addListener((observable, oldValue, newValue) -> {
                if (erroreNome != null) {
                    erroreNome.setVisible(false);
                    erroreNome.setManaged(false);
                }
                boolean lunghezzaOk = newValue.replace(" ", "").length() >= 5;
                // Nota: Costanti.OGGETTO_FIELD_REGEX deve essere definito nella tua classe Costanti
                if (!newValue.matches(Costanti.OGGETTO_FIELD_REGEX) || !lunghezzaOk) {
                    erroreNome.setText("Errore! inserire un nome valido (min 5 caratteri, no speciali)");
                    erroreNome.setManaged(true);
                    erroreNome.setVisible(true);
                }
                controllaCampiValidi();
            });
        }
    }
}