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
import javafx.stage.Stage;
import java.io.File;

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

    private HomePageBoundary homePageBoundary;

    // Fondamentale per far parlare Navbar e Homepage
    public void setHomePageBoundary(HomePageBoundary home) {
        this.homePageBoundary = home;
    }

    @FXML
    public void initialize() {
        // --- FEEDBACK VISIVO (MANINA) ---
        bottoneAggiungiAnnuncio.setCursor(Cursor.HAND);
        bottoneRicerca.setCursor(Cursor.HAND);
        logo.setCursor(Cursor.HAND);
        fotoProfilo.setCursor(Cursor.HAND);

        // --- AZIONE AGGIUNGI ANNUNCIO ---
        bottoneAggiungiAnnuncio.setOnAction(event -> {
            gestoreScene.CambiaScena(Costanti.pathCreaAnnuncio, "Crea Annuncio", event);
        });

        // --- AZIONE RICERCA ---
        bottoneRicerca.setOnAction(event -> {
            String selezione = filtroBarraDiRicerca.getValue();
            String testo = barraDiRicerca.getText();

            if ("Articoli".equals(selezione)) {
                if (homePageBoundary != null) {
                    System.out.println("Ricerca articoli per: " + testo);
                    homePageBoundary.caricaCatalogoAnnunci(testo);
                }
            } else {
                System.out.println("Ricerca utenti non ancora implementata.");
            }
        });

        // --- CONFIGURAZIONE CHOICEBOX ---
        if (filtroBarraDiRicerca.getItems().isEmpty()) {
            filtroBarraDiRicerca.getItems().addAll("Articoli", "Utenti");
            filtroBarraDiRicerca.setValue("Articoli");
        }
        filtroBarraDiRicerca.setCursor(Cursor.HAND);

        // --- LOGO ---
        try {
            logo.setImage(new Image(getClass().getResourceAsStream("/com/example/uninaswap/images/uninaLogo.png")));
        } catch (Exception e) {
            System.err.println("Errore caricamento logo: " + e.getMessage());
        }

        aggiornaFotoProfilo();
        setupMenuProfilo();
    }

    public void aggiornaFotoProfilo() {
        try {
            Utente u = controllerUninaSwap.getUtente();
            boolean caricato = false;

            if (u != null && u.getPathImmagineProfilo() != null) {
                String path = u.getPathImmagineProfilo();
                if (!path.isEmpty() && !path.equals("default")) {
                    // Percorso nella cartella dati_utenti
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

            // Fallback immagine default
            if (!caricato) {
                Image def = new Image(getClass().getResourceAsStream("/com/example/uninaswap/images/immagineProfiloDefault.jpg"));
                fotoProfilo.setImage(def);
                centraImmagine(fotoProfilo, def);
            }
            applicaCerchio();
        } catch (Exception e) { e.printStackTrace(); }
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

        // CREAZIONE UNIFORME PER ALLINEAMENTO PERFETTO
        MenuItem mieOfferte = creaVoceMenu("Le mie offerte", null);
        MenuItem mieiAnnunci = creaVoceMenu("I miei annunci", null);

        MenuItem inv = creaVoceMenu("Mostra il mio inventario", null);
        inv.setOnAction(e -> gestoreScene.CambiaScena(Costanti.pathInventario, Costanti.inventario, (Stage) fotoProfilo.getScene().getWindow()));

        MenuItem mod = creaVoceMenu("Modifica Profilo", null);
        mod.setOnAction(e -> gestoreScene.CambiaScena(Costanti.pathModificaProfilo, "Modifica Profilo", (Stage) fotoProfilo.getScene().getWindow()));

        MenuItem log = creaVoceMenu("Logout", "menu-item-logout");
        log.setOnAction(e -> {
            controllerUninaSwap.setUtente(null);
            gestoreScene.CambiaScena(Costanti.pathSignIn, Costanti.accedi, (Stage) fotoProfilo.getScene().getWindow());
        });

        // Composizione Menu
        menuProfilo.getItems().addAll(mieOfferte, mieiAnnunci, inv, mod, new SeparatorMenuItem(), log);

        // Gestione apertura menu al click sulla foto
        fotoProfilo.setOnMouseClicked(e -> {
            if (menuProfilo.isShowing()) menuProfilo.hide();
            else showmenuProfilo(e);
        });
    }

    private void showmenuProfilo(MouseEvent e) {
        Point2D p = fotoProfilo.localToScreen(0, fotoProfilo.getBoundsInLocal().getHeight());
        if (p != null) menuProfilo.show(fotoProfilo, p.getX(), p.getY());
    }

    /**
     * Helper che garantisce manina e allineamento per ogni voce del menu
     */
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