package com.example.uninaswap.boundary;

import com.example.uninaswap.controller.*;
import com.example.uninaswap.interfaces.GestoreMessaggio;
import javafx.fxml.FXML;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;

public class HomePageBoundary implements GestoreMessaggio {
    ControllerUninaSwap controllerUninaSwap = ControllerUninaSwap.getInstance();
    @FXML private AnchorPane navbarComponent;
    @FXML private NavBarComponent navBarComponentController;
    @FXML private ImageView fotoProfilo;
    @FXML private Messaggio notificaController;

    @FXML
    private void initialize() {
            if (navBarComponentController != null) {
                navBarComponentController.initialize();
            }

        }

    @Override
    public void mostraMessaggioEsterno(String testo, Messaggio.TIPI tipo) {
        if(notificaController!=null){
            notificaController.mostraMessaggio(testo, tipo);
        }
    }
}
