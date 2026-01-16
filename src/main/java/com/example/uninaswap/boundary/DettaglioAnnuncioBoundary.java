package com.example.uninaswap.boundary;

import com.example.uninaswap.Costanti;
import com.example.uninaswap.controller.ControllerUninaSwap;
import com.example.uninaswap.entity.*;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.File;

public class DettaglioAnnuncioBoundary {

    @FXML private ImageView immagineAnnuncio;
    @FXML private ImageView immagineProfiloVenditore; // Nuova icona profilo
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

        // 1. Dati Annuncio base
        txtTitoloDescrizione.setText(annuncioCorrente.getDescrizione());
        txtSede.setText("📍 " + (annuncioCorrente.getSede() != null ? annuncioCorrente.getSede().getNomeSede() : "Sede non specificata"));

        // 2. Immagine dell'oggetto in alta qualità
        caricaImmagineAnnuncio();

        // 3. Dati VENDITORE (Recupero informazioni profilo)
        Utente venditore = annuncioCorrente.getUtente();
        if (venditore != null) {
            txtVenditore.setText(venditore.getUsername());
            txtEmailVenditore.setText(venditore.getEmail());
            caricaFotoProfiloVenditore(venditore);
        } else {
            txtVenditore.setText("Utente #" + annuncioCorrente.getUtenteId());
            txtEmailVenditore.setText("Email non disponibile");
            setFotoProfiloDefault();
        }

        // 4. Dati OGGETTO (Condizione)
        if (annuncioCorrente.getOggetti() != null && !annuncioCorrente.getOggetti().isEmpty()) {
            Oggetto obj = annuncioCorrente.getOggetti().get(0);
            txtCondizioni.setText(obj.getCondizione().toString().replace("_", " "));
        }

        // 5. Logica Polimorfica (Badge e Prezzi)
        configuraBadgeEPrezzi();

        // 6. Restrizione: Nascondi bottone "Fai Offerta" se l'annuncio è dell'utente loggato
        try {
            Utente loggato = controller.getUtente();
            if (loggato != null && annuncioCorrente.getUtenteId() == loggato.getId()) {
                btnFaiOfferta.setVisible(false);
                btnFaiOfferta.setManaged(false);
            }
        } catch (Exception e) {
            // Se non c'è utente loggato, il bottone rimane visibile (gestito poi dal login)
        }
    }

    private void configuraBadgeEPrezzi() {
        badgeTipo.getStyleClass().removeAll("badge-vendita", "badge-scambio", "badge-regalo");
        txtPrezzoMinimo.setText("");

        if (annuncioCorrente instanceof AnnuncioVendita av) {
            badgeTipo.setText("VENDITA");
            badgeTipo.getStyleClass().add("badge-vendita");
            txtDettagliSpecifici.setText(av.getPrezzoMedio() + " €");

            if (av.getPrezzoMinimo() != null && av.getPrezzoMinimo().doubleValue() > 0) {
                txtPrezzoMinimo.setText("Prezzo minimo accettato: " + av.getPrezzoMinimo() + " €");
            }
        } else if (annuncioCorrente instanceof AnnuncioScambio as) {
            badgeTipo.setText("SCAMBIO");
            badgeTipo.getStyleClass().add("badge-scambio");
            txtDettagliSpecifici.setText("Cerco: " + as.getListaOggetti());
        } else {
            badgeTipo.setText("REGALO");
            badgeTipo.getStyleClass().add("badge-regalo");
            txtDettagliSpecifici.setText("Disponibile gratuitamente");
        }
    }

    private void caricaFotoProfiloVenditore(Utente u) {
        try {
            String path = u.getPathImmagineProfilo();
            if (path != null && !path.equals("default") && !path.isEmpty()) {
                File file = new File(System.getProperty("user.dir") + File.separator + "dati_utenti" + File.separator + path);
                if (file.exists()) {
                    immagineProfiloVenditore.setImage(new Image(file.toURI().toString()));
                    applicoClipCircolare(immagineProfiloVenditore);
                    return;
                }
            }
            setFotoProfiloDefault();
        } catch (Exception e) {
            setFotoProfiloDefault();
        }
    }

    private void setFotoProfiloDefault() {
        immagineProfiloVenditore.setImage(new Image(getClass().getResourceAsStream("/com/example/uninaswap/images/immagineProfiloDefault.jpg")));
        applicoClipCircolare(immagineProfiloVenditore);
    }

    private void applicoClipCircolare(ImageView iv) {
        Circle clip = new Circle(25, 25, 25); // Basato su fitWidth/Height 50
        iv.setClip(clip);
    }

    private void caricaImmagineAnnuncio() {
        try {
            if (annuncioCorrente.getOggetti() != null && !annuncioCorrente.getOggetti().isEmpty() &&
                    !annuncioCorrente.getOggetti().get(0).getImmagini().isEmpty()) {

                String path = annuncioCorrente.getOggetti().get(0).getImmagini().get(0);
                File file = new File(System.getProperty("user.dir") + File.separator + "dati_utenti" + File.separator + path);

                if (file.exists()) {
                    // Caricamento in 800x800 per mantenere il dettaglio
                    immagineAnnuncio.setImage(new Image(file.toURI().toString(), 800, 800, true, true, true));
                } else {
                    setDefaultImage();
                }
            } else {
                setDefaultImage();
            }
        } catch (Exception e) {
            setDefaultImage();
        }
    }

    private void setDefaultImage() {
        immagineAnnuncio.setImage(new Image(getClass().getResourceAsStream("/com/example/uninaswap/images/uninaLogo.png")));
    }

    @FXML
    public void apriSchermataOfferta() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(Costanti.pathEffettuaOfferta));
            Parent root = loader.load();

            // Passaggio dati al controller dell'offerta
            Object controllerOfferta = loader.getController();
            if (controllerOfferta instanceof EffettuaOffertaBoundary eob) {
                eob.initData(annuncioCorrente);
            }

            Stage stage = (Stage) btnFaiOfferta.getScene().getWindow();
            stage.setScene(new Scene(root, stage.getScene().getWidth(), stage.getScene().getHeight()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void tornaIndietro() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(Costanti.pathHomePage));
            Parent root = loader.load();
            Stage stage = (Stage) btnFaiOfferta.getScene().getWindow();
            stage.setScene(new Scene(root, stage.getScene().getWidth(), stage.getScene().getHeight()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}