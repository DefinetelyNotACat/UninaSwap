package com.example.uninaswap.boundary;

import com.example.uninaswap.Costanti;
import com.example.uninaswap.controller.ControllerUninaSwap;
import com.example.uninaswap.entity.Categoria;
import com.example.uninaswap.entity.Oggetto;
import com.example.uninaswap.entity.Utente;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
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

    private ControllerUninaSwap controllerUninaSwap;

    private final List<File> immaginiNuove = new ArrayList<>();
    private final List<String> immaginiEsistenti = new ArrayList<>();
    private Oggetto oggettoDaModificare = null;

    // Proprietà di validazione
    private final BooleanProperty nomeRegexValido = new SimpleBooleanProperty(false);

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        controllerUninaSwap = ControllerUninaSwap.getInstance();

        // 1. Popolamento categorie
        for (Categoria c : controllerUninaSwap.getCategorie()) {
            CheckBox cb = new CheckBox(c.getNome());
            cb.setUserData(c);
            cb.selectedProperty().addListener((obs, oldV, newV) -> controllaCampiValidi());
            categorieMenuButton.getItems().add(new CustomMenuItem(cb, false));
        }

        // 2. Popolamento condizioni
        condizioneBox.getItems().setAll(controllerUninaSwap.getCondizioni());

        // 3. Listener per Validazione Regex Nome
        nomeOggettoField.textProperty().addListener((obs, oldV, newV) -> {
            boolean matchesRegex = newV != null && newV.matches(Costanti.FIELDS_REGEX_SPAZIO);
            boolean isLongEnough = newV != null && newV.trim().length() >= 5;
            boolean ok = matchesRegex && isLongEnough;

            nomeRegexValido.set(ok);

            if(erroreNome != null) {
                if (!matchesRegex) {
                    erroreNome.setText("Caratteri speciali non ammessi (usare lettere, numeri e ? ! ' €)");
                } else if (!isLongEnough) {
                    erroreNome.setText("Il nome deve contenere almeno 5 caratteri");
                }
                erroreNome.setVisible(!ok);
                erroreNome.setManaged(!ok);
            }
            gestisciStileCampo(nomeOggettoField, ok);
            controllaCampiValidi();
        });

        condizioneBox.valueProperty().addListener((o,old,newV) -> controllaCampiValidi());
    }

    private void controllaCampiValidi() {
        // Verifica Categorie
        List<Categoria> selezionate = getCategorieSelezionate();
        boolean categoriaOk = !selezionate.isEmpty();
        categorieMenuButton.setText(categoriaOk ? selezionate.size() + " categorie selezionate" : "Seleziona categorie");

        // Verifica Condizione e Immagini
        boolean condizioneOk = condizioneBox.getValue() != null;
        boolean immaginiOk = !immaginiNuove.isEmpty() || !immaginiEsistenti.isEmpty();

        if (erroreImmagini != null) {
            erroreImmagini.setVisible(!immaginiOk);
            erroreImmagini.setManaged(!immaginiOk);
        }

        // Abilitazione pulsante basata su tutti i controlli (incluso il Regex Property)
        if (aggiungiButton != null) {
            aggiungiButton.setDisable(!(nomeRegexValido.get() && categoriaOk && condizioneOk && immaginiOk));
        }
    }

    private void gestisciStileCampo(Control f, boolean ok) {
        f.getStyleClass().removeAll("error", "right");
        f.getStyleClass().add(ok ? "right" : "error");
    }

    // =================================================================================
    // SETUP PER MODIFICA
    // =================================================================================

    public void setOggettoDaModificare(Oggetto obj) {
        this.oggettoDaModificare = obj;
        if(aggiungiButton != null) aggiungiButton.setText("Salva Modifiche");

        nomeOggettoField.setText(obj.getNome());

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
            immaginiEsistenti.clear();
            immaginiEsistenti.addAll(obj.getImmagini());
            aggiornaVisualizzazioneImmagini();
        }
        controllaCampiValidi();
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

        for (String path : immaginiEsistenti) {
            String fullPath = System.getProperty("user.dir") + File.separator + "dati_utenti" + File.separator + path;
            creaMiniatura(new File(fullPath).toURI().toString(), path, true);
        }

        for (File file : immaginiNuove) {
            creaMiniatura(file.toURI().toString(), file.getAbsolutePath(), false);
        }
    }

    private void creaMiniatura(String displayPath, String originalPath, boolean isEsistente) {
        try {
            Image image = new Image(displayPath, 150, 150, true, true);
            ImageView imageView = new ImageView(image);
            imageView.setPreserveRatio(true);

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
            e.printStackTrace();
        }
    }

    // =================================================================================
    // AZIONI UTENTE
    // =================================================================================

    public void onPubblicaClick(ActionEvent actionEvent) {
        try {
            Utente utenteCorrente = controllerUninaSwap.getUtente();
            Oggetto oggettoToSave = (oggettoDaModificare != null) ? oggettoDaModificare : new Oggetto();

            oggettoToSave.setNome(nomeOggettoField.getText().trim());
            oggettoToSave.setCondizione(condizioneBox.getValue());
            oggettoToSave.setCategorie(new ArrayList<>(getCategorieSelezionate()));

            ArrayList<String> tuttiIPaths = new ArrayList<>(immaginiEsistenti);
            for (File f : immaginiNuove) {
                tuttiIPaths.add(f.getAbsolutePath());
            }
            oggettoToSave.setImmagini(tuttiIPaths);

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