package com.example.uninaswap.boundary;

import com.example.uninaswap.entity.Annuncio;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;


public class ArticoloCardComponent {
    @FXML ImageView imgAnnuncio;
    @FXML Label titoloAnnuncio;
    @FXML Label prezzoAnnuncio;

    public void setData(Annuncio annuncio){
        try{

        }catch (Exception e){
            System.err.println("Errore nel caricamento dell'immagine: " + e.getMessage());
            //Si potrebbe implementare un'immagine place holder da mettere in caso non riesca a caricare l'immagine

        }


    }
}
