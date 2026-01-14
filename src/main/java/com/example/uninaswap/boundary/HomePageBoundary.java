package com.example.uninaswap.boundary;

import com.example.uninaswap.controller.ControllerUninaSwap;
import com.example.uninaswap.entity.*;
import com.example.uninaswap.interfaces.GestoreMessaggio;
import javafx.fxml.FXML;
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
    @FXML private NavBarComponent navBarComponentController;

    @FXML
    private void initialize() {
        if (navBarComponentController != null) {
            navBarComponentController.initialize();
        }
        caricaCatalogoAnnunci();
    }

    private void caricaCatalogoAnnunci() {
        containerAnnunci.getChildren().clear();
        List<Annuncio> annunci = controller.OttieniAnnunciNonMiei();

        if (annunci == null || annunci.isEmpty()) {
            containerAnnunci.getChildren().add(new Text("Nessun annuncio disponibile al momento."));
            return;
        }

        for (Annuncio a : annunci) {
            VBox card = creaCardAnnuncio(a);
            containerAnnunci.getChildren().add(card);
        }
    }

    private VBox creaCardAnnuncio(Annuncio a) {
        VBox card = new VBox(10);
        card.getStyleClass().add("ad-card");
        card.setAlignment(Pos.TOP_CENTER);
        card.setPrefWidth(250);

        // --- IMMAGINE (Prende la prima dell'oggetto principale) ---
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

        // --- BADGE TIPOLOGIA ---
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

        // --- TESTI ---
        Text desc = new Text(a.getDescrizione());
        desc.setWrappingWidth(220);
        desc.getStyleClass().add("ad-description");

        Text sede = new Text("📍 " + (a.getSede() != null ? a.getSede().getNomeSede() : "Sede non specificata"));
        sede.getStyleClass().add("ad-location");

        HBox footer = new HBox();
        footer.setAlignment(Pos.CENTER_RIGHT);
        Text extra = new Text(infoExtra);
        extra.getStyleClass().add("ad-extra-info");
        footer.getChildren().add(extra);

        card.getChildren().addAll(imgView, badge, desc, sede, footer);

        // Effetto click
        card.setOnMouseClicked(e -> System.out.println("Hai cliccato l'annuncio ID: " + a.getId()));

        return card;
    }

    @Override
    public void mostraMessaggioEsterno(String testo, Messaggio.TIPI tipo) {
        if(notificaController != null) notificaController.mostraMessaggio(testo, tipo);
    }
}