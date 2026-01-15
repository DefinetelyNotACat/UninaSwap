package com.example.uninaswap.boundary;

import com.example.uninaswap.Costanti;
import com.example.uninaswap.controller.ControllerUninaSwap;
import com.example.uninaswap.entity.Categoria;
import com.example.uninaswap.entity.Oggetto;
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

    // Nuovi filtri iniettati dall'FXML
    @FXML private ChoiceBox<Oggetto.CONDIZIONE> filtroCondizione;
    @FXML private ChoiceBox<Categoria> filtroCategoria;

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

        // --- POPOLAMENTO FILTRI DINAMICI ---
        popolaFiltri();

        // --- LOGICA VISIBILITÀ FILTRI ---
        filtroBarraDiRicerca.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            boolean isArticoli = "Articoli".equals(newVal);
            filtroCondizione.setVisible(isArticoli);
            filtroCondizione.setManaged(isArticoli);
            filtroCategoria.setVisible(isArticoli);
            filtroCategoria.setManaged(isArticoli);
        });

        // --- LOGICA VALIDAZIONE REGEX ---
        barraDiRicerca.textProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal == null || newVal.trim().isEmpty()) {
                setStatoErrore(false);
            } else {
                boolean isValido = newVal.matches(Costanti.FIELDS_REGEX_SPAZIO);
                setStatoErrore(!isValido);
            }
        });

        // --- LOGICA DI RICERCA (MODIFICATA PER PASSARE I FILTRI) ---
        bottoneRicerca.setOnAction(event -> {
            String selezione = filtroBarraDiRicerca.getValue();
            String testo = barraDiRicerca.getText();

            // Richiesta: stampa se vuoto e resetta catalogo
            if (testo == null || testo.trim().isEmpty()) {
                System.out.println("Ricerca generale - Reset catalogo");
                try {
                    if (homePageBoundary != null) {
                        // Passiamo null ai filtri per mostrare tutto
                        homePageBoundary.caricaCatalogoAnnunci(null, null, null, true);
                    }
                } catch (Exception e) { e.printStackTrace(); }
                return;
            }

            // Protezione Regex
            if (!testo.matches(Costanti.FIELDS_REGEX_SPAZIO)) return;

            if (homePageBoundary != null) {
                try {
                    if ("Articoli".equals(selezione)) {
                        // RECUPERO VALORI DAI CHOICEBOX FILTRI
                        Oggetto.CONDIZIONE cond = filtroCondizione.getValue();
                        Categoria cat = filtroCategoria.getValue();

                        // CHIAMATA AL METODO FILTRATO DELLA HOMEPAGE
                        homePageBoundary.caricaCatalogoAnnunci(testo.trim(), cond, cat, true);
                    } else {
                        // Cerca utente (la ricerca utenti ignora cond e cat)
                        homePageBoundary.caricaCatalogoAnnunci(testo.trim(), false);
                    }
                } catch (Exception e) {
                    System.err.println("Errore durante la ricerca dalla NavBar: " + e.getMessage());
                }
            }
        });

        // --- SETUP CHOICEBOX PRINCIPALE ---
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

    private void popolaFiltri() {
        // Recupero Condizioni e Categorie dal Controller
        filtroCondizione.getItems().setAll(controllerUninaSwap.getCondizioni());
        filtroCategoria.getItems().setAll(controllerUninaSwap.getCategorie());

        // Setup iniziale visibilità (default Articoli è selezionato)
        filtroCondizione.setVisible(true);
        filtroCondizione.setManaged(true);
        filtroCategoria.setVisible(true);
        filtroCategoria.setManaged(true);
    }

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

        MenuItem inv = creaVoceMenu("Mostra il mio inventario", null);
        inv.setOnAction(e -> gestoreScene.CambiaScena(Costanti.pathInventario, Costanti.inventario, (Stage) fotoProfilo.getScene().getWindow()));

        MenuItem annunci = creaVoceMenu("I miei annunci", null);
        annunci.setOnAction(e -> gestoreScene.CambiaScena(Costanti.pathMieiAnnunci, "I Miei Annunci", (Stage) fotoProfilo.getScene().getWindow()));

        MenuItem mod = creaVoceMenu("Modifica Profilo", null);
        mod.setOnAction(e -> gestoreScene.CambiaScena(Costanti.pathModificaProfilo, "Modifica Profilo", (Stage) fotoProfilo.getScene().getWindow()));

        MenuItem logout = creaVoceMenu("Logout", "menu-item-logout");
        logout.setOnAction(e -> {
            controllerUninaSwap.setUtente(null);
            gestoreScene.CambiaScena(Costanti.pathSignIn, Costanti.accedi, (Stage) fotoProfilo.getScene().getWindow());
        });

        menuProfilo.getItems().addAll(
                creaVoceMenu("Le mie offerte", null),
                annunci,
                inv, mod, new SeparatorMenuItem(), logout
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