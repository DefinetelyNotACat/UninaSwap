package com.example.uninaswap.boundary;

import com.example.uninaswap.controller.*;
import javafx.fxml.FXML;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;

public class HomePageBoundary {
    ControllerUninaSwap controllerUninaSwap = ControllerUninaSwap.getInstance();
    @FXML private AnchorPane navbarComponent;
    @FXML private NavBarComponent navBarComponentController;
    @FXML private ImageView fotoProfilo;

    @FXML
    private void initialize() {

            if (navBarComponentController != null) {
                navBarComponentController.initialize();
            }

        }
    }
