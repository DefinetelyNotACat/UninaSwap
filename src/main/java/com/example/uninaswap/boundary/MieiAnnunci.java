package com.example.uninaswap.boundary;

import com.example.uninaswap.Costanti;
import com.example.uninaswap.controller.ControllerUninaSwap;
import com.example.uninaswap.entity.Annuncio;
import com.example.uninaswap.entity.Utente;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class MieiAnnunci implements Initializable {

    @FXML private FlowPane containerAnnunci;
    @FXML private Text testoVuoto;

    private final ControllerUninaSwap controller = ControllerUninaSwap.getInstance();
    private final GestoreScene gestoreScene = new GestoreScene();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        caricaAnnunci();
    }

    private void caricaAnnunci() {
        containerAnnunci.getChildren().clear();

        Utente utente;
        try {
            utente = controller.getUtente();
        } catch (Exception e) {
            System.err.println("Utente non loggato");
            return;
        }

        // Chiamata al metodo che abbiamo aggiunto nel ControllerUninaSwap
        ArrayList<Annuncio> mieiAnnunci = controller.OttieniAnnunciDiUtente(utente.getId());

        if (mieiAnnunci == null || mieiAnnunci.isEmpty()) {
            testoVuoto.setVisible(true);
            testoVuoto.setManaged(true);
            return;
        }

        testoVuoto.setVisible(false);
        testoVuoto.setManaged(false);

        // Generazione dinamica delle card
        for (Annuncio annuncio : mieiAnnunci) {
            VBox card = creaCardAnnuncio(annuncio);
            containerAnnunci.getChildren().add(card);
        }
    }

    private VBox creaCardAnnuncio(Annuncio annuncio) {
        // --- Struttura Card (stile simile a Inventario e Homepage) ---
        VBox card = new VBox();
        card.getStyleClass().add("ad-card"); // Assicurati che questo stile sia in homepage.css
        card.setPrefWidth(260);
        card.setMinWidth(260);
        card.setMaxWidth(260);
        card.setSpacing(10);
        card.setAlignment(Pos.TOP_CENTER);

        // --- Immagine ---
        ImageView imgView = new ImageView();
        imgView.setFitWidth(240);
        imgView.setFitHeight(180);
        imgView.setPreserveRatio(true);
        caricaImmagine(annuncio, imgView);

        // --- Testo Descrizione ---
        Text descrizione = new Text(annuncio.getDescrizione());
        descrizione.getStyleClass().add("ad-description");
        descrizione.setWrappingWidth(240);
        descrizione.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);

        // --- Badge Tipo (Vendita/Scambio) ---
        Label badgeTipo = new Label(annuncio.getTipoAnnuncio());
        badgeTipo.getStyleClass().add("badge-base");

        if ("Vendita".equalsIgnoreCase(annuncio.getTipoAnnuncio())) {
            badgeTipo.getStyleClass().add("badge-vendita");
        } else if ("Scambio".equalsIgnoreCase(annuncio.getTipoAnnuncio())) {
            badgeTipo.getStyleClass().add("badge-scambio");
        } else {
            badgeTipo.getStyleClass().add("badge-regalo");
        }

        // --- Bottoni Azione (Elimina) ---
        HBox btnBox = new HBox(10);
        btnBox.setAlignment(Pos.CENTER);

        // Qui potresti aggiungere anche un tasto "Modifica" se previsto
        Button btnElimina = new Button("Elimina");
        btnElimina.getStyleClass().addAll("button-small", "button-danger");
        // Stile inline per renderlo rosso se non presente nel CSS
        btnElimina.setStyle("-fx-background-color: #dc3545; -fx-text-fill: white; -fx-cursor: hand;");

        btnElimina.setOnAction(e -> onEliminaAnnuncio(annuncio));

        btnBox.getChildren().addAll(badgeTipo, btnElimina);

        card.getChildren().addAll(imgView, descrizione, btnBox);
        return card;
    }

    private void onEliminaAnnuncio(Annuncio annuncio) {
        if (controller.EliminaAnnuncio(annuncio)) {
            System.out.println("Annuncio eliminato con successo");
            caricaAnnunci(); // Ricarica la lista per aggiornare la vista
        } else {
            System.err.println("Errore nell'eliminazione dell'annuncio");
            // Qui potresti mostrare un alert di errore
        }
    }

    private void caricaImmagine(Annuncio annuncio, ImageView imgView) {
        System.out.println("--- DEBUG ANNUNCIO ID: " + annuncio.getId() + " ---");

        try {
            String pathRelativo = null;

            // Controllo 1: L'annuncio ha oggetti?
            if (annuncio.getOggetti() == null || annuncio.getOggetti().isEmpty()) {
                System.out.println("ATTENZIONE: L'annuncio " + annuncio.getId() + " non ha oggetti associati.");
            } else {
                System.out.println("L'annuncio ha " + annuncio.getOggetti().size() + " oggetti.");

                // Controllo 2: Il primo oggetto ha immagini?
                if (annuncio.getOggetti().get(0).getImmagini() == null || annuncio.getOggetti().get(0).getImmagini().isEmpty()) {
                    System.out.println("ATTENZIONE: L'oggetto " + annuncio.getOggetti().get(0).getId() + " non ha immagini.");
                } else {
                    pathRelativo = annuncio.getOggetti().get(0).getImmagini().get(0);
                    System.out.println("Path trovato nel DB: " + pathRelativo);
                }
            }

            if (pathRelativo != null) {
                File file = new File(System.getProperty("user.dir") + File.separator + "dati_utenti" + File.separator + pathRelativo);
                System.out.println("Percorso finale calcolato: " + file.getAbsolutePath());

                if (file.exists()) {
                    imgView.setImage(new Image(file.toURI().toString(), 240, 180, true, true, true));
                    System.out.println("Immagine caricata con successo!");
                } else {
                    System.out.println("ERRORE: Il file non esiste sul disco a quel percorso.");
                    setDefaultImage(imgView);
                }
            } else {
                System.out.println("Path relativo nullo: imposto logo default.");
                setDefaultImage(imgView);
            }
        } catch (Exception e) {
            System.err.println("Eccezione nel caricamento: " + e.getMessage());
            setDefaultImage(imgView);
        }
    }
    private void setDefaultImage(ImageView iv) {
        iv.setImage(new Image(getClass().getResourceAsStream("/com/example/uninaswap/images/uninaLogo.png")));
    }

    @FXML
    public void onIndietroClick(ActionEvent event) {
        gestoreScene.CambiaScena(Costanti.pathHomePage, Costanti.homepage, event);
    }
}