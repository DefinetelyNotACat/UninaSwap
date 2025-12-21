package com.example.uninaswap.boundary;

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

    // =================================================================================
    // INITIALIZATION
    // =================================================================================

    @FXML
    public void initialize() {
        caricaInventarioUtente();
    }

    /**
     * Recupera gli oggetti dell'utente e popola dinamicamente la lista di CheckBox.
     */
    private void caricaInventarioUtente() {
        // TODO: Recuperare la lista reale degli oggetti dell'utente (es. List<OggettoBean>) dal DB
        List<String> oggettiMock = List.of("Libro Analisi 1", "Calcolatrice", "Appunti Fisica");

        contenitoreOggetti.getChildren().clear();

        if (oggettiMock.isEmpty()) {
            contenitoreOggetti.getChildren().add(new Text("Nessun oggetto nell'inventario."));
            return;
        }

        for (String nomeOggetto : oggettiMock) {
            CheckBox cb = new CheckBox(nomeOggetto);
            // cb.setUserData(idOggetto); // TODO: Associare ID oggetto per il recupero successivo
            cb.setStyle("-fx-font-size: 14px; -fx-text-fill: #333;");
            contenitoreOggetti.getChildren().add(cb);
        }
    }

    // =================================================================================
    // EVENT HANDLERS
    // =================================================================================

    /**
     * Gestisce la logica di visualizzazione dei pannelli (Vendita/Scambio/Regalo)
     * in base al RadioButton selezionato dall'utente.
     */
    @FXML
    void onTipologiaChange(ActionEvent event) {
        nascondiBox(vboxVendita);
        nascondiBox(vboxScambio);
        nascondiBox(vboxRegalo);

        if (radioVendita.isSelected()) {
            mostraBox(vboxVendita);
        } else if (radioScambio.isSelected()) {
            mostraBox(vboxScambio);
        } else if (radioRegalo.isSelected()) {
            mostraBox(vboxRegalo);
        }
    }

    /**
     * Coordina la validazione di tutti i campi input.
     * Se i dati sono validi, procede con la logica di pubblicazione.
     */
    @FXML
    public void onPubblicaClick(ActionEvent actionEvent) {
        resetErrori();
        boolean isValid = true;

        // Validazione Descrizione
        if (descrizioneAnnuncioArea.getText().trim().isEmpty()) {
            erroreDescrizione.setVisible(true);
            erroreDescrizione.setManaged(true);
            isValid = false;
        }

        // Validazione Selezione Oggetti
        List<String> oggettiSelezionati = getOggettiSelezionati();
        if (oggettiSelezionati.isEmpty()) {
            erroreOggetti.setVisible(true);
            erroreOggetti.setManaged(true);
            isValid = false;
        }

        // Validazione Tipologia e campi specifici
        if (tipoAnnuncioGroup.getSelectedToggle() == null) {
            erroreTipologia.setVisible(true);
            erroreTipologia.setManaged(true);
            isValid = false;
        } else {
            if (radioVendita.isSelected() && !isPrezzoValido(prezzoField.getText())) {
                errorePrezzo.setVisible(true);
                errorePrezzo.setManaged(true);
                isValid = false;
            } else if (radioScambio.isSelected() && desideriScambioArea.getText().trim().isEmpty()) {
                erroreScambio.setVisible(true);
                erroreScambio.setManaged(true);
                isValid = false;
            }
        }

        if (isValid) {
            System.out.println("Annuncio pubblicato con successo!");
            // TODO: Chiamare il Controller Applicativo per salvare l'annuncio nel DB
            // es. annuncioController.creaAnnuncio(oggettiSelezionati, tipo, dettagli...);
        }
    }

    @FXML
    public void onAnnullaClick(ActionEvent actionEvent) {
        // TODO: Implementare navigazione per tornare alla Home o chiudere la finestra
        System.out.println("Annulla cliccato - Torna indietro");
    }

    // =================================================================================
    // HELPER METHODS
    // =================================================================================

    private void nascondiBox(VBox box) {
        box.setVisible(false);
        box.setManaged(false);
    }

    private void mostraBox(VBox box) {
        box.setVisible(true);
        box.setManaged(true);
    }

    private void resetErrori() {
        erroreDescrizione.setVisible(false);
        erroreDescrizione.setManaged(false);
        erroreOggetti.setVisible(false);
        erroreOggetti.setManaged(false);
        erroreTipologia.setVisible(false);
        erroreTipologia.setManaged(false);
        errorePrezzo.setVisible(false);
        errorePrezzo.setManaged(false);
        erroreScambio.setVisible(false);
        erroreScambio.setManaged(false);
    }

    private List<String> getOggettiSelezionati() {
        List<String> selected = new ArrayList<>();
        for (Node node : contenitoreOggetti.getChildren()) {
            if (node instanceof CheckBox cb && cb.isSelected()) {
                selected.add(cb.getText());
                // TODO: Usare cb.getUserData() per ritornare oggetti reali invece di stringhe
            }
        }
        return selected;
    }

    private boolean isPrezzoValido(String prezzoStr) {
        try {
            double prezzo = Double.parseDouble(prezzoStr.replace(",", "."));
            return prezzo > 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}