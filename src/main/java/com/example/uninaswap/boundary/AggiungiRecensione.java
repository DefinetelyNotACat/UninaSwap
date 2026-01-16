package com.example.uninaswap.boundary;

import com.example.uninaswap.Costanti;
import com.example.uninaswap.controller.ControllerUninaSwap;
import com.example.uninaswap.entity.Utente;
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
        // initialize viene chiamato al caricamento dell'FXML
        // Non settiamo l'utente qui perché arriverà dopo tramite initData
        setupValidazione();
    }

    /**
     * METODO MANCANTE: Riceve l'utente da recensire dalla Boundary precedente
     */
    public void initData(Utente target) {
        this.utenteDaRecensire = target;
        try {
            this.utenteRecensore = controller.getUtente();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        if (this.utenteDaRecensire != null && this.utenteRecensore != null) {
            testoRecensito.setText("Tu, " + this.utenteRecensore.getUsername() +
                    " stai recensendo: " + this.utenteDaRecensire.getUsername());
        }
    }

    private void setupValidazione() {
        commentoArea.textProperty().addListener((obs, old, newVal) -> {
            boolean ok = newVal == null || newVal.isEmpty() || newVal.matches(Costanti.FIELDS_REGEX_SPAZIO);
            commentoRegexOk.set(ok);
            gestisciErroreVisivo(commentoArea, erroreCommento, ok);
        });

        // Il pulsante invia si abilita solo se c'è un voto (>=1) e il commento è valido
        inviaButton.disableProperty().bind(
                votoSelezionato.lessThan(1)
                        .or(commentoRegexOk.not())
        );
    }

    @FXML
    private void onStarClick(MouseEvent event) {
        Label starCliccata = (Label) event.getSource();
        String id = starCliccata.getId();
        // star1, star2... estraiamo l'ultimo carattere come intero
        int voto = Character.getNumericValue(id.charAt(id.length() - 1));

        votoSelezionato.set(voto);
        aggiornaStelle(voto);

        erroreVoto.setVisible(false);
        erroreVoto.setManaged(false);
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

            if (voto < 1) {
                erroreVoto.setVisible(true);
                erroreVoto.setManaged(true);
                return;
            }

            // Chiamata al controller per salvare la recensione
            boolean successo = controller.pubblicaRecensione(
                    utenteDaRecensire, // Ricevuto da initData
                    utenteRecensore,   // Preso dal controller
                    voto,
                    commentoArea.getText()
            );

            if (successo) {
                new GestoreScene().CambiaScena(Costanti.pathHomePage, Costanti.homepage, event, "Recensione inviata!", Messaggio.TIPI.SUCCESS);
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
        if (e != null) { e.setVisible(!ok); e.setManaged(!ok); }
    }
}