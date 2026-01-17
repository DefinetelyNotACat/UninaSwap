package com.example.uninaswap.boundary;

import com.example.uninaswap.Costanti;
import com.example.uninaswap.controller.ControllerUninaSwap;
import com.example.uninaswap.entity.*;
import com.example.uninaswap.interfaces.GestoreMessaggio;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;

import java.io.File;
import java.util.List;

public class HomePageBoundary implements GestoreMessaggio {
    private final ControllerUninaSwap controller = ControllerUninaSwap.getInstance();

    @FXML private FlowPane containerAnnunci;
    @FXML private Messaggio notificaController;
    @FXML private NavBarComponent navBarComponentController;

    private static String queryPrenotata = null;
    private static boolean cercaAnnunciPrenotato = true;

    public static void prenotaRicerca(String query, boolean cercaAnnunci) {
        queryPrenotata = query;
        cercaAnnunciPrenotato = cercaAnnunci;
    }

    public static String getQueryPrenotata() { return queryPrenotata; }

    @FXML
    private void initialize() throws Exception {
        if (navBarComponentController != null) {
            navBarComponentController.setHomePageBoundary(this);
        }

        if (queryPrenotata != null) {
            caricaCatalogoAnnunci(queryPrenotata, cercaAnnunciPrenotato);
            // Non resettiamo qui, lasciamo che NavBarComponent lo faccia dopo aver letto il testo
        } else {
            caricaCatalogoAnnunci(null, true);
        }
    }

    public void resetRicercaPrenotata() { queryPrenotata = null; }

    public void caricaCatalogoAnnunci(String query, boolean ricercaAnnuncio) throws Exception {
        containerAnnunci.getChildren().clear();
        if (ricercaAnnuncio) {
            caricaCatalogoAnnunci(query, null, null, true);
        } else {
            if (query == null || query.trim().isEmpty()) return;
            List<Utente> utentiTrovati = controller.cercaUtenti(query.trim());
            if (utentiTrovati == null || utentiTrovati.isEmpty()) {
                mostraMessaggioVuoto("Utente non trovato.", "Nessun utente corrisponde a '" + query + "'.");
                return;
            }
            for (Utente u : utentiTrovati) containerAnnunci.getChildren().add(creaCardUtente(u));
        }
    }

    public void caricaCatalogoAnnunci(String query, Oggetto.CONDIZIONE cond, Categoria cat, boolean ricercaAnnuncio) throws Exception {
        containerAnnunci.getChildren().clear();
        if (ricercaAnnuncio) {
            List<Annuncio> annunci = controller.FiltraAnnunciCatalogo(query, cond, cat);
            if (annunci == null || annunci.isEmpty()) {
                mostraMessaggioVuoto("Nessun risultato.", "La ricerca non ha prodotto risultati.");
                return;
            }
            for (Annuncio a : annunci) containerAnnunci.getChildren().add(creaCardAnnuncio(a));
        } else {
            caricaCatalogoAnnunci(query, false);
        }
    }

