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
    @FXML private ComboBox<String> condizioneBox;
    @FXML private HBox contenitoreImmagini;
    @FXML private Button caricaFotoButton;
    @FXML private Text erroreImmagini; // Utile per nascondere l'errore se si aggiungono foto

    // --- MODIFICA 1: Colleghiamo il bottone dal FXML ---
    @FXML private Button aggiungiButton;

    private ControllerUninaSwap controllerUninaSwap;
    private final OggettoDAO oggettoDAO = new OggettoDAO();
    private final List<File> immaginiSelezionate = new ArrayList<>();

    // --- MODIFICA 2: Metodo centrale per verificare validità ---
    private void controllaCampiValidi() {
        // 1. Validazione Nome
        String testoNome = nomeOggettoField.getText();
        boolean nomeOk = false;
        if (testoNome != null) {
            String nomePulito = testoNome.replace(" ", "");
            // Lunghezza >= 5 E Regex (lettere/numeri)
            if (nomePulito.length() >= 5 && testoNome.matches(Costanti.OGGETTO_FIELD_REGEX)) {
                nomeOk = true;
            }
        }

        // 2. Validazione Categoria e Condizione
        boolean categoriaOk = categoriaBox.getValue() != null;
        boolean condizioneOk = condizioneBox.getValue() != null;

        // 3. Validazione Immagini (Almeno una)
        boolean immaginiOk = !immaginiSelezionate.isEmpty();

        // Aggiorna visibilità errore immagini (opzionale ma carino)
        if (erroreImmagini != null) {
            erroreImmagini.setVisible(!immaginiOk);
            erroreImmagini.setManaged(!immaginiOk);
        }

        // 4. Abilita/Disabilita il bottone
        // Il bottone è DISABILITATO se NON sono tutti ok
        if (aggiungiButton != null) {
            aggiungiButton.setDisable(!(nomeOk && categoriaOk && condizioneOk && immaginiOk));
        }
    }

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
            // --- MODIFICA 3: Ricontrolla i campi dopo aver aggiunto foto ---
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
                // --- MODIFICA 3: Ricontrolla i campi dopo aver rimosso foto ---
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

    public void onAnnullaClick(ActionEvent actionEvent) {
        GestoreScene gestoreScene = new GestoreScene();
        gestoreScene.CambiaScena(Costanti.pathHomePage, Costanti.homepage, actionEvent, "Annullamento aggiungimento prodotto", Messaggio.TIPI.INFO);
    }

    public void onPubblicaClick(ActionEvent actionEvent) {

        String nome = nomeOggettoField.getText();
        String nomeCategoria = categoriaBox.getValue();
        String nomeCondizione = condizioneBox.getValue();

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

        try {
            Oggetto.CONDIZIONE condizioneEnum = Oggetto.CONDIZIONE.fromString(nomeCondizione);
            if (condizioneEnum == null) {
                condizioneEnum = Oggetto.CONDIZIONE.valueOf(nomeCondizione.replace(" ", "_").toUpperCase());
            }
            nuovoOggetto.setCondizione(condizioneEnum);
        } catch (Exception e) {
            System.err.println("Errore conversione condizione");
            return;
        }

        ArrayList<Categoria> listaCategorie = new ArrayList<>();
        listaCategorie.add(new Categoria(nomeCategoria));
        nuovoOggetto.setCategorie(listaCategorie);

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

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        controllerUninaSwap = ControllerUninaSwap.getInstance();

        // Disabilita il bottone all'avvio
        if (aggiungiButton != null) {
            aggiungiButton.setDisable(true);
        }

        ArrayList<Categoria> categorie = controllerUninaSwap.getCategorie();
        ArrayList<String> condizioni = controllerUninaSwap.getCondizioni();

        for (Categoria categoria : categorie) {
            categoriaBox.getItems().add(categoria.getNome());
        }

        categoriaBox.setCellFactory(lv -> new ListCell<String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setCursor(Cursor.DEFAULT);
                } else {
                    setText(item);
                    setCursor(Cursor.HAND);
                }
            }
        });
        categoriaBox.setCursor(Cursor.HAND);
        // Listener  categoria
        categoriaBox.valueProperty().addListener((obs, oldVal, newVal) -> controllaCampiValidi());


        for (String condizione : condizioni) {
            condizioneBox.getItems().add(condizione);
        }
        condizioneBox.setCursor(Cursor.HAND);
        // Listener condizione
        condizioneBox.valueProperty().addListener((obs, oldVal, newVal) -> controllaCampiValidi());


        if (nomeOggettoField != null) {
            nomeOggettoField.textProperty().addListener((observable, oldValue, newValue) -> {
                if (erroreNome != null) {
                    erroreNome.setVisible(false);
                    erroreNome.setManaged(false);
                }
                boolean lunghezzaOk = newValue.replace(" ", "").length() >= 5;
                if (!newValue.matches(Costanti.OGGETTO_FIELD_REGEX) || !lunghezzaOk) {
                    erroreNome.setText("Errore! inserire un nome che sia di almeno 5 lettere (spazi esclusi) senza caratteri speciali");
                    erroreNome.setManaged(true);
                    erroreNome.setVisible(true);
                }
                controllaCampiValidi();
            });
        }
    }
}