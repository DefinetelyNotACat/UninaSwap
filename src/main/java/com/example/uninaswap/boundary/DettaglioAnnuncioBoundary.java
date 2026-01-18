package com.example.uninaswap.boundary;

import com.example.uninaswap.Costanti;
import com.example.uninaswap.controller.ControllerUninaSwap;
import com.example.uninaswap.entity.*;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.File;
import java.util.List;

public class DettaglioAnnuncioBoundary {

    @FXML private ImageView immagineAnnuncio;
    @FXML private HBox containerMiniature;
    @FXML private ScrollPane scrollMiniature;
    @FXML private ImageView immagineProfiloVenditore;
    @FXML private Label badgeTipo;
    @FXML private Text txtSede;
    @FXML private Text txtTitoloDescrizione;
    @FXML private Text txtDettagliSpecifici;
    @FXML private Text txtPrezzoMinimo;
    @FXML private Text txtCondizioni;
    @FXML private Text txtVenditore;
    @FXML private Text txtEmailVenditore;
    @FXML private Button btnFaiOfferta;

    private Annuncio annuncioCorrente;
    private final ControllerUninaSwap controller = ControllerUninaSwap.getInstance();

    public void initData(Annuncio annuncio) {
        this.annuncioCorrente = annuncio;
        popolaCampi();
    }

