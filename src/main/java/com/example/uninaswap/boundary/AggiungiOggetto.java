package com.example.uninaswap.boundary;

import com.example.uninaswap.Costanti;
import com.example.uninaswap.entity.Categoria;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import com.example.uninaswap.controller.ControllerUninaSwap;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;

public class AggiungiOggetto implements Initializable {

    @FXML
    private Text erroreNome;
    @FXML
    private TextField nomeOggettoField;
    @FXML
    private ComboBox<String> categoriaBox;
    @FXML
    private ComboBox<String> condizioneBox;

    private ControllerUninaSwap controllerUninaSwap;
    public void onCaricaFotoClick(ActionEvent actionEvent) {
    }

    public void onAnnullaClick(ActionEvent actionEvent) {
        GestoreScene gestoreScene = new GestoreScene();
        gestoreScene.CambiaScena(Costanti.pathHomePage, Costanti.homepage,actionEvent, "Annullamento aggiungimento prodotto", Messaggio.TIPI.INFO);
    }

    public void onPubblicaClick(ActionEvent actionEvent) {
    }
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle){
        controllerUninaSwap = ControllerUninaSwap.getInstance();

        ArrayList<Categoria> categorie = controllerUninaSwap.getCategorie();
        ArrayList <String> condizioni = controllerUninaSwap.getCondizioni();
        for (Categoria categoria : categorie){
            categoriaBox.getItems().add(categoria.getNome());
        }
       for(String condizione : condizioni){
           condizioneBox.getItems().add(condizione);
       }
       if(nomeOggettoField != null){
           nomeOggettoField.textProperty().addListener((observable, oldValue, newValue) -> {
               if(erroreNome != null){
                   erroreNome.setVisible(false);
                   erroreNome.setManaged(false);
               }
               boolean lunghezzaOk = nomeOggettoField.getText().replace(" ", "").length() >= 5;
               if(!nomeOggettoField.getText().matches(Costanti.OGGETTO_FIELD_REGEX) || !lunghezzaOk){
                   erroreNome.setText("Errore! inserire un nome che sia di almeno 5 lettere (spazi esclusi) senza caratteri speciali");
                   erroreNome.setManaged(true);
                   erroreNome.setVisible(true);
               }
           });
       }
    }

}
