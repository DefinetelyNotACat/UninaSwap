package com.example.uninaswap.boundary;

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

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

public class AggiungiAnnuncio {

    // =================================================================================
    // FXML COMPONENTS
    // =================================================================================

    @FXML private TextArea descrizioneAnnuncioArea;
    @FXML private Text erroreDescrizione;

    @FXML private ComboBox<String> sedeBox;
    @FXML private Text erroreSede;

    // --- NUOVO: ORARI ---
    @FXML private TextField orarioInizioField;
    @FXML private TextField orarioFineField;
    @FXML private Text erroreOrario;

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

    // Proprietà e Costanti
    private final BooleanProperty almenoUnOggettoSelezionato = new SimpleBooleanProperty(false);
    private static final String PRICE_REGEX = "^[0-9]+([.,][0-9]{1,2})?$";
    private static final String TIME_REGEX = "^([01]?[0-9]|2[0-3]):[0-5][0-9]$"; // HH:mm format

    // =================================================================================
    // INITIALIZATION
    // =================================================================================

    @FXML
    public void initialize() {
        caricaSedi();
        caricaInventarioUtente();
        setupValidazioneRealTime();
    }

    private void caricaSedi() {
        // TODO: Recuperare dal DB
        List<String> sedi = List.of("Monte Sant'Angelo", "Piazzale Tecchio", "Via Claudio", "Corso Umberto");
        sedeBox.getItems().addAll(sedi);
    }

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

        BooleanBinding sedeValida = sedeBox.valueProperty().isNotNull();

        // VALIDAZIONE ORARI COMPLESSA (Regex + Logica Temporale)
        BooleanBinding orariValidi = Bindings.createBooleanBinding(() -> {
            String inizio = orarioInizioField.getText();
            String fine = orarioFineField.getText();

            // Check Regex base
            if (inizio == null || !inizio.matches(TIME_REGEX) || fine == null || !fine.matches(TIME_REGEX)) {
                return false;
            }

            // Check Logico (Inizio <= Fine)
            try {
                LocalTime tInizio = LocalTime.parse(inizio, DateTimeFormatter.ofPattern("HH:mm"));
                LocalTime tFine = LocalTime.parse(fine, DateTimeFormatter.ofPattern("HH:mm"));
                return !tInizio.isAfter(tFine); // Restituisce true se Inizio <= Fine
            } catch (DateTimeParseException e) {
                return false;
            }

        }, orarioInizioField.textProperty(), orarioFineField.textProperty());


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

        sedeBox.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                sedeBox.getStyleClass().remove("error"); erroreSede.setVisible(false); erroreSede.setManaged(false);
            } else {
                sedeBox.getStyleClass().add("error"); erroreSede.setVisible(true); erroreSede.setManaged(true);
            }
        });

        // Listener combinato per gli orari
        orariValidi.addListener((obs, oldVal, newVal) -> {
            if (newVal) {
                // Tutto ok
                orarioInizioField.getStyleClass().remove("error");
                orarioFineField.getStyleClass().remove("error");
                erroreOrario.setVisible(false); erroreOrario.setManaged(false);
            } else {
                // Errore (mostro solo se entrambi i campi sono compilati parzialmente o errati)
                String in = orarioInizioField.getText();
                String fi = orarioFineField.getText();
                if(in != null && !in.isEmpty() && fi != null && !fi.isEmpty()) {
                    orarioInizioField.getStyleClass().add("error");
                    orarioFineField.getStyleClass().add("error");
                    erroreOrario.setVisible(true); erroreOrario.setManaged(true);
                }
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
            if (radioVendita.isSelected()) gestisciErroreInput(prezzoField, errorePrezzo, newVal.matches(PRICE_REGEX));
        });

        desideriScambioArea.textProperty().addListener((obs, oldVal, newVal) -> {
            if (radioScambio.isSelected()) gestisciErroreInput(desideriScambioArea, erroreScambio, !newVal.trim().isEmpty());
        });

        // 3. Binding Finale sul Bottone
        pubblicaButton.disableProperty().bind(
                descrizioneValida.not()
                        .or(sedeValida.not())
                        .or(orariValidi.not()) // Aggiunto controllo orari
                        .or(almenoUnOggettoSelezionato.not())
                        .or(tipologiaSelezionata.not())
                        .or(sezioneSpecificaValida.not())
        );
    }

    // =================================================================================
    // EVENT HANDLERS & HELPER METHODS
    // =================================================================================

    @FXML
    void onTipologiaChange(ActionEvent event) {
        nascondiBox(vboxVendita); nascondiBox(vboxScambio); nascondiBox(vboxRegalo);
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
        erroreTipologia.setVisible(false); erroreTipologia.setManaged(false);
    }

    @FXML
    public void onPubblicaClick(ActionEvent actionEvent) {
        System.out.println("Annuncio pubblicato!");
        System.out.println("Orario: " + orarioInizioField.getText() + " - " + orarioFineField.getText());
        // TODO: Salvare su DB
    }

    @FXML
    public void onAnnullaClick(ActionEvent actionEvent) {
        System.out.println("Annulla");
        // TODO: Go Home
    }

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
        field.getStyleClass().remove("error"); field.getStyleClass().remove("right");
        erroreText.setVisible(false); erroreText.setManaged(false);
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