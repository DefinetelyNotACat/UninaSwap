package com.example.uninaswap.boundary;

import com.example.uninaswap.controller.ControllerUninaSwap;
import com.example.uninaswap.entity.Utente;
import javafx.animation.PauseTransition;
import javafx.fxml.FXML;
import javafx.geometry.Rectangle2D;
import javafx.scene.Cursor;
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

    @FXML private Button bottoneRicerca;
    @FXML private Button bottoneAggiungiAnnuncio;
    @FXML private ChoiceBox<String> filtroBarraDiRicerca;
    @FXML private TextField barraDiRicerca;
    @FXML private ImageView fotoProfilo;
    @FXML private ImageView logo;

    private ContextMenu menuProfilo;
    private final ControllerUninaSwap controllerUninaSwap = ControllerUninaSwap.getInstance();
    private final GestoreScene gestoreScene = new GestoreScene();

    @FXML
    public void initialize() {
        // --- LOGICA BOTTONE AGGIUNGI ANNUNCIO ---
        bottoneAggiungiAnnuncio.setOnAction(event -> {
            gestoreScene.CambiaScena(Costanti.pathCreaAnnuncio, "Crea Annuncio", event);
        });

        // --- LOGICA BARRA DI RICERCA (STAMPA CIAO/HELLO) ---
        bottoneRicerca.setOnAction(event -> {
            String selezione = filtroBarraDiRicerca.getValue();
            if ("Articoli".equals(selezione)) {
                System.out.println("ciao");
            } else if ("Utenti".equals(selezione)) {
                System.out.println("hello");
            }
        });

        // --- CONFIGURAZIONE CHOICEBOX ---
        filtroBarraDiRicerca.setCursor(Cursor.HAND);
        filtroBarraDiRicerca.getItems().addAll("Articoli", "Utenti");
        filtroBarraDiRicerca.setValue("Articoli");

        // --- CARICAMENTO LOGO ---
        try {
            Image logoImage = new Image(getClass().getResourceAsStream("/com/example/uninaswap/images/uninaLogo.png"));
            logo.setImage(logoImage);
        } catch (Exception e) {
            System.err.println("Logo non trovato: " + e.getMessage());
        }

        // --- AGGIORNAMENTO UI ---
        aggiornaFotoProfilo();
        setupMenuProfilo();
    }

    public void aggiornaFotoProfilo() {
        try {
            Utente utente = controllerUninaSwap.getUtente();
            boolean caricato = false;

            if (utente != null && utente.getPathImmagineProfilo() != null) {
                String pathDalDb = utente.getPathImmagineProfilo();
                boolean isDefault = pathDalDb.trim().isEmpty() || pathDalDb.equals("default");

                if (!isDefault) {
                    String BASE_PATH = System.getProperty("user.dir") + File.separator + "dati_utenti" + File.separator;
                    File fileImmagine;

                    if (pathDalDb.contains(File.separator) && (pathDalDb.contains(":") || pathDalDb.startsWith("/"))) {
                        fileImmagine = new File(pathDalDb);
                    } else {
                        fileImmagine = new File(BASE_PATH + pathDalDb);
                    }

                    if (fileImmagine.exists()) {
                        Image image = new Image(fileImmagine.toURI().toString());
                        fotoProfilo.setImage(image);
                        centraImmagine(fotoProfilo, image);
                        caricato = true;
                    }
                }
            }

            if (!caricato) {
                Image defaultImg = new Image(getClass().getResourceAsStream("/com/example/uninaswap/images/immagineProfiloDefault.jpg"));
                if (defaultImg != null) {
                    fotoProfilo.setImage(defaultImg);
                    centraImmagine(fotoProfilo, defaultImg);
                }
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
        ilMioInventario.setOnAction(event -> {
            Stage stage = (Stage) fotoProfilo.getScene().getWindow();
            gestoreScene.CambiaScena(Costanti.pathInventario, Costanti.inventario, stage);
        });

        MenuItem modificaProfilo = creaVoceMenu("Modifica Profilo", null);
        modificaProfilo.setOnAction(event -> {
            Stage stage = (Stage) fotoProfilo.getScene().getWindow();
            gestoreScene.CambiaScena(Costanti.pathModificaProfilo, "Modifica Profilo", stage);
        });

        MenuItem logout = creaVoceMenu("Logout", "menu-item-logout");
        logout.setOnAction(e -> {
            Stage stage = (Stage) fotoProfilo.getScene().getWindow();
            controllerUninaSwap.setUtente(null);
            gestoreScene.CambiaScena(Costanti.pathSignIn, Costanti.accedi, stage);
        });

        menuProfilo.getItems().addAll(leMieOfferte, iMieiAnnunci, ilMioInventario, modificaProfilo, new SeparatorMenuItem(), logout);

        fotoProfilo.setOnMouseClicked(event -> {
            if (menuProfilo.isShowing()) menuProfilo.hide();
            else showmenuProfilo(event);
        });

        fotoProfilo.setCursor(Cursor.HAND);
    }

    private void showmenuProfilo(MouseEvent event) {
        Point2D point = fotoProfilo.localToScreen(0, fotoProfilo.getBoundsInLocal().getHeight());
        if (point != null) menuProfilo.show(fotoProfilo, point.getX(), point.getY());
        else menuProfilo.show(fotoProfilo, event.getScreenX(), event.getScreenY());
    }

    private MenuItem creaVoceMenu(String testo, String customClass) {
        MenuItem item = new MenuItem();
        Label label = new Label(testo);
        label.setCursor(Cursor.HAND);
        item.setGraphic(label);
        if (customClass != null) {
            item.getStyleClass().add(customClass);
            label.getStyleClass().add(customClass);
        }
        return item;
    }
}