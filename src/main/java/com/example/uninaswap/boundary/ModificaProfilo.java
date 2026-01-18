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

    @FXML private Text erroreUsername;
    @FXML private Text erroreMatricola;
    @FXML private Text errorePassword;
    @FXML private Text erroreConfermaPassword;
    @FXML private Text erroreGenerico;

    private static final String REGEX_PASSWORD = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])[a-zA-Z0-9@$!%*?&._-]{8,20}$";
    private static final String REGEX_CAMPI_SEMPLICI = "^[a-zA-Z0-9]+$";
    private static final String REGEX_ALMENO_UN_NUMERO = ".*\\d.*";

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
            matricolaField.setDisable(true);
            usernameField.setText(profiloUtente.getUsername());
            emailField.setText(profiloUtente.getEmail());
            emailField.setDisable(true); // Email non modificabile
            caricaImmagineProfilo();
        }

        // 3. Configurazione Validazione (QUI C'È LA LOGICA DEL BOTTONE)
        configuraValidazione();
    }

    private void configuraValidazione() {
        BooleanBinding usernameNonValido = Bindings.createBooleanBinding(() -> {
            String campoUsername = usernameField.getText();
            return campoUsername == null || campoUsername.trim().isEmpty() || !campoUsername.matches(REGEX_CAMPI_SEMPLICI);
        }, usernameField.textProperty());

        usernameField.textProperty().addListener((obs, oldVal, newVal) ->
                gestisciStileCampo(newVal, usernameField, erroreUsername, false)
        );

        BooleanBinding matricolaNonValida = Bindings.createBooleanBinding(() -> {
            String campoMatricola = matricolaField.getText();
            return campoMatricola == null || campoMatricola.trim().isEmpty() || !campoMatricola.matches(REGEX_CAMPI_SEMPLICI) || !campoMatricola.matches(REGEX_ALMENO_UN_NUMERO);
        }, matricolaField.textProperty());

        matricolaField.textProperty().addListener((obs, oldVal, newVal) ->
                gestisciStileCampo(newVal, matricolaField, erroreMatricola, true)
        );

        BooleanBinding passwordNonValida = Bindings.createBooleanBinding(() -> {
            String campoPassword = passwordField.getText();
            String campoConfermaPassword = confermaPasswordField.getText();
            boolean isPasswordVuota = campoPassword == null || campoPassword.trim().isEmpty();
            if (isPasswordVuota) {
                return false;
            }

            boolean regexOk = campoPassword.matches(REGEX_PASSWORD);
            boolean matchOk = campoPassword.equals(campoConfermaPassword);

            return !regexOk || !matchOk;

        }, passwordField.textProperty(), confermaPasswordField.textProperty());

        passwordField.textProperty().addListener((obs, oldVal, newVal) -> gestisciStilePassword());
        confermaPasswordField.textProperty().addListener((obs, oldVal, newVal) -> gestisciStilePassword());

        if (salvaButton != null) {
            salvaButton.disableProperty().bind(
                    usernameNonValido.or(matricolaNonValida).or(passwordNonValida)
            );
        }
    }

    private void gestisciStileCampo(String valore, TextField campo, Text testoErrore, boolean isMatricola) {
        if (erroreGenerico != null) {
            erroreGenerico.setVisible(false);
            erroreGenerico.setManaged(false);
        }

        boolean checkVuoto = valore == null || valore.trim().isEmpty();
        boolean checkRegex = valore != null && valore.matches(REGEX_CAMPI_SEMPLICI);
        boolean checkLunghezza = valore != null && valore.length() >= 3 && valore.length() <= 20;
        boolean checkMatricola = !isMatricola || (valore != null && valore.matches(REGEX_ALMENO_UN_NUMERO));

        if (!checkVuoto && checkRegex && checkLunghezza && checkMatricola) {
            applicaStileValido(campo, testoErrore);
        } else {
            if (checkVuoto) rimuoviStili(campo, testoErrore);
            else applicaStileErrore(campo, testoErrore);
        }
    }

    private void gestisciStilePassword() {
        String campoPassword = passwordField.getText();
        String campoConfermaPassword = confermaPasswordField.getText();
        boolean passVuota = campoPassword == null || campoPassword.trim().isEmpty();

        if (passVuota) {
            rimuoviStili(passwordField, errorePassword);
            rimuoviStili(confermaPasswordField, erroreConfermaPassword);
            return;
        }

        if (campoPassword.matches(REGEX_PASSWORD)) {
            applicaStileValido(passwordField, errorePassword);
        } else {
            applicaStileErrore(passwordField, errorePassword);
        }

        if (campoPassword.equals(campoConfermaPassword)) {
            if (campoPassword.matches(REGEX_PASSWORD)) {
                applicaStileValido(confermaPasswordField, erroreConfermaPassword);
            } else {
                applicaStileErrore(confermaPasswordField, erroreConfermaPassword);
            }
        } else {
            applicaStileErrore(confermaPasswordField, erroreConfermaPassword);
        }
    }

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

    @FXML
    public void onSalvaClick(ActionEvent event) {

        String usernameInserito = usernameField.getText();
        String matricolaInserita = matricolaField.getText();
        String passwordInserita = passwordField.getText();

        // Controllo Duplicati DB
        boolean userCambiato = !usernameInserito.equals(profiloUtente.getUsername());
        boolean matricolaCambiata = !matricolaInserita.equals(profiloUtente.getMatricola());

        if (userCambiato || matricolaCambiata) {
            if (controllerUninaSwap.verificaCredenzialiDuplicate(usernameInserito, matricolaInserita, profiloUtente.getEmail())) {
                mostraErroreGenerico("Username o Matricola già in uso!");
                return;
            }
        }

        // Aggiorna Entità
        profiloUtente.setUsername(usernameInserito);
        profiloUtente.setMatricola(matricolaInserita);
        String pathImg = (fileImmagineSelezionata != null) ? fileImmagineSelezionata.getAbsolutePath() : profiloUtente.getPathImmagineProfilo();
        try {
            profiloUtente.modificaImmagineProfilo(pathImg);
        }catch (Exception e){
            e.printStackTrace();
        }


        if (!passwordInserita.isEmpty()) {
            profiloUtente.setPassword(passwordInserita);
            System.out.println("nuova password vale " + passwordInserita);
        }

        // Salva DB
        try {
            if (controllerUninaSwap.ModificaUtente(profiloUtente)) {
                gestoreScene.CambiaScena(Costanti.pathHomePage, Costanti.homepage, event, "Profilo modificato con successo", SUCCESS);
            } else {
                mostraErroreGenerico("Errore salvataggio DB");
            }
        } catch (Exception exception) {
            erroreGenerico.setVisible(true);
            erroreGenerico.setManaged(true);
            erroreGenerico.setText(exception.getMessage());
        }
    }

    @FXML
    public void onAnnullaClick(ActionEvent actionEvent) {
        gestoreScene.CambiaScena(Costanti.pathHomePage, Costanti.homepage, actionEvent, "Modifica profilo annullata", INFO);
    }

    @FXML
    public void cambiaImmagineProfilo(ActionEvent actionEvent) {
        notificaController.mostraMessaggio("Funzione cambio immagine in arrivo...", INFO);
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Seleziona Immagine");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Immagini", "*.png", "*.jpg", "*.jpeg"));

        Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        File file = fileChooser.showOpenDialog(stage);

        if (file != null) {
            this.fileImmagineSelezionata = file;
            impostaImmagineCircolare(new Image(file.toURI().toString()));
        }
    }

    private void caricaImmagineProfilo() {
        try {
            String pathDalDb = profiloUtente.getPathImmagineProfilo();
            Image immagine = null;

            String BASE_PATH = System.getProperty("user.dir") + File.separator + "dati_utenti" + File.separator;

            System.out.println("Base Path impostato a: " + BASE_PATH);

            boolean isDefault = pathDalDb == null || pathDalDb.trim().isEmpty() || pathDalDb.equals("default");

            if (isDefault) {
                URL res = getClass().getResource("/com/example/uninaswap/images/immagine_di_profilo_default.jpg");
                if (res != null) immagine = new Image(res.toExternalForm());
            } else {
                File file = new File(BASE_PATH + pathDalDb);

                System.out.println("Tento di caricare da: " + file.getAbsolutePath());

                if (file.exists()) {
                    immagine = new Image(file.toURI().toString());
                } else {
                    System.out.println("File non trovato! Carico default.");
                    URL risultato = getClass().getResource("/com/example/uninaswap/images/ImmagineProfiloDefault.jpg");
                    if (risultato != null) immagine = new Image(risultato.toExternalForm());
                }
            }

            if (immagine != null) {
                impostaImmagineCircolare(immagine);
            }

        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    private void impostaImmagineCircolare(Image immagine) {
        if (immagine == null || profileImageView == null) return;
        double min = Math.min(immagine.getWidth(), immagine.getHeight());
        double x = (immagine.getWidth() - min) / 2;
        double y = (immagine.getHeight() - min) / 2;

        profileImageView.setViewport(new Rectangle2D(x, y, min, min));
        profileImageView.setImage(immagine);

        double r = Math.min(profileImageView.getFitWidth(), profileImageView.getFitHeight()) / 2;
        profileImageView.setClip(new Circle(profileImageView.getFitWidth()/2, profileImageView.getFitHeight()/2, r));
    }

    private void mostraErroreGenerico(String messaggioErrore) {
        if (erroreGenerico != null) {
            erroreGenerico.setText(messaggioErrore);
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