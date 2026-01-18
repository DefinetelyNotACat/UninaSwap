package com.example.uninaswap.boundary;

import com.example.uninaswap.Costanti;
import com.example.uninaswap.controller.ControllerUninaSwap;
import com.example.uninaswap.entity.*;
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

import java.math.BigDecimal;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

public class AggiungiAnnuncio {

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
    @FXML private RadioButton radioVendita, radioScambio, radioRegalo;
    @FXML private VBox vboxVendita, vboxScambio, vboxRegalo;
    @FXML private TextField prezzoField, prezzoMinField;
    @FXML private Text errorePrezzo;
    @FXML private TextArea desideriScambioArea;
    @FXML private Text erroreScambio;
    @FXML private TextField infoRitiroField;
    @FXML private Text erroreRitiro;
    @FXML private Button annullaButton, pubblicaButton;

    // Proprietà di validazione
    private final BooleanProperty almenoUnOggettoSelezionato = new SimpleBooleanProperty(false);
    private final BooleanProperty prezziValidiProperty = new SimpleBooleanProperty(false);
    private final BooleanProperty orariValidiProperty = new SimpleBooleanProperty(false);

    // Nuove proprietà per Regex
    private final BooleanProperty descrizioneRegexValida = new SimpleBooleanProperty(false);
    private final BooleanProperty scambioRegexValido = new SimpleBooleanProperty(false);
    private final BooleanProperty ritiroRegexValido = new SimpleBooleanProperty(true);

    @FXML
    public void initialize() {
        caricaSedi();
        caricaInventarioUtente();
        setupValidazioneRealTime();
    }

    private void caricaSedi() {
        List<Sede> sedi = ControllerUninaSwap.getInstance().getSedi();
        if (sedi != null) {
            for (Sede sede : sedi) sedeBox.getItems().add(sede.getNomeSede());
        }
    }

