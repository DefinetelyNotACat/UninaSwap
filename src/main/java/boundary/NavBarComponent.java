package boundary;

import controller.ControllerUninaSwap;
import entity.Utente;
import javafx.animation.PauseTransition;
import javafx.fxml.FXML;
import javafx.geometry.Rectangle2D; // Importante per il ritaglio
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.Circle;
import javafx.util.Duration;
import javafx.geometry.Point2D;
import java.io.File;

public class NavBarComponent {

    @FXML private ChoiceBox<String> filtroBarraDiRicerca;
    @FXML private TextField barraDiRicerca;
    @FXML private ImageView fotoProfilo;
    @FXML private Button bottonePubblicaAnnuncio;
    @FXML private ImageView logo;

    private ContextMenu menuProfilo;
    private PauseTransition hideDelay;

    private final ControllerUninaSwap controllerUninaSwap = ControllerUninaSwap.getInstance();

    @FXML
    public void initialize() {
        filtroBarraDiRicerca.getItems().addAll("Articoli", "Utenti");
        filtroBarraDiRicerca.setValue("Articoli");

        // Caricamento Logo
        try {
            Image logoImage = new Image(getClass().getResourceAsStream("/com/example/uninaswap/images/uninaLogo.png"));
            logo.setImage(logoImage);
        } catch (Exception e) {
            System.err.println("Logo non trovato: " + e.getMessage());
        }

        // Caricamento Foto Profilo
        aggiornaFotoProfilo();

        setupMenuProfilo();
    }

    public void aggiornaFotoProfilo() {
        try {
            Utente utente = controllerUninaSwap.getUtente();
            boolean caricato = false;

            if (utente != null && utente.getPathImmagineProfilo() != null) {
                File fileImmagine = new File(utente.getPathImmagineProfilo());
                if (fileImmagine.exists()) {
                    Image image = new Image(fileImmagine.toURI().toString());

                    // 1. Imposta l'immagine
                    fotoProfilo.setImage(image);

                    // 2. MAGIA: Ritaglia il quadrato centrale (come nella SignBoundary)
                    centraImmagine(fotoProfilo, image);

                    caricato = true;
                }
            }

            if (!caricato) {
                Image defaultImg = new Image(getClass().getResourceAsStream("/com/example/uninaswap/images/immagineProfiloDefault.jpg"));
                fotoProfilo.setImage(defaultImg);
                // Anche per quella di default, centriamola per sicurezza
                centraImmagine(fotoProfilo, defaultImg);
            }

            // 3. Applica il cerchio finale
            applicaCerchio();

        } catch (Exception e) {
            System.err.println("Errore caricamento foto profilo navbar: " + e.getMessage());
        }
    }

    // === METODO CHIAVE PER NON DEFORMARE L'IMMAGINE ===
    private void centraImmagine(ImageView imageView, Image img) {
        if (img == null) return;

        double width = img.getWidth();
        double height = img.getHeight();

        // Trova il lato più piccolo
        double minDimension = Math.min(width, height);

        // Calcola le coordinate per prendere esattamente il centro dell'immagine
        double x = (width - minDimension) / 2;
        double y = (height - minDimension) / 2;

        // Imposta il Viewport: stiamo dicendo "Mostra solo questo quadrato centrale"
        Rectangle2D cropArea = new Rectangle2D(x, y, minDimension, minDimension);
        imageView.setViewport(cropArea);

        // Disabilitiamo preserveRatio perché il viewport è già quadrato perfetto
        // e vogliamo che riempia tutto lo spazio dell'ImageView
        imageView.setPreserveRatio(false);
        imageView.setSmooth(true);
    }

    private void applicaCerchio() {
        // Crea una maschera circolare basata sulla grandezza della ImageView nella Navbar
        double raggio = Math.min(fotoProfilo.getFitWidth(), fotoProfilo.getFitHeight()) / 2;
        Circle clip = new Circle(
                fotoProfilo.getFitWidth() / 2,
                fotoProfilo.getFitHeight() / 2,
                raggio
        );
        fotoProfilo.setClip(clip);
    }

    private void setupMenuProfilo() {
        menuProfilo = new ContextMenu();
        MenuItem leMieOfferte = new MenuItem("Mostra le mie offerte");
        MenuItem iMieiAnnunci = new MenuItem("Mostra i miei annunci");
        MenuItem ilMioInventario = new MenuItem("Mostra il mio inventario");
        MenuItem logout = new MenuItem("Logout");

        // Esempio Logout
        logout.setOnAction(e -> {
            // logica logout
        });

        menuProfilo.getItems().addAll(leMieOfferte, iMieiAnnunci, ilMioInventario, new SeparatorMenuItem(), logout);

        hideDelay = new PauseTransition(Duration.millis(200));
        hideDelay.setOnFinished(e -> {
            if (menuProfilo.isShowing()) menuProfilo.hide();
        });

        fotoProfilo.setOnMouseEntered(event -> {
            if (hideDelay.getStatus() == javafx.animation.Animation.Status.RUNNING) {
                hideDelay.stop();
            }
            showmenuProfilo(event);
        });

        fotoProfilo.setOnMouseExited(event -> hideDelay.playFromStart());

        menuProfilo.setOnShown(e -> {
            Scene menuScene = menuProfilo.getScene();
            if (menuScene != null) {
                Node root = menuScene.getRoot();
                root.setOnMouseEntered(ev -> {
                    if (hideDelay.getStatus() == javafx.animation.Animation.Status.RUNNING) {
                        hideDelay.stop();
                    }
                });
                root.setOnMouseExited(ev -> hideDelay.playFromStart());
            }
        });
    }

    private void showmenuProfilo(MouseEvent event) {
        if (menuProfilo.isShowing()) return;

        Point2D point = fotoProfilo.localToScreen(0, fotoProfilo.getBoundsInLocal().getHeight());
        if (point != null) {
            menuProfilo.show(fotoProfilo, point.getX(), point.getY());
        } else {
            menuProfilo.show(fotoProfilo, event.getScreenX(), event.getScreenY());
        }
    }
}