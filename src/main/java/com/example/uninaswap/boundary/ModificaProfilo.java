package com.example.uninaswap.boundary;

import com.example.uninaswap.Costanti;
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

            // 3. LOGICA CARICAMENTO IMMAGINE
            String pathImmagineDB = profilo.getPathImmagineProfilo();
            Image imageDaCaricare = null;

            try {
                if (pathImmagineDB == null || pathImmagineDB.contains("default") || pathImmagineDB.isEmpty()) {
                    // Immagine Default
                    URL resource = getClass().getResource("/com/example/uninaswap/images/immagineProfiloDefault.jpg");
                    if (resource != null) {
                        imageDaCaricare = new Image(resource.toExternalForm());
                    }
                } else {
                    // Immagine Utente
                    File fileImmagine = new File(pathImmagineDB);
                    if (fileImmagine.exists()) {
                        imageDaCaricare = new Image(fileImmagine.toURI().toString());
                    }
                }
            } catch (Exception e) {
                System.out.println("Errore caricamento: " + e.getMessage());
            }

            // 4. Applica l'immagine col ritaglio corretto
            if (imageDaCaricare != null) {
                impostaImmagineCircolare(imageDaCaricare);
            }
        }

        // NOTA: Ho rimosso il blocco if (profileImageView != null) { setViewport(null)... }
        // che avevi qui sotto, perché annullava il lavoro fatto sopra!
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

            // Carica l'immagine
            Image originalImage = new Image(selectedFile.toURI().toString());

            // Usa lo stesso metodo helper per coerenza ed evitare errori
            impostaImmagineCircolare(originalImage);
        }
    }

    // Metodo Helper per centrare e ritagliare
    private void impostaImmagineCircolare(Image image) {
        if (image == null || profileImageView == null) return;

        double width = image.getWidth();
        double height = image.getHeight();
        double minDimension = Math.min(width, height);

        double x = 0;
        double y = 0;

        // Calcolo per centrare il quadrato di ritaglio
        if (width > height) {
            // Immagine orizzontale: prendo il centro orizzontale
            x = (width - height) / 2;
        } else if (height > width) {
            // Immagine verticale: prendo il centro verticale
            y = (height - width) / 2;
        }

        // 1. Imposta il Viewport (il ritaglio quadrato centrato)
        Rectangle2D cropArea = new Rectangle2D(x, y, minDimension, minDimension);
        profileImageView.setViewport(cropArea);

        // 2. Imposta l'immagine
        profileImageView.setImage(image);

        // 3. Applica il cerchio (Clip)
        // Usiamo fitWidth/fitHeight dell'ImageView definiti nell'FXML (150.0)
        double raggio = Math.min(profileImageView.getFitWidth(), profileImageView.getFitHeight()) / 2;
        Circle clip = new Circle(
                profileImageView.getFitWidth() / 2,
                profileImageView.getFitHeight() / 2,
                raggio
        );
        profileImageView.setClip(clip);
    }

    @FXML
    public void onSalvaClick(ActionEvent event) {
        ControllerCambioBoundary controllerCambioBoundary = new ControllerCambioBoundary();
        String nuovoUsername = usernameField.getText();
        String nuovaPass = passwordField.getText();
        String confermaPass = confermaPasswordField.getText();

        if (!nuovaPass.isEmpty() && !nuovaPass.equals(confermaPass)) {
            System.out.println("Le password non coincidono!");
            return; // Aggiungi qui un messaggio di errore a video se vuoi
        }

        // Logica salvataggio...
        String pathDaSalvare = (immagineSelezionata != null) ? immagineSelezionata.getAbsolutePath() : profilo.getPathImmagineProfilo();

        profilo.setUsername(nuovoUsername);
        profilo.setPathImmagineProfilo(pathDaSalvare);

        // Se la password è cambiata, aggiornala
        if(!nuovaPass.isEmpty()){
            profilo.setPassword(nuovaPass);
        }

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