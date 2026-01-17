package com.example.uninaswap.boundary;

import com.example.uninaswap.controller.ControllerUninaSwap;
import com.example.uninaswap.entity.*;
import javafx.embed.swing.SwingNode;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;

import javax.swing.SwingUtilities;
import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.DoubleSummaryStatistics;

public class GestioneOfferteBoundary {

    // --- RIFERIMENTI FXML (Devono corrispondere agli fx:id nel file FXML) ---
    @FXML private ScrollPane viewListaOfferte;      // La vista classica a scorrimento
    @FXML private VBox containerOfferte;            // Il contenitore delle card dentro lo ScrollPane

    // Nuovi componenti per il Report
    @FXML private VBox viewReport;                  // La vista del report (inizialmente nascosta)
    @FXML private StackPane chartContainer;         // Dove inseriremo il grafico Swing
    @FXML private Label lblMedia;                   // Statistiche: Media
    @FXML private Label lblMin;                     // Statistiche: Minimo
    @FXML private Label lblMax;                     // Statistiche: Massimo

    // Bottoni di navigazione
    @FXML private Button btnNavRicevute;
    @FXML private Button btnNavInviate;
    @FXML private Button btnNavReport;              // Nuovo bottone per le statistiche

    private final ControllerUninaSwap controller = ControllerUninaSwap.getInstance();
    private boolean visualizzandoRicevute = true;
    private static final String CLASS_ACTIVE = "nav-btn-active";

    @FXML
    public void initialize() {
        // All'avvio mostriamo le offerte ricevute e nascondiamo il report
        mostraRicevute();
    }

    // =================================================================================
    // SEZIONE NAVIGAZIONE (Ricevute / Inviate / Report)
    // =================================================================================

    @FXML
    public void mostraRicevute() {
        visualizzandoRicevute = true;
        impostaVistaAttiva(viewListaOfferte);
        aggiornaBottoni(btnNavRicevute);
        caricaOfferte();
    }

    @FXML
    public void mostraInviate() {
        visualizzandoRicevute = false;
        impostaVistaAttiva(viewListaOfferte);
        aggiornaBottoni(btnNavInviate);
        caricaOfferte();
    }

    @FXML
    public void mostraReport() {
        // 1. Attiva la vista report e nasconde la lista
        impostaVistaAttiva(viewReport);
        aggiornaBottoni(btnNavReport);

        // 2. Calcola i dati e genera il grafico
        generaReportStatistico();
    }

    // Metodo helper per gestire la visibilità (Switch delle viste)
    private void impostaVistaAttiva(javafx.scene.Node vistaDaMostrare) {
        // Nascondiamo tutto
        if(viewListaOfferte != null) {
            viewListaOfferte.setVisible(false);
            viewListaOfferte.setManaged(false);
        }
        if(viewReport != null) {
            viewReport.setVisible(false);
            viewReport.setManaged(false);
        }

        // Mostriamo solo quello richiesto
        if(vistaDaMostrare != null) {
            vistaDaMostrare.setVisible(true);
            vistaDaMostrare.setManaged(true);
        }
    }

    private void aggiornaBottoni(Button attivo) {
        // Rimuove lo stile attivo da tutti
        if(btnNavRicevute != null) btnNavRicevute.getStyleClass().remove(CLASS_ACTIVE);
        if(btnNavInviate != null) btnNavInviate.getStyleClass().remove(CLASS_ACTIVE);
        if(btnNavReport != null) btnNavReport.getStyleClass().remove(CLASS_ACTIVE);

        // Aggiunge lo stile solo a quello cliccato
        if(attivo != null) attivo.getStyleClass().add(CLASS_ACTIVE);
    }

    // =================================================================================
    // SEZIONE LOGICA REPORT (JFreeChart)
    // =================================================================================