    /**
     * FIX ALLINEAMENTO CARD UTENTE
     */
    private VBox creaCardUtente(Utente u) {
        VBox card = new VBox(15);
        card.getStyleClass().add("ad-card");
        card.setAlignment(Pos.CENTER); // Centra i figli nel VBox
        card.setPrefWidth(280);
        card.setPadding(new Insets(25));

        // 1. Immagine Profilo
        ImageView imgView = new ImageView();
        imgView.setFitWidth(100);
        imgView.setFitHeight(100);
        caricaFotoProfilo(u, imgView);
        imgView.setClip(new Circle(50, 50, 50));

        // 2. Nome Utente (Usiamo LABEL per centramento fottuto)
        Label username = new Label(u.getUsername());
        username.setAlignment(Pos.CENTER);
        username.setMaxWidth(Double.MAX_VALUE);
        username.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: #003366;");

        // 3. Email (Usiamo LABEL)
        Label email = new Label(u.getEmail());
        email.setAlignment(Pos.CENTER);
        email.setMaxWidth(Double.MAX_VALUE);
        email.setStyle("-fx-font-size: 14px; -fx-text-fill: #666; -fx-font-style: italic;");

        // 4. Container Bottoni
        VBox containerBottoni = new VBox(12);
        containerBottoni.setAlignment(Pos.CENTER);
        containerBottoni.setPrefWidth(200);
        containerBottoni.setMaxWidth(200);

        Button btnProfilo = new Button("Vedi Annunci");
        btnProfilo.getStyleClass().add("button-primary");
        btnProfilo.setMaxWidth(Double.MAX_VALUE); // Riempie il containerBottoni che è centrato
        btnProfilo.setOnAction(e -> {
            containerAnnunci.getChildren().clear();
            List<Annuncio> annunciUtente = controller.OttieniAnnunciDiUtente(u.getId());
            if (annunciUtente.isEmpty()) mostraMessaggioVuoto("Nessun annuncio.", u.getUsername() + " non ha annunci.");
            else for (Annuncio a : annunciUtente) containerAnnunci.getChildren().add(creaCardAnnuncio(a));
        });

        Button btnRecensioni = new Button("Vedi Recensioni");
        btnRecensioni.getStyleClass().add("button-outline");
        btnRecensioni.setMaxWidth(Double.MAX_VALUE);
        btnRecensioni.setOnAction(e -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource(Costanti.pathRecensioni));
                Parent root = loader.load();
                Recensioni controllerRecensioni = loader.getController();
                controllerRecensioni.initData(u);
                Stage stage = (Stage) containerAnnunci.getScene().getWindow();
                stage.setScene(new Scene(root, stage.getScene().getWidth(), stage.getScene().getHeight()));
            } catch (Exception ex) { ex.printStackTrace(); }
        });

        containerBottoni.getChildren().addAll(btnProfilo, btnRecensioni);
        card.getChildren().addAll(imgView, username, email, containerBottoni);

        return card;
    }

    private VBox creaCardAnnuncio(Annuncio a) {
        VBox card = new VBox(12);
        card.getStyleClass().add("ad-card");
        card.setAlignment(Pos.TOP_CENTER);
        card.setPrefWidth(260);
        card.setPadding(new Insets(15));

        ImageView imgView = new ImageView();
        imgView.setFitWidth(230);
        imgView.setFitHeight(150);
        imgView.setPreserveRatio(true);
        caricaFotoOggetto(a, imgView);

        HBox headerInfo = new HBox(10);
        headerInfo.setAlignment(Pos.CENTER_LEFT);
        Label badge = new Label(determinaTipo(a));
        badge.getStyleClass().addAll("badge-base", determinaClasseBadge(a));
        Text location = new Text("📍 " + (a.getSede() != null ? a.getSede().getNomeSede() : "N/A"));
        location.getStyleClass().add("ad-location");
        headerInfo.getChildren().addAll(badge, location);

        Text desc = new Text(a.getDescrizione());
        desc.setWrappingWidth(230);
        desc.getStyleClass().add("ad-description");

        Text extraInfo = new Text();
        extraInfo.getStyleClass().add("ad-extra-info");
        if (a instanceof AnnuncioVendita av) extraInfo.setText("💰 " + av.getPrezzoMedio() + " €");
        else if (a instanceof AnnuncioScambio as) extraInfo.setText("🔄 " + as.getListaOggetti());
        else extraInfo.setText("🎁 REGALO");

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        card.getChildren().addAll(imgView, headerInfo, desc, spacer, extraInfo);
        card.setOnMouseClicked(e -> apriDettaglioAnnuncio(a));
        return card;
    }

    private void caricaFotoProfilo(Utente u, ImageView iv) {
        try {
            String path = u.getPathImmagineProfilo();
            if (path != null && !path.equals("default") && !path.isEmpty()) {
                File file = new File(System.getProperty("user.dir") + File.separator + "dati_utenti" + File.separator + path);
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

    private void caricaFotoOggetto(Annuncio a, ImageView iv) {
        try {
            if (a.getOggetti() != null && !a.getOggetti().isEmpty() && !a.getOggetti().get(0).getImmagini().isEmpty()) {
                File f = new File(System.getProperty("user.dir") + File.separator + "dati_utenti" + File.separator + a.getOggetti().get(0).getImmagini().get(0));
                if (f.exists()) {
                    iv.setImage(new Image(f.toURI().toString(), 400, 300, true, true));
                    return;
                }
            }
            iv.setImage(new Image(getClass().getResourceAsStream("/com/example/uninaswap/images/uninaLogo.png")));
        } catch (Exception e) {
            iv.setImage(new Image(getClass().getResourceAsStream("/com/example/uninaswap/images/uninaLogo.png")));
        }
    }

    private String determinaTipo(Annuncio a) {
        if (a instanceof AnnuncioVendita) return "VENDITA";
        if (a instanceof AnnuncioScambio) return "SCAMBIO";
        return "REGALO";
    }

    private String determinaClasseBadge(Annuncio a) {
        if (a instanceof AnnuncioVendita) return "badge-vendita";
        if (a instanceof AnnuncioScambio) return "badge-scambio";
        return "badge-regalo";
    }

    private void apriDettaglioAnnuncio(Annuncio annuncio) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(Costanti.pathDettaglioAnnuncio));
            Parent root = loader.load();
            DettaglioAnnuncioBoundary controllerDettaglio = loader.getController();
            controllerDettaglio.initData(annuncio);
            Stage stage = (Stage) containerAnnunci.getScene().getWindow();
            stage.setScene(new Scene(root, stage.getScene().getWidth(), stage.getScene().getHeight()));
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void mostraMessaggioVuoto(String titolo, String sottotitolo) {
        VBox boxVuoto = new VBox(15);
        boxVuoto.setAlignment(Pos.CENTER);
        boxVuoto.setMinWidth(800);
        boxVuoto.setPadding(new Insets(50, 0, 0, 0));
        Text t1 = new Text(titolo);
        t1.setStyle("-fx-font-size: 20px; -fx-fill: #888; -fx-font-weight: bold;");
        Text t2 = new Text(sottotitolo);
        t2.setStyle("-fx-font-size: 14px; -fx-fill: #aaa;");
        boxVuoto.getChildren().addAll(t1, t2);
        containerAnnunci.getChildren().add(boxVuoto);
    }

    @Override public void mostraMessaggioEsterno(String testo, Messaggio.TIPI tipo) {
        if(notificaController != null) notificaController.mostraMessaggio(testo, tipo);
    }

    public void svuotaCatalogo(){ containerAnnunci.getChildren().clear(); }
    public Scene getScene() { return containerAnnunci.getScene(); }
}