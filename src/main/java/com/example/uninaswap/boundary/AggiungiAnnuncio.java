package com.example.uninaswap.boundary;

import com.example.uninaswap.controller.ControllerUninaSwap;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import java.util.ArrayList;
import java.util.List;

public class AggiungiAnnuncio {

    // =================================================================================
    // FXML COMPONENTS
    // =================================================================================

    @FXML private TextArea descrizioneAnnuncioArea;
    @FXML private Text erroreDescrizione;

    // --- NUOVO CAMPO SEDE ---
    @FXML private ComboBox<String> sedeBox;
    @FXML private Text erroreSede;

    @FXML private VBox contenitoreOggetti;
    @FXML private Text erroreOggetti;

    @FXML private ToggleGroup tipoAnnuncioGroup;
    @FXML private RadioButton radioVendita;
    @FXML private RadioButton radioScambio;
    @FXML private RadioButton radioRegalo;
    @FXML private Text erroreTipologia;

    @FXML private VBox vboxVendita;
    @FXML private TextField prezzoField;
    @FXML private TextField prezzoMinField;
    @FXML private Text errorePrezzo;

    @FXML private VBox vboxScambio;
    @FXML private TextArea desideriScambioArea;
    @FXML private Text erroreScambio;

    @FXML private VBox vboxRegalo;
    @FXML private TextField infoRitiroField;

    @FXML private Button annullaButton;
    @FXML private Button pubblicaButton;

    // Proprietà per monitorare lo stato degli oggetti selezionati
    private final BooleanProperty almenoUnOggettoSelezionato = new SimpleBooleanProperty(false);

    // Regex semplice per prezzo
    private static final String PRICE_REGEX = "^[0-9]+([.,][0-9]{1,2})?$";

    // =================================================================================
    // INITIALIZATION
    // =================================================================================

    @FXML
    public void initialize() {
        caricaSedi();
        caricaInventarioUtente();
        setupValidazioneRealTime();
    }

    /**
     * Carica le sedi disponibili (Simulazione DB)
     */
    private void caricaSedi() {
        // TODO: Recuperare dal DB\
        ControllerUninaSwap controllerUninaSwap = ControllerUninaSwap.getInstance();
        List<String> sedi = List.of("Monte Sant'Angelo", "Piazzale Tecchio", "Via Claudio", "Corso Umberto", "Policlinico");
        sedeBox.getItems().addAll(sedi);
    }

    /**
     * Recupera gli oggetti dell'utente e popola dinamicamente la lista di CheckBox.
     */
    private void caricaInventarioUtente() {
        // TODO: Recuperare la lista reale degli oggetti dell'utente dal DB
        List<String> oggettiMock = List.of("Libro Analisi 1", "Calcolatrice", "Appunti Fisica");

        contenitoreOggetti.getChildren().clear();

        if (oggettiMock.isEmpty()) {
            contenitoreOggetti.getChildren().add(new Text("Nessun oggetto nell'inventario."));
            return;
        }

        for (String nomeOggetto : oggettiMock) {
            CheckBox cb = new CheckBox(nomeOggetto);
            cb.setStyle("-fx-font-size: 14px; -fx-text-fill: #333;");

            // Aggiungo listener: ogni volta che clicco un box, ricalcolo se c'è almeno un oggetto selezionato
            cb.selectedProperty().addListener((obs, oldVal, newVal) -> aggiornaStatoOggetti());

            contenitoreOggetti.getChildren().add(cb);
        }
    }

