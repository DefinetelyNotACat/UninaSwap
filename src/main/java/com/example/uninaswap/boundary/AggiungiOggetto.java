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
    @FXML private MenuButton categorieMenuButton;
    @FXML private ComboBox<Oggetto.CONDIZIONE> condizioneBox;

    @FXML private HBox contenitoreImmagini;
    @FXML private Button caricaFotoButton;
    @FXML private Text erroreImmagini;
    @FXML private Button aggiungiButton;

    private ControllerUninaSwap controllerUninaSwap;
    private final OggettoDAO oggettoDAO = new OggettoDAO();

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

        // Pre-selezione categorie
        for (MenuItem item : categorieMenuButton.getItems()) {
            if (item instanceof CustomMenuItem customItem && customItem.getContent() instanceof CheckBox cb) {
                Categoria catNelMenu = (Categoria) cb.getUserData();
                boolean presente = obj.getCategorie().stream()
                        .anyMatch(c -> c.getNome().equals(catNelMenu.getNome()));
                cb.setSelected(presente);
            }
        }

        condizioneBox.setValue(obj.getCondizione());

        if (obj.getImmagini() != null) {
            immaginiEsistenti.addAll(obj.getImmagini());
            aggiornaVisualizzazioneImmagini();
        }

        controllaCampiValidi();
    }

    // =================================================================================
    // LOGICA DI VALIDAZIONE (FIELDS_REGEX_SPAZIO)
    // =================================================================================

    private void controllaCampiValidi() {
        String testoNome = nomeOggettoField.getText();

        // Validazione Nome con Regex di Costanti e lunghezza minima
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

        for (String path : immaginiEsistenti) creaMiniatura(path, true);
        for (File file : immaginiNuove) creaMiniatura(file.toURI().toString(), false);
    }

    private void creaMiniatura(String imagePath, boolean isEsistente) {
        try {
            Image image = (imagePath.startsWith("file:") || imagePath.startsWith("http"))
                    ? new Image(imagePath)
                    : new Image(new File(imagePath).toURI().toString());

            ImageView imageView = new ImageView(image);
            imageView.setFitHeight(150);
            imageView.setFitWidth(150);
            imageView.setPreserveRatio(true);

            Button rimuoviBtn = new Button("X");
            rimuoviBtn.getStyleClass().add("button-remove");

            rimuoviBtn.setOnAction(e -> {
                if (isEsistente) immaginiEsistenti.remove(imagePath);
                else immaginiNuove.removeIf(f -> f.toURI().toString().equals(imagePath));
                aggiornaVisualizzazioneImmagini();
                controllaCampiValidi();
            });

            VBox box = new VBox(5, imageView, rimuoviBtn);
            box.setAlignment(Pos.CENTER);
            box.getStyleClass().add("image-card");
            contenitoreImmagini.getChildren().add(box);
        } catch (Exception e) {
            System.err.println("Errore miniatura: " + e.getMessage());
        }
    }

    // =================================================================================
    // AZIONI UTENTE
    // =================================================================================

    public void onPubblicaClick(ActionEvent actionEvent) {
        try {
            Utente utenteCorrente = controllerUninaSwap.getUtente();
            Oggetto oggettoToSave = (oggettoDaModificare != null) ? oggettoDaModificare : new Oggetto();

            oggettoToSave.setNome(nomeOggettoField.getText());
            oggettoToSave.setCondizione(condizioneBox.getValue());
            oggettoToSave.setCategorie(new ArrayList<>(getCategorieSelezionate()));

            ArrayList<String> pathsFinali = new ArrayList<>(immaginiEsistenti);
            for (File f : immaginiNuove) pathsFinali.add(f.getAbsolutePath());
            oggettoToSave.setImmagini(pathsFinali);

            boolean esito = (oggettoDaModificare == null)
                    ? oggettoDAO.salvaOggetto(oggettoToSave, utenteCorrente)
                    : oggettoDAO.modificaOggetto(oggettoToSave);

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

        // Popola categorie
        for (Categoria c : controllerUninaSwap.getCategorie()) {
            CheckBox cb = new CheckBox(c.getNome());
            cb.setUserData(c);
            cb.selectedProperty().addListener((obs, oldV, newV) -> controllaCampiValidi());
            categorieMenuButton.getItems().add(new CustomMenuItem(cb, false));
        }

        condizioneBox.getItems().setAll(controllerUninaSwap.getCondizioni());

        // Listener Nome con FIELDS_REGEX_SPAZIO
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