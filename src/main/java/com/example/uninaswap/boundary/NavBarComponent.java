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
    @FXML private ComboBox<String> filtroBarraDiRicerca;
    @FXML private TextField barraDiRicerca;
    @FXML private Text erroreRicerca;
    @FXML private ImageView fotoProfilo;
    @FXML private ImageView logo;

    @FXML private ComboBox<Oggetto.CONDIZIONE> filtroCondizione;
    @FXML private ComboBox<Categoria> filtroCategoria;

    private ContextMenu menuProfilo;
    private final ControllerUninaSwap controllerUninaSwap = ControllerUninaSwap.getInstance();
    private final GestoreScene gestoreScene = new GestoreScene();

    private HomePageBoundary homePageBoundary;

    public void setHomePageBoundary(HomePageBoundary home) {
        this.homePageBoundary = home;
        // Ripristina il testo nella barra se arriviamo da una ricerca prenotata
        String q = HomePageBoundary.getQueryPrenotata();
        if (q != null) barraDiRicerca.setText(q);
    }

    @FXML
    public void initialize() {
        setupUI();
        popolaFiltri();

        // Navigazione Rapida
        bottoneAggiungiAnnuncio.setOnAction(e -> gestoreScene.CambiaScena(Costanti.pathCreaAnnuncio, "Crea Annuncio", e));
        logo.setOnMouseClicked(e -> {
            HomePageBoundary.prenotaRicerca(null, true);
            gestoreScene.CambiaScena(Costanti.pathHomePage, "Home", (Stage) logo.getScene().getWindow());
        });

        // Toggle filtri extra
        filtroBarraDiRicerca.getSelectionModel().selectedItemProperty().addListener((obs, o, n) -> {
            boolean isArticoli = "Articoli".equals(n);
            filtroCondizione.setVisible(isArticoli);
            filtroCondizione.setManaged(isArticoli);
            filtroCategoria.setVisible(isArticoli);
            filtroCategoria.setManaged(isArticoli);
        });

        // Regex barra di ricerca
        barraDiRicerca.textProperty().addListener((obs, o, n) -> {
            setStatoErrore(n != null && !n.trim().isEmpty() && !n.matches(Costanti.FIELDS_REGEX_SPAZIO));
        });

        bottoneRicerca.setOnAction(e -> eseguiRicerca());

        if (filtroBarraDiRicerca.getItems().isEmpty()) {
            filtroBarraDiRicerca.getItems().addAll("Articoli", "Utenti");
            filtroBarraDiRicerca.setValue("Articoli");
        }

        aggiornaFotoProfilo();
        setupMenuProfilo();
    }

    private void eseguiRicerca() {
        String sel = filtroBarraDiRicerca.getValue();
        String txt = barraDiRicerca.getText();
        boolean isAnn = "Articoli".equals(sel);

        if (homePageBoundary != null && logo.getScene() != null && logo.getScene().equals(homePageBoundary.getScene())) {
            try {
                if (isAnn) homePageBoundary.caricaCatalogoAnnunci(txt, filtroCondizione.getValue(), filtroCategoria.getValue(), true);
                else homePageBoundary.caricaCatalogoAnnunci(txt, false);
            } catch (Exception e) { e.printStackTrace(); }
        } else {
            HomePageBoundary.prenotaRicerca(txt, isAnn);
            gestoreScene.CambiaScena(Costanti.pathHomePage, "Home", (Stage) logo.getScene().getWindow());
        }
    }

    private void setupMenuProfilo() {
        menuProfilo = new ContextMenu();
        menuProfilo.getStyleClass().add("profilo-context-menu");

        MenuItem esplora = creaVoceMenu("Esplora tutti gli annunci", null);
        esplora.setOnAction(e -> {
            HomePageBoundary.prenotaRicerca(null, true);
            gestoreScene.CambiaScena(Costanti.pathHomePage, "Home", (Stage) fotoProfilo.getScene().getWindow());
        });

        MenuItem offerte = creaVoceMenu("Le mie offerte", null);
        offerte.setOnAction(e -> gestoreScene.CambiaScena(Costanti.pathGestioneOfferte, "Offerte", (Stage) fotoProfilo.getScene().getWindow()));

        // --- VOCE REINSERITA ---
        MenuItem annunci = creaVoceMenu("I miei annunci", null);
        annunci.setOnAction(e -> gestoreScene.CambiaScena(Costanti.pathMieiAnnunci, "I Miei Annunci", (Stage) fotoProfilo.getScene().getWindow()));

        MenuItem inv = creaVoceMenu("Il mio inventario", null);
        inv.setOnAction(e -> gestoreScene.CambiaScena(Costanti.pathInventario, "Inventario", (Stage) fotoProfilo.getScene().getWindow()));

        MenuItem mod = creaVoceMenu("Modifica Profilo", null);
        mod.setOnAction(e -> gestoreScene.CambiaScena(Costanti.pathModificaProfilo, "Modifica", (Stage) fotoProfilo.getScene().getWindow()));

        MenuItem logout = creaVoceMenu("Logout", "menu-item-logout");
        logout.setOnAction(e -> {
            controllerUninaSwap.setUtente(null);
            gestoreScene.CambiaScena(Costanti.pathSignIn, "Login", (Stage) fotoProfilo.getScene().getWindow());
        });

        menuProfilo.getItems().addAll(esplora, new SeparatorMenuItem(), offerte, annunci, inv, mod, new SeparatorMenuItem(), logout);
        fotoProfilo.setOnMouseClicked(e -> showmenuProfilo(e));
    }

    // Metodi grafici (Foto profilo, centra immagine ecc.) rimangono identici
    private void setupUI() {
        Cursor h = Cursor.HAND;
        bottoneAggiungiAnnuncio.setCursor(h); bottoneRicerca.setCursor(h);
        logo.setCursor(h); fotoProfilo.setCursor(h); filtroBarraDiRicerca.setCursor(h);
    }

    private void popolaFiltri() {
        filtroCondizione.getItems().setAll(controllerUninaSwap.getCondizioni());
        filtroCategoria.getItems().setAll(controllerUninaSwap.getCategorie());
    }

    private void setStatoErrore(boolean err) {
        barraDiRicerca.getStyleClass().remove("error");
        if (err) barraDiRicerca.getStyleClass().add("error");
        erroreRicerca.setVisible(err); erroreRicerca.setManaged(err);
        bottoneRicerca.setDisable(err);
    }

    public void aggiornaFotoProfilo() {
        try {
            Utente u = controllerUninaSwap.getUtente();
            if (u != null && u.getPathImmagineProfilo() != null && !u.getPathImmagineProfilo().equals("default") && !u.getPathImmagineProfilo().isEmpty()) {
                File f = new File(System.getProperty("user.dir") + File.separator + "dati_utenti" + File.separator + u.getPathImmagineProfilo());
                if (f.exists()) {
                    Image img = new Image(f.toURI().toString());
                    fotoProfilo.setImage(img);
                    centraImmagine(fotoProfilo, img);
                }
            } else {
                fotoProfilo.setImage(new Image(getClass().getResourceAsStream("/com/example/uninaswap/images/immagineProfiloDefault.jpg")));
            }
            double r = Math.min(fotoProfilo.getFitWidth(), fotoProfilo.getFitHeight()) / 2;
            fotoProfilo.setClip(new Circle(fotoProfilo.getFitWidth()/2, fotoProfilo.getFitHeight()/2, r));
            logo.setImage(new Image(getClass().getResourceAsStream("/com/example/uninaswap/images/uninaLogo.png")));
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void centraImmagine(ImageView iv, Image img) {
        if (img == null) return;
        double min = Math.min(img.getWidth(), img.getHeight());
        iv.setViewport(new Rectangle2D((img.getWidth() - min) / 2, (img.getHeight() - min) / 2, min, min));
    }

    private void showmenuProfilo(MouseEvent e) {
        Point2D p = fotoProfilo.localToScreen(0, fotoProfilo.getBoundsInLocal().getHeight());
        menuProfilo.show(fotoProfilo, p.getX(), p.getY());
    }

    private MenuItem creaVoceMenu(String testo, String customClass) {
        Label label = new Label(testo); label.setCursor(Cursor.HAND);
        MenuItem item = new MenuItem(); item.setGraphic(label);
        if (customClass != null) label.getStyleClass().add(customClass);
        return item;
    }
}