package com.example.uninaswap.boundary;

import com.example.uninaswap.Costanti; // Assumo tu abbia una classe Costanti
import com.example.uninaswap.controller.ControllerUninaSwap;
import com.example.uninaswap.entity.Utente;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.control.TextField;
import javafx.scene.control.PasswordField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.shape.Circle;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.net.URL;

public class ModificaProfilo {

    @FXML private TextField matricolaField;
    @FXML private TextField usernameField;
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private PasswordField confermaPasswordField;
    @FXML private ImageView profileImageView;

    private Utente profilo;
    private File immagineSelezionata;
    private ControllerUninaSwap controllerUninaSwap;

    @FXML
    public void initialize() {
        controllerUninaSwap = ControllerUninaSwap.getInstance();

        // 1. Recupero Utente
        try {
            profilo = controllerUninaSwap.getUtente();
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        // 2. Riempio i campi testuali
        if (profilo != null) {
            matricolaField.setText(profilo.getMatricola());
            usernameField.setText(profilo.getUsername());
            emailField.setText(profilo.getEmail());

            // 3. LOGICA ROBUSTA CARICAMENTO IMMAGINE
            String pathImmagineDB = profilo.getPathImmagineProfilo();

            try {
                if (pathImmagineDB == null || pathImmagineDB.contains("default") || pathImmagineDB.isEmpty()) {
                    // CASO A: Immagine di Default (Risorsa interna)
                    // Nota: Assicurati che il percorso nell'FXML (@images/...) sia corretto,
                    // oppure caricala qui esplicitamente:
                    URL resource = getClass().getResource("/com/example/uninaswap/images/immagineProfiloDefault.jpg");
                    if (resource != null) {
                        profileImageView.setImage(new Image(resource.toExternalForm()));
                    }
                } else {
                    // CASO B: Immagine caricata dall'utente (File su disco)
                    File fileImmagine = new File(pathImmagineDB);
                    if (fileImmagine.exists()) {
                        // IMPORTANTE: Convertire File -> URI -> String
                        profileImageView.setImage(new Image(fileImmagine.toURI().toString()));
                    } else {
                        System.out.println("Immagine non trovata sul disco: " + pathImmagineDB + " - Carico default.");
                    }
                }
            } catch (Exception e) {
                System.out.println("Errore caricamento immagine: " + e.getMessage());
                // Non crashare, lascia l'immagine che c'è nell'FXML
            }
        }

        // 4. Applico il cerchio all'immagine (CLIP)
        if (profileImageView != null) {
            // Reset della viewport per evitare zoom strani precedenti
            profileImageView.setViewport(null);

            Circle clip = new Circle(
                    profileImageView.getFitWidth() / 2,
                    profileImageView.getFitHeight() / 2,
                    profileImageView.getFitWidth() / 2
            );
            profileImageView.setClip(clip);
        }
    }

    @FXML
    public void cambiaImmagineProfilo(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Seleziona Immagine Profilo");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg")
        );

        Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        File selectedFile = fileChooser.showOpenDialog(stage);

        if (selectedFile != null) {
            this.immagineSelezionata = selectedFile;

            // Carica l'immagine usando toURI() per evitare errori di percorso
            Image originalImage = new Image(selectedFile.toURI().toString());

            if (profileImageView != null) {
                // Logica di ritaglio e centratura
                double width = originalImage.getWidth();
                double height = originalImage.getHeight();
                double minDimension = Math.min(width, height);
                double x, y;
                if (width > height) {
                    x = (width - height) / 2;
                    y = 0;
                } else {
                    x = 0;
                    y = 0;
                }
                Rectangle2D cropArea = new Rectangle2D(x, y, minDimension, minDimension);
                profileImageView.setViewport(cropArea);
                profileImageView.setImage(originalImage);
                // Rimuovi clip precedenti se necessario o riapplicali
            }
        }
    }

    @FXML
    public void onSalvaClick(ActionEvent event) {
        ControllerCambioBoundary controllerCambioBoundary = new ControllerCambioBoundary();
        String nuovoUsername = usernameField.getText();
        String nuovaPass = passwordField.getText();
        String confermaPass = confermaPasswordField.getText();

        if (!nuovaPass.isEmpty() && !nuovaPass.equals(confermaPass)) {
            System.out.println("Le password non coincidono!");
            return;
        }

        // Logica salvataggio...
        String pathDaSalvare = (immagineSelezionata != null) ? immagineSelezionata.getAbsolutePath() : profilo.getPathImmagineProfilo();
        profilo.setUsername(nuovoUsername);
        profilo.setPathImmagineProfilo(pathDaSalvare);
        System.out.println("Salvataggio... Nuovo path immagine: " + pathDaSalvare);
        controllerUninaSwap.ModificaUtente(this.profilo);
        controllerCambioBoundary.CambiaScena(Costanti.pathHomePage, Costanti.homepage, event);
    }

    @FXML
    public void onAnnullaClick(ActionEvent event) {
        ControllerCambioBoundary cambio = new ControllerCambioBoundary();
        cambio.CambiaScena(Costanti.pathHomePage, Costanti.homepage, event);
    }
}