package com.example.uninaswap.boundary;

import com.example.uninaswap.controller.ControllerUninaSwap;
import com.example.uninaswap.entity.Offerta;
import com.example.uninaswap.entity.OffertaVendita;
import com.example.uninaswap.entity.OffertaScambio;
import com.example.uninaswap.entity.OffertaRegalo;
import javafx.embed.swing.SwingNode;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;

import javax.swing.SwingUtilities;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.DoubleSummaryStatistics;
import java.util.stream.Collectors;

public class ReportOfferteBoundary {

    @FXML private StackPane chartContainer;
    @FXML private Label lblMedia;
    @FXML private Label lblMin;
    @FXML private Label lblMax;

    private final ControllerUninaSwap controller = ControllerUninaSwap.getInstance();

    @FXML
    public void initialize() {
        calcolaEVisualizzaDati();
    }

    private void calcolaEVisualizzaDati() {
        // 1. Recupero Dati dal Controller
        ArrayList<Offerta> mieOfferte = controller.OttieniLeMieOfferte();

        if (mieOfferte == null || mieOfferte.isEmpty()) {
            lblMedia.setText("Nessuna offerta trovata.");
            return;
        }

        // 2. Calcolo Statistiche Conteggio (Totali vs Accettate)
        int totVendita = 0, accVendita = 0;
        int totScambio = 0, accScambio = 0;
        int totRegalo = 0, accRegalo = 0;

        // Lista per statistiche economiche
        ArrayList<Double> prezziAccettati = new ArrayList<>();

        for (Offerta o : mieOfferte) {
            boolean isAccettata = o.getStato() == Offerta.STATO_OFFERTA.ACCETTATA;

            if (o instanceof OffertaVendita) {
                totVendita++;
                if (isAccettata) {
                    accVendita++;
                    BigDecimal prezzo = ((OffertaVendita) o).getPrezzoOffertaVendita();
                    if (prezzo != null) prezziAccettati.add(prezzo.doubleValue());
                }
            } else if (o instanceof OffertaScambio) {
                totScambio++;
                if (isAccettata) accScambio++;
            } else if (o instanceof OffertaRegalo) {
                totRegalo++;
                if (isAccettata) accRegalo++;
            }
        }

        // 3. Generazione Grafico a Barre (JFreeChart)
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        // Serie Inviate
        dataset.addValue(totVendita, "Inviate", "Vendita");
        dataset.addValue(totScambio, "Inviate", "Scambio");
        dataset.addValue(totRegalo, "Inviate", "Regalo");

        // Serie Accettate
        dataset.addValue(accVendita, "Accettate", "Vendita");
        dataset.addValue(accScambio, "Accettate", "Scambio");
        dataset.addValue(accRegalo, "Accettate", "Regalo");

        JFreeChart barChart = ChartFactory.createBarChart(
                "Statistiche Offerte Inviate",
                "Tipologia",
                "Numero Offerte",
                dataset,
                PlotOrientation.VERTICAL,
                true, true, false);

        // Integrazione in JavaFX tramite SwingNode
        SwingNode swingNode = new SwingNode();
        SwingUtilities.invokeLater(() -> {
            ChartPanel panel = new ChartPanel(barChart);
            swingNode.setContent(panel);
        });
        chartContainer.getChildren().add(swingNode);

        // 4. Calcolo Statistiche Economiche (Vendita Accettata)
        if (!prezziAccettati.isEmpty()) {
            DoubleSummaryStatistics stats = prezziAccettati.stream()
                    .mapToDouble(Double::doubleValue)
                    .summaryStatistics();

            lblMedia.setText(String.format("Media: %.2f €", stats.getAverage()));
            lblMin.setText(String.format("Minimo: %.2f €", stats.getMin()));
            lblMax.setText(String.format("Massimo: %.2f €", stats.getMax()));
        } else {
            lblMedia.setText("Nessuna offerta di vendita accettata.");
            lblMin.setVisible(false);
            lblMax.setVisible(false);
        }
    }

    @FXML
    public void chiudiFinestra() {
        Stage stage = (Stage) lblMedia.getScene().getWindow();
        stage.close();
    }
}