package com.example.uninaswap.boundary;

import com.example.uninaswap.Costanti;
import com.example.uninaswap.controller.ControllerUninaSwap;
import com.example.uninaswap.entity.*;
import com.example.uninaswap.interfaces.GestoreMessaggio;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
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

            for (Annuncio annuncio : mieiAnnunci) {
                containerAnnunci.getChildren().add(creaCardAnnuncio(annuncio));
            }
        } catch (Exception e) { e.printStackTrace(); }
    }

    private VBox creaCardAnnuncio(Annuncio annuncio) {
        VBox card = new VBox();
        card.getStyleClass().add("ad-card");

        ImageView imgView = new ImageView();
        imgView.setFitWidth(240);
        imgView.setFitHeight(160);
        imgView.setPreserveRatio(true);
        caricaImmagine(annuncio, imgView);

        HBox header = new HBox();
        header.getStyleClass().add("ad-header");

        Label badge = new Label(determinaTipo(annuncio).toUpperCase());
        badge.getStyleClass().addAll("badge-base", determinaClasseBadge(annuncio));

        Text sede = new Text("📍 " + (annuncio.getSede() != null ? annuncio.getSede().getNomeSede() : "N/A"));
        sede.getStyleClass().add("ad-location");
        header.getChildren().addAll(badge, sede);

        Text desc = new Text(annuncio.getDescrizione());
        desc.getStyleClass().add("ad-description");

        Text extraInfo = new Text();
        extraInfo.getStyleClass().add("ad-extra-info");
        if (annuncio instanceof AnnuncioVendita av) {
            extraInfo.setText("💰 " + av.getPrezzoMedio() + " €");
        } else if (annuncio instanceof AnnuncioScambio as) {
            extraInfo.setText("🔄 Cerco: " + as.getListaOggetti());
        } else {
            extraInfo.setText("🎁 REGALO");
        }

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        StackPane actionArea = new StackPane();
        actionArea.getStyleClass().add("action-area");

        Button btnElimina = new Button("🗑 Elimina Annuncio");
        btnElimina.getStyleClass().add("button-danger-full");

        if (annuncio.getStato() != Annuncio.STATO_ANNUNCIO.DISPONIBILE) {
            btnElimina.setVisible(false);
            btnElimina.setManaged(false);

            String testoStato = "Affare Concluso ✅";
            if (annuncio instanceof AnnuncioScambio) testoStato = "Scambio Effettuato 🤝";
            if (annuncio instanceof AnnuncioRegalo) testoStato = "Regalo Consegnato 🎉";

            Label lblStato = new Label(testoStato);
            lblStato.getStyleClass().add("lbl-stato-concluso");
            actionArea.getChildren().add(lblStato);
        } else {
            btnElimina.setOnAction(e -> onEliminaAnnuncio(annuncio));
            actionArea.getChildren().add(btnElimina);
        }

        card.getChildren().addAll(imgView, header, desc, extraInfo, spacer, actionArea);
        return card;
    }

    private String determinaTipo(Annuncio annuncio) {
        if (annuncio instanceof AnnuncioVendita) return "Vendita";
        if (annuncio instanceof AnnuncioScambio) return "Scambio";
        return "Regalo";
    }

    private String determinaClasseBadge(Annuncio annuncio) {
        if (annuncio instanceof AnnuncioVendita) return "badge-vendita";
        if (annuncio instanceof AnnuncioScambio) return "badge-scambio";
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

    private void caricaImmagine(Annuncio annuncio, ImageView imageView) {
        try {
            String path = (annuncio.getOggetti() != null && !annuncio.getOggetti().isEmpty() &&
                    !annuncio.getOggetti().get(0).getImmagini().isEmpty()) ? annuncio.getOggetti().get(0).getImmagini().get(0) : null;
            if (path != null) {
                File file = new File(System.getProperty("user.dir") + File.separator + "dati_utenti" + File.separator + path);
                if (file.exists()) { imageView.setImage(new Image(file.toURI().toString(), 400, 300, true, true)); return; }
            }
            imageView.setImage(new Image(getClass().getResourceAsStream("/com/example/uninaswap/images/uninaLogo.png")));
        } catch (Exception e) { imageView.setImage(new Image(getClass().getResourceAsStream("/com/example/uninaswap/images/uninaLogo.png"))); }
    }

    @FXML public void onIndietroClick(ActionEvent actionEvent) { gestoreScene.CambiaScena(Costanti.pathHomePage, Costanti.homepage, actionEvent); }

    @Override public void mostraMessaggioEsterno(String messaggio, Messaggio.TIPI tipi) { if (notificaController != null) notificaController.mostraMessaggio(messaggio, tipi); }

}   