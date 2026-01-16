package com.example.uninaswap.boundary;

import com.example.uninaswap.interfaces.GestoreMessaggio;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
public class GestoreScene {

    /* ============================================SPIEGAZIONE CODICE ============================================
    * Ci sono diversi cambia scena, 2 sono utili per quando l'utente preme un bottone, gli altri 2 sono
    * quando l'utente deve essere ridirezionato da un elemento che non è un bottone, esiste la variante con
    * il messaggio da dare l'utente e quella senza
    *
    *
    * */
    // METODO 1: utile per i menu
    /*public void CambiaScena(String pathFxml, String TitoloScene, Stage stage){
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
            e.printStackTrace();
        }
    }*/

    public void CambiaScena(String pathFxml, String TitoloScene, Stage stage){
        try{
            System.out.println("--- DEBUG ---");
            System.out.println("Cerco il file: " + pathFxml);
            var url = getClass().getResource(pathFxml);
            System.out.println("URL Trovato: " + url);

            if (url == null) {
                System.err.println("ERRORE FATALE: Il file FXML non è stato trovato! Controlla nome e percorso.");
                return; // Interrompe per evitare il crash Location is not set
            }

            FXMLLoader loader = new FXMLLoader(url); // Uso l'URL verificato
            Parent root = loader.load();

            // ... resto del codice uguale ...
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

    // METODO 2: utile per i bottoni
    public void CambiaScena(String pathFxml, String TitoloScene, ActionEvent actionEvent, String messaggio, Messaggio.TIPI tipo){
        try {
            Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
            // Riutilizzo della logica di sopra
            CambiaScena(pathFxml, TitoloScene, stage, messaggio, tipo);
        } catch (Exception e) {
            System.out.println("Errore nell'estrarre lo stage dall'evento: " + e.getMessage());
        }
    }

    public void CambiaScena(String pathFxml, String TitoloScene, ActionEvent event){
        try {
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
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
            if(controller instanceof GestoreMessaggio){
                ((GestoreMessaggio) controller).mostraMessaggioEsterno(messaggio, tipo);
            }
            //TODO! verificare per ogni boundary
            /*
            *
            * ! ATTENZIONE ! POTREBBE NON ESSERE LA MIGLIOR IDEA
            * DA DISCUTERE
            * */
        }
        catch (Exception e){
            System.out.println("Errore " + e.getMessage());
            e.printStackTrace(); // Utile per vedere l'errore completo
        }
    }
}