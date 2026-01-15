package com.example.uninaswap.boundary;

import com.example.uninaswap.Costanti;
import com.example.uninaswap.controller.ControllerUninaSwap;
import com.example.uninaswap.entity.Categoria;
import com.example.uninaswap.entity.Oggetto;
import com.example.uninaswap.entity.Utente;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
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
    @FXML private MenuButton categorieMenuButton;
    @FXML private ComboBox<Oggetto.CONDIZIONE> condizioneBox;

    @FXML private HBox contenitoreImmagini;
    @FXML private Button caricaFotoButton;
    @FXML private Text erroreImmagini;
    @FXML private Button aggiungiButton;

    // Interazione delegata esclusivamente al Controller
    private ControllerUninaSwap controllerUninaSwap;

    private final List<File> immaginiNuove = new ArrayList<>();
    private final List<String> immaginiEsistenti = new ArrayList<>();

    private Oggetto oggettoDaModificare = null;

    // =================================================================================
    // SETUP PER MODIFICA
    // =================================================================================

    public void setOggettoDaModificare(Oggetto obj) {
        this.oggettoDaModificare = obj;
        if(aggiungiButton != null) aggiungiButton.setText("Salva Modifiche");

        nomeOggettoField.setText(obj.getNome());

        // Pre-selezione delle categorie esistenti
        for (MenuItem item : categorieMenuButton.getItems()) {
            if (item instanceof CustomMenuItem customItem && customItem.getContent() instanceof CheckBox cb) {
                Categoria catNelMenu = (Categoria) cb.getUserData();
                boolean presente = obj.getCategorie().stream()
                        .anyMatch(c -> c.getNome().equals(catNelMenu.getNome()));
                cb.setSelected(presente);
            }
        }

        condizioneBox.setValue(obj.getCondizione());

        // Caricamento dei path relativi esistenti nel sistema
        if (obj.getImmagini() != null) {
            immaginiEsistenti.clear();
            immaginiEsistenti.addAll(obj.getImmagini());
            aggiornaVisualizzazioneImmagini();
        }

        controllaCampiValidi();
    }

    // =================================================================================
    // LOGICA DI VALIDAZIONE
    // =================================================================================

    private void controllaCampiValidi() {
        String testoNome = nomeOggettoField.getText();

        boolean nomeOk = (testoNome != null &&
                testoNome.trim().length() >= 5 &&
                testoNome.matches(Costanti.FIELDS_REGEX_SPAZIO));

        List<Categoria> selezionate = getCategorieSelezionate();
        boolean categoriaOk = !selezionate.isEmpty();

        if (categoriaOk) {
            categorieMenuButton.setText(selezionate.size() + " categorie selezionate");
        } else {
            categorieMenuButton.setText("Seleziona categorie");
        }

        boolean condizioneOk = condizioneBox.getValue() != null;
        boolean immaginiOk = !immaginiNuove.isEmpty() || !immaginiEsistenti.isEmpty();

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
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Immagini", "*.png", "*.jpg", "*.jpeg"));

        Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        List<File> files = fileChooser.showOpenMultipleDialog(stage);

        if (files != null) {
            immaginiNuove.addAll(files);
            aggiornaVisualizzazioneImmagini();
            controllaCampiValidi();
        }
    }

    private void aggiornaVisualizzazioneImmagini() {
        contenitoreImmagini.getChildren().clear();

        if (immaginiNuove.isEmpty() && immaginiEsistenti.isEmpty()) {
            Text placeholder = new Text("Nessuna immagine caricata");
            placeholder.getStyleClass().add("placeholder-text");
            contenitoreImmagini.getChildren().add(placeholder);
            return;
        }

        // Visualizzazione immagini già salvate (risoluzione del path relativo)
        for (String path : immaginiEsistenti) {
            String fullPath = System.getProperty("user.dir") + File.separator + "dati_utenti" + File.separator + path;
            creaMiniatura(new File(fullPath).toURI().toString(), path, true);
        }

        // Visualizzazione nuove immagini caricate dal disco
        for (File file : immaginiNuove) {
            creaMiniatura(file.toURI().toString(), file.getAbsolutePath(), false);
        }
    }

    private void creaMiniatura(String displayPath, String originalPath, boolean isEsistente) {
        try {
            // Caricamento ad alta qualità per l'anteprima
            Image image = new Image(displayPath, 150, 150, true, true);
            ImageView imageView = new ImageView(image);
            imageView.setPreserveRatio(true);
            imageView.setSmooth(true);

            Button rimuoviBtn = new Button("X");
            rimuoviBtn.getStyleClass().add("button-remove");

            rimuoviBtn.setOnAction(e -> {
                if (isEsistente) immaginiEsistenti.remove(originalPath);
                else immaginiNuove.removeIf(f -> f.getAbsolutePath().equals(originalPath));
                aggiornaVisualizzazioneImmagini();
                controllaCampiValidi();
            });

            VBox box = new VBox(5, imageView, rimuoviBtn);
            box.setAlignment(Pos.CENTER);
            box.getStyleClass().add("image-card");
            contenitoreImmagini.getChildren().add(box);
        } catch (Exception e) {
            System.err.println("Errore caricamento miniatura: " + e.getMessage());
        }
    }

    // =================================================================================
    // AZIONI UTENTE (CHIAMATE AL CONTROLLER)
    // =================================================================================

    public void onPubblicaClick(ActionEvent actionEvent) {
        try {
            Utente utenteCorrente = controllerUninaSwap.getUtente();
            Oggetto oggettoToSave = (oggettoDaModificare != null) ? oggettoDaModificare : new Oggetto();

            oggettoToSave.setNome(nomeOggettoField.getText());
            oggettoToSave.setCondizione(condizioneBox.getValue());
            oggettoToSave.setCategorie(new ArrayList<>(getCategorieSelezionate()));

            // Combinazione di path relativi esistenti e path assoluti nuovi
            ArrayList<String> tuttiIPaths = new ArrayList<>();
            tuttiIPaths.addAll(immaginiEsistenti);
            for (File f : immaginiNuove) {
                tuttiIPaths.add(f.getAbsolutePath());
            }

            oggettoToSave.setImmagini(tuttiIPaths);

            // Interazione mediata esclusivamente dal Controller
            boolean esito = (oggettoDaModificare == null)
                    ? controllerUninaSwap.SalvaOggetto(oggettoToSave, utenteCorrente)
                    : controllerUninaSwap.ModificaOggetto(oggettoToSave);

            if (esito) {
                new GestoreScene().CambiaScena(Costanti.pathInventario, "Il Tuo Inventario", actionEvent,
                        oggettoDaModificare == null ? "Oggetto aggiunto!" : "Oggetto modificato!", Messaggio.TIPI.SUCCESS);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void onAnnullaClick(ActionEvent actionEvent) {
        new GestoreScene().CambiaScena(Costanti.pathInventario, "Il Tuo Inventario", actionEvent);
    }

    // =================================================================================
    // INITIALIZE E HELPER
    // =================================================================================

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        controllerUninaSwap = ControllerUninaSwap.getInstance();

        // Popolamento dinamico delle categorie tramite Controller
        for (Categoria c : controllerUninaSwap.getCategorie()) {
            CheckBox cb = new CheckBox(c.getNome());
            cb.setUserData(c);
            cb.selectedProperty().addListener((obs, oldV, newV) -> controllaCampiValidi());
            categorieMenuButton.getItems().add(new CustomMenuItem(cb, false));
        }

        // Popolamento condizioni tramite Controller
        condizioneBox.getItems().setAll(controllerUninaSwap.getCondizioni());

        nomeOggettoField.textProperty().addListener((obs, oldV, newV) -> {
            boolean matchesRegex = newV.matches(Costanti.FIELDS_REGEX_SPAZIO);
            boolean isLongEnough = newV.trim().length() >= 5;
            boolean ok = matchesRegex && isLongEnough;

            if(erroreNome != null) {
                erroreNome.setVisible(!ok);
                erroreNome.setManaged(!ok);
                if (!matchesRegex) erroreNome.setText("Caratteri speciali non ammessi");
                else if (!isLongEnough) erroreNome.setText("Minimo 5 caratteri");
            }
            controllaCampiValidi();
        });

        condizioneBox.valueProperty().addListener((o,old,newV) -> controllaCampiValidi());
    }

    private List<Categoria> getCategorieSelezionate() {
        List<Categoria> selezionate = new ArrayList<>();
        for (MenuItem item : categorieMenuButton.getItems()) {
            if (item instanceof CustomMenuItem customItem && customItem.getContent() instanceof CheckBox cb) {
                if (cb.isSelected()) selezionate.add((Categoria) cb.getUserData());
            }
        }
        return selezionate;
    }
}