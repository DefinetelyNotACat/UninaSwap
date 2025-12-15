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

public class AggiungiOggetto implements Initializable {
    @FXML
    private ComboBox<String> categoriaBox;
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
        for (Categoria categoria : categorie){
            categoriaBox.getItems().add(categoria.getNome());
        }

    }
}
