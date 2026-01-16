package com.example.uninaswap.boundary;

import com.example.uninaswap.controller.ControllerUninaSwap;
import com.example.uninaswap.entity.*;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import java.io.File;
import java.util.ArrayList;

public class GestioneOfferteBoundary {

    @FXML private VBox containerOfferte;
    @FXML private Button btnNavRicevute;
    @FXML private Button btnNavInviate;

    private ControllerUninaSwap controller = ControllerUninaSwap.getInstance();
    private boolean visualizzandoRicevute = true;
    private static final String CLASS_ACTIVE = "nav-btn-active";

    @FXML
    public void initialize() {
        mostraRicevute();
    }

    @FXML
    public void mostraRicevute() {
        visualizzandoRicevute = true;
        aggiornaStatoGrafico(btnNavRicevute, btnNavInviate);
        caricaOfferte();
    }

    @FXML
    public void mostraInviate() {
        visualizzandoRicevute = false;
        aggiornaStatoGrafico(btnNavInviate, btnNavRicevute);
        caricaOfferte();
    }

    private void aggiornaStatoGrafico(Button daAttivare, Button daDisattivare) {
        daAttivare.getStyleClass().removeAll(CLASS_ACTIVE);
        daDisattivare.getStyleClass().removeAll(CLASS_ACTIVE);
        daAttivare.getStyleClass().add(CLASS_ACTIVE);
    }

    private void caricaOfferte() {
        containerOfferte.getChildren().clear();
        try {
            ArrayList<Offerta> lista = visualizzandoRicevute
                    ? controller.OttieniOfferteRicevute()
                    : controller.OttieniLeMieOfferte();

            if (lista == null || lista.isEmpty()) {
                containerOfferte.getChildren().add(creaLabelVuota(visualizzandoRicevute ? "Nessuna offerta ricevuta." : "Non hai inviato offerte."));
            } else {
                for (Offerta o : lista) {
                    containerOfferte.getChildren().add(creaCardOfferta(o, visualizzandoRicevute));
                }
            }
        } catch (Exception e) {
            containerOfferte.getChildren().add(new Label("Errore nel caricamento."));
        }
    }

    private VBox creaCardOfferta(Offerta o, boolean isRicevuta) {
        VBox card = new VBox(15);
        card.getStyleClass().add("offer-card");

        // Identifichiamo l'utente da mostrare (L'offerente o il Venditore)
        Utente utenteDaMostrare = isRicevuta ? o.getUtente() : o.getAnnuncio().getUtente();

        // --- HEADER CON IMMAGINE E TESTO ---
        HBox header = new HBox(15);
        header.setAlignment(Pos.CENTER_LEFT);

        ImageView imgProfilo = new ImageView();
        imgProfilo.setFitWidth(50);
        imgProfilo.setFitHeight(50);
        Circle clip = new Circle(25, 25, 25);
        imgProfilo.setClip(clip);
        caricaFotoProfilo(utenteDaMostrare, imgProfilo);

        VBox infoUtente = new VBox(2);
        Text nomeUtente = new Text(utenteDaMostrare != null ? utenteDaMostrare.getUsername() : "Utente sconosciuto");
        nomeUtente.getStyleClass().add("offer-card-title");

        Text infoContesto = new Text(isRicevuta ? "Ti ha fatto un'offerta per:" : "Hai fatto un'offerta a:");
        infoContesto.setStyle("-fx-fill: #95a5a6; -fx-font-size: 11px;");

        Text descAnnuncio = new Text(o.getAnnuncio().getDescrizione());
        descAnnuncio.getStyleClass().add("offer-card-subtitle");

        infoUtente.getChildren().addAll(infoContesto, nomeUtente, descAnnuncio);
        HBox.setHgrow(infoUtente, Priority.ALWAYS);

        // Badge Stato
        Label statoLabel = new Label(o.getStato().toString().replace("_", " "));
        statoLabel.getStyleClass().add("status-badge");
        impostaColoreStato(statoLabel, o.getStato());

        header.getChildren().addAll(imgProfilo, infoUtente, statoLabel);

        // --- DETTAGLI ---
        String dettagliStr = (o instanceof OffertaVendita ov) ? "Proposta economica: " + ov.getPrezzoOffertaVendita() + " €" : "Tipo: " + (o instanceof OffertaScambio ? "Scambio" : "Regalo");
        Label details = new Label(dettagliStr);
        details.getStyleClass().add("offer-details-label");

        Text msg = new Text("\"" + o.getMessaggio() + "\"");
        msg.getStyleClass().add("offer-message-text");
        msg.setWrappingWidth(600);

        card.getChildren().addAll(header, details, msg);

        // Pulsanti Azione
        if (isRicevuta && o.getStato() == Offerta.STATO_OFFERTA.IN_ATTESA) {
            HBox azioni = new HBox(15);
            azioni.setAlignment(Pos.CENTER_RIGHT);
            Button btnAccetta = new Button("Accetta");
            btnAccetta.getStyleClass().add("btn-accept");
            Button btnRifiuta = new Button("Rifiuta");
            btnRifiuta.getStyleClass().add("btn-reject");
            btnAccetta.setOnAction(e -> cambiaStato(o, Offerta.STATO_OFFERTA.ACCETTATA));
            btnRifiuta.setOnAction(e -> cambiaStato(o, Offerta.STATO_OFFERTA.RIFIUTATA));
            azioni.getChildren().addAll(btnRifiuta, btnAccetta);
            card.getChildren().add(azioni);
        }

        return card;
    }

    private void caricaFotoProfilo(Utente u, ImageView iv) {
        try {
            if (u != null && u.getPathImmagineProfilo() != null && !u.getPathImmagineProfilo().isEmpty()) {
                File file = new File(System.getProperty("user.dir") + File.separator + "dati_utenti" + File.separator + u.getPathImmagineProfilo());
                if (file.exists()) {
                    iv.setImage(new Image(file.toURI().toString()));
                    return;
                }
            }
            iv.setImage(new Image(getClass().getResourceAsStream("/com/example/uninaswap/images/immagineProfiloDefault.jpg")));
        } catch (Exception e) {
            iv.setImage(new Image(getClass().getResourceAsStream("/com/example/uninaswap/images/immagineProfiloDefault.jpg")));
        }
    }

    private void impostaColoreStato(Label l, Offerta.STATO_OFFERTA s) {
        switch (s) {
            case ACCETTATA: l.getStyleClass().add("status-accepted"); break;
            case RIFIUTATA: l.getStyleClass().add("status-rejected"); break;
            default: l.getStyleClass().add("status-pending"); break;
        }
    }

    private Label creaLabelVuota(String testo) {
        Label l = new Label(testo);
        l.setStyle("-fx-text-fill: #95a5a6; -fx-font-size: 16px; -fx-padding: 50 0 0 0;");
        return l;
    }

    private void cambiaStato(Offerta o, Offerta.STATO_OFFERTA nuovoStato) {
        if (controller.GestisciStatoOfferta(o, nuovoStato)) {
            caricaOfferte();
        }
    }
}