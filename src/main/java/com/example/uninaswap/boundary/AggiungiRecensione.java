package com.example.uninaswap.boundary;

import com.example.uninaswap.Costanti;
import com.example.uninaswap.controller.ControllerUninaSwap;
import com.example.uninaswap.entity.Recensione;
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
        setupValidazione();
    }

    public void initData(Utente utenteDaRecensire) {
        try {
            this.utenteDaRecensire = utenteDaRecensire;
            this.utenteRecensore = controller.getUtente();

            if (this.utenteDaRecensire != null && this.utenteRecensore != null) {
                testoRecensito.setText("Stai recensendo: " + this.utenteDaRecensire.getUsername());

                // Cerchiamo nel DB se esiste già una recensione tra questi due utenti
                Recensione esistente = controller.trovaRecensioneEsistente(utenteRecensore, this.utenteDaRecensire);

                if (esistente != null) {
                    commentoArea.setText(esistente.getCommento());
                    votoSelezionato.set(esistente.getVoto());
                    aggiornaStelle(esistente.getVoto());
                    inviaButton.setText("Aggiorna Recensione");
                }
            }
        } catch (Exception exception) {
            System.err.println("Errore nel caricamento dati recensione: " + exception.getMessage());
        }
    }

    private void setupValidazione() {
        commentoArea.textProperty().addListener((obs, old, newVal) -> {
            boolean ok = newVal == null || newVal.isEmpty() || newVal.matches(Costanti.FIELDS_REGEX_SPAZIO);
            commentoRegexOk.set(ok);
            gestisciErroreVisivo(commentoArea, erroreCommento, ok);
        });

        inviaButton.disableProperty().bind(
                votoSelezionato.lessThan(1).or(commentoRegexOk.not())
        );
    }

    @FXML
    private void onStarClick(MouseEvent mouseEvent) {
        Label starCliccata = (Label) mouseEvent.getSource();
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
    public void onInviaClick(ActionEvent actionEvent) {
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
                new GestoreScene().CambiaScena(Costanti.pathHomePage, Costanti.homepage, actionEvent,
                        "Recensione salvata con successo!", Messaggio.TIPI.SUCCESS);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    void onAnnullaClick(ActionEvent actionEvent) {
        new GestoreScene().CambiaScena(Costanti.pathHomePage, Costanti.homepage, actionEvent);
    }

    private void gestisciErroreVisivo(Control controller, Text messaggio, boolean check) {
        controller.getStyleClass().removeAll("error", "right");
        controller.getStyleClass().add(check ? "right" : "error");
        if (messaggio != null) {
            messaggio.setVisible(!check);
            messaggio.setManaged(!check);
        }
    }
}