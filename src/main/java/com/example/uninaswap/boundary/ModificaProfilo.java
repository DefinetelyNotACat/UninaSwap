package com.example.uninaswap.boundary;

import com.example.uninaswap.Costanti;
import com.example.uninaswap.controller.ControllerUninaSwap;
import com.example.uninaswap.entity.Utente;
import com.example.uninaswap.interfaces.GestoreMessaggio;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

import static com.example.uninaswap.boundary.Messaggio.TIPI.*;

public class ModificaProfilo implements Initializable, GestoreMessaggio {

    @FXML private TextField matricolaField;
    @FXML private TextField usernameField;
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private PasswordField confermaPasswordField;
    @FXML private ImageView profileImageView;
    @FXML private Button salvaButton;

    @FXML
    private Messaggio notificaController;

    // --- Testi Errore ---
    @FXML private Text erroreUsername;
    @FXML private Text erroreMatricola;
    @FXML private Text errorePassword;
    @FXML private Text erroreConfermaPassword;
    @FXML private Text erroreGenerico;

    // --- Regex ---
    private static final String REGEX_PASSWORD = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])[a-zA-Z0-9@$!%*?&._-]{8,20}$";
    private static final String REGEX_CAMPI_SEMPLICI = "^[a-zA-Z0-9]+$";
    private static final String REGEX_ALMENO_UN_NUMERO = ".*\\d.*";

    // --- Variabili di istanza ---
    private Utente profiloUtente;
    private File fileImmagineSelezionata;
    private ControllerUninaSwap controllerUninaSwap;
    private GestoreScene gestoreScene;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        controllerUninaSwap = ControllerUninaSwap.getInstance();
        gestoreScene = new GestoreScene();
        try {
            profiloUtente = controllerUninaSwap.getUtente();
        } catch (Exception e) {
            Platform.runLater(()->{
                Stage stage = (Stage) salvaButton.getScene().getWindow();
                gestoreScene.CambiaScena(Costanti.pathSignIn, Costanti.accedi, stage, "Errore nel recupero utente, Accedere o Registrarsi", ERROR);
            }

            );
            return;
        }
        if (profiloUtente != null) {
            matricolaField.setText(profiloUtente.getMatricola());
            usernameField.setText(profiloUtente.getUsername());
            emailField.setText(profiloUtente.getEmail());
            emailField.setDisable(true); // Email non modificabile
            caricaImmagineProfilo();
        }

        // 3. Configurazione Validazione (QUI C'È LA LOGICA DEL BOTTONE)
        configuraValidazione();
    }

    private void configuraValidazione() {
        // --- 1. Binding Username ---
        BooleanBinding usernameNonValido = Bindings.createBooleanBinding(() -> {
            String u = usernameField.getText();
            return u == null || u.trim().isEmpty() || !u.matches(REGEX_CAMPI_SEMPLICI);
        }, usernameField.textProperty());

        // Listener per errori visivi (Username)
        usernameField.textProperty().addListener((obs, oldVal, newVal) ->
                gestisciStileCampo(newVal, usernameField, erroreUsername, false)
        );

        // --- 2. Binding Matricola ---
        BooleanBinding matricolaNonValida = Bindings.createBooleanBinding(() -> {
            String m = matricolaField.getText();
            return m == null || m.trim().isEmpty() || !m.matches(REGEX_CAMPI_SEMPLICI) || !m.matches(REGEX_ALMENO_UN_NUMERO);
        }, matricolaField.textProperty());

        // Listener per errori visivi (Matricola)
        matricolaField.textProperty().addListener((obs, oldVal, newVal) ->
                gestisciStileCampo(newVal, matricolaField, erroreMatricola, true)
        );

        // --- 3. Binding Password (LA PARTE CRITICA) ---
        BooleanBinding passwordNonValida = Bindings.createBooleanBinding(() -> {
            String pass = passwordField.getText();
            String conf = confermaPasswordField.getText();
            boolean isPasswordVuota = pass == null || pass.trim().isEmpty();

            // CASO A: Se il campo è vuoto, NON è invalido (l'utente tiene la vecchia pass).
            // Quindi restituiamo false (nessun errore).
            if (isPasswordVuota) {
                return false;
            }

            // CASO B: L'utente ha scritto qualcosa.
            // Deve rispettare la REGEX E deve essere uguale alla CONFERMA.
            boolean regexOk = pass.matches(REGEX_PASSWORD);
            boolean matchOk = pass.equals(conf);

            // Se anche solo una condizione fallisce, restituiamo true (C'È un errore).
            return !regexOk || !matchOk;

        }, passwordField.textProperty(), confermaPasswordField.textProperty());

        // Listener per errori visivi (Password)
        passwordField.textProperty().addListener((obs, oldVal, newVal) -> gestisciStilePassword());
        confermaPasswordField.textProperty().addListener((obs, oldVal, newVal) -> gestisciStilePassword());


        // --- 4. LEGAME COL BOTTONE SALVA ---
        // Il bottone si disabilita SE:
        // Username invalido OPPURE Matricola invalida OPPURE Password invalida (se iniziata a scrivere)
        if (salvaButton != null) {
            salvaButton.disableProperty().bind(
                    usernameNonValido.or(matricolaNonValida).or(passwordNonValida)
            );
        }
    }

    // --- Gestione Stili CSS Generica ---
    private void gestisciStileCampo(String valore, TextField campo, Text testoErrore, boolean isMatricola) {
        if (erroreGenerico != null) {
            erroreGenerico.setVisible(false);
            erroreGenerico.setManaged(false);
        }

        boolean isVuoto = valore == null || valore.trim().isEmpty();
        boolean regexOk = valore != null && valore.matches(REGEX_CAMPI_SEMPLICI);
        boolean lunghezzaOk = valore != null && valore.length() >= 3 && valore.length() <= 20;
        boolean matricolaOk = !isMatricola || (valore != null && valore.matches(REGEX_ALMENO_UN_NUMERO));

        if (!isVuoto && regexOk && lunghezzaOk && matricolaOk) {
            applicaStileValido(campo, testoErrore);
        } else {
            if (isVuoto) rimuoviStili(campo, testoErrore);
            else applicaStileErrore(campo, testoErrore);
        }
    }

    // --- Gestione Stili Password ---
    private void gestisciStilePassword() {
        String pass = passwordField.getText();
        String conf = confermaPasswordField.getText();
        boolean passVuota = pass == null || pass.trim().isEmpty();

        if (passVuota) {
            rimuoviStili(passwordField, errorePassword);
            rimuoviStili(confermaPasswordField, erroreConfermaPassword);
            return;
        }

        // Controllo Regex
        if (pass.matches(REGEX_PASSWORD)) {
            applicaStileValido(passwordField, errorePassword);
        } else {
            applicaStileErrore(passwordField, errorePassword);
        }

        // Controllo Match
        if (pass.equals(conf)) {
            if (pass.matches(REGEX_PASSWORD)) {
                applicaStileValido(confermaPasswordField, erroreConfermaPassword);
            } else {
                // Uguali ma deboli
                applicaStileErrore(confermaPasswordField, erroreConfermaPassword);
            }
        } else {
            applicaStileErrore(confermaPasswordField, erroreConfermaPassword);
        }
    }

    // Helpers CSS
    private void applicaStileErrore(Node nodo, Text testoErrore) {
        if(erroreGenerico != null) {
            erroreGenerico.setVisible(false);
            erroreGenerico.setManaged(false);
        }
        if (!nodo.getStyleClass().contains("error")) nodo.getStyleClass().add("error");
        nodo.getStyleClass().remove("right");
        if (testoErrore != null) {
            testoErrore.setVisible(true);
            testoErrore.setManaged(true);
        }
    }

    private void applicaStileValido(Node nodo, Text testoErrore) {
        nodo.getStyleClass().remove("error");
        if (!nodo.getStyleClass().contains("right")) nodo.getStyleClass().add("right");
        if (testoErrore != null) {
            testoErrore.setVisible(false);
            testoErrore.setManaged(false);
        }
    }

    private void rimuoviStili(Node nodo, Text testoErrore) {
        nodo.getStyleClass().remove("error");
        nodo.getStyleClass().remove("right");
        if (testoErrore != null) {
            testoErrore.setVisible(false);
            testoErrore.setManaged(false);
        }
    }
    // --- Logica Click Salva ---
    @FXML
    public void onSalvaClick(ActionEvent event) {
        // Raccogli dati
        String nuovoUser = usernameField.getText();
        String nuovaMatr = matricolaField.getText();
        String nuovaPass = passwordField.getText();

        // Controllo Duplicati DB
        boolean userCambiato = !nuovoUser.equals(profiloUtente.getUsername());
        boolean matrCambiata = !nuovaMatr.equals(profiloUtente.getMatricola());

        if (userCambiato || matrCambiata) {
            if (controllerUninaSwap.verificaCredenzialiDuplicate(nuovoUser, nuovaMatr, profiloUtente.getEmail())) {
                mostraErroreGenerico("Username o Matricola già in uso!");
                return;
            }
        }

        // Aggiorna Entità
        profiloUtente.setUsername(nuovoUser);
        profiloUtente.setMatricola(nuovaMatr);
        String pathImg = (fileImmagineSelezionata != null) ? fileImmagineSelezionata.getAbsolutePath() : profiloUtente.getPathImmagineProfilo();
        profiloUtente.setPathImmagineProfilo(pathImg);

        if (!nuovaPass.isEmpty()) {
            profiloUtente.setPassword(nuovaPass);
            System.out.println("nuova password vale " + nuovaPass);
        }

        // Salva DB
        try {
            if (controllerUninaSwap.ModificaUtente(profiloUtente)) {
                gestoreScene.CambiaScena(Costanti.pathHomePage, Costanti.homepage, event);
            } else {
                mostraErroreGenerico("Errore salvataggio DB");
            }
        } catch (Exception e) {
            erroreGenerico.setVisible(true);
            erroreGenerico.setManaged(true);
            erroreGenerico.setText(e.getMessage());
        }
    }

    @FXML
    public void onAnnullaClick(ActionEvent event) {
        gestoreScene.CambiaScena(Costanti.pathHomePage, Costanti.homepage, event);
    }

    // --- Gestione Immagine ---
    @FXML
    public void cambiaImmagineProfilo(ActionEvent event) {
        notificaController.mostraMessaggio("Funzione cambio immagine in arrivo...", INFO);
        FileChooser fc = new FileChooser();
        fc.setTitle("Seleziona Immagine");
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("Immagini", "*.png", "*.jpg", "*.jpeg"));

        Stage s = (Stage) ((Node) event.getSource()).getScene().getWindow();
        File f = fc.showOpenDialog(s);

        if (f != null) {
            this.fileImmagineSelezionata = f;
            impostaImmagineCircolare(new Image(f.toURI().toString()));
        }
    }

    private void caricaImmagineProfilo() {
        try {
            String p = profiloUtente.getPathImmagineProfilo();
            Image img = null;
            if (p == null || p.contains("default") || p.isEmpty()) {
                URL res = getClass().getResource("/com/example/uninaswap/images/immagineProfiloDefault.jpg");
                if (res != null) img = new Image(res.toExternalForm());
            } else {
                File f = new File(p);
                if (f.exists()) img = new Image(f.toURI().toString());
            }
            if (img != null) impostaImmagineCircolare(img);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void impostaImmagineCircolare(Image img) {
        if (img == null || profileImageView == null) return;
        double min = Math.min(img.getWidth(), img.getHeight());
        double x = (img.getWidth() - min) / 2;
        double y = (img.getHeight() - min) / 2;

        profileImageView.setViewport(new Rectangle2D(x, y, min, min));
        profileImageView.setImage(img);

        double r = Math.min(profileImageView.getFitWidth(), profileImageView.getFitHeight()) / 2;
        profileImageView.setClip(new Circle(profileImageView.getFitWidth()/2, profileImageView.getFitHeight()/2, r));
    }

    private void mostraErroreGenerico(String msg) {
        if (erroreGenerico != null) {
            erroreGenerico.setText(msg);
            erroreGenerico.setVisible(true);
            erroreGenerico.setManaged(true);
        }
    }
    @Override
    public void mostraMessaggioEsterno(String testo, Messaggio.TIPI tipo) {
        if (notificaController != null) {
            notificaController.mostraMessaggio(testo, tipo);
        }
    }
}