    /**
     * Configura i Binding e i Listener per la validazione in tempo reale.
     */
    private void setupValidazioneRealTime() {

        // 1. Definisco le condizioni di validità
        BooleanBinding descrizioneValida = Bindings.createBooleanBinding(() -> {
            String txt = descrizioneAnnuncioArea.getText();
            return txt != null && !txt.trim().isEmpty();
        }, descrizioneAnnuncioArea.textProperty());

        // Validazione Sede: deve essere selezionato un valore
        BooleanBinding sedeValida = sedeBox.valueProperty().isNotNull();

        BooleanBinding prezzoValido = Bindings.createBooleanBinding(() -> {
            String txt = prezzoField.getText();
            return txt != null && txt.matches(PRICE_REGEX);
        }, prezzoField.textProperty());

        BooleanBinding scambioValido = Bindings.createBooleanBinding(() -> {
            String txt = desideriScambioArea.getText();
            return txt != null && !txt.trim().isEmpty();
        }, desideriScambioArea.textProperty());

        BooleanBinding tipologiaSelezionata = radioVendita.selectedProperty()
                .or(radioScambio.selectedProperty())
                .or(radioRegalo.selectedProperty());

        BooleanBinding sezioneSpecificaValida = Bindings.createBooleanBinding(() -> {
            if (radioVendita.isSelected()) return prezzoValido.get();
            if (radioScambio.isSelected()) return scambioValido.get();
            if (radioRegalo.isSelected()) return true;
            return false;
        }, radioVendita.selectedProperty(), radioScambio.selectedProperty(), radioRegalo.selectedProperty(), prezzoValido, scambioValido);

        // 2. Listener per GUI (Errori visivi)

        descrizioneAnnuncioArea.textProperty().addListener((obs, oldVal, newVal) ->
                gestisciErroreInput(descrizioneAnnuncioArea, erroreDescrizione, !newVal.trim().isEmpty())
        );

        // Listener Sede
        sedeBox.valueProperty().addListener((obs, oldVal, newVal) -> {
            boolean isValid = newVal != null;
            if (isValid) {
                sedeBox.getStyleClass().remove("error");
                sedeBox.getStyleClass().add("right"); // opzionale se hai stile right per combobox
                erroreSede.setVisible(false);
                erroreSede.setManaged(false);
            } else {
                sedeBox.getStyleClass().add("error");
                erroreSede.setVisible(true);
                erroreSede.setManaged(true);
            }
        });

        almenoUnOggettoSelezionato.addListener((obs, oldVal, newVal) -> {
            if (!newVal) {
                erroreOggetti.setVisible(true); erroreOggetti.setManaged(true);
            } else {
                erroreOggetti.setVisible(false); erroreOggetti.setManaged(false);
            }
        });

        prezzoField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (radioVendita.isSelected()) {
                gestisciErroreInput(prezzoField, errorePrezzo, newVal.matches(PRICE_REGEX));
            }
        });

        desideriScambioArea.textProperty().addListener((obs, oldVal, newVal) -> {
            if (radioScambio.isSelected()) {
                gestisciErroreInput(desideriScambioArea, erroreScambio, !newVal.trim().isEmpty());
            }
        });

        // 3. Binding Finale sul Bottone
        pubblicaButton.disableProperty().bind(
                descrizioneValida.not()
                        .or(sedeValida.not())  // Aggiunto controllo sede
                        .or(almenoUnOggettoSelezionato.not())
                        .or(tipologiaSelezionata.not())
                        .or(sezioneSpecificaValida.not())
        );
    }

    // =================================================================================
    // EVENT HANDLERS
    // =================================================================================

    @FXML
    void onTipologiaChange(ActionEvent event) {
        nascondiBox(vboxVendita);
        nascondiBox(vboxScambio);
        nascondiBox(vboxRegalo);

        resetStiliCampo(prezzoField, errorePrezzo);
        resetStiliCampo(desideriScambioArea, erroreScambio);

        if (radioVendita.isSelected()) {
            mostraBox(vboxVendita);
            gestisciErroreInput(prezzoField, errorePrezzo, prezzoField.getText().matches(PRICE_REGEX));
        } else if (radioScambio.isSelected()) {
            mostraBox(vboxScambio);
            gestisciErroreInput(desideriScambioArea, erroreScambio, !desideriScambioArea.getText().trim().isEmpty());
        } else if (radioRegalo.isSelected()) {
            mostraBox(vboxRegalo);
        }

        erroreTipologia.setVisible(false);
        erroreTipologia.setManaged(false);
    }

    @FXML
    public void onPubblicaClick(ActionEvent actionEvent) {
        System.out.println("Annuncio pubblicato!");
        System.out.println("Sede: " + sedeBox.getValue());
        System.out.println("Oggetti: " + getOggettiSelezionati());
        // TODO: Salvare su DB
    }

    @FXML
    public void onAnnullaClick(ActionEvent actionEvent) {
        System.out.println("Annulla");
        // TODO: Go Home
    }

    // =================================================================================
    // HELPER METHODS
    // =================================================================================

    private void gestisciErroreInput(Control field, Text erroreText, boolean isValido) {
        String testo = (field instanceof TextInputControl) ? ((TextInputControl) field).getText() : "";
        boolean isVuoto = testo == null || testo.trim().isEmpty();

        if (isValido) {
            field.getStyleClass().remove("error");
            if (!field.getStyleClass().contains("right")) field.getStyleClass().add("right");
            erroreText.setVisible(false); erroreText.setManaged(false);
        } else if (isVuoto) {
            field.getStyleClass().remove("error"); field.getStyleClass().remove("right");
            erroreText.setVisible(false); erroreText.setManaged(false);
        } else {
            field.getStyleClass().remove("right");
            if (!field.getStyleClass().contains("error")) field.getStyleClass().add("error");
            erroreText.setVisible(true); erroreText.setManaged(true);
        }
    }

    private void resetStiliCampo(Control field, Text erroreText) {
        field.getStyleClass().remove("error");
        field.getStyleClass().remove("right");
        erroreText.setVisible(false);
        erroreText.setManaged(false);
    }

    private void aggiornaStatoOggetti() {
        boolean almenoUno = false;
        for (Node node : contenitoreOggetti.getChildren()) {
            if (node instanceof CheckBox cb && cb.isSelected()) {
                almenoUno = true;
                break;
            }
        }
        almenoUnOggettoSelezionato.set(almenoUno);
    }

    private void nascondiBox(VBox box) { box.setVisible(false); box.setManaged(false); }
    private void mostraBox(VBox box) { box.setVisible(true); box.setManaged(true); }

    private List<String> getOggettiSelezionati() {
        List<String> selected = new ArrayList<>();
        for (Node node : contenitoreOggetti.getChildren()) {
            if (node instanceof CheckBox cb && cb.isSelected()) selected.add(cb.getText());
        }
        return selected;
    }
}