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

    private Utente utenteDaRecensire;
    private final ControllerUninaSwap controller = ControllerUninaSwap.getInstance();

    private final IntegerProperty votoSelezionato = new SimpleIntegerProperty(0);
    private final BooleanProperty commentoRegexOk = new SimpleBooleanProperty(true);

    @FXML
    public void initialize() {
        setupValidazione();
    }

    public void setUtenteDaRecensire(Utente u) {
        this.utenteDaRecensire = u;
        testoRecensito.setText("Stai recensendo: " + u.getUsername());
    }

    private void setupValidazione() {
        commentoArea.textProperty().addListener((obs, old, newVal) -> {
            boolean ok = newVal == null || newVal.isEmpty() || newVal.matches(Costanti.FIELDS_REGEX_SPAZIO);
            commentoRegexOk.set(ok);
            gestisciErroreVisivo(commentoArea, erroreCommento, ok);
        });

        // Binding rigoroso: il voto deve essere almeno 1
        inviaButton.disableProperty().bind(
                votoSelezionato.lessThan(1)
                        .or(commentoRegexOk.not())
        );
    }

    @FXML
    private void onStarClick(MouseEvent event) {
        Label starCliccata = (Label) event.getSource();
        // Estraiamo il numero ID (star1 -> 1, star2 -> 2...)
        String id = starCliccata.getId();
        int voto = Character.getNumericValue(id.charAt(id.length() - 1));

        votoSelezionato.set(voto);
        aggiornaStelle(voto);

        // Nascondi errore voto se presente
        erroreVoto.setVisible(false);
        erroreVoto.setManaged(false);
    }

    private void aggiornaStelle(int voto) {
        // Cicliamo su tutti i figli del contenitore stelle
        for (int i = 0; i < boxStelle.getChildren().size(); i++) {
            Node node = boxStelle.getChildren().get(i);
            if (node instanceof Label star) {
                // Rimuoviamo TUTTE le istanze precedenti per evitare bug di rendering
                star.getStyleClass().removeAll("star-filled");

                // Se l'indice i è minore del voto (es: i=0 per voto 1), accendiamo
                if (i < voto) {
                    star.getStyleClass().add("star-filled");
                }
            }
        }
    }

    @FXML
    public void onInviaClick(ActionEvent event) {
        try {
            Utente recensore = controller.getUtente();
            int voto = votoSelezionato.get();

            if (voto < 1) {
                erroreVoto.setVisible(true);
                erroreVoto.setManaged(true);
                return;
            }

            boolean successo = controller.pubblicaRecensione(
                    utenteDaRecensire,
                    recensore,
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