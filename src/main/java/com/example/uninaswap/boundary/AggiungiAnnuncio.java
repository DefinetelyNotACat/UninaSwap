package com.example.uninaswap.boundary;

import com.example.uninaswap.controller.ControllerUninaSwap;
import com.example.uninaswap.entity.Oggetto;
import com.example.uninaswap.entity.Sede;
import com.example.uninaswap.entity.Utente;
import com.example.uninaswap.entity.Annuncio; // Assicurati di avere l'import per l'entity Annuncio
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

    // Proprietà per la validazione
    private final BooleanProperty almenoUnOggettoSelezionato = new SimpleBooleanProperty(false);
    private final BooleanProperty prezziValidiProperty = new SimpleBooleanProperty(false);
    private final BooleanProperty orariValidiProperty = new SimpleBooleanProperty(false);

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
        ControllerUninaSwap controller = ControllerUninaSwap.getInstance();
        List<Sede> sedi = controller.getSedi();
        if (sedi != null) {
            for (Sede sede : sedi) {
                sedeBox.getItems().add(sede.getNomeSede());
            }
        }
    }

    private void caricaInventarioUtente() {
        // 1. Pulizia preventiva del contenitore
        contenitoreOggetti.getChildren().clear();

        ControllerUninaSwap controller = ControllerUninaSwap.getInstance();
        try {
            Utente utenteCorrente = controller.getUtente();
            // Allineato al nome metodo del tuo Controller
            List<Oggetto> oggettiReali = controller.OttieniOggetti(utenteCorrente);

            if (oggettiReali == null || oggettiReali.isEmpty()) {
                Text vuoto = new Text("Nessun oggetto disponibile nel tuo inventario.");
                vuoto.getStyleClass().add("placeholder-text");
                contenitoreOggetti.getChildren().add(vuoto);
                return;
            }

            // 2. Popolamento con dati reali
            for (Oggetto obj : oggettiReali) {
                CheckBox cb = new CheckBox(obj.getNome());
                cb.setUserData(obj); // Salva l'oggetto per recuperarlo in fase di pubblicazione
                cb.setStyle("-fx-font-size: 14px; -fx-text-fill: #333;");
                cb.selectedProperty().addListener((obs, oldVal, newVal) -> aggiornaStatoOggetti());

                contenitoreOggetti.getChildren().add(cb);
            }
        } catch (Exception e) {
            System.err.println("Errore caricamento oggetti: " + e.getMessage());
            contenitoreOggetti.getChildren().add(new Text("Errore nel caricamento dell'inventario."));
        }

        // RIMOSSA LA SEZIONE MOCK CHE SOVRASCRIVEVA TUTTO
    }

    /**
     * Configura i Binding e i Listener per la validazione in tempo reale.
     */
    private void setupValidazioneRealTime() {

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

        // Listeners Real-Time
        descrizioneAnnuncioArea.textProperty().addListener((obs, oldVal, newVal) ->
                gestisciErroreGenerico(descrizioneAnnuncioArea, erroreDescrizione, !newVal.trim().isEmpty())
        );

        sedeBox.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                sedeBox.getStyleClass().remove("error");
                erroreSede.setVisible(false); erroreSede.setManaged(false);
            } else {
                sedeBox.getStyleClass().add("error");
                erroreSede.setVisible(true); erroreSede.setManaged(true);
            }
        });

        orarioInizioField.textProperty().addListener((obs, oldVal, newVal) -> validaOrari());
        orarioFineField.textProperty().addListener((obs, oldVal, newVal) -> validaOrari());

        almenoUnOggettoSelezionato.addListener((obs, oldVal, newVal) -> {
            erroreOggetti.setVisible(!newVal);
            erroreOggetti.setManaged(!newVal);
        });

        prezzoField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (radioVendita.isSelected()) validaPrezzi();
        });
        prezzoMinField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (radioVendita.isSelected()) validaPrezzi();
        });

        desideriScambioArea.textProperty().addListener((obs, oldVal, newVal) -> {
            if (radioScambio.isSelected()) gestisciErroreGenerico(desideriScambioArea, erroreScambio, !newVal.trim().isEmpty());
        });

        // Logica finale pulsante
        BooleanBinding sezioneSpecificaValida = Bindings.createBooleanBinding(() -> {
            if (radioVendita.isSelected()) return prezziValidiProperty.get();
            if (radioScambio.isSelected()) return scambioValido.get();
            if (radioRegalo.isSelected()) return true;
            return false;
        }, radioVendita.selectedProperty(), radioScambio.selectedProperty(), radioRegalo.selectedProperty(), prezziValidiProperty, scambioValido);

        pubblicaButton.disableProperty().bind(
                descrizioneValida.not()
                        .or(sedeValida.not())
                        .or(orariValidiProperty.not())
                        .or(almenoUnOggettoSelezionato.not())
                        .or(tipologiaSelezionata.not())
                        .or(sezioneSpecificaValida.not())
        );
    }

    // =================================================================================
    // VALIDATION METHODS
    // =================================================================================

    private void validaOrari() {
        String inizio = orarioInizioField.getText();
        String fine = orarioFineField.getText();

        boolean inizioVuoto = (inizio == null || inizio.trim().isEmpty());
        boolean fineVuota = (fine == null || fine.trim().isEmpty());
        boolean erroreRilevato = false;

        if (!inizioVuoto) {
            impostaStile(orarioInizioField, inizio.matches(TIME_REGEX));
            if (!inizio.matches(TIME_REGEX)) erroreRilevato = true;
        } else resetStiliCampo(orarioInizioField, null);

        if (!fineVuota) {
            impostaStile(orarioFineField, fine.matches(TIME_REGEX));
            if (!fine.matches(TIME_REGEX)) erroreRilevato = true;
        } else resetStiliCampo(orarioFineField, null);

        if (!erroreRilevato && !inizioVuoto && !fineVuota) {
            try {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
                LocalTime tInizio = LocalTime.parse(inizio, formatter);
                LocalTime tFine = LocalTime.parse(fine, formatter);

                if (tInizio.isAfter(tFine)) {
                    impostaStile(orarioInizioField, false);
                    impostaStile(orarioFineField, false);
                    erroreRilevato = true;
                } else {
                    orariValidiProperty.set(true);
                }
            } catch (DateTimeParseException e) {
                erroreRilevato = true;
            }
        }

        erroreOrario.setVisible(erroreRilevato);
        erroreOrario.setManaged(erroreRilevato);
        orariValidiProperty.set(!erroreRilevato && !inizioVuoto && !fineVuota);
    }

    private void validaPrezzi() {
        String pRichiestoStr = prezzoField.getText();
        String pMinStr = prezzoMinField.getText();
        boolean pRichiestoSintassiOk = pRichiestoStr != null && pRichiestoStr.matches(PRICE_REGEX);
        boolean pMinSintassiOk = pMinStr == null || pMinStr.isEmpty() || pMinStr.matches(PRICE_REGEX);

        boolean logicaOk = true;
        String messaggioErrore = "Inserire un prezzo valido";

        impostaStile(prezzoField, pRichiestoSintassiOk);
        if (!pRichiestoSintassiOk) logicaOk = false;

        if (pMinSintassiOk && (pMinStr != null && !pMinStr.isEmpty())) {
            impostaStile(prezzoMinField, true);
        } else if (pMinStr != null && !pMinStr.isEmpty()) {
            impostaStile(prezzoMinField, false);
            logicaOk = false;
        } else {
            resetStiliCampo(prezzoMinField, null);
        }

        if (logicaOk && pRichiestoSintassiOk && pMinSintassiOk && pMinStr != null && !pMinStr.isEmpty()) {
            try {
                double valRichiesto = Double.parseDouble(pRichiestoStr.replace(",", "."));
                double valMin = Double.parseDouble(pMinStr.replace(",", "."));
                if (valMin > valRichiesto) {
                    logicaOk = false;
                    messaggioErrore = "Il prezzo minimo non può superare quello richiesto";
                    impostaStile(prezzoMinField, false);
                }
            } catch (NumberFormatException e) { logicaOk = false; }
        }

        errorePrezzo.setText(messaggioErrore);
        errorePrezzo.setVisible(!logicaOk);
        errorePrezzo.setManaged(!logicaOk);
        prezziValidiProperty.set(logicaOk);
    }

    // =================================================================================
    // EVENT HANDLERS
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

    @FXML
    public void onPubblicaClick(ActionEvent actionEvent) {
        List<Oggetto> selezionati = ottieniOggettiSelezionati();
        System.out.println("Pubblicazione annuncio con " + selezionati.size() + " oggetti.");
        // Qui invoca il metodo controller.PubblicaAnnuncio(...)
    }

    @FXML
    public void onAnnullaClick(ActionEvent actionEvent) {
        // Logica per tornare indietro
    }

    // =================================================================================
    // HELPER METHODS
    // =================================================================================

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

    private List<Oggetto> ottieniOggettiSelezionati() {
        List<Oggetto> selezionati = new ArrayList<>();
        for (Node node : contenitoreOggetti.getChildren()) {
            if (node instanceof CheckBox cb && cb.isSelected()) {
                selezionati.add((Oggetto) cb.getUserData());
            }
        }
        return selezionati;
    }

    // Metodo per la modifica
    public void setAnnuncioDaModificare(Annuncio annuncio) {
        this.descrizioneAnnuncioArea.setText(annuncio.getDescrizione());
        // Aggiungi qui la logica per settare gli altri campi (prezzi, oggetti, ecc.)
        this.pubblicaButton.setText("Salva Modifiche");
    }
}