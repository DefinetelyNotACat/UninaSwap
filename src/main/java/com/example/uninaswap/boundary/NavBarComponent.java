package com.example.uninaswap.boundary;

import com.example.uninaswap.Costanti;
import com.example.uninaswap.controller.ControllerUninaSwap;
import com.example.uninaswap.entity.Utente;
import javafx.fxml.FXML;
import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.scene.Cursor;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import java.io.File;

public class NavBarComponent {

    @FXML private Button bottoneRicerca;
    @FXML private Button bottoneAggiungiAnnuncio;
    @FXML private ChoiceBox<String> filtroBarraDiRicerca;
    @FXML private TextField barraDiRicerca;
    @FXML private Text erroreRicerca;
    @FXML private ImageView fotoProfilo;
    @FXML private ImageView logo;

    private ContextMenu menuProfilo;
    private final ControllerUninaSwap controllerUninaSwap = ControllerUninaSwap.getInstance();
    private final GestoreScene gestoreScene = new GestoreScene();

    private HomePageBoundary homePageBoundary;

    public void setHomePageBoundary(HomePageBoundary home) {
        this.homePageBoundary = home;
    }

    @FXML
    public void initialize() {
        bottoneAggiungiAnnuncio.setCursor(Cursor.HAND);
        bottoneRicerca.setCursor(Cursor.HAND);
        logo.setCursor(Cursor.HAND);
        fotoProfilo.setCursor(Cursor.HAND);

        // --- NAVIGAZIONE: CREA ANNUNCIO ---
        bottoneAggiungiAnnuncio.setOnAction(event -> {
            gestoreScene.CambiaScena(Costanti.pathCreaAnnuncio, "Crea Annuncio", event);
        });

        // --- NAVIGAZIONE: HOME ---
        logo.setOnMouseClicked(event -> {
            gestoreScene.CambiaScena(Costanti.pathHomePage, "Home", (Stage) logo.getScene().getWindow());
        });

        // --- VALIDAZIONE ---
        barraDiRicerca.textProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal == null || newVal.trim().isEmpty()) {
                setStatoErrore(false);
            } else {
                boolean isValido = newVal.matches(Costanti.FIELDS_REGEX_SPAZIO);
                setStatoErrore(!isValido);
            }
        });

        // --- RICERCA ---
        bottoneRicerca.setOnAction(event -> {
            String selezione = filtroBarraDiRicerca.getValue();
            String testo = barraDiRicerca.getText();

            if (testo != null && !testo.trim().isEmpty() && !testo.matches(Costanti.FIELDS_REGEX_SPAZIO)) {
                return;
            }

            if (homePageBoundary != null) {
                try {
                    if ("Articoli".equals(selezione)) {
                        homePageBoundary.caricaCatalogoAnnunci(testo, true);
                    } else {
                        homePageBoundary.caricaCatalogoAnnunci(testo, false);
                    }
                } catch (Exception e) {
                    System.err.println("Errore ricerca: " + e.getMessage());
                }
            }
        });

        if (filtroBarraDiRicerca.getItems().isEmpty()) {
            filtroBarraDiRicerca.getItems().addAll("Articoli", "Utenti");
            filtroBarraDiRicerca.setValue("Articoli");
        }
        filtroBarraDiRicerca.setCursor(Cursor.HAND);

        aggiornaFotoProfilo();
        setupMenuProfilo();
    }

    private void setStatoErrore(boolean erroreAttivo) {
        if (erroreAttivo) {
            if (!barraDiRicerca.getStyleClass().contains("error")) barraDiRicerca.getStyleClass().add("error");
            erroreRicerca.setVisible(true);
            erroreRicerca.setManaged(true);
            bottoneRicerca.setDisable(true);
        } else {
            barraDiRicerca.getStyleClass().remove("error");
            erroreRicerca.setVisible(false);
            erroreRicerca.setManaged(false);
            bottoneRicerca.setDisable(false);
        }
    }

    public void aggiornaFotoProfilo() {
        try {
            // ... (Logica caricamento immagine invariata) ...
            try {
                Utente u = controllerUninaSwap.getUtente();
                boolean caricato = false;
                if (u != null && u.getPathImmagineProfilo() != null && !u.getPathImmagineProfilo().isEmpty() && !u.getPathImmagineProfilo().equals("default")) {
                    File f = new File(System.getProperty("user.dir") + File.separator + "dati_utenti" + File.separator + u.getPathImmagineProfilo());
                    if (f.exists()) {
                        Image img = new Image(f.toURI().toString());
                        fotoProfilo.setImage(img);
                        centraImmagine(fotoProfilo, img);
                        caricato = true;
                    }
                }
                if (!caricato) {
                    Image def = new Image(getClass().getResourceAsStream("/com/example/uninaswap/images/immagineProfiloDefault.jpg"));
                    fotoProfilo.setImage(def);
                    centraImmagine(fotoProfilo, def);
                }
                applicaCerchio();
            } catch (Exception e) { }

            // Carica Logo
            try {
                logo.setImage(new Image(getClass().getResourceAsStream("/com/example/uninaswap/images/uninaLogo.png")));
            } catch(Exception e) {}

        } catch (Exception e) { }
    }

    private void centraImmagine(ImageView iv, Image img) {
        if (img == null) return;
        double min = Math.min(img.getWidth(), img.getHeight());
        iv.setViewport(new Rectangle2D((img.getWidth() - min) / 2, (img.getHeight() - min) / 2, min, min));
        iv.setPreserveRatio(false);
        iv.setSmooth(true);
    }

    private void applicaCerchio() {
        double r = Math.min(fotoProfilo.getFitWidth(), fotoProfilo.getFitHeight()) / 2;
        fotoProfilo.setClip(new Circle(fotoProfilo.getFitWidth()/2, fotoProfilo.getFitHeight()/2, r));
    }

    // =========================================================================================
    // QUI C'ERA L'ERRORE: ORA USA LE COSTANTI PER TUTTI I LINK
    // =========================================================================================
    private void setupMenuProfilo() {
        menuProfilo = new ContextMenu();
        menuProfilo.getStyleClass().add("profilo-context-menu");

        // 1. Offerte
        MenuItem offerte = creaVoceMenu("Le mie offerte", null);
        offerte.setOnAction(e -> gestoreScene.CambiaScena(Costanti.pathGestioneOfferte, "Gestione Offerte", (Stage) fotoProfilo.getScene().getWindow()));

        // 2. I Miei Annunci (CORRETTO QUI)
        MenuItem annunci = creaVoceMenu("I miei annunci", null);
        annunci.setOnAction(e -> gestoreScene.CambiaScena(Costanti.pathMieiAnnunci, "I Miei Annunci", (Stage) fotoProfilo.getScene().getWindow()));

        // 3. Inventario
        MenuItem inv = creaVoceMenu("Mostra il mio inventario", null);
        inv.setOnAction(e -> gestoreScene.CambiaScena(Costanti.pathInventario, Costanti.inventario, (Stage) fotoProfilo.getScene().getWindow()));

        // 4. Modifica Profilo
        MenuItem mod = creaVoceMenu("Modifica Profilo", null);
        mod.setOnAction(e -> gestoreScene.CambiaScena(Costanti.pathModificaProfilo, "Modifica Profilo", (Stage) fotoProfilo.getScene().getWindow()));

        // 5. Logout
        MenuItem logout = creaVoceMenu("Logout", "menu-item-logout");
        logout.setOnAction(e -> {
            controllerUninaSwap.setUtente(null);
            gestoreScene.CambiaScena(Costanti.pathSignIn, Costanti.accedi, (Stage) fotoProfilo.getScene().getWindow());
        });

        menuProfilo.getItems().addAll(offerte, annunci, inv, mod, new SeparatorMenuItem(), logout);

        fotoProfilo.setOnMouseClicked(e -> {
            if (menuProfilo.isShowing()) menuProfilo.hide();
            else showmenuProfilo(e);
        });
    }

    private void showmenuProfilo(MouseEvent e) {
        Point2D p = fotoProfilo.localToScreen(0, fotoProfilo.getBoundsInLocal().getHeight());
        if (p != null) menuProfilo.show(fotoProfilo, p.getX(), p.getY());
    }

    private MenuItem creaVoceMenu(String testo, String customClass) {
        Label label = new Label(testo);
        label.setCursor(Cursor.HAND);
        MenuItem item = new MenuItem();
        item.setGraphic(label);
        if (customClass != null) {
            item.getStyleClass().add(customClass);
            label.getStyleClass().add(customClass);
        }
        return item;
    }
}