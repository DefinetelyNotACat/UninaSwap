package com.example.uninaswap.boundary;

import com.example.uninaswap.controller.ControllerUninaSwap;
import com.example.uninaswap.entity.*;
import com.example.uninaswap.interfaces.GestoreMessaggio;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.shape.Circle;
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
    private void initialize() throws Exception {
        if (navBarComponentController != null) {
            // Colleghiamo la navbar a questa istanza di HomePage
            navBarComponentController.setHomePageBoundary(this);
        }
        // Caricamento iniziale degli annunci
        caricaCatalogoAnnunci(null, true);
    }

    public void caricaCatalogoAnnunci(String query, boolean ricercaAnnuncio) throws Exception {
        containerAnnunci.getChildren().clear();

        if (ricercaAnnuncio) {
            // --- LOGICA RICERCA ANNUNCI ---
            List<Annuncio> annunci;
            if (query == null || query.trim().isEmpty()) {
                annunci = controller.OttieniAnnunciNonMiei();
            } else {
                annunci = controller.OttieniAnnunciRicercaUtente(query.trim());
            }

            if (annunci == null || annunci.isEmpty()) {
                if(query != null && !query.isEmpty())
                    mostraMessaggioVuoto("Nessun annuncio trovato.", "La ricerca per '" + query + "' non ha prodotto risultati.");
                else
                    mostraMessaggioVuoto("Nessun annuncio trovato", "Non ci sono ancora annunci disponibili.");
                return;
            }

            for (Annuncio a : annunci) {
                containerAnnunci.getChildren().add(creaCardAnnuncio(a));
            }
        } else {
            // --- LOGICA RICERCA UTENTE ---
            if (query == null || query.trim().isEmpty()) return;

            Utente utenteTrovato = controller.trovaUtente(query.trim());

            if (utenteTrovato == null) {
                mostraMessaggioVuoto("Utente non trovato.", "Nessun utente con nickname '" + query + "' (Case Sensitive).");
                return;
            }

            // Aggiungiamo la card dell'utente trovato
            containerAnnunci.getChildren().add(creaCardUtente(utenteTrovato));
        }
    }

    private VBox creaCardUtente(Utente u) {
        VBox card = new VBox(15);
        card.getStyleClass().add("ad-card");
        card.setAlignment(Pos.CENTER);
        card.setPrefWidth(280);
        card.setPadding(new Insets(20));

        ImageView imgView = new ImageView();
        imgView.setFitWidth(100);
        imgView.setFitHeight(100);

        try {
            String path = u.getPathImmagineProfilo();
            if (path != null && !path.equals("default") && !path.isEmpty()) {
                File file = new File(System.getProperty("user.dir") + File.separator + "dati_utenti" + File.separator + path);
                System.out.println("DEBUG: Cerco l'immagine qui -> " + file.getAbsolutePath());
                if (file.exists()) {
                    imgView.setImage(new Image(file.toURI().toString()));
                } else {
                    imgView.setImage(new Image(getClass().getResourceAsStream("/com/example/uninaswap/images/immagineProfiloDefault.jpg")));
                }
            } else {
                imgView.setImage(new Image(getClass().getResourceAsStream("/com/example/uninaswap/images/immagineProfiloDefault.jpg")));
            }
        } catch (Exception e) {
            imgView.setImage(new Image(getClass().getResourceAsStream("/com/example/uninaswap/images/immagineProfiloDefault.jpg")));
        }

        Circle clip = new Circle(50, 50, 50);
        imgView.setClip(clip);

        Text username = new Text(u.getUsername());
        username.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-fill: #003366;");

        Text matricola = new Text("Matricola: " + u.getMatricola());
        matricola.setStyle("-fx-font-size: 14px; -fx-fill: #666;");

        Text email = new Text(u.getEmail());
        email.setStyle("-fx-font-size: 13px; -fx-fill: #888; -fx-font-style: italic;");

        Button btnProfilo = new Button("Vedi Profilo");
        btnProfilo.getStyleClass().add("button");
        btnProfilo.setOnAction(e -> System.out.println("Navigazione al profilo di: " + u.getUsername()));

        card.getChildren().addAll(imgView, username, matricola, email, btnProfilo);
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

        // --- LOGICA CARICAMENTO FOTO OGGETTO ---
        try {
            // Verifichiamo che la gerarchia non sia nulla (Annuncio -> Oggetti -> Immagini)
            if (a.getOggetti() != null && !a.getOggetti().isEmpty() &&
                    a.getOggetti().get(0).getImmagini() != null && !a.getOggetti().get(0).getImmagini().isEmpty()) {

                String pathRelativo = a.getOggetti().get(0).getImmagini().get(0);

                // Ricostruiamo il path assoluto puntando alla cartella dati_utenti
                // Il path nel DB è tipo: "1/immagini_utente/immagine_123.jpg"
                File file = new File(System.getProperty("user.dir") + File.separator + "dati_utenti" + File.separator + pathRelativo);

                if (file.exists()) {
                    // Carichiamo l'immagine con dimensioni fisse per ottimizzare la memoria
                    imgView.setImage(new Image(file.toURI().toString(), 230, 160, true, true));
                } else {
                    System.err.println("File non trovato: " + file.getAbsolutePath());
                    imgView.setImage(new Image(getClass().getResourceAsStream("/com/example/uninaswap/images/uninaLogo.png")));
                }
            } else {
                imgView.setImage(new Image(getClass().getResourceAsStream("/com/example/uninaswap/images/uninaLogo.png")));
            }
        } catch (Exception e) {
            System.err.println("Errore caricamento immagine annuncio: " + e.getMessage());
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

        // Nome sede (caricato dalla JOIN)
        String nomeSede = (a.getSede() != null && a.getSede().getNomeSede() != null) ? a.getSede().getNomeSede() : "N/A";
        Text sede = new Text("📍 " + nomeSede);
        sede.getStyleClass().add("ad-location");

        HBox footer = new HBox();
        footer.setAlignment(Pos.CENTER_RIGHT);
        Text extra = new Text(infoExtra);
        extra.getStyleClass().add("ad-extra-info");
        footer.getChildren().add(extra);

        card.getChildren().addAll(imgView, badge, desc, sede, footer);

        // Effetto click sull'annuncio
        card.setOnMouseClicked(e -> System.out.println("Hai cliccato l'annuncio ID: " + a.getId()));

        return card;
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

    @Override
    public void mostraMessaggioEsterno(String testo, Messaggio.TIPI tipo) {
        if(notificaController != null) notificaController.mostraMessaggio(testo, tipo);
    }

    public void svuotaCatalogo(){
        containerAnnunci.getChildren().clear();
    }
}