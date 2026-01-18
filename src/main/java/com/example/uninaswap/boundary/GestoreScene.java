package com.example.uninaswap.boundary;

import com.example.uninaswap.interfaces.GestoreMessaggio;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
public class GestoreScene {

    public void CambiaScena(String pathFxml, String TitoloScene, Stage stage){
        try{
            System.out.println("--- DEBUG ---");
            System.out.println("Cerco il file: " + pathFxml);
            var url = getClass().getResource(pathFxml);
            System.out.println("URL Trovato: " + url);

            if (url == null) {
                System.err.println("ERRORE FATALE: Il file FXML non è stato trovato! Controlla nome e percorso.");
                return;
            }

            FXMLLoader loader = new FXMLLoader(url);
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
            e.printStackTrace();
        }
    }

    public void CambiaScena(String pathFxml, String TitoloScene, ActionEvent actionEvent, String messaggio, Messaggio.TIPI tipo){
        try {
            Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
            CambiaScena(pathFxml, TitoloScene, stage, messaggio, tipo);
        } catch (Exception e) {
            System.out.println("Errore nell'estrarre lo stage dall'evento: " + e.getMessage());
        }
    }

    public void CambiaScena(String pathFxml, String TitoloScene, ActionEvent event){
        try {
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
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
            if(controller instanceof GestoreMessaggio){
                ((GestoreMessaggio) controller).mostraMessaggioEsterno(messaggio, tipo);
            }
        }
        catch (Exception e){
            System.out.println("Errore " + e.getMessage());
            e.printStackTrace();
        }
    }
}