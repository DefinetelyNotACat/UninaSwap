package com.example.uninaswap.boundary;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import com.example.uninaswap.boundary.Messaggio;
public class GestoreScene {

    // METODO 1: utile per i menu
    public void CambiaScena(String pathFxml, String TitoloScene, Stage stage){
        try{
            FXMLLoader loader = new FXMLLoader(getClass().getResource(pathFxml));
            Parent root = loader.load();
            double larghezza = stage.getScene().getWidth();
            double lunghezza = stage.getScene().getHeight();
            stage.setTitle(TitoloScene);
            stage.setMaximized(true);
            Scene scene = new Scene(root, larghezza, lunghezza);
            stage.setScene(scene);
            stage.show();
        }
        catch (Exception e){
            System.out.println("Errore " + e.getMessage());
            e.printStackTrace(); // Utile per vedere l'errore completo
        }
    }

    // METODO 2: utile per i bottoni
    public void CambiaScena(String pathFxml, String TitoloScene, ActionEvent actionEvent){
        try {
            Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
            // Riutilizzo della logica di sopra
            CambiaScena(pathFxml, TitoloScene, stage);
        } catch (Exception e) {
            System.out.println("Errore nell'estrarre lo stage dall'evento: " + e.getMessage());
        }
    }
    public void CambiaScena(String pathFxml, String TitoloScene, Stage stage, String messaggio, Messaggio.TIPI tipo){
        try{
            FXMLLoader loader = new FXMLLoader(getClass().getResource(pathFxml));
            Parent root = loader.load();
            double larghezza = stage.getScene().getWidth();
            double lunghezza = stage.getScene().getHeight();
            stage.setTitle(TitoloScene);
            stage.setMaximized(true);
            Scene scene = new Scene(root, larghezza, lunghezza);
            stage.setScene(scene);
            stage.show();
            Object controller = loader.getController();
            if (controller instanceof SignBoundary) {
                ((SignBoundary) controller).mostraMessaggioEsterno(messaggio, tipo);
            }
            //TODO! verificare per ogni boundary
        }
        catch (Exception e){
            System.out.println("Errore " + e.getMessage());
            e.printStackTrace(); // Utile per vedere l'errore completo
        }
    }
}