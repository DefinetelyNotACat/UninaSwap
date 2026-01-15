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

    /**
     * Collega la Navbar alla HomePage per permettere l'aggiornamento dinamico della griglia.
     */
    public void setHomePageBoundary(HomePageBoundary home) {
        this.homePageBoundary = home;
    }

    @FXML
    public void initialize() {
        // --- CONFIGURAZIONE CURSORI ---
        bottoneAggiungiAnnuncio.setCursor(Cursor.HAND);
        bottoneRicerca.setCursor(Cursor.HAND);
        logo.setCursor(Cursor.HAND);
        fotoProfilo.setCursor(Cursor.HAND);

        // --- NAVIGAZIONE: CREA ANNUNCIO ---
        bottoneAggiungiAnnuncio.setOnAction(event -> {
            gestoreScene.CambiaScena(Costanti.pathCreaAnnuncio, "Crea Annuncio", event);
        });

        // --- LOGICA VALIDAZIONE REGEX ---
        barraDiRicerca.textProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal == null || newVal.trim().isEmpty()) {
                setStatoErrore(false);
            } else {
                // Controllo caratteri speciali proibiti tramite Regex
                boolean isValido = newVal.matches(Costanti.FIELDS_REGEX_SPAZIO);
                setStatoErrore(!isValido);
            }
        });

        // --- LOGICA DI RICERCA ---
        bottoneRicerca.setOnAction(event -> {
            String selezione = filtroBarraDiRicerca.getValue();
            String testo = barraDiRicerca.getText();

            // Protezione: non inviare query se il regex è violato
            if (testo != null && !testo.trim().isEmpty() && !testo.matches(Costanti.FIELDS_REGEX_SPAZIO)) {
                return;
            }

            if (homePageBoundary != null) {
                try {
                    if ("Articoli".equals(selezione)) {
                        // Cerca annunci (non case-sensitive nel DB con ILIKE)
                        homePageBoundary.caricaCatalogoAnnunci(testo, true);
                    } else {
                        // Cerca utente specifico (Case-Sensitive)
                        homePageBoundary.caricaCatalogoAnnunci(testo, false);
                    }
                } catch (Exception e) {
                    System.err.println("Errore durante la ricerca dalla NavBar: " + e.getMessage());
                }
            }
        });

        // --- SETUP CHOICEBOX ---
        if (filtroBarraDiRicerca.getItems().isEmpty()) {
            filtroBarraDiRicerca.getItems().addAll("Articoli", "Utenti");
            filtroBarraDiRicerca.setValue("Articoli");
        }
        filtroBarraDiRicerca.setCursor(Cursor.HAND);

        // --- CARICAMENTO LOGO ---
        try {
            logo.setImage(new Image(getClass().getResourceAsStream("/com/example/uninaswap/images/uninaLogo.png")));
        } catch (Exception e) {
            System.err.println("Logo non caricato.");
        }

        aggiornaFotoProfilo();
        setupMenuProfilo();
    }

    /**
     * Gestisce il feedback visivo (bordo rosso, testo errore e disabilitazione pulsante).
     */
    private void setStatoErrore(boolean erroreAttivo) {
        if (erroreAttivo) {
            if (!barraDiRicerca.getStyleClass().contains("error")) {
                barraDiRicerca.getStyleClass().add("error");
            }
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
            Utente u = controllerUninaSwap.getUtente();
            boolean caricato = false;

            if (u != null && u.getPathImmagineProfilo() != null) {
                String path = u.getPathImmagineProfilo();
                if (!path.isEmpty() && !path.equals("default")) {
                    String fullPath = System.getProperty("user.dir") + File.separator + "dati_utenti" + File.separator + path;
                    File f = new File(fullPath);
                    if (f.exists()) {
                        Image img = new Image(f.toURI().toString());
                        fotoProfilo.setImage(img);
                        centraImmagine(fotoProfilo, img);
                        caricato = true;
                    }
                }
            }

            if (!caricato) {
                Image def = new Image(getClass().getResourceAsStream("/com/example/uninaswap/images/immagineProfiloDefault.jpg"));
                fotoProfilo.setImage(def);
                centraImmagine(fotoProfilo, def);
            }
            applicaCerchio();
        } catch (Exception e) {
            System.err.println("Errore caricamento foto Navbar: " + e.getMessage());
        }
    }

    private void centraImmagine(ImageView iv, Image img) {
        if (img == null) return;
        double min = Math.min(img.getWidth(), img.getHeight());
        double x = (img.getWidth() - min) / 2;
        double y = (img.getHeight() - min) / 2;
        iv.setViewport(new Rectangle2D(x, y, min, min));
        iv.setPreserveRatio(false);
        iv.setSmooth(true);
    }

    private void applicaCerchio() {
        double r = Math.min(fotoProfilo.getFitWidth(), fotoProfilo.getFitHeight()) / 2;
        fotoProfilo.setClip(new Circle(fotoProfilo.getFitWidth()/2, fotoProfilo.getFitHeight()/2, r));
    }

    private void setupMenuProfilo() {
        menuProfilo = new ContextMenu();
        menuProfilo.getStyleClass().add("profilo-context-menu");

        // --- VOCE: INVENTARIO ---
        MenuItem inv = creaVoceMenu("Mostra il mio inventario", null);
        inv.setOnAction(e -> gestoreScene.CambiaScena(Costanti.pathInventario, Costanti.inventario, (Stage) fotoProfilo.getScene().getWindow()));

        // --- VOCE: I MIEI ANNUNCI (COLLEGAMENTO AGGIUNTO) ---
        MenuItem annunci = creaVoceMenu("I miei annunci", null);
        annunci.setOnAction(e -> gestoreScene.CambiaScena(Costanti.pathMieiAnnunci, "I Miei Annunci", (Stage) fotoProfilo.getScene().getWindow()));

        // --- VOCE: MODIFICA PROFILO ---
        MenuItem mod = creaVoceMenu("Modifica Profilo", null);
        mod.setOnAction(e -> gestoreScene.CambiaScena(Costanti.pathModificaProfilo, "Modifica Profilo", (Stage) fotoProfilo.getScene().getWindow()));

        // --- VOCE: LOGOUT ---
        MenuItem logout = creaVoceMenu("Logout", "menu-item-logout");
        logout.setOnAction(e -> {
            controllerUninaSwap.setUtente(null);
            gestoreScene.CambiaScena(Costanti.pathSignIn, Costanti.accedi, (Stage) fotoProfilo.getScene().getWindow());
        });

        // Aggiungi tutto al menu (Rimuovi la stringa statica "I miei annunci" se l'avevi messa prima)
        menuProfilo.getItems().addAll(
                creaVoceMenu("Le mie offerte", null),
                annunci, // Usiamo la variabile con l'azione associata
                inv,
                mod,
                new SeparatorMenuItem(),
                logout
        );

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