    private void generaReportStatistico() {
        // Recuperiamo le offerte INVIATE dall'utente
        ArrayList<Offerta> mieOfferte = controller.OttieniLeMieOfferte();

        // Variabili per i conteggi
        int totVendita = 0, accVendita = 0;
        int totScambio = 0, accScambio = 0;
        int totRegalo = 0, accRegalo = 0;

        ArrayList<Double> prezziAccettati = new ArrayList<>();

        if (mieOfferte != null) {
            for (Offerta o : mieOfferte) {
                boolean isAccettata = o.getStato() == Offerta.STATO_OFFERTA.ACCETTATA;

                if (o instanceof OffertaVendita) {
                    totVendita++;
                    if (isAccettata) {
                        accVendita++;
                        // Salviamo il prezzo per le statistiche economiche
                        BigDecimal p = ((OffertaVendita) o).getPrezzoOffertaVendita();
                        if (p != null) prezziAccettati.add(p.doubleValue());
                    }
                } else if (o instanceof OffertaScambio) {
                    totScambio++;
                    if (isAccettata) accScambio++;
                } else if (o instanceof OffertaRegalo) {
                    totRegalo++;
                    if (isAccettata) accRegalo++;
                }
            }
        }

        // 1. Creazione Dataset per il Grafico
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        // Barre per le offerte totali inviate
        dataset.addValue(totVendita, "Totale Inviate", "Vendita");
        dataset.addValue(totScambio, "Totale Inviate", "Scambio");
        dataset.addValue(totRegalo, "Totale Inviate", "Regalo");

        // Barre per le offerte accettate
        dataset.addValue(accVendita, "Accettate", "Vendita");
        dataset.addValue(accScambio, "Accettate", "Scambio");
        dataset.addValue(accRegalo, "Accettate", "Regalo");

        // 2. Creazione Grafico JFreeChart
        JFreeChart barChart = ChartFactory.createBarChart(
                "", // Titolo vuoto (già presente nella label FXML)
                "Tipologia",
                "Numero Offerte",
                dataset,
                PlotOrientation.VERTICAL,
                true, true, false);

        // Personalizzazione colori sfondo (opzionale)
        barChart.getPlot().setBackgroundPaint(new java.awt.Color(255, 255, 255));

        // 3. Integrazione in JavaFX tramite SwingNode
        SwingNode swingNode = new SwingNode();
        SwingUtilities.invokeLater(() -> {
            ChartPanel panel = new ChartPanel(barChart);
            // Impostiamo una dimensione preferita
            panel.setPreferredSize(new java.awt.Dimension(750, 350));
            swingNode.setContent(panel);
        });

        if(chartContainer != null) {
            chartContainer.getChildren().clear();
            chartContainer.getChildren().add(swingNode);
        }

        // 4. Aggiornamento Labels Statistiche Economiche
        if (lblMedia != null && lblMin != null && lblMax != null) {
            if (!prezziAccettati.isEmpty()) {
                DoubleSummaryStatistics stats = prezziAccettati.stream().mapToDouble(Double::doubleValue).summaryStatistics();
                lblMedia.setText(String.format("Media: %.2f €", stats.getAverage()));
                lblMin.setText(String.format("Minimo: %.2f €", stats.getMin()));
                lblMax.setText(String.format("Massimo: %.2f €", stats.getMax()));
            } else {
                lblMedia.setText("Media: -");
                lblMin.setText("Minimo: -");
                lblMax.setText("Massimo: -");
            }
        }
    }

    // =================================================================================
    // SEZIONE LOGICA LISTA OFFERTE (Codice Originale Mantenuto)
    // =================================================================================

