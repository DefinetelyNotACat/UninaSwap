package com.example.uninaswap.boundary;

import com.example.uninaswap.Costanti;
import com.example.uninaswap.controller.ControllerUninaSwap;
import com.example.uninaswap.entity.*;
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

    private Annuncio annuncioTarget;
    private final ControllerUninaSwap controller = ControllerUninaSwap.getInstance();

    // Controlli dinamici
    private TextField inputPrezzo;
    private ListView<Oggetto> listaMieiOggetti;

    public void initData(Annuncio annuncio) {
        this.annuncioTarget = annuncio;
        txtInfoAnnuncio.setText("Per: " + annuncio.getDescrizione());
        costruisciInterfaccia();
    }

    private void costruisciInterfaccia() {
        containerSpecifico.getChildren().clear();

        if (annuncioTarget instanceof AnnuncioVendita) {
            // --- CASO VENDITA ---
            Label l = new Label("Inserisci la tua offerta in €:");
            l.setStyle("-fx-font-weight: bold; -fx-font-size: 15px;");

            inputPrezzo = new TextField();
            inputPrezzo.setPromptText("Es. 50.00");
            inputPrezzo.getStyleClass().add("text-field-premium");
            inputPrezzo.setMaxWidth(250);

            containerSpecifico.getChildren().addAll(l, inputPrezzo);

        } else if (annuncioTarget instanceof AnnuncioScambio) {
            // --- CASO SCAMBIO (CHECKLIST STICKY) ---
            Label l = new Label("Seleziona gli oggetti da offrire (clicca per aggiungere/rimuovere):");
            l.setStyle("-fx-font-weight: bold; -fx-text-fill: #2d3436; -fx-font-size: 15px;");

            listaMieiOggetti = new ListView<>();
            listaMieiOggetti.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
            listaMieiOggetti.setPrefHeight(250);

            // --- TRUCCO PERFETTO: EVENT FILTER ---
            // Intercettiamo il click prima che la ListView esegua la deselezione automatica
            listaMieiOggetti.addEventFilter(MouseEvent.MOUSE_PRESSED, event -> {
                event.consume(); // Blocchiamo il comportamento nativo di JavaFX

                // Troviamo manualmente quale cella è stata cliccata
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
                    private final VBox textContainer = new VBox(2);
                    private final Label nome = new Label();
                    private final Label info = new Label();

                    {
                        nome.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
                        info.setStyle("-fx-font-size: 11px; -fx-text-fill: #666;");
                        textContainer.getChildren().addAll(nome, info);
                        cellBox.setAlignment(Pos.CENTER_LEFT);
                        cellBox.getChildren().addAll(cb, textContainer);

                        // La CheckBox deve ignorare il mouse perché gestiamo tutto tramite l'EventFilter sopra
                        cb.setMouseTransparent(true);
                        cb.setFocusTraversable(false);
                    }

                    @Override
                    protected void updateItem(Oggetto item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty || item == null) {
                            setGraphic(null);
                        } else {
                            nome.setText(item.getNome());
                            info.setText("Condizione: " + item.getCondizione());
                            // Sincronizza lo stato della checkbox con il modello di selezione
                            cb.setSelected(listaMieiOggetti.getSelectionModel().isSelected(getIndex()));
                            setGraphic(cellBox);
                        }
                    }
                });
            } catch (Exception e) {
                lblErrore.setText("Errore caricamento oggetti: " + e.getMessage());
            }
            containerSpecifico.getChildren().addAll(l, listaMieiOggetti);

        } else {
            // --- CASO REGALO ---
            VBox boxRegalo = new VBox(10);
            boxRegalo.setAlignment(Pos.CENTER);
            Label l = new Label("🎁 Questo annuncio è un REGALO!");
            l.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #fd7e14;");
            Text t = new Text("Invierai una richiesta di interesse. Il venditore sceglierà a chi regalarlo.");
            t.setStyle("-fx-fill: #636e72;");
            boxRegalo.getChildren().addAll(l, t);
            containerSpecifico.getChildren().add(boxRegalo);
        }
    }

    @FXML
    public void confermaOfferta() {
        try {
            Utente me = controller.getUtente();
            String msg = txtMessaggio.getText();
            Offerta nuovaOfferta = null;

            if (annuncioTarget instanceof AnnuncioVendita av) {
                if (inputPrezzo.getText().isEmpty()) throw new Exception("Inserisci un prezzo.");
                double prezzoOfferto = Double.parseDouble(inputPrezzo.getText().replace(",", "."));

                // Validazione Prezzo
                if (av.getPrezzoMinimo() != null && prezzoOfferto < av.getPrezzoMinimo().doubleValue()) {
                    throw new Exception("Offerta troppo bassa! Il minimo è " + av.getPrezzoMinimo() + "€.");
                } else if (prezzoOfferto < av.getPrezzoMedio().doubleValue()) {
                    throw new Exception("L'offerta deve essere almeno " + av.getPrezzoMedio() + "€.");
                }

                nuovaOfferta = new OffertaVendita(
                        annuncioTarget, msg, Offerta.STATO_OFFERTA.IN_ATTESA,
                        LocalTime.now(), LocalTime.now().plusHours(1), null, me,
                        BigDecimal.valueOf(prezzoOfferto), av
                );

            } else if (annuncioTarget instanceof AnnuncioScambio) {
                // Recuperiamo la lista corretta dal SelectionModel
                var selezionati = new ArrayList<>(listaMieiOggetti.getSelectionModel().getSelectedItems());

                if (selezionati.isEmpty()) {
                    throw new Exception("Seleziona almeno un oggetto per lo scambio.");
                }

                OffertaScambio os = new OffertaScambio(
                        (AnnuncioScambio) annuncioTarget, msg, Offerta.STATO_OFFERTA.IN_ATTESA,
                        LocalTime.now(), LocalTime.now().plusHours(1), selezionati.get(0), me
                );
                os.setOggetti(selezionati);
                nuovaOfferta = os;

            } else {
                nuovaOfferta = new OffertaRegalo(
                        annuncioTarget, msg, Offerta.STATO_OFFERTA.IN_ATTESA,
                        LocalTime.now(), LocalTime.now().plusHours(1), null, me,
                        (AnnuncioRegalo) annuncioTarget
                );
            }

            com.example.uninaswap.dao.OffertaDAO dao = new com.example.uninaswap.dao.OffertaDAO();
            if (dao.salvaOfferta(nuovaOfferta)) {
                System.out.println("Offerta inviata correttamente!");
                tornaHome();
            } else {
                lblErrore.setText("Errore di connessione al database.");
            }

        } catch (NumberFormatException e) {
            lblErrore.setText("Errore: il prezzo deve essere un numero (es: 10.50)");
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
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}