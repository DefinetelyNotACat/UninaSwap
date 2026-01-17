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

    /**
     * Sincronizza la Navbar con la Home e ripristina lo stato della ricerca (Testo + Filtro)
     */
    public void setHomePageBoundary(HomePageBoundary home) {
        this.homePageBoundary = home;

        // Recuperiamo i dati prenotati dalla Home
        String q = HomePageBoundary.getQueryPrenotata();
        boolean isAnnuncio = HomePageBoundary.isCercaAnnunciPrenotato();

        // FIX: Ripristiniamo sia il testo che la selezione del ComboBox (Articoli/Utenti)
        if (q != null) {
            barraDiRicerca.setText(q);
            filtroBarraDiRicerca.setValue(isAnnuncio ? "Articoli" : "Utenti");
        }

        // Puliamo la memoria della Home solo dopo aver sincronizzato la Navbar
        home.resetRicercaPrenotata();
    }

    @FXML
    public void initialize() {
        setupUI();
        popolaFiltri();

        // Navigazione Rapida
        bottoneAggiungiAnnuncio.setOnAction(e -> gestoreScene.CambiaScena(Costanti.pathCreaAnnuncio, "Crea Annuncio", e));

        logo.setOnMouseClicked(e -> {
            HomePageBoundary.prenotaRicerca(null, true); // Reset ricerca
            gestoreScene.CambiaScena(Costanti.pathHomePage, "Home", (Stage) logo.getScene().getWindow());
        });

        // Toggle visibilità filtri extra basato sulla selezione
        filtroBarraDiRicerca.getSelectionModel().selectedItemProperty().addListener((obs, o, n) -> {
            boolean isArticoli = "Articoli".equals(n);
            filtroCondizione.setVisible(isArticoli);
            filtroCondizione.setManaged(isArticoli);
            filtroCategoria.setVisible(isArticoli);
            filtroCategoria.setManaged(isArticoli);
        });

        // Validazione Regex in tempo reale
        barraDiRicerca.textProperty().addListener((obs, o, n) -> {
            setStatoErrore(n != null && !n.trim().isEmpty() && !n.matches(Costanti.FIELDS_REGEX_SPAZIO));
        });

        bottoneRicerca.setOnAction(e -> eseguiRicerca());

        // Inizializzazione Combo (verrà sovrascritta da setHomePageBoundary se c'è una ricerca attiva)
        if (filtroBarraDiRicerca.getItems().isEmpty()) {
            filtroBarraDiRicerca.getItems().addAll("Articoli", "Utenti");
            filtroBarraDiRicerca.setValue("Articoli");
        }

        aggiornaFotoProfilo();
        setupMenuProfilo();
    }

    /**
     * Esegue la ricerca o prenota i dati per la Home se ci troviamo in un'altra pagina
     */
    private void eseguiRicerca() {
        String sel = filtroBarraDiRicerca.getValue();
        String txt = barraDiRicerca.getText();
        boolean isAnn = "Articoli".equals(sel);

        // Se siamo già in Home, aggiorniamo il catalogo direttamente
        if (homePageBoundary != null && logo.getScene() != null && logo.getScene().equals(homePageBoundary.getScene())) {
            try {
                if (isAnn) homePageBoundary.caricaCatalogoAnnunci(txt, filtroCondizione.getValue(), filtroCategoria.getValue(), true);
                else homePageBoundary.caricaCatalogoAnnunci(txt, false);
            } catch (Exception e) { e.printStackTrace(); }
        } else {
            // Se siamo altrove (Inventario, Offerte...), salviamo i dati e cambiamo scena verso la Home
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

    public void aggiornaFotoProfilo() {
        try {
            Utente u = controllerUninaSwap.getUtente();
            Image imgDaCaricare = null;
            boolean isDefault = true;

            if (u != null && u.getPathImmagineProfilo() != null && !u.getPathImmagineProfilo().equals("default") && !u.getPathImmagineProfilo().isEmpty()) {
                File f = new File(System.getProperty("user.dir") + File.separator + "dati_utenti" + File.separator + u.getPathImmagineProfilo());
                if (f.exists()) {
                    imgDaCaricare = new Image(f.toURI().toString());
                    isDefault = false;
                }
            }

            if (isDefault) {
                imgDaCaricare = new Image(getClass().getResourceAsStream("/com/example/uninaswap/images/immagineProfiloDefault.jpg"));
                fotoProfilo.setViewport(null); // RESET VIEWPORT PER FOTO DEFAULT
            }

            if (imgDaCaricare != null) {
                fotoProfilo.setImage(imgDaCaricare);
                centraImmagine(fotoProfilo, imgDaCaricare);
            }

            applicaCerchio();
            logo.setImage(new Image(getClass().getResourceAsStream("/com/example/uninaswap/images/uninaLogo.png")));
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void centraImmagine(ImageView iv, Image img) {
        if (img == null || img.isError()) return;
        double d = Math.min(img.getWidth(), img.getHeight());
        double x = (img.getWidth() - d) / 2;
        double y = (img.getHeight() - d) / 2;
        iv.setViewport(new Rectangle2D(x, y, d, d));
    }

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

    private void applicaCerchio() {
        double centerX = fotoProfilo.getFitWidth() / 2;
        double centerY = fotoProfilo.getFitHeight() / 2;
        double radius = Math.min(centerX, centerY);
        fotoProfilo.setClip(new Circle(centerX, centerY, radius));
    }
}