    private void popolaCampi() {
        if (annuncioCorrente == null) return;

        txtTitoloDescrizione.setText(annuncioCorrente.getDescrizione());
        txtSede.setText("📍 " + (annuncioCorrente.getSede() != null ? annuncioCorrente.getSede().getNomeSede() : "Sede non specificata"));

        // Caricamento Immagini (Principale + Miniature)
        gestisciImmaginiOggetto();

        Utente venditore = annuncioCorrente.getUtente();
        if (venditore != null) {
            txtVenditore.setText(venditore.getUsername());
            txtEmailVenditore.setText(venditore.getEmail());
            caricaFotoProfiloVenditore(venditore);
        } else {
            txtVenditore.setText("Utente #" + annuncioCorrente.getUtenteId());
            setFotoProfiloDefault();
        }

        if (annuncioCorrente.getOggetti() != null && !annuncioCorrente.getOggetti().isEmpty()) {
            txtCondizioni.setText(annuncioCorrente.getOggetti().get(0).getCondizione().toString().replace("_", " "));
        }

        configuraBadgeEPrezzi();

        // Controllo se sono io il proprietario
        try {
            Utente loggato = controller.getUtente();
            if (loggato != null && annuncioCorrente.getUtenteId() == loggato.getId()) {
                btnFaiOfferta.setVisible(false);
                btnFaiOfferta.setManaged(false);
            }
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void gestisciImmaginiOggetto() {
        containerMiniature.getChildren().clear();
        if (annuncioCorrente.getOggetti() == null || annuncioCorrente.getOggetti().isEmpty()) {
            setDefaultImage();
            scrollMiniature.setVisible(false);
            return;
        }

        List<String> paths = annuncioCorrente.getOggetti().get(0).getImmagini();
        if (paths == null || paths.isEmpty()) {
            setDefaultImage();
            scrollMiniature.setVisible(false);
            return;
        }

        // Imposta la prima immagine come principale
        caricaImmaginePrincipale(paths.get(0));

        // Se ci sono più immagini, mostra le miniature
        if (paths.size() > 1) {
            scrollMiniature.setVisible(true);
            for (String path : paths) {
                containerMiniature.getChildren().add(creaMiniatura(path));
            }
        } else {
            scrollMiniature.setVisible(false);
        }
    }

    private void caricaImmaginePrincipale(String path) {
        File file = new File(System.getProperty("user.dir") + File.separator + "dati_utenti" + File.separator + path);
        if (file.exists()) {
            immagineAnnuncio.setImage(new Image(file.toURI().toString(), 800, 800, true, true));
        } else {
            setDefaultImage();
        }
    }

    private StackPane creaMiniatura(String path) {
        File file = new File(System.getProperty("user.dir") + File.separator + "dati_utenti" + File.separator + path);
        ImageView miniView = new ImageView();
        miniView.setFitWidth(60);
        miniView.setFitHeight(60);
        miniView.setPreserveRatio(true);

        if (file.exists()) {
            miniView.setImage(new Image(file.toURI().toString(), 120, 120, true, true));
        }

        StackPane wrapper = new StackPane(miniView);
        wrapper.getStyleClass().add("thumbnail-wrapper");
        wrapper.setOnMouseClicked(e -> caricaImmaginePrincipale(path));
        return wrapper;
    }

    private void caricaFotoProfiloVenditore(Utente utente) {
        String path = utente.getPathImmagineProfilo();
        if (path != null && !path.equals("default") && !path.isEmpty()) {
            File file = new File(System.getProperty("user.dir") + File.separator + "dati_utenti" + File.separator + path);
            if (file.exists()) {
                Image img = new Image(file.toURI().toString());
                impostaImmagineQuadrata(immagineProfiloVenditore, img);
                return;
            }
        }
        setFotoProfiloDefault();
    }

    private void impostaImmagineQuadrata(ImageView iv, Image img) {
        iv.setImage(img);
        double side = Math.min(img.getWidth(), img.getHeight());
        double x = (img.getWidth() - side) / 2;
        double y = (img.getHeight() - side) / 2;
        iv.setViewport(new Rectangle2D(x, y, side, side));

        Circle clip = new Circle(25, 25, 25);
        iv.setClip(clip);
    }

    private void setFotoProfiloDefault() {
        Image def = new Image(getClass().getResourceAsStream("/com/example/uninaswap/images/immagineProfiloDefault.jpg"));
        impostaImmagineQuadrata(immagineProfiloVenditore, def);
    }

    private void configuraBadgeEPrezzi() {
        badgeTipo.getStyleClass().removeAll("badge-vendita", "badge-scambio", "badge-regalo");
        if (annuncioCorrente instanceof AnnuncioVendita av) {
            badgeTipo.setText("VENDITA");
            badgeTipo.getStyleClass().add("badge-vendita");
            txtDettagliSpecifici.setText(av.getPrezzoMedio() + " €");
            txtPrezzoMinimo.setText(av.getPrezzoMinimo().doubleValue() > 0 ? "Minimo accettato: " + av.getPrezzoMinimo() + " €" : "");
        } else if (annuncioCorrente instanceof AnnuncioScambio as) {
            badgeTipo.setText("SCAMBIO");
            badgeTipo.getStyleClass().add("badge-scambio");
            txtDettagliSpecifici.setText("Cerco: " + as.getListaOggetti());
            txtPrezzoMinimo.setText("");
        } else {
            badgeTipo.setText("REGALO");
            badgeTipo.getStyleClass().add("badge-regalo");
            txtDettagliSpecifici.setText("Gratis");
            txtPrezzoMinimo.setText("");
        }
    }

    private void setDefaultImage() {
        immagineAnnuncio.setImage(new Image(getClass().getResourceAsStream("/com/example/uninaswap/images/uninaLogo.png")));
    }

    @FXML public void tornaIndietro() {
        new GestoreScene().CambiaScena(Costanti.pathHomePage, "Home", (Stage) btnFaiOfferta.getScene().getWindow());
    }

    @FXML public void apriSchermataOfferta() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(Costanti.pathEffettuaOfferta));
            Parent root = loader.load();
            if (loader.getController() instanceof EffettuaOffertaBoundary eob) eob.initData(annuncioCorrente);
            Stage stage = (Stage) btnFaiOfferta.getScene().getWindow();
            stage.setScene(new Scene(root, stage.getScene().getWidth(), stage.getScene().getHeight()));
        } catch (Exception e) { e.printStackTrace(); }
    }
}