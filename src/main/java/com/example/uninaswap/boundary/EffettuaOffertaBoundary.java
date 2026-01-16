package com.example.uninaswap.boundary;

import com.example.uninaswap.Costanti;
import com.example.uninaswap.controller.ControllerUninaSwap;
import com.example.uninaswap.entity.*;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.ArrayList;

public class EffettuaOffertaBoundary {

    @FXML private Text txtInfoAnnuncio;
    @FXML private VBox containerSpecifico;
    @FXML private TextArea txtMessaggio;
    @FXML private Label lblErrore;
    @FXML private Text erroreRegexMessaggio;
    @FXML private Button btnInvia;

    private Annuncio annuncioTarget;
    private final ControllerUninaSwap controller = ControllerUninaSwap.getInstance();

    private final BooleanProperty messaggioRegexOk = new SimpleBooleanProperty(true);

    private TextField inputPrezzo;
    private ListView<Oggetto> listaMieiOggetti;

    @FXML
    public void initialize() {
        setupValidazioneRegex();
    }

    private void setupValidazioneRegex() {
        txtMessaggio.textProperty().addListener((obs, old, newVal) -> {
            boolean ok = newVal == null || newVal.isEmpty() || newVal.matches(Costanti.FIELDS_REGEX_SPAZIO);
            messaggioRegexOk.set(ok);
            gestisciErroreVisivo(txtMessaggio, erroreRegexMessaggio, ok);
        });

        btnInvia.disableProperty().bind(messaggioRegexOk.not());
    }

    private void gestisciErroreVisivo(Control f, Text e, boolean ok) {
        f.getStyleClass().removeAll("error", "right");
        f.getStyleClass().add(ok ? "right" : "error");
        if (e != null) {
            e.setVisible(!ok);
            e.setManaged(!ok);
        }
    }

    public void initData(Annuncio annuncio) {
        this.annuncioTarget = annuncio;
        txtInfoAnnuncio.setText("Per: " + annuncio.getDescrizione());
        costruisciInterfaccia();
    }

