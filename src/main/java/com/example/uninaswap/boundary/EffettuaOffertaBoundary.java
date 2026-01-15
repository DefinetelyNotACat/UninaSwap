package com.example.uninaswap.boundary;

import com.example.uninaswap.Costanti;
import com.example.uninaswap.controller.ControllerUninaSwap;
import com.example.uninaswap.entity.*;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
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
    private ControllerUninaSwap controller = ControllerUninaSwap.getInstance();

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
            Label l = new Label("Inserisci la tua offerta in €:");
            inputPrezzo = new TextField();
            inputPrezzo.setPromptText("Es. 50.00");
            containerSpecifico.getChildren().addAll(l, inputPrezzo);

        } else if (annuncioTarget instanceof AnnuncioScambio) {
            Label l = new Label("Seleziona uno o più oggetti dal tuo inventario da scambiare:");
            listaMieiOggetti = new ListView<>();
            listaMieiOggetti.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

            try {
                // Recupera gli oggetti disponibili dell'utente loggato
                ArrayList<Oggetto> mieiOggetti = controller.OttieniOggettiDisponibili(controller.getUtente());
                listaMieiOggetti.getItems().addAll(mieiOggetti);
                // Per visualizzare bene l'oggetto nella lista
                listaMieiOggetti.setCellFactory(param -> new ListCell<>() {
                    @Override
                    protected void updateItem(Oggetto item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty || item == null) setText(null);
                        else setText(item.getNome() + " (" + item.getCondizione() + ")");
                    }
                });
            } catch (Exception e) {
                lblErrore.setText("Errore caricamento oggetti: " + e.getMessage());
            }
            containerSpecifico.getChildren().addAll(l, listaMieiOggetti);

        } else {
            // Regalo
            Label l = new Label("Questo annuncio è un REGALO! Conferma il tuo interesse.");
            containerSpecifico.getChildren().add(l);
        }
    }

    @FXML
    public void confermaOfferta() {
        try {
            Utente me = controller.getUtente();
            String msg = txtMessaggio.getText();
            Offerta nuovaOfferta = null;

            if (annuncioTarget instanceof AnnuncioVendita) {
                double prezzo = Double.parseDouble(inputPrezzo.getText().replace(",", "."));
                nuovaOfferta = new OffertaVendita(annuncioTarget, msg, Offerta.STATO_OFFERTA.IN_ATTESA,
                        LocalTime.now(), LocalTime.now().plusHours(1), null, me, BigDecimal.valueOf(prezzo), (AnnuncioVendita) annuncioTarget);

            } else if (annuncioTarget instanceof AnnuncioScambio) {
                var selezionati = new ArrayList<>(listaMieiOggetti.getSelectionModel().getSelectedItems());
                if (selezionati.isEmpty()) throw new Exception("Seleziona almeno un oggetto per lo scambio.");

                OffertaScambio os = new OffertaScambio((AnnuncioScambio) annuncioTarget, msg, Offerta.STATO_OFFERTA.IN_ATTESA,
                        LocalTime.now(), LocalTime.now().plusHours(1), selezionati.get(0), me);
                os.setOggetti(selezionati); // Imposta la lista completa
                nuovaOfferta = os;

            } else {
                // Regalo
                nuovaOfferta = new OffertaRegalo(annuncioTarget, msg, Offerta.STATO_OFFERTA.IN_ATTESA,
                        LocalTime.now(), LocalTime.now().plusHours(1), null, me, (AnnuncioRegalo) annuncioTarget);
            }

            // Salvataggio tramite DAO (o Controller se esposto)
            com.example.uninaswap.dao.OffertaDAO dao = new com.example.uninaswap.dao.OffertaDAO();
            if (dao.salvaOfferta(nuovaOfferta)) {
                System.out.println("Offerta Inviata!");
                tornaHome();
            } else {
                lblErrore.setText("Errore durante il salvataggio dell'offerta.");
            }

        } catch (NumberFormatException e) {
            lblErrore.setText("Formato prezzo non valido.");
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
            // CORREZIONE: Usa Costanti.pathHomePage
            FXMLLoader loader = new FXMLLoader(getClass().getResource(Costanti.pathHomePage));
            Parent root = loader.load();
            Stage stage = (Stage) containerSpecifico.getScene().getWindow();
            stage.setScene(new Scene(root, stage.getScene().getWidth(), stage.getScene().getHeight()));
        } catch (Exception e) { e.printStackTrace(); }
    }
}