    private void caricaInventarioUtente() {
        contenitoreOggetti.getChildren().clear();
        try {
            Utente u = ControllerUninaSwap.getInstance().getUtente();
            List<Oggetto> list = ControllerUninaSwap.getInstance().OttieniOggettiDisponibili(u);
            if (list == null || list.isEmpty()) {
                contenitoreOggetti.getChildren().add(new Text("Inventario vuoto."));
                return;
            }
            for (Oggetto obj : list) {
                CheckBox cb = new CheckBox(obj.getNome());
                cb.setUserData(obj);
                cb.selectedProperty().addListener((obs, old, newVal) -> aggiornaStatoOggetti());
                contenitoreOggetti.getChildren().add(cb);
            }
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void setupValidazioneRealTime() {
        // --- 1. VALIDAZIONE DESCRIZIONE (REGEX) ---
        descrizioneAnnuncioArea.textProperty().addListener((obs, old, newVal) -> {
            boolean ok = newVal != null && newVal.matches(Costanti.FIELDS_REGEX_SPAZIO);
            descrizioneRegexValida.set(ok);
            gestisciErroreGenerico(descrizioneAnnuncioArea, erroreDescrizione, ok);
        });

        // --- 2. VALIDAZIONE SCAMBIO (REGEX) ---
        desideriScambioArea.textProperty().addListener((obs, old, newVal) -> {
            boolean ok = newVal != null && newVal.matches(Costanti.FIELDS_REGEX_SPAZIO);
            scambioRegexValido.set(ok);
            gestisciErroreGenerico(desideriScambioArea, erroreScambio, ok);
        });

        // --- 3. VALIDAZIONE RITIRO (REGEX - Opzionale ma controllata se scritta) ---
        infoRitiroField.textProperty().addListener((obs, old, newVal) -> {
            boolean ok = newVal.isEmpty() || newVal.matches(Costanti.FIELDS_REGEX_SPAZIO);
            ritiroRegexValido.set(ok);
            gestisciErroreGenerico(infoRitiroField, erroreRitiro, ok);
        });

        // --- 4. ALTRE VALIDAZIONI ---
        orarioInizioField.textProperty().addListener((o, old, n) -> validaOrari());
        orarioFineField.textProperty().addListener((o, old, n) -> validaOrari());
        prezzoField.textProperty().addListener((o, old, n) -> validaPrezzi());
        prezzoMinField.textProperty().addListener((o, old, n) -> validaPrezzi());

        // --- 5. BINDING DISABILITAZIONE PULSANTE ---
        BooleanBinding tipologiaScelta = radioVendita.selectedProperty()
                .or(radioScambio.selectedProperty())
                .or(radioRegalo.selectedProperty());

        // Validazione dinamica basata sulla tipologia
        BooleanBinding sezioneSpecificaOk = Bindings.createBooleanBinding(() -> {
                    if (radioVendita.isSelected()) return prezziValidiProperty.get();
                    if (radioScambio.isSelected()) return scambioRegexValido.get();
                    if (radioRegalo.isSelected()) return ritiroRegexValido.get();
                    return false;
                }, radioVendita.selectedProperty(), radioScambio.selectedProperty(), radioRegalo.selectedProperty(),
                prezziValidiProperty, scambioRegexValido, ritiroRegexValido);

        // IL TESTO DEVE ESSERE VALIDO, LA SEDE SCELTA, GLI ORARI OK, ALMENO UN OGGETTO E LA SEZIONE SPECIFICA OK
        pubblicaButton.disableProperty().bind(
                descrizioneRegexValida.not()
                        .or(sedeBox.valueProperty().isNull())
                        .or(orariValidiProperty.not())
                        .or(almenoUnOggettoSelezionato.not())
                        .or(tipologiaScelta.not())
                        .or(sezioneSpecificaOk.not())
        );
    }

    private void validaOrari() {
        try {
            DateTimeFormatter f = DateTimeFormatter.ofPattern("HH:mm");
            LocalTime i = LocalTime.parse(orarioInizioField.getText(), f);
            LocalTime o = LocalTime.parse(orarioFineField.getText(), f);
            boolean ok = i.isBefore(o);
            orariValidiProperty.set(ok);
            impostaStile(orarioInizioField, ok);
            impostaStile(orarioFineField, ok);
            erroreOrario.setVisible(!ok);
        } catch (Exception e) { orariValidiProperty.set(false); }
    }

    private void validaPrezzi() {
        boolean ok = prezzoField.getText().matches(Costanti.PRICE_REGEX);
        prezziValidiProperty.set(ok);
        impostaStile(prezzoField, ok);
        errorePrezzo.setVisible(!ok);
    }

    private void gestisciErroreGenerico(Control f, Text e, boolean ok) {
        f.getStyleClass().removeAll("error", "right");
        f.getStyleClass().add(ok ? "right" : "error");
        if (e != null) { e.setVisible(!ok); e.setManaged(!ok); }
    }

    private void impostaStile(Control f, boolean ok) {
        f.getStyleClass().removeAll("error", "right");
        f.getStyleClass().add(ok ? "right" : "error");
    }

    private void aggiornaStatoOggetti() {
        boolean uno = false;
        for (Node n : contenitoreOggetti.getChildren()) if (n instanceof CheckBox cb && cb.isSelected()) uno = true;
        almenoUnOggettoSelezionato.set(uno);
        erroreOggetti.setVisible(!uno);
    }

    @FXML
    public void onPubblicaClick(ActionEvent actionEvent) {

        ControllerUninaSwap controller = ControllerUninaSwap.getInstance();
        try {
            // 1. Dati Comuni
            String descrizione = descrizioneAnnuncioArea.getText();
            Sede sede = trovaSedePerNome(sedeBox.getValue());
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
            LocalTime inizio = LocalTime.parse(orarioInizioField.getText(), formatter);
            LocalTime fine = LocalTime.parse(orarioFineField.getText(), formatter);

            List<Oggetto> selezionati = ottieniOggettiSelezionati();
            Oggetto primoOggetto = selezionati.get(0); // Primo oggetto richiesto dai costruttori delle entity

            Annuncio annuncioDaInviare = null;

            // 2. Creazione istanza specifica
            if (radioVendita.isSelected()) {
                BigDecimal prezzoRichiesto = new BigDecimal(prezzoField.getText().replace(",", "."));
                AnnuncioVendita av = new AnnuncioVendita(sede, descrizione, inizio, fine, primoOggetto, prezzoRichiesto);
                if (!prezzoMinField.getText().isEmpty()) {
                    av.setPrezzoMinimo(new BigDecimal(prezzoMinField.getText().replace(",", ".")));
                }
                annuncioDaInviare = av;

            } else if (radioScambio.isSelected()) {
                String cosaCerco = desideriScambioArea.getText();
                annuncioDaInviare = new AnnuncioScambio(sede, descrizione, inizio, fine, primoOggetto, cosaCerco);

            } else if (radioRegalo.isSelected()) {
                annuncioDaInviare = new AnnuncioRegalo(sede, descrizione, inizio, fine, primoOggetto);
                // Nota: Se AnnuncioRegalo avesse un campo per infoRitiroField, andrebbe settato qui.
            }

            // 3. Aggiunta oggetti supplementari
            if (annuncioDaInviare != null && selezionati.size() > 1) {
                for (int i = 1; i < selezionati.size(); i++) {
                    annuncioDaInviare.addOggetto(selezionati.get(i));
                }
            }

            // 4. Invio al Controller
            if (annuncioDaInviare != null) {

                boolean successo = controller.PubblicaAnnuncio(annuncioDaInviare);
                if (successo) {
                    new GestoreScene().CambiaScena(Costanti.pathHomePage, Costanti.homepage, actionEvent, "Annuncio pubblicato!", Messaggio.TIPI.SUCCESS);
                }
            }

        } catch (Exception e) {
            System.err.println("Errore pubblicazione: " + e.getMessage());
        }

    }

    @FXML
    void onTipologiaChange(ActionEvent e) {
        vboxVendita.setVisible(radioVendita.isSelected()); vboxVendita.setManaged(radioVendita.isSelected());
        vboxScambio.setVisible(radioScambio.isSelected()); vboxScambio.setManaged(radioScambio.isSelected());
        vboxRegalo.setVisible(radioRegalo.isSelected()); vboxRegalo.setManaged(radioRegalo.isSelected());
    }

    private Sede trovaSedePerNome(String n) {
        for (Sede s : ControllerUninaSwap.getInstance().getSedi()) if (s.getNomeSede().equals(n)) return s;
        return null;
    }

    private List<Oggetto> ottieniOggettiSelezionati() {
        List<Oggetto> s = new ArrayList<>();
        for (Node n : contenitoreOggetti.getChildren()) if (n instanceof CheckBox cb && cb.isSelected()) s.add((Oggetto) cb.getUserData());
        return s;
    }

    @FXML void onAnnullaClick(ActionEvent e) {
        new GestoreScene().CambiaScena(Costanti.pathHomePage, Costanti.homepage, e, "Pubblicazione annuncio annullata", Messaggio.TIPI.INFO);
    }

}