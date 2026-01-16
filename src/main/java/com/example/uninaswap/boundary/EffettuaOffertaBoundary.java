package com.example.uninaswap.boundary;

import com.example.uninaswap.Costanti;
import com.example.uninaswap.controller.ControllerUninaSwap;
import com.example.uninaswap.entity.*;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
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
            Label l = new Label("Inserisci la tua offerta in €:");
            l.setStyle("-fx-font-weight: bold;");

            inputPrezzo = new TextField();
            inputPrezzo.setPromptText("Es. 50.00");
            inputPrezzo.setMaxWidth(200);

            containerSpecifico.getChildren().addAll(l, inputPrezzo);

        } else if (annuncioTarget instanceof AnnuncioScambio) {
            Label l = new Label("Seleziona gli oggetti dal tuo inventario:");
            l.setStyle("-fx-font-weight: bold;");

            listaMieiOggetti = new ListView<>();
            listaMieiOggetti.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
            listaMieiOggetti.setPrefHeight(200);
            listaMieiOggetti.setCursor(Cursor.HAND);

            try {
                ArrayList<Oggetto> mieiOggetti = controller.OttieniOggettiDisponibili(controller.getUtente());
                listaMieiOggetti.getItems().addAll(mieiOggetti);

                listaMieiOggetti.setCellFactory(param -> new ListCell<>() {
                    @Override
                    protected void updateItem(Oggetto item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty || item == null) {
                            setText(null);
                            setGraphic(null);
                        } else {
                            HBox cellBox = new HBox(10);
                            cellBox.setAlignment(Pos.CENTER_LEFT);
                            VBox textContainer = new VBox(2);
                            Label nome = new Label(item.getNome());
                            nome.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
                            Label info = new Label("Condizione: " + item.getCondizione());
                            info.setStyle("-fx-font-size: 11px; -fx-text-fill: #666;");
                            textContainer.getChildren().addAll(nome, info);
                            cellBox.getChildren().add(textContainer);
                            setGraphic(cellBox);
                        }
                    }
                });
            } catch (Exception e) {
                lblErrore.setText("Errore caricamento oggetti: " + e.getMessage());
            }
            containerSpecifico.getChildren().addAll(l, listaMieiOggetti);

        } else {
            Label l = new Label("🎁 Questo annuncio è un REGALO!");
            l.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #fd7e14;");
            Text t = new Text("Confermando l'offerta, il venditore riceverà la tua richiesta di interesse.");
            t.setWrappingWidth(400);
            containerSpecifico.getChildren().addAll(l, t);
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

                // Conversione input in double per il check
                double prezzoOfferto = Double.parseDouble(inputPrezzo.getText().replace(",", "."));

                // --- LOGICA DI VALIDAZIONE PREZZO RICHIESTA ---
                // 1. Controllo Prezzo Minimo (se non è null)
                if (av.getPrezzoMinimo() != null && prezzoOfferto < av.getPrezzoMinimo().doubleValue()) {
                    throw new Exception("L'offerta è troppo bassa! Il prezzo minimo accettato è " + av.getPrezzoMinimo() + "€.");
                }
                // 2. Controllo Prezzo Medio (Prezzo Normale)
                else if (prezzoOfferto < av.getPrezzoMedio().doubleValue()) {
                    throw new Exception("L'offerta non può essere inferiore al prezzo richiesto di " + av.getPrezzoMedio() + "€.");
                }

                nuovaOfferta = new OffertaVendita(
                        annuncioTarget, msg, Offerta.STATO_OFFERTA.IN_ATTESA,
                        LocalTime.now(), LocalTime.now().plusHours(1), null, me,
                        BigDecimal.valueOf(prezzoOfferto), av
                );

            } else if (annuncioTarget instanceof AnnuncioScambio) {
                var selezionati = new ArrayList<>(listaMieiOggetti.getSelectionModel().getSelectedItems());
                if (selezionati.isEmpty()) throw new Exception("Seleziona almeno un oggetto per lo scambio.");

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
                System.out.println("Offerta Inviata con successo!");
                tornaHome();
            } else {
                lblErrore.setText("Errore durante il salvataggio dell'offerta nel database.");
            }

        } catch (NumberFormatException e) {
            lblErrore.setText("Inserisci un numero valido per il prezzo.");
        } catch (Exception e) {
            lblErrore.setText(e.getMessage());
            e.printStackTrace();
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
            double width = stage.getScene().getWidth();
            double height = stage.getScene().getHeight();
            stage.setScene(new Scene(root, width, height));
            stage.show();
        } catch (Exception e) {
            System.err.println("Errore nel ritorno alla Home: " + e.getMessage());
            e.printStackTrace();
        }
    }
}