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
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.File;
import java.util.List;

public class HomePageBoundary implements GestoreMessaggio {
    private final ControllerUninaSwap controller = ControllerUninaSwap.getInstance();

    @FXML private FlowPane containerAnnunci;
    @FXML private Messaggio notificaController;
    @FXML private NavBarComponent navBarComponentController;

    @FXML
    private void initialize() throws Exception {
        if (navBarComponentController != null) {
            navBarComponentController.setHomePageBoundary(this);
        }
        // Caricamento iniziale
        caricaCatalogoAnnunci(null, true);
    }

    /**
     * Versione per ricerca rapida o caricamento iniziale (Annunci o Utenti).
     */
    public void caricaCatalogoAnnunci(String query, boolean ricercaAnnuncio) throws Exception {
        containerAnnunci.getChildren().clear();

        if (ricercaAnnuncio) {
            // Richiama la versione completa con filtri null
            caricaCatalogoAnnunci(query, null, null, true);
        } else {
            // --- LOGICA RICERCA MULTI-UTENTE ---
            if (query == null || query.trim().isEmpty()) return;

            List<Utente> utentiTrovati = controller.cercaUtenti(query.trim());

            if (utentiTrovati == null || utentiTrovati.isEmpty()) {
                mostraMessaggioVuoto("Utente non trovato.", "Nessun utente corrisponde a '" + query + "'.");
                return;
            }

            for (Utente u : utentiTrovati) {
                containerAnnunci.getChildren().add(creaCardUtente(u));
            }
        }
    }

    /**
     * Versione COMPLETA con FILTRI.
     */
    public void caricaCatalogoAnnunci(String query, Oggetto.CONDIZIONE cond, Categoria cat, boolean ricercaAnnuncio) throws Exception {
        containerAnnunci.getChildren().clear();

        if (ricercaAnnuncio) {
            List<Annuncio> annunci = controller.FiltraAnnunciCatalogo(query, cond, cat);

            if (annunci == null || annunci.isEmpty()) {
                mostraMessaggioVuoto("Nessun risultato.", "La ricerca non ha prodotto risultati.");
                return;
            }

            for (Annuncio a : annunci) {
                containerAnnunci.getChildren().add(creaCardAnnuncio(a));
            }
        } else {
            // Se ricerca utente, delega al metodo semplificato
            caricaCatalogoAnnunci(query, false);
        }
    }

    private VBox creaCardUtente(Utente u) {
        VBox card = new VBox(15);
        card.getStyleClass().add("ad-card");
        card.setAlignment(Pos.CENTER);
        card.setPrefWidth(280);
        card.setPadding(new Insets(20));

        // Gestione Immagine Profilo
        ImageView imgView = new ImageView();
        imgView.setFitWidth(100);
        imgView.setFitHeight(100);
        caricaFotoProfilo(u, imgView);

        Circle clip = new Circle(50, 50, 50);
        imgView.setClip(clip);

        // Username
        Text username = new Text(u.getUsername());
        username.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-fill: #003366;");

        // Email
        Text email = new Text(u.getEmail());
        email.setStyle("-fx-font-size: 14px; -fx-fill: #666; -fx-font-style: italic;");

        // Container per i bottoni per gestirne la spaziatura
        VBox containerBottoni = new VBox(10);
        containerBottoni.setAlignment(Pos.CENTER);

        // Pulsante Vedi Annunci
        Button btnProfilo = new Button("Vedi Annunci");
        btnProfilo.getStyleClass().add("button");
        btnProfilo.setMinWidth(180); // Larghezza fissa per uniformità
        btnProfilo.setOnAction(e -> {
            containerAnnunci.getChildren().clear();
            List<Annuncio> annunciUtente = controller.OttieniAnnunciDiUtente(u.getId());
            if (annunciUtente == null || annunciUtente.isEmpty()) {
                mostraMessaggioVuoto("Nessun annuncio.", u.getUsername() + " non ha annunci.");
            } else {
                for (Annuncio a : annunciUtente) containerAnnunci.getChildren().add(creaCardAnnuncio(a));
            }
        });

        // --- NUOVO PULSANTE VEDI RECENSIONI ---
        Button btnRecensioni = new Button("Vedi Recensioni");
        btnRecensioni.getStyleClass().add("button");
        // Se vuoi differenziarlo graficamente puoi usare uno stile inline o una classe CSS diversa
        // btnRecensioni.setStyle("-fx-background-color: #28a745;");
        btnRecensioni.setMinWidth(180);
        btnRecensioni.setOnAction(e -> {
            // Qui andrà la logica per aprire la schermata delle recensioni dell'utente u
            System.out.println("Apertura recensioni per: " + u.getUsername());

            // Esempio di cambio scena se hai già il path:
            // gestoreScene.CambiaScena(Costanti.pathVediRecensioni, "Recensioni Utente", e);
        });

        containerBottoni.getChildren().addAll(btnProfilo, btnRecensioni);

        // Aggiungi tutto alla card
        card.getChildren().addAll(imgView, username, email, containerBottoni);
        return card;
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
        imgView.setSmooth(true);
        caricaFotoOggetto(a, imgView);

        Label badge = new Label(determinaTipo(a));
        badge.getStyleClass().addAll("badge-base", determinaClasseBadge(a));

        Text desc = new Text(a.getDescrizione());
        desc.setWrappingWidth(220);
        desc.getStyleClass().add("ad-description");

        card.getChildren().addAll(imgView, badge, desc);
        card.setOnMouseClicked(e -> apriDettaglioAnnuncio(a));

        return card;
    }

    // --- METODI DI SUPPORTO ---

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
                String path = a.getOggetti().get(0).getImmagini().get(0);
                File file = new File(System.getProperty("user.dir") + File.separator + "dati_utenti" + File.separator + path);
                if (file.exists()) {
                    iv.setImage(new Image(file.toURI().toString(), 0, 0, true, true, true));
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
        } catch (Exception e) {
            e.printStackTrace();
        }
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
    public javafx.scene.Scene getScene() { return containerAnnunci.getScene(); }
}