    private void costruisciInterfaccia() {
        containerSpecifico.getChildren().clear();

        if (annuncioTarget instanceof AnnuncioVendita) {
            Label l = new Label("Inserisci la tua offerta in €:");
            l.setStyle("-fx-font-weight: bold; -fx-font-size: 15px;");

            inputPrezzo = new TextField();
            inputPrezzo.setPromptText("Es. 50.00");
            inputPrezzo.getStyleClass().add("text-field-premium");
            inputPrezzo.setMaxWidth(250);

            containerSpecifico.getChildren().addAll(l, inputPrezzo);

        } else if (annuncioTarget instanceof AnnuncioScambio) {
            Label l = new Label("Seleziona gli oggetti da offrire:");
            l.setStyle("-fx-font-weight: bold; -fx-text-fill: #2d3436; -fx-font-size: 15px;");

            listaMieiOggetti = new ListView<>();
            listaMieiOggetti.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
            listaMieiOggetti.setPrefHeight(250);

            listaMieiOggetti.addEventFilter(MouseEvent.MOUSE_PRESSED, event -> {
                event.consume();
                javafx.scene.Node node = event.getPickResult().getIntersectedNode();
                while (node != null && !(node instanceof ListCell)) {
                    node = node.getParent();
                }
                if (node instanceof ListCell) {
                    ListCell<?> cell = (ListCell<?>) node;
                    if (!cell.isEmpty()) {
                        int index = cell.getIndex();
                        if (listaMieiOggetti.getSelectionModel().isSelected(index)) {
                            listaMieiOggetti.getSelectionModel().clearSelection(index);
                        } else {
                            listaMieiOggetti.getSelectionModel().select(index);
                        }
                    }
                }
            });

            try {
                ArrayList<Oggetto> mieiOggetti = controller.OttieniOggettiDisponibili(controller.getUtente());
                listaMieiOggetti.getItems().addAll(mieiOggetti);

                listaMieiOggetti.setCellFactory(param -> new ListCell<>() {
                    private final CheckBox cb = new CheckBox();
                    private final HBox cellBox = new HBox(15);
                    private final Label nome = new Label();
                    {
                        cb.setMouseTransparent(true);
                        cb.setFocusTraversable(false);
                        cellBox.setAlignment(Pos.CENTER_LEFT);
                        cellBox.getChildren().addAll(cb, nome);
                    }
                    @Override
                    protected void updateItem(Oggetto item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty || item == null) {
                            setGraphic(null);
                        } else {
                            nome.setText(item.getNome());
                            cb.setSelected(listaMieiOggetti.getSelectionModel().isSelected(getIndex()));
                            setGraphic(cellBox);
                        }
                    }
                });
            } catch (Exception e) {
                lblErrore.setText("Errore caricamento oggetti.");
            }
            containerSpecifico.getChildren().addAll(l, listaMieiOggetti);

        } else {
            VBox boxRegalo = new VBox(10);
            boxRegalo.setAlignment(Pos.CENTER);
            Label l = new Label("🎁 Questo annuncio è un REGALO!");
            l.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #fd7e14;");
            boxRegalo.getChildren().add(l);
            containerSpecifico.getChildren().add(boxRegalo);
        }
    }

    @FXML
    public void confermaOfferta() {
        if (!messaggioRegexOk.get()) {
            lblErrore.setText("Correggi il messaggio prima di inviare.");
            return;
        }

        try {
            Utente me = controller.getUtente();
            String msg = txtMessaggio.getText();
            Offerta nuovaOfferta = null;

            if (annuncioTarget instanceof AnnuncioVendita av) {
                if (inputPrezzo.getText().isEmpty()) throw new Exception("Inserisci un prezzo.");
                double prezzoOfferto = Double.parseDouble(inputPrezzo.getText().replace(",", "."));

                if (av.getPrezzoMinimo() != null && prezzoOfferto < av.getPrezzoMinimo().doubleValue()) {
                    throw new Exception("Offerta troppo bassa!");
                }

                nuovaOfferta = new OffertaVendita(annuncioTarget, msg, Offerta.STATO_OFFERTA.IN_ATTESA,
                        LocalTime.now(), LocalTime.now().plusHours(1), null, me,
                        BigDecimal.valueOf(prezzoOfferto), av);

            } else if (annuncioTarget instanceof AnnuncioScambio) {
                var selezionati = new ArrayList<>(listaMieiOggetti.getSelectionModel().getSelectedItems());
                if (selezionati.isEmpty()) throw new Exception("Seleziona un oggetto.");

                OffertaScambio os = new OffertaScambio((AnnuncioScambio) annuncioTarget, msg, Offerta.STATO_OFFERTA.IN_ATTESA,
                        LocalTime.now(), LocalTime.now().plusHours(1), selezionati.get(0), me);
                os.setOggetti(selezionati);
                nuovaOfferta = os;

            } else {
                nuovaOfferta = new OffertaRegalo(annuncioTarget, msg, Offerta.STATO_OFFERTA.IN_ATTESA,
                        LocalTime.now(), LocalTime.now().plusHours(1), null, me, (AnnuncioRegalo) annuncioTarget);
            }

            // --- DELEGATA L'INTERAZIONE AL CONTROLLER ---
            if (controller.EseguiOfferta(me, nuovaOfferta)) {
                tornaHome();
            } else {
                lblErrore.setText("Errore durante l'invio dell'offerta.");
            }

        } catch (Exception e) {
            lblErrore.setText(e.getMessage());
        }
    }

    @FXML
    public void annulla() {
        tornaHome();
    }

    private void tornaHome() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(Costanti.pathHomePage));
            Parent root = loader.load();
            Stage stage = (Stage) containerSpecifico.getScene().getWindow();
            stage.setScene(new Scene(root, stage.getScene().getWidth(), stage.getScene().getHeight()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}