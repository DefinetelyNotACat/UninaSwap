package com.example.uninaswap.boundary;

import com.example.uninaswap.Costanti;
import com.example.uninaswap.controller.ControllerUninaSwap;
import com.example.uninaswap.dao.OggettoDAO;
import com.example.uninaswap.entity.Categoria;
import com.example.uninaswap.entity.Oggetto;
import com.example.uninaswap.entity.Utente;
import com.example.uninaswap.interfaces.GestoreMessaggio;
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
    @FXML private Text titoloPagina; // Opzionale: per cambiare titolo in "Modifica Oggetto"

    private ControllerUninaSwap controllerUninaSwap;
    private final OggettoDAO oggettoDAO = new OggettoDAO();

    // Liste per gestire immagini nuove (File) e vecchie (String path)
    private final List<File> immaginiNuove = new ArrayList<>();
    private final List<String> immaginiEsistenti = new ArrayList<>();

    private Oggetto oggettoDaModificare = null; // Se null, siamo in modalità CREAZIONE

    // =================================================================================
    // SETUP PER MODIFICA
    // =================================================================================

    /**
     * Chiama questo metodo dall'Inventario per passare in modalità MODIFICA
     */
    public void setOggettoDaModificare(Oggetto obj) {
        this.oggettoDaModificare = obj;

        // 1. Cambia UI
        if(aggiungiButton != null) aggiungiButton.setText("Salva Modifiche");
        // if(titoloPagina != null) titoloPagina.setText("Modifica Oggetto");

        // 2. Popola i campi
        nomeOggettoField.setText(obj.getNome());

        // Categoria
        if (!obj.getCategorie().isEmpty()) {
            categoriaBox.setValue(obj.getCategorie().get(0).getNome());
        }

        // Condizione
        condizioneBox.setValue(obj.getCondizione());

        // 3. Carica immagini esistenti
        if (obj.getImmagini() != null) {
            immaginiEsistenti.addAll(obj.getImmagini());
            aggiornaVisualizzazioneImmagini(); // Mostra quelle vecchie
        }

        controllaCampiValidi();
    }

    // =================================================================================
    // LOGICA DI VALIDAZIONE
    // =================================================================================

    private void controllaCampiValidi() {
        String testoNome = nomeOggettoField.getText();
        boolean nomeOk = false;
        if (testoNome != null) {
            String nomePulito = testoNome.replace(" ", "");
            if (nomePulito.length() >= 5 && testoNome.matches(Costanti.OGGETTO_FIELD_REGEX)) {
                nomeOk = true;
            }
        }

        boolean categoriaOk = categoriaBox.getValue() != null;
        boolean condizioneOk = condizioneBox.getValue() != null;

        // Ok se c'è almeno un'immagine nuova OPPURE una esistente
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

        // Mostra immagini esistenti (da String path)
        for (String path : immaginiEsistenti) {
            creaMiniatura(path, true);
        }

        // Mostra immagini nuove (da File)
        for (File file : immaginiNuove) {
            creaMiniatura(file.toURI().toString(), false);
        }
    }

    private void creaMiniatura(String imagePath, boolean isEsistente) {
        try {
            Image image;
            if(imagePath.startsWith("file:") || imagePath.startsWith("http")) {
                image = new Image(imagePath);
            } else {
                image = new Image(new File(imagePath).toURI().toString());
            }

            ImageView imageView = new ImageView(image);
            imageView.setFitHeight(150);
            imageView.setFitWidth(150);
            imageView.setPreserveRatio(true);

            Button rimuoviBtn = new Button("X");
            rimuoviBtn.getStyleClass().add("button-remove");
            rimuoviBtn.setPrefSize(20, 20);

            rimuoviBtn.setOnAction(e -> {
                if (isEsistente) {
                    immaginiEsistenti.remove(imagePath);
                } else {
                    // Cerca il file corrispondente nell'array e rimuovilo (logica semplificata)
                    immaginiNuove.removeIf(f -> f.toURI().toString().equals(imagePath));
                }
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
        Utente utenteCorrente = null;
        try {
            utenteCorrente = controllerUninaSwap.getUtente();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        Oggetto oggettoToSave = new Oggetto();
        // Se stiamo modificando, manteniamo l'ID fondamentale per la query SQL WHERE id=?
        if (oggettoDaModificare != null) {
            oggettoToSave.setId(oggettoDaModificare.getId());
        }

        oggettoToSave.setNome(nomeOggettoField.getText());
        oggettoToSave.setCondizione(condizioneBox.getValue());

        // Attenzione: se permetti di modificare la disponibilità, devi prenderla da una box.
        // Se l'interfaccia non ha il campo disponibilità, mantieni quella vecchia o metti DISPONIBILE
        if (oggettoDaModificare != null) {
            oggettoToSave.setDisponibilita(oggettoDaModificare.getDisponibilita());
        } else {
            oggettoToSave.setDisponibilita(Oggetto.DISPONIBILITA.DISPONIBILE);
        }

        ArrayList<Categoria> listaCat = new ArrayList<>();
        // Attenzione: Categoria deve essere valida (non null)
        if (categoriaBox.getValue() != null) {
            listaCat.add(new Categoria(categoriaBox.getValue()));
        }
        oggettoToSave.setCategorie(listaCat);

        // Uniamo i path: quelli vecchi rimasti + quelli nuovi convertiti in stringa
        ArrayList<String> pathsFinali = new ArrayList<>(immaginiEsistenti);
        for (File f : immaginiNuove) {
            pathsFinali.add(f.getAbsolutePath());
        }
        oggettoToSave.setImmagini(pathsFinali);

        boolean esito;
        if (oggettoDaModificare == null) {
            // --- CASO INSERIMENTO (Nuovo Oggetto) ---
            esito = oggettoDAO.salvaOggetto(oggettoToSave, utenteCorrente);
        } else {
            // --- CASO MODIFICA (Update) ---
            // PRIMA C'ERA SOLO LA SIMULAZIONE, ORA CHIAMIAMO IL DAO:
            System.out.println("Eseguo Update Reale per Oggetto ID: " + oggettoToSave.getId());

            // CHIAMA IL METODO CHE ABBIAMO CORRETTO NEL DAO
            esito = oggettoDAO.modificaOggetto(oggettoToSave);
        }

        if (esito) {
            GestoreScene gestoreScene = new GestoreScene();
            // Torna all'inventario mostrando il messaggio di successo
            gestoreScene.CambiaScena(Costanti.pathInventario, "Il Tuo Inventario", actionEvent,
                    oggettoDaModificare == null ? "Oggetto aggiunto!" : "Oggetto modificato con successo!", Messaggio.TIPI.SUCCESS);
        } else {
            System.err.println("Errore salvataggio DB");
            // Opzionale: Mostra un alert di errore all'utente qui
        }
    }
    public void onAnnullaClick(ActionEvent actionEvent) {
        GestoreScene gestoreScene = new GestoreScene();
        gestoreScene.CambiaScena(Costanti.pathInventario, "Il Tuo Inventario", actionEvent);
    }

    // =================================================================================
    // INITIALIZE
    // =================================================================================

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        controllerUninaSwap = ControllerUninaSwap.getInstance();
        if (aggiungiButton != null) aggiungiButton.setDisable(true);

        // Popola Categorie
        for (Categoria c : controllerUninaSwap.getCategorie()) {
            categoriaBox.getItems().add(c.getNome());
        }

        // Popola Condizioni
        condizioneBox.getItems().setAll(controllerUninaSwap.getCondizioni());

        // Cell factories per visualizzazione pulita
        condizioneBox.setCellFactory(lv -> new ListCell<>() {
            @Override protected void updateItem(Oggetto.CONDIZIONE item, boolean empty) {
                super.updateItem(item, empty);
                setText((empty || item == null) ? null : item.toString().replace("_", " "));
            }
        });
        condizioneBox.setButtonCell(new ListCell<>() {
            @Override protected void updateItem(Oggetto.CONDIZIONE item, boolean empty) {
                super.updateItem(item, empty);
                setText((empty || item == null) ? null : item.toString().replace("_", " "));
            }
        });

        // Listeners
        categoriaBox.valueProperty().addListener((o,old,newV) -> controllaCampiValidi());
        condizioneBox.valueProperty().addListener((o,old,newV) -> controllaCampiValidi());

        nomeOggettoField.textProperty().addListener((obs, oldV, newV) -> {
            boolean ok = newV.trim().length() >= 5 && newV.matches(Costanti.OGGETTO_FIELD_REGEX);
            if(erroreNome != null) {
                erroreNome.setVisible(!ok); erroreNome.setManaged(!ok);
                if(!ok) erroreNome.setText("Nome non valido (min 5 caratteri)");
            }
            controllaCampiValidi();
        });
    }
}