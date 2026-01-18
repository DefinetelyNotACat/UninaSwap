package com.example.uninaswap.boundary;

import com.example.uninaswap.Costanti;
import com.example.uninaswap.controller.ControllerUninaSwap;
import com.example.uninaswap.entity.Categoria;
import com.example.uninaswap.entity.Oggetto;
import com.example.uninaswap.entity.Utente;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.scene.Cursor;
import javafx.scene.Parent;
import javafx.scene.Scene;
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

        String query = HomePageBoundary.getQueryPrenotata();
        boolean isAnnuncio = HomePageBoundary.isCercaAnnunciPrenotato();

        if (query != null) {
            barraDiRicerca.setText(query);
            filtroBarraDiRicerca.setValue(isAnnuncio ? "Articoli" : "Utenti");
        }

        home.resetRicercaPrenotata();
    }

    @FXML
    public void initialize() {
        setupUI();
        popolaFiltri();

        bottoneAggiungiAnnuncio.setOnAction(actionEvent -> gestoreScene.CambiaScena(Costanti.pathCreaAnnuncio, "Crea Annuncio", actionEvent));

        logo.setOnMouseClicked(mouseEvent -> {
            HomePageBoundary.prenotaRicerca(null, true);
            gestoreScene.CambiaScena(Costanti.pathHomePage, "Home", (Stage) logo.getScene().getWindow());
        });

        filtroBarraDiRicerca.getSelectionModel().selectedItemProperty().addListener((obs, o, n) -> {
            boolean isArticoli = "Articoli".equals(n);
            filtroCondizione.setVisible(isArticoli);
            filtroCondizione.setManaged(isArticoli);
            filtroCategoria.setVisible(isArticoli);
            filtroCategoria.setManaged(isArticoli);
        });

        barraDiRicerca.textProperty().addListener((obs, o, n) -> {
            setStatoErrore(n != null && !n.trim().isEmpty() && !n.matches(Costanti.FIELDS_REGEX_SPAZIO));
        });

        bottoneRicerca.setOnAction(actionEvent-> eseguiRicerca());

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

        // Se siamo già in Home, aggiorniamo il catalogo direttamente
        if (homePageBoundary != null && logo.getScene() != null && logo.getScene().equals(homePageBoundary.getScene())) {
            try {
                if (isAnn) homePageBoundary.caricaCatalogoAnnunci(txt, filtroCondizione.getValue(), filtroCategoria.getValue(), true);
                else homePageBoundary.caricaCatalogoAnnunci(txt, false);
            } catch (Exception exception) { exception.printStackTrace(); }
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

        MenuItem mieRecensioni = creaVoceMenu("Le mie recensioni", null);
        mieRecensioni.setOnAction(e -> {
            try {
                Utente me = controllerUninaSwap.getUtente();
                FXMLLoader loader = new FXMLLoader(getClass().getResource(Costanti.pathRecensioni));
                Parent root = loader.load();
                Recensioni controllerRec = loader.getController();
                controllerRec.initData(me);
                Stage stage = (Stage) fotoProfilo.getScene().getWindow();
                stage.setScene(new Scene(root, stage.getScene().getWidth(), stage.getScene().getHeight()));
            } catch (Exception ex) { ex.printStackTrace(); }
        });

        MenuItem mod = creaVoceMenu("Modifica Profilo", null);
        mod.setOnAction(e -> gestoreScene.CambiaScena(Costanti.pathModificaProfilo, "Modifica", (Stage) fotoProfilo.getScene().getWindow()));

        MenuItem logout = creaVoceMenu("Logout", "menu-item-logout");
        logout.setOnAction(e -> {
            controllerUninaSwap.setUtente(null);
            gestoreScene.CambiaScena(Costanti.pathSignIn, "Login", (Stage) fotoProfilo.getScene().getWindow());
        });

        menuProfilo.getItems().addAll(esplora, new SeparatorMenuItem(), offerte, annunci, inv, mieRecensioni, mod, new SeparatorMenuItem(), logout);

        fotoProfilo.setOnMouseClicked(e -> {
            if (menuProfilo.isShowing()) menuProfilo.hide();
            else showmenuProfilo(e);
        });
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

    private void setStatoErrore(boolean errore) {
        barraDiRicerca.getStyleClass().remove("error");
        if (errore) barraDiRicerca.getStyleClass().add("error");
        erroreRicerca.setVisible(errore); erroreRicerca.setManaged(errore);
        bottoneRicerca.setDisable(errore);
    }

    public void aggiornaFotoProfilo() {
        try {
            Utente utente = controllerUninaSwap.getUtente();
            Image immagineDaCaricare = null;
            boolean isDefault = true;

            //Controllo se l'utente ha una foto valida
            if (utente != null && utente.getPathImmagineProfilo() != null &&
                    !utente.getPathImmagineProfilo().equals("default") &&
                    !utente.getPathImmagineProfilo().isEmpty()) {

                File f = new File(System.getProperty("user.dir") + File.separator + "dati_utenti" + File.separator + utente.getPathImmagineProfilo());
                if (f.exists()) {
                    immagineDaCaricare = new Image(f.toURI().toString());
                    isDefault = false;
                }
            }

            //Se non ho trovato nulla, carico quella di default
            if (isDefault) {
                immagineDaCaricare = new Image(getClass().getResourceAsStream("/com/example/uninaswap/images/immagineProfiloDefault.jpg"));
                // RESETTA IL VIEWPORT altrimenti l'immagine di default non si vede cazzo!
                fotoProfilo.setViewport(null);
            }

            //Imposto l'immagine e la centro
            if (immagineDaCaricare != null) {
                fotoProfilo.setImage(immagineDaCaricare);
                centraImmagine(fotoProfilo, immagineDaCaricare);
            }

            //Applico il cerchio e il logo
            applicaCerchio();
            logo.setImage(new Image(getClass().getResourceAsStream("/com/example/uninaswap/images/uninaLogo.png")));

        } catch (Exception exception) {
            System.err.println("ERRORE CRITICO CARICAMENTO PFP: " + exception.getMessage());
            exception.printStackTrace();
        }
    }

    private void centraImmagine(ImageView imageView, Image immagine) {
        if (immagine == null || immagine.isError()) return;

        // Calcoliamo il lato più corto per fare un quadrato perfetto
        double d = Math.min(immagine.getWidth(), immagine.getHeight());
        double x = (immagine.getWidth() - d) / 2;
        double y = (immagine.getHeight() - d) / 2;

        // Impostiamo il Viewport per centrare la parte quadrata dell'immagine
        imageView.setViewport(new Rectangle2D(x, y, d, d));
    }

    private void showmenuProfilo(MouseEvent mouseEvent) {
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
        // Il cerchio deve essere sempre centrato sulla ImageView da 40x40
        double centerX = fotoProfilo.getFitWidth() / 2;
        double centerY = fotoProfilo.getFitHeight() / 2;
        double radius = Math.min(centerX, centerY);

        fotoProfilo.setClip(new Circle(centerX, centerY, radius));
    }

}