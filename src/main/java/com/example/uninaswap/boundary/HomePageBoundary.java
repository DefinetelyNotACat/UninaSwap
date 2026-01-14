package com.example.uninaswap.boundary;

import com.example.uninaswap.controller.ControllerUninaSwap;
import com.example.uninaswap.entity.*;
import com.example.uninaswap.interfaces.GestoreMessaggio;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import java.io.File;
import java.util.List;

public class HomePageBoundary implements GestoreMessaggio {
    private final ControllerUninaSwap controller = ControllerUninaSwap.getInstance();

    @FXML private FlowPane containerAnnunci;
    @FXML private Messaggio notificaController;

    // JavaFX inietta automaticamente il controller dell'include usando: [id] + "Controller"
    @FXML private NavBarComponent navBarComponentController;

    @FXML
    private void initialize() {
        if (navBarComponentController != null) {
            // Colleghiamo la navbar a questa istanza di HomePage
            navBarComponentController.setHomePageBoundary(this);
        }
        // Caricamento iniziale
        caricaCatalogoAnnunci(null);
    }

    public void caricaCatalogoAnnunci(String query) {
        containerAnnunci.getChildren().clear();
        List<Annuncio> annunci;

        if (query == null || query.trim().isEmpty()) {
            annunci = controller.OttieniAnnunciNonMiei();
        } else {
            annunci = controller.OttieniAnnunciRicercaUtente(query.trim());
        }

        // GESTIONE RICERCA SENZA RISULTATI
        if (annunci == null || annunci.isEmpty()) {
            VBox boxVuoto = new VBox(15);
            boxVuoto.setAlignment(Pos.CENTER);
            boxVuoto.setMinWidth(800); // Assicura che sia centrato nella griglia
            boxVuoto.setPadding(new Insets(50, 0, 0, 0));

            Text t1 = new Text("Nessun annuncio trovato.");
            t1.setStyle("-fx-font-size: 20px; -fx-fill: #888; -fx-font-weight: bold;");

            Text t2 = new Text("La ricerca per '" + (query == null ? "" : query) + "' non ha prodotto risultati.");
            t2.setStyle("-fx-font-size: 14px; -fx-fill: #aaa;");

            boxVuoto.getChildren().addAll(t1, t2);
            containerAnnunci.getChildren().add(boxVuoto);
            return;
        }

        for (Annuncio a : annunci) {
            containerAnnunci.getChildren().add(creaCardAnnuncio(a));
        }
    }

    private VBox creaCardAnnuncio(Annuncio a) {
        VBox card = new VBox(10);
        card.getStyleClass().add("ad-card");
        card.setAlignment(Pos.TOP_CENTER);
        card.setPrefWidth(250);

        ImageView imgView = new ImageView();
        imgView.setFitWidth(230);
        imgView.setFitHeight(160);
        imgView.setPreserveRatio(true);

        try {
            if (!a.getOggetti().isEmpty() && !a.getOggetti().get(0).getImmagini().isEmpty()) {
                String path = a.getOggetti().get(0).getImmagini().get(0);
                imgView.setImage(new Image(new File(path).toURI().toString()));
            } else {
                imgView.setImage(new Image(getClass().getResourceAsStream("/com/example/uninaswap/images/uninaLogo.png")));
            }
        } catch (Exception e) {
            imgView.setImage(new Image(getClass().getResourceAsStream("/com/example/uninaswap/images/uninaLogo.png")));
        }

        Label badge = new Label();
        badge.getStyleClass().add("badge-base");
        String infoExtra = "";

        if (a instanceof AnnuncioVendita av) {
            badge.setText("VENDITA");
            badge.getStyleClass().add("badge-vendita");
            infoExtra = av.getPrezzoMedio() + " €";
        } else if (a instanceof AnnuncioScambio as) {
            badge.setText("SCAMBIO");
            badge.getStyleClass().add("badge-scambio");
            infoExtra = "Cerco: " + as.getListaOggetti();
        } else {
            badge.setText("REGALO");
            badge.getStyleClass().add("badge-regalo");
            infoExtra = "Gratis";
        }

        Text desc = new Text(a.getDescrizione());
        desc.setWrappingWidth(220);
        desc.getStyleClass().add("ad-description");

        Text sede = new Text("📍 " + (a.getSede() != null ? a.getSede().getNomeSede() : "N/A"));
        sede.getStyleClass().add("ad-location");

        HBox footer = new HBox();
        footer.setAlignment(Pos.CENTER_RIGHT);
        Text extra = new Text(infoExtra);
        extra.getStyleClass().add("ad-extra-info");
        footer.getChildren().add(extra);

        card.getChildren().addAll(imgView, badge, desc, sede, footer);
        return card;
    }

    @Override
    public void mostraMessaggioEsterno(String testo, Messaggio.TIPI tipo) {
        if(notificaController != null) notificaController.mostraMessaggio(testo, tipo);
    }
}