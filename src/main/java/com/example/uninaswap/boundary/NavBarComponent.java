package com.example.uninaswap.boundary;

import com.example.uninaswap.controller.ControllerUninaSwap;
import com.example.uninaswap.entity.Utente;
import javafx.animation.PauseTransition;
import javafx.fxml.FXML;
import javafx.geometry.Rectangle2D;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.Circle;
import javafx.geometry.Point2D;
import java.io.File;
import com.example.uninaswap.Costanti;
import javafx.stage.Stage;

public class NavBarComponent {

    @FXML private ChoiceBox<String> filtroBarraDiRicerca;
    @FXML private TextField barraDiRicerca;
    @FXML private ImageView fotoProfilo;
    @FXML private Button bottonePubblicaAnnuncio;
    @FXML private ImageView logo;
    private ContextMenu menuProfilo;
    private PauseTransition hideDelay;
    private final ControllerUninaSwap controllerUninaSwap = ControllerUninaSwap.getInstance();
    private final GestoreScene gestoreScene = new GestoreScene();
    @FXML
    public void initialize() {
        filtroBarraDiRicerca.getItems().addAll("Articoli", "Utenti");
        filtroBarraDiRicerca.setValue("Articoli");
        try {
            Image logoImage = new Image(getClass().getResourceAsStream("/com/example/uninaswap/images/uninaLogo.png"));
            logo.setImage(logoImage);
        } catch (Exception e) {
            System.err.println("Logo non trovato: " + e.getMessage());
        }

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
                    fotoProfilo.setImage(image);
                    centraImmagine(fotoProfilo, image);
                    caricato = true;
                }
            }

            if (!caricato) {
                Image defaultImg = new Image(getClass().getResourceAsStream("/com/example/uninaswap/images/immagineProfiloDefault.jpg"));
                fotoProfilo.setImage(defaultImg);
                centraImmagine(fotoProfilo, defaultImg);
            }
            applicaCerchio();

        } catch (Exception e) {
            System.err.println("Errore caricamento foto profilo navbar: " + e.getMessage());
        }
    }
    private void centraImmagine(ImageView imageView, Image img) {
        if (img == null) return;
        double width = img.getWidth();
        double height = img.getHeight();
        double minDimension = Math.min(width, height);
        double x = (width - minDimension) / 2;
        double y = (height - minDimension) / 2;
        Rectangle2D cropArea = new Rectangle2D(x, y, minDimension, minDimension);
        imageView.setViewport(cropArea);
        imageView.setPreserveRatio(false);
        imageView.setSmooth(true);
    }

    private void applicaCerchio() {
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
        menuProfilo.getStyleClass().add("profilo-context-menu");

        MenuItem leMieOfferte = creaVoceMenu("Mostra le mie offerte", null);
        MenuItem iMieiAnnunci = creaVoceMenu("Mostra i miei annunci", null);
        MenuItem ilMioInventario = creaVoceMenu("Mostra il mio inventario", null);
        // Aggiungi l'azione al click
        ilMioInventario.setOnAction(event -> {
            try {
                Stage stage = (Stage) fotoProfilo.getScene().getWindow();
                gestoreScene.CambiaScena(
                        Costanti.pathInventario,
                        Costanti.inventario,
                        stage
                );
            } catch (Exception e) {
                System.err.println("Errore apertura inventario: " + e.getMessage());
                e.printStackTrace();
            }
        });
        // --------------------
        MenuItem modificaProfilo = creaVoceMenu("Modifica Profilo", null);

        modificaProfilo.setOnAction(event -> {
            try {

                Stage stage = (Stage) fotoProfilo.getScene().getWindow();
                gestoreScene.CambiaScena(
                        Costanti.pathModificaProfilo,
                        "Modifica Profilo",
                        stage
                );
            } catch (Exception e) {
                System.err.println("Errore nel cambio scena: " + e.getMessage());
                e.printStackTrace();
            }
        });
        MenuItem logout = creaVoceMenu("Logout", "menu-item-logout");

        logout.setOnAction(e -> {
            Stage stage = (Stage) fotoProfilo.getScene().getWindow();
            System.out.println("Logout");
            controllerUninaSwap.setUtente(null);
            gestoreScene.CambiaScena(Costanti.pathSignIn, Costanti.accedi, stage);
        });

        menuProfilo.getItems().addAll(
                leMieOfferte,
                iMieiAnnunci,
                ilMioInventario,
                modificaProfilo,
                new SeparatorMenuItem(), logout);

        fotoProfilo.setOnMouseClicked(event -> {
            if (menuProfilo.isShowing()) {
                menuProfilo.hide();
            } else {
                showmenuProfilo(event);
            }
        });

        fotoProfilo.setCursor(javafx.scene.Cursor.HAND);
    }    private void showmenuProfilo(MouseEvent event) {
        if (menuProfilo.isShowing()) return;

        Point2D point = fotoProfilo.localToScreen(0, fotoProfilo.getBoundsInLocal().getHeight());
        if (point != null) {
            menuProfilo.show(fotoProfilo, point.getX(), point.getY());
        } else {
            menuProfilo.show(fotoProfilo, event.getScreenX(), event.getScreenY());
        }
    }
    private MenuItem creaVoceMenu(String testo, String customClass) {
        MenuItem item = new MenuItem();
        Label label = new Label(testo);

        label.setCursor(javafx.scene.Cursor.HAND);

        item.setGraphic(label);

        if (customClass != null) {
            item.getStyleClass().add(customClass);
            label.getStyleClass().add(customClass);
        }

        return item;
    }
}