    private void caricaOfferte() {
        containerOfferte.getChildren().clear();
        try {
            ArrayList<Offerta> lista = visualizzandoRicevute
                    ? controller.OttieniOfferteRicevute()
                    : controller.OttieniLeMieOfferte();

            if (lista == null || lista.isEmpty()) {
                containerOfferte.getChildren().add(creaLabelVuota(visualizzandoRicevute ? "Nessuna offerta ricevuta." : "Non hai inviato offerte."));
            } else {
                for (Offerta o : lista) {
                    containerOfferte.getChildren().add(creaCardOfferta(o, visualizzandoRicevute));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            containerOfferte.getChildren().add(new Label("Errore nel caricamento delle offerte."));
        }
    }

    /**
     * Crea la card principale dell'offerta
     */
    private VBox creaCardOfferta(Offerta o, boolean isRicevuta) {
        VBox card = new VBox(15);
        card.getStyleClass().add("offer-card");

        Utente utenteControparte = isRicevuta ? o.getUtente() : o.getAnnuncio().getUtente();

        // --- 1. HEADER ---
        HBox header = new HBox(15);
        header.setAlignment(Pos.CENTER_LEFT);

        ImageView imgProfilo = new ImageView();
        imgProfilo.setFitWidth(50); imgProfilo.setFitHeight(50);
        imgProfilo.setClip(new Circle(25, 25, 25));
        caricaFotoProfilo(utenteControparte, imgProfilo);

        VBox infoUtente = new VBox(2);
        Text nomeUtente = new Text(utenteControparte != null ? utenteControparte.getUsername() : "Utente");
        nomeUtente.getStyleClass().add("offer-card-title");
        Text descAnnuncio = new Text(o.getAnnuncio().getDescrizione());
        descAnnuncio.getStyleClass().add("offer-card-subtitle");
        infoUtente.getChildren().addAll(nomeUtente, descAnnuncio);
        HBox.setHgrow(infoUtente, Priority.ALWAYS);

        Label statoLabel = new Label(o.getStato().toString().replace("_", " "));
        statoLabel.getStyleClass().add("status-badge");
        impostaColoreStato(statoLabel, o.getStato());
        header.getChildren().addAll(imgProfilo, infoUtente, statoLabel);

        // --- 2. CORPO DETTAGLI ---
        VBox corpoDettagli = new VBox(10);
        if (o instanceof OffertaVendita ov) {
            Label l = new Label("💰 Proposta economica: " + ov.getPrezzoOffertaVendita() + " €");
            l.getStyleClass().add("offer-details-label");
            corpoDettagli.getChildren().add(l);
        } else if (o instanceof OffertaScambio os) {
            Label l = new Label("🔄 Oggetti proposti per lo scambio:");
            l.getStyleClass().add("offer-details-label");
            corpoDettagli.getChildren().add(l);

            FlowPane containerOggetti = new FlowPane(10, 10);
            containerOggetti.setPadding(new Insets(5, 0, 5, 0));
            for (Oggetto obj : os.getOggetti()) {
                containerOggetti.getChildren().add(creaMiniCardOggetto(obj));
            }
            corpoDettagli.getChildren().add(containerOggetti);
        } else {
            Label l = new Label("🎁 Richiesta di regalo");
            l.getStyleClass().add("offer-details-label");
            corpoDettagli.getChildren().add(l);
        }

        // --- 3. MESSAGGIO ---
        Text msgText = new Text("\"" + o.getMessaggio() + "\"");
        msgText.getStyleClass().add("offer-message-text");
        msgText.setWrappingWidth(650);

        card.getChildren().addAll(header, corpoDettagli, msgText);

        // --- 4. TASTI AZIONE ---
        if (isRicevuta && o.getStato() == Offerta.STATO_OFFERTA.IN_ATTESA) {
            HBox azioni = new HBox(15);
            azioni.setAlignment(Pos.CENTER_RIGHT);
            Button btnAccetta = new Button("Accetta");
            btnAccetta.getStyleClass().add("btn-accept");
            Button btnRifiuta = new Button("Rifiuta");
            btnRifiuta.getStyleClass().add("btn-reject");

            btnAccetta.setOnAction(e -> cambiaStato(o, Offerta.STATO_OFFERTA.ACCETTATA));
            btnRifiuta.setOnAction(e -> cambiaStato(o, Offerta.STATO_OFFERTA.RIFIUTATA));

            azioni.getChildren().addAll(btnRifiuta, btnAccetta);
            card.getChildren().add(azioni);
        }

        return card;
    }

    private VBox creaMiniCardOggetto(Oggetto obj) {
        VBox miniCard = new VBox(5);
        miniCard.setAlignment(Pos.CENTER);
        miniCard.setStyle("-fx-background-color: #ffffff; -fx-padding: 10; -fx-background-radius: 10; " +
                "-fx-border-color: #bdc3c7; -fx-border-width: 1; -fx-border-radius: 10;");
        miniCard.setPrefWidth(140);

        ImageView imgObj = new ImageView();
        imgObj.setFitWidth(110); imgObj.setFitHeight(80);
        imgObj.setPreserveRatio(true);
        if (obj.getImmagini() != null && !obj.getImmagini().isEmpty()) {
            File file = new File(System.getProperty("user.dir") + File.separator + "dati_utenti" + File.separator + obj.getImmagini().get(0));
            if (file.exists()) imgObj.setImage(new Image(file.toURI().toString(), 200, 160, true, true));
            else setDefaultItemImage(imgObj);
        } else { setDefaultItemImage(imgObj); }

        Label nome = new Label(obj.getNome());
        nome.setStyle("-fx-font-weight: bold; -fx-font-size: 12px; -fx-text-fill: #000000;");
        nome.setWrapText(true);
        nome.setTextAlignment(TextAlignment.CENTER);
        nome.setMaxWidth(120);

        FlowPane containerCategorie = new FlowPane(4, 4);
        containerCategorie.setAlignment(Pos.CENTER);
        for (Categoria c : obj.getCategorie()) {
            Label chip = new Label(c.getNome());
            chip.setStyle("-fx-font-size: 9px; -fx-text-fill: #003366; -fx-background-color: #e1f5fe; " +
                    "-fx-padding: 2 5; -fx-background-radius: 10; -fx-border-color: #b3e5fc; -fx-border-radius: 10;");
            containerCategorie.getChildren().add(chip);
        }

        Label cond = new Label(obj.getCondizione().toString().replace("_", " "));
        cond.setStyle("-fx-font-size: 10px; -fx-text-fill: #000000; -fx-background-color: #f1f2f6; " +
                "-fx-padding: 3 6; -fx-background-radius: 5; -fx-border-color: #dfe4ea; -fx-border-radius: 5;");
        cond.setWrapText(true);
        cond.setTextAlignment(TextAlignment.CENTER);

        miniCard.getChildren().addAll(imgObj, nome, containerCategorie, cond);
        return miniCard;
    }

    private void caricaFotoProfilo(Utente u, ImageView iv) {
        try {
            if (u != null && u.getPathImmagineProfilo() != null && !u.getPathImmagineProfilo().isEmpty()) {
                File file = new File(System.getProperty("user.dir") + File.separator + "dati_utenti" + File.separator + u.getPathImmagineProfilo());
                if (file.exists()) {
                    iv.setImage(new Image(file.toURI().toString()));
                    return;
                }
            }
            iv.setImage(new Image(getClass().getResourceAsStream("/com/example/uninaswap/images/immagineProfiloDefault.jpg")));
        } catch (Exception e) {
            iv.setImage(new Image(getClass().getResourceAsStream("/com/example/uninaswap/images/immagineProfiloDefault.jpg")));
        }
    }

    private void setDefaultItemImage(ImageView iv) {
        iv.setImage(new Image(getClass().getResourceAsStream("/com/example/uninaswap/images/uninaLogo.png")));
    }

    private void impostaColoreStato(Label l, Offerta.STATO_OFFERTA s) {
        l.getStyleClass().removeAll("status-accepted", "status-rejected", "status-pending");
        switch (s) {
            case ACCETTATA: l.getStyleClass().add("status-accepted"); break;
            case RIFIUTATA: l.getStyleClass().add("status-rejected"); break;
            default: l.getStyleClass().add("status-pending"); break;
        }
    }

    private Label creaLabelVuota(String testo) {
        Label l = new Label(testo);
        l.setStyle("-fx-text-fill: #95a5a6; -fx-font-size: 16px; -fx-padding: 50 0 0 0;");
        return l;
    }

    private void cambiaStato(Offerta o, Offerta.STATO_OFFERTA nuovoStato) {
        if (controller.GestisciStatoOfferta(o, nuovoStato)) {
            caricaOfferte();
        }
    }
}