package com.example.uninaswap.boundary;

import com.example.uninaswap.Costanti;
import com.example.uninaswap.controller.ControllerUninaSwap;
import com.example.uninaswap.entity.Recensione;
import com.example.uninaswap.entity.Utente;
import com.example.uninaswap.interfaces.GestoreMessaggio;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;

public class AggiungiRecensione {

    @FXML private Text testoRecensito, erroreVoto, erroreCommento;
    @FXML private HBox boxStelle;
    @FXML private TextArea commentoArea;
    @FXML private Button inviaButton;

    private Utente utenteDaRecensire, utenteRecensore;
    private final ControllerUninaSwap controller = ControllerUninaSwap.getInstance();

    private final IntegerProperty votoSelezionato = new SimpleIntegerProperty(0);
    private final BooleanProperty commentoRegexOk = new SimpleBooleanProperty(true);

    @FXML
    public void initialize() {
        setupValidazione();
    }

    public void initData(Utente target) {
        try {
            this.utenteDaRecensire = target;
            this.utenteRecensore = controller.getUtente();

            if (this.utenteDaRecensire != null && this.utenteRecensore != null) {
                testoRecensito.setText("Stai recensendo: " + this.utenteDaRecensire.getUsername());

                // Cerchiamo nel DB se esiste già una recensione tra questi due utenti
                Recensione esistente = controller.trovaRecensioneEsistente(utenteRecensore, utenteDaRecensire);

                if (esistente != null) {
                    commentoArea.setText(esistente.getCommento());
                    votoSelezionato.set(esistente.getVoto());
                    aggiornaStelle(esistente.getVoto());
                    inviaButton.setText("Aggiorna Recensione");
                }
            }
        } catch (Exception e) {
            System.err.println("Errore nel caricamento dati recensione: " + e.getMessage());
        }
    }

    private void setupValidazione() {
        // --- VALIDAZIONE REGEX COMMENTO ---
        commentoArea.textProperty().addListener((obs, old, newVal) -> {
            // Il commento è opzionale, ma se presente deve rispettare il regex
            boolean ok = newVal == null || newVal.isEmpty() || newVal.matches(Costanti.FIELDS_REGEX_SPAZIO);
            commentoRegexOk.set(ok);
            gestisciErroreVisivo(commentoArea, erroreCommento, ok);
        });

        // Abilita il tasto solo se il voto è >= 1 E il regex del commento è OK
        inviaButton.disableProperty().bind(
                votoSelezionato.lessThan(1).or(commentoRegexOk.not())
        );
    }

    @FXML
    private void onStarClick(MouseEvent event) {
        Label starCliccata = (Label) event.getSource();
        String id = starCliccata.getId();
        int voto = Character.getNumericValue(id.charAt(id.length() - 1));

        votoSelezionato.set(voto);
        aggiornaStelle(voto);

        if (erroreVoto != null) {
            erroreVoto.setVisible(false);
            erroreVoto.setManaged(false);
        }
    }

    private void aggiornaStelle(int voto) {
        for (int i = 0; i < boxStelle.getChildren().size(); i++) {
            Node node = boxStelle.getChildren().get(i);
            if (node instanceof Label star) {
                star.getStyleClass().removeAll("star-filled");
                if (i < voto) {
                    star.getStyleClass().add("star-filled");
                }
            }
        }
    }

    @FXML
    public void onInviaClick(ActionEvent event) {
        try {
            int voto = votoSelezionato.get();

            // Il controller gestirà l'INSERT o l'UPDATE confrontando gli ID degli utenti
            boolean successo = controller.pubblicaRecensione(
                    utenteDaRecensire,
                    utenteRecensore,
                    voto,
                    commentoArea.getText()
            );

            if (successo) {
                new GestoreScene().CambiaScena(Costanti.pathHomePage, Costanti.homepage, event,
                        "Recensione salvata con successo!", Messaggio.TIPI.SUCCESS);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    void onAnnullaClick(ActionEvent e) {
        new GestoreScene().CambiaScena(Costanti.pathHomePage, Costanti.homepage, e);
    }

    private void gestisciErroreVisivo(Control f, Text e, boolean ok) {
        f.getStyleClass().removeAll("error", "right");
        f.getStyleClass().add(ok ? "right" : "error");
        if (e != null) {
            e.setVisible(!ok);
            e.setManaged(!ok);
        }
    }
}