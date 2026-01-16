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
import javafx.scene.layout.*;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import java.io.File;
import java.util.ArrayList;

public class GestioneOfferteBoundary {

    @FXML private VBox containerOfferte;
    @FXML private Button btnNavRicevute;
    @FXML private Button btnNavInviate;

    private final ControllerUninaSwap controller = ControllerUninaSwap.getInstance();
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
            e.printStackTrace();
            containerOfferte.getChildren().add(new Label("Errore nel caricamento delle offerte."));
        }
    }

    /**
     * Crea la card principale dell'offerta (Header, Oggetti proposti, Messaggio, Azioni)
     */
    private VBox creaCardOfferta(Offerta o, boolean isRicevuta) {
        VBox card = new VBox(15);
        card.getStyleClass().add("offer-card");

        // Determiniamo chi mostrare nella card
        Utente utenteControparte = isRicevuta ? o.getUtente() : o.getAnnuncio().getUtente();

        // --- 1. HEADER (Foto Profilo + Testi + Badge Stato) ---
        HBox header = new HBox(15);
        header.setAlignment(Pos.CENTER_LEFT);

        ImageView imgProfilo = new ImageView();
        imgProfilo.setFitWidth(50); imgProfilo.setFitHeight(50);
        imgProfilo.setClip(new Circle(25, 25, 25));
        caricaFotoProfilo(utenteControparte, imgProfilo);

        VBox infoUtente = new VBox(2);
        Text nomeUtente = new Text(utenteControparte != null ? utenteControparte.getUsername() : "Utente");
        nomeUtente.getStyleClass().add("offer-card-title");
        Text descAnnuncio = new Text(o.getAnnuncio().getDescrizione());
        descAnnuncio.getStyleClass().add("offer-card-subtitle");
        infoUtente.getChildren().addAll(nomeUtente, descAnnuncio);
        HBox.setHgrow(infoUtente, Priority.ALWAYS);

        Label statoLabel = new Label(o.getStato().toString().replace("_", " "));
        statoLabel.getStyleClass().add("status-badge");
        impostaColoreStato(statoLabel, o.getStato());
        header.getChildren().addAll(imgProfilo, infoUtente, statoLabel);

        // --- 2. CORPO DETTAGLI (Prezzo o Oggetti Scambio) ---
        VBox corpoDettagli = new VBox(10);
        if (o instanceof OffertaVendita ov) {
            Label l = new Label("💰 Proposta economica: " + ov.getPrezzoOffertaVendita() + " €");
            l.getStyleClass().add("offer-details-label");
            corpoDettagli.getChildren().add(l);
        } else if (o instanceof OffertaScambio os) {
            Label l = new Label("🔄 Oggetti proposti per lo scambio:");
            l.getStyleClass().add("offer-details-label");
            corpoDettagli.getChildren().add(l);

            FlowPane containerOggetti = new FlowPane(10, 10);
            containerOggetti.setPadding(new Insets(5, 0, 5, 0));
            for (Oggetto obj : os.getOggetti()) {
                containerOggetti.getChildren().add(creaMiniCardOggetto(obj));
            }
            corpoDettagli.getChildren().add(containerOggetti);
        } else {
            Label l = new Label("🎁 Richiesta di regalo");
            l.getStyleClass().add("offer-details-label");
            corpoDettagli.getChildren().add(l);
        }

        // --- 3. MESSAGGIO PERSONALE ---
        Text msgText = new Text("\"" + o.getMessaggio() + "\"");
        msgText.getStyleClass().add("offer-message-text");
        msgText.setWrappingWidth(650);

        card.getChildren().addAll(header, corpoDettagli, msgText);

        // --- 4. TASTI AZIONE (Solo se ricevuta e in attesa) ---
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

    /**
     * MINI CARD OGGETTO: Testo nero, a capo e foto reale
     */
    /**
     * MINI CARD OGGETTO COMPLETA: Immagine, Nome, Categorie e Condizione
     */
    private VBox creaMiniCardOggetto(Oggetto obj) {
        VBox miniCard = new VBox(5);
        miniCard.setAlignment(Pos.CENTER);
        miniCard.setStyle("-fx-background-color: #ffffff; -fx-padding: 10; -fx-background-radius: 10; " +
                "-fx-border-color: #bdc3c7; -fx-border-width: 1; -fx-border-radius: 10;");
        miniCard.setPrefWidth(140); // Leggermente più larga per far stare le categorie

        // 1. Immagine
        ImageView imgObj = new ImageView();
        imgObj.setFitWidth(110); imgObj.setFitHeight(80);
        imgObj.setPreserveRatio(true);
        if (obj.getImmagini() != null && !obj.getImmagini().isEmpty()) {
            File file = new File(System.getProperty("user.dir") + File.separator + "dati_utenti" + File.separator + obj.getImmagini().get(0));
            if (file.exists()) imgObj.setImage(new Image(file.toURI().toString(), 200, 160, true, true));
            else setDefaultItemImage(imgObj);
        } else { setDefaultItemImage(imgObj); }

        // 2. Nome (Nero e Grassetto)
        Label nome = new Label(obj.getNome());
        nome.setStyle("-fx-font-weight: bold; -fx-font-size: 12px; -fx-text-fill: #000000;");
        nome.setWrapText(true);
        nome.setTextAlignment(TextAlignment.CENTER);
        nome.setMaxWidth(120);

        // 3. SEZIONE CATEGORIE (I nuovi "Chip" colorati)
        FlowPane containerCategorie = new FlowPane(4, 4);
        containerCategorie.setAlignment(Pos.CENTER);
        for (Categoria c : obj.getCategorie()) {
            Label chip = new Label(c.getNome());
            chip.setStyle("-fx-font-size: 9px; -fx-text-fill: #003366; -fx-background-color: #e1f5fe; " +
                    "-fx-padding: 2 5; -fx-background-radius: 10; -fx-border-color: #b3e5fc; -fx-border-radius: 10;");
            containerCategorie.getChildren().add(chip);
        }

        // 4. Condizione (Badge scuro)
        Label cond = new Label(obj.getCondizione().toString().replace("_", " "));
        cond.setStyle("-fx-font-size: 10px; -fx-text-fill: #000000; -fx-background-color: #f1f2f6; " +
                "-fx-padding: 3 6; -fx-background-radius: 5; -fx-border-color: #dfe4ea; -fx-border-radius: 5;");
        cond.setWrapText(true);
        cond.setTextAlignment(TextAlignment.CENTER);

        miniCard.getChildren().addAll(imgObj, nome, containerCategorie, cond);
        return miniCard;
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

    private void setDefaultItemImage(ImageView iv) {
        iv.setImage(new Image(getClass().getResourceAsStream("/com/example/uninaswap/images/uninaLogo.png")));
    }

    private void impostaColoreStato(Label l, Offerta.STATO_OFFERTA s) {
        l.getStyleClass().removeAll("status-accepted", "status-rejected", "status-pending");
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