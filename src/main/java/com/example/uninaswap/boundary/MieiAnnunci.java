package com.example.uninaswap.boundary;

import com.example.uninaswap.Costanti;
import com.example.uninaswap.controller.ControllerUninaSwap;
import com.example.uninaswap.entity.*;
import com.example.uninaswap.interfaces.GestoreMessaggio;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class MieiAnnunci implements Initializable, GestoreMessaggio {

    @FXML private FlowPane containerAnnunci;
    @FXML private Text testoVuoto;
    @FXML private Messaggio notificaController;

    private final ControllerUninaSwap controller = ControllerUninaSwap.getInstance();
    private final GestoreScene gestoreScene = new GestoreScene();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        caricaAnnunci();
    }

    private void caricaAnnunci() {
        containerAnnunci.getChildren().clear();
        try {
            Utente utente = controller.getUtente();
            ArrayList<Annuncio> mieiAnnunci = controller.OttieniAnnunciDiUtente(utente.getId());

            if (mieiAnnunci == null || mieiAnnunci.isEmpty()) {
                testoVuoto.setVisible(true);
                testoVuoto.setManaged(true);
                return;
            }

            testoVuoto.setVisible(false);
            testoVuoto.setManaged(false);

            for (Annuncio a : mieiAnnunci) {
                containerAnnunci.getChildren().add(creaCardAnnuncio(a));
            }
        } catch (Exception e) { e.printStackTrace(); }
    }

    private VBox creaCardAnnuncio(Annuncio a) {
        VBox card = new VBox(12);
        card.getStyleClass().add("ad-card");
        card.setPrefWidth(270);
        card.setPadding(new Insets(15));
        card.setAlignment(Pos.TOP_CENTER);

        // 1. Immagine
        ImageView imgView = new ImageView();
        imgView.setFitWidth(240);
        imgView.setFitHeight(160);
        imgView.setPreserveRatio(true);
        caricaImmagine(a, imgView);

        // 2. Badge e Sede
        HBox header = new HBox(10);
        header.setAlignment(Pos.CENTER_LEFT);
        Label badge = new Label(determinaTipo(a).toUpperCase());
        badge.getStyleClass().addAll("badge-base", determinaClasseBadge(a));
        Text sede = new Text("📍 " + (a.getSede() != null ? a.getSede().getNomeSede() : "N/A"));
        sede.getStyleClass().add("ad-location");
        header.getChildren().addAll(badge, sede);

        // 3. Descrizione
        Text desc = new Text(a.getDescrizione());
        desc.getStyleClass().add("ad-description");
        desc.setWrappingWidth(240);

        // 4. Info Economica Specifica
        Text extraInfo = new Text();
        extraInfo.getStyleClass().add("ad-extra-info");
        if (a instanceof AnnuncioVendita av) {
            extraInfo.setText("💰 " + av.getPrezzoMedio() + " €");
        } else if (a instanceof AnnuncioScambio as) {
            extraInfo.setText("🔄 Cerco: " + as.getListaOggetti());
        } else {
            extraInfo.setText("🎁 REGALO");
        }

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        // 5. Area Azioni (Tasto Elimina o Stato Concluso)
        StackPane actionArea = new StackPane();
        actionArea.setMaxWidth(Double.MAX_VALUE);

        Button btnElimina = new Button("🗑 Elimina Annuncio");
        btnElimina.getStyleClass().add("button-danger");
        btnElimina.setMaxWidth(Double.MAX_VALUE);

        if (a.getStato() != Annuncio.STATO_ANNUNCIO.DISPONIBILE) {
            // Se l'annuncio NON è disponibile (Affare fatto)
            btnElimina.setOpacity(0.0); // Nascondiamo il tasto ma teniamo lo spazio
            btnElimina.setDisable(true);

            // Creiamo un'etichetta fighissima per lo stato
            String testoStato = "Affare Concluso ✅";
            if (a instanceof AnnuncioScambio) testoStato = "Scambio Effettuato 🤝";
            if (a instanceof AnnuncioRegalo) testoStato = "Regalo Consegnato 🎉";

            Label lblStato = new Label(testoStato);
            lblStato.setStyle("-fx-text-fill: #27ae60; -fx-font-weight: bold; -fx-font-size: 15px;");

            actionArea.getChildren().addAll(btnElimina, lblStato);
        } else {
            // Se è ancora disponibile, mostriamo il tasto elimina normalmente
            btnElimina.setOnAction(e -> onEliminaAnnuncio(a));
            actionArea.getChildren().add(btnElimina);
        }

        card.getChildren().addAll(imgView, header, desc, extraInfo, spacer, actionArea);
        return card;
    }

    private String determinaTipo(Annuncio a) {
        if (a instanceof AnnuncioVendita) return "Vendita";
        if (a instanceof AnnuncioScambio) return "Scambio";
        return "Regalo";
    }

    private String determinaClasseBadge(Annuncio a) {
        if (a instanceof AnnuncioVendita) return "badge-vendita";
        if (a instanceof AnnuncioScambio) return "badge-scambio";
        return "badge-regalo";
    }

    private void onEliminaAnnuncio(Annuncio annuncio) {
        if (controller.EliminaAnnuncio(annuncio)) {
            Stage stage = (Stage) containerAnnunci.getScene().getWindow();
            gestoreScene.CambiaScena(Costanti.pathMieiAnnunci, "I Miei Annunci", stage,
                    "Annuncio eliminato correttamente!", Messaggio.TIPI.SUCCESS);
        } else {
            mostraMessaggioEsterno("Errore nell'eliminazione dell'annuncio", Messaggio.TIPI.ERROR);
        }
    }

    private void caricaImmagine(Annuncio annuncio, ImageView imgView) {
        try {
            String path = (annuncio.getOggetti() != null && !annuncio.getOggetti().isEmpty() &&
                    !annuncio.getOggetti().get(0).getImmagini().isEmpty()) ? annuncio.getOggetti().get(0).getImmagini().get(0) : null;
            if (path != null) {
                File file = new File(System.getProperty("user.dir") + File.separator + "dati_utenti" + File.separator + path);
                if (file.exists()) { imgView.setImage(new Image(file.toURI().toString(), 400, 300, true, true)); return; }
            }
            imgView.setImage(new Image(getClass().getResourceAsStream("/com/example/uninaswap/images/uninaLogo.png")));
        } catch (Exception e) { imgView.setImage(new Image(getClass().getResourceAsStream("/com/example/uninaswap/images/uninaLogo.png"))); }
    }

    @FXML public void onIndietroClick(ActionEvent event) { gestoreScene.CambiaScena(Costanti.pathHomePage, Costanti.homepage, event); }
    @Override public void mostraMessaggioEsterno(String t, Messaggio.TIPI tip) { if (notificaController != null) notificaController.mostraMessaggio(t, tip); }
}