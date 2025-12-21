package com.example.uninaswap.boundary;

import com.example.uninaswap.controller.ControllerUninaSwap;
import com.example.uninaswap.entity.Sede;
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
import java.util.List;

public class AggiungiAnnuncio {

    // =================================================================================
    // FXML COMPONENTS
    // =================================================================================

    @FXML private TextArea descrizioneAnnuncioArea;
    @FXML private Text erroreDescrizione;

    @FXML private ComboBox<String> sedeBox;
    @FXML private Text erroreSede;

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

    // Proprietà
    private final BooleanProperty almenoUnOggettoSelezionato = new SimpleBooleanProperty(false);
    private final BooleanProperty prezziValidiProperty = new SimpleBooleanProperty(false);
    private final BooleanProperty orariValidiProperty = new SimpleBooleanProperty(false);

    // Regex rigorosa: Obbliga HH:mm (es. 09:00, 23:59). Niente "92284".
    private static final String TIME_REGEX = "^([01]?[0-9]|2[0-3]):[0-5][0-9]$";
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

    private void caricaSedi() {
        ControllerUninaSwap controllerUninaSwap = ControllerUninaSwap.getInstance();
        List<Sede> sedi = controllerUninaSwap.getSedi();
        for (Sede sede : sedi) {
            sedeBox.getItems().add(sede.getNomeSede());
        }
    }

    private void caricaInventarioUtente() {
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

        // 1. Binding Semplici
        BooleanBinding descrizioneValida = Bindings.createBooleanBinding(() -> {
            String txt = descrizioneAnnuncioArea.getText();
            return txt != null && !txt.trim().isEmpty();
        }, descrizioneAnnuncioArea.textProperty());

        BooleanBinding sedeValida = sedeBox.valueProperty().isNotNull();

        BooleanBinding scambioValido = Bindings.createBooleanBinding(() -> {
            String txt = desideriScambioArea.getText();
            return txt != null && !txt.trim().isEmpty();
        }, desideriScambioArea.textProperty());

        BooleanBinding tipologiaSelezionata = radioVendita.selectedProperty()
                .or(radioScambio.selectedProperty())
                .or(radioRegalo.selectedProperty());

        // 2. LISTENERS REAL-TIME

        // Descrizione
        descrizioneAnnuncioArea.textProperty().addListener((obs, oldVal, newVal) ->
                gestisciErroreGenerico(descrizioneAnnuncioArea, erroreDescrizione, !newVal.trim().isEmpty())
        );

        // Sede
        sedeBox.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                sedeBox.getStyleClass().remove("error");
                erroreSede.setVisible(false); erroreSede.setManaged(false);
            } else {
                sedeBox.getStyleClass().add("error");
                erroreSede.setVisible(true); erroreSede.setManaged(true);
            }
        });

        // --- VALIDAZIONE ORARI ---
        orarioInizioField.textProperty().addListener((obs, oldVal, newVal) -> validaOrari());
        orarioFineField.textProperty().addListener((obs, oldVal, newVal) -> validaOrari());

        // Oggetti
        almenoUnOggettoSelezionato.addListener((obs, oldVal, newVal) -> {
            if (!newVal) {
                erroreOggetti.setVisible(true); erroreOggetti.setManaged(true);
            } else {
                erroreOggetti.setVisible(false); erroreOggetti.setManaged(false);
            }
        });

        // Prezzi
        prezzoField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (radioVendita.isSelected()) validaPrezzi();
        });
        prezzoMinField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (radioVendita.isSelected()) validaPrezzi();
        });

        // Scambio
        desideriScambioArea.textProperty().addListener((obs, oldVal, newVal) -> {
            if (radioScambio.isSelected()) gestisciErroreGenerico(desideriScambioArea, erroreScambio, !newVal.trim().isEmpty());
        });

        // 3. LOGICA FINALE BOTTONE PUBBLICA
        BooleanBinding sezioneSpecificaValida = Bindings.createBooleanBinding(() -> {
            if (radioVendita.isSelected()) return prezziValidiProperty.get();
            if (radioScambio.isSelected()) return scambioValido.get();
            if (radioRegalo.isSelected()) return true;
            return false;
        }, radioVendita.selectedProperty(), radioScambio.selectedProperty(), radioRegalo.selectedProperty(), prezziValidiProperty, scambioValido);

        pubblicaButton.disableProperty().bind(
                descrizioneValida.not()
                        .or(sedeValida.not())
                        .or(orariValidiProperty.not()) // Il bottone si disabilita se la validazione fallisce
                        .or(almenoUnOggettoSelezionato.not())
                        .or(tipologiaSelezionata.not())
                        .or(sezioneSpecificaValida.not())
        );
    }

    /**
     * Valida i campi orario carattere per carattere.
     * Gestisce errori di Formato (es. 92284) e Logici (Start > End).
     */
    private void validaOrari() {
        String inizio = orarioInizioField.getText();
        String fine = orarioFineField.getText();

        boolean inizioVuoto = (inizio == null || inizio.trim().isEmpty());
        boolean fineVuota = (fine == null || fine.trim().isEmpty());

        boolean erroreRilevato = false;

        // 1. Controllo Sintassi Inizio
        if (!inizioVuoto) {
            if (!inizio.matches(TIME_REGEX)) {
                impostaStile(orarioInizioField, false); // Rosso subito (es. "92284")
                erroreRilevato = true;
            } else {
                impostaStile(orarioInizioField, true); // Verde
            }
        } else {
            resetStiliCampo(orarioInizioField, null);
        }

        // 2. Controllo Sintassi Fine
        if (!fineVuota) {
            if (!fine.matches(TIME_REGEX)) {
                impostaStile(orarioFineField, false); // Rosso subito
                erroreRilevato = true;
            } else {
                impostaStile(orarioFineField, true); // Verde
            }
        } else {
            resetStiliCampo(orarioFineField, null);
        }

        // 3. Controllo Logico (Solo se entrambi sono sintatticamente validi e pieni)
        if (!erroreRilevato && !inizioVuoto && !fineVuota) {
            try {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
                LocalTime tInizio = LocalTime.parse(inizio, formatter);
                LocalTime tFine = LocalTime.parse(fine, formatter);

                if (tInizio.isAfter(tFine)) {
                    // Errore logico: Inizio dopo Fine
                    impostaStile(orarioInizioField, false);
                    impostaStile(orarioFineField, false);
                    erroreRilevato = true;
                } else {
                    // Tutto perfetto
                    orariValidiProperty.set(true);
                }
            } catch (DateTimeParseException e) {
                // Caso limite non catturato dalla regex
                erroreRilevato = true;
            }
        }

        // 4. Aggiornamento UI Finale
        if (erroreRilevato) {
            erroreOrario.setVisible(true);
            erroreOrario.setManaged(true);
            orariValidiProperty.set(false);
        } else if (inizioVuoto || fineVuota) {
            // Se uno manca, non è errore ma non è nemmeno "valido" per pubblicare
            erroreOrario.setVisible(false);
            erroreOrario.setManaged(false);
            orariValidiProperty.set(false);
        } else {
            // Tutto pieno e valido
            erroreOrario.setVisible(false);
            erroreOrario.setManaged(false);
            orariValidiProperty.set(true);
        }
    }

    private void validaPrezzi() {
        String pRichiestoStr = prezzoField.getText();
        String pMinStr = prezzoMinField.getText();
        boolean pRichiestoSintassiOk = pRichiestoStr != null && pRichiestoStr.matches(PRICE_REGEX);
        boolean pMinSintassiOk = pMinStr == null || pMinStr.isEmpty() || pMinStr.matches(PRICE_REGEX);

        boolean logicaOk = true;
        String messaggioErrore = "Inserire un prezzo valido";

        if (!pRichiestoSintassiOk) { logicaOk = false; impostaStile(prezzoField, false); }
        else { impostaStile(prezzoField, true); }

        if (!pMinSintassiOk) { logicaOk = false; impostaStile(prezzoMinField, false); }
        else {
            if (pMinStr == null || pMinStr.isEmpty()) {
                prezzoMinField.getStyleClass().remove("error"); prezzoMinField.getStyleClass().remove("right");
            } else {
                impostaStile(prezzoMinField, true);
            }
        }

        if (pRichiestoSintassiOk && pMinSintassiOk && pMinStr != null && !pMinStr.isEmpty()) {
            try {
                double valRichiesto = Double.parseDouble(pRichiestoStr.replace(",", "."));
                double valMin = Double.parseDouble(pMinStr.replace(",", "."));
                if (valMin > valRichiesto) {
                    logicaOk = false;
                    messaggioErrore = "Il prezzo minimo non può essere superiore a quello richiesto";
                    prezzoMinField.getStyleClass().remove("right");
                    if (!prezzoMinField.getStyleClass().contains("error")) prezzoMinField.getStyleClass().add("error");
                }
            } catch (NumberFormatException e) { logicaOk = false; }
        }

        if (logicaOk) {
            errorePrezzo.setVisible(false); errorePrezzo.setManaged(false); prezziValidiProperty.set(true);
        } else {
            errorePrezzo.setText(messaggioErrore);
            errorePrezzo.setVisible(true); errorePrezzo.setManaged(true);
            prezziValidiProperty.set(false);
        }
    }

    // =================================================================================
    // EVENT HANDLERS & HELPER METHODS
    // =================================================================================

    @FXML
    void onTipologiaChange(ActionEvent event) {
        nascondiBox(vboxVendita); nascondiBox(vboxScambio); nascondiBox(vboxRegalo);
        resetStiliCampo(prezzoField, errorePrezzo);
        resetStiliCampo(prezzoMinField, null);
        resetStiliCampo(desideriScambioArea, erroreScambio);

        if (radioVendita.isSelected()) {
            mostraBox(vboxVendita); validaPrezzi();
        } else if (radioScambio.isSelected()) {
            mostraBox(vboxScambio);
            gestisciErroreGenerico(desideriScambioArea, erroreScambio, !desideriScambioArea.getText().trim().isEmpty());
        } else if (radioRegalo.isSelected()) {
            mostraBox(vboxRegalo);
        }
        erroreTipologia.setVisible(false); erroreTipologia.setManaged(false);
    }

    @FXML public void onPubblicaClick(ActionEvent actionEvent) {
        System.out.println("Annuncio pubblicato!");
    }
    @FXML public void onAnnullaClick(ActionEvent actionEvent) {
        System.out.println("Annulla");
    }

    private void impostaStile(Control field, boolean isValido) {
        if (isValido) {
            field.getStyleClass().remove("error");
            if (!field.getStyleClass().contains("right")) field.getStyleClass().add("right");
        } else {
            field.getStyleClass().remove("right");
            if (!field.getStyleClass().contains("error")) field.getStyleClass().add("error");
        }
    }

    private void gestisciErroreGenerico(Control field, Text erroreText, boolean isValido) {
        String testo = (field instanceof TextInputControl) ? ((TextInputControl) field).getText() : "";
        boolean isVuoto = testo == null || testo.trim().isEmpty();

        if (isValido) {
            impostaStile(field, true);
            erroreText.setVisible(false); erroreText.setManaged(false);
        } else if (isVuoto) {
            field.getStyleClass().remove("error"); field.getStyleClass().remove("right");
            erroreText.setVisible(false); erroreText.setManaged(false);
        } else {
            impostaStile(field, false);
            erroreText.setVisible(true); erroreText.setManaged(true);
        }
    }

    private void resetStiliCampo(Control field, Text erroreText) {
        field.getStyleClass().remove("error"); field.getStyleClass().remove("right");
        if(erroreText != null) { erroreText.setVisible(false); erroreText.setManaged(false); }
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
}