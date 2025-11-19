package boundary;

import Controller.ControllerUninaSwap;
import com.example.uninaswap.Costanti;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class SignBoundary implements Initializable {
    @FXML
    private TextField emailField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private PasswordField confermaPasswordField;
    @FXML
    private TextField usernameField;
    @FXML
    private TextField matricolaField;
    @FXML
    private Button confermaButton;
    private static final String EMAIL_REGEX = "^[\\w-_.+]+@studenti\\.unina\\.it$";    private ControllerUninaSwap controllerUninaSwap;
    public void onConfermaClick(ActionEvent actionEvent) {
        System.out.println("Login premuto! Validazione OK.");
        System.out.println("Email: " + emailField.getText());
        System.out.println("Password: " + passwordField.getText());
        if(confermaPasswordField == null){
            //TODO! LOGICA SIGN IN
        }
        else{
            registraUtente();
        }
    }

    public void onRegistraClick(ActionEvent actionEvent) {
        System.out.println(actionEvent + "\n" + actionEvent.getSource().toString());
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/uninaswap/signUp.fxml"));
            Parent newRoot = loader.load();
            Scene currentScene = ((Node) actionEvent.getSource()).getScene();
            currentScene.setRoot(newRoot);
            Stage stage = (Stage) currentScene.getWindow();
            stage.setTitle(Costanti.registrati);
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Errore nel caricamento di signUp.fxml: " + e.getMessage());
        }
    }

    public void onAccediClick(ActionEvent actionEvent) {
        System.out.println(actionEvent);
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/uninaswap/signIn.fxml"));
            Parent newRoot = loader.load();
            Scene currentScene = ((Node) actionEvent.getSource()).getScene();
            currentScene.setRoot(newRoot);
            Stage stage = (Stage) currentScene.getWindow();
            stage.setTitle(Costanti.accedi);
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Errore nel caricamento di signUp.fxml: " + e.getMessage());
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        this.controllerUninaSwap = new ControllerUninaSwap();
        BooleanBinding isEmailInvalid = Bindings.createBooleanBinding(() -> {
            String email = emailField.getText();
            return email == null || email.trim().isEmpty() || !email.matches(EMAIL_REGEX);
        }, emailField.textProperty());
        BooleanBinding isPasswordInvalid = Bindings.createBooleanBinding(() -> {
            String pass = passwordField.getText();
            return pass == null || pass.trim().isEmpty() || pass.length() < 8;
        }, passwordField.textProperty());

        emailField.textProperty().addListener((observable, oldValue, newValue) -> {
            boolean isEmpty = newValue == null || newValue.trim().isEmpty();
            boolean isValid = newValue != null && newValue.matches(EMAIL_REGEX);
            if (!isEmpty && !isValid) {
                if (!emailField.getStyleClass().contains("error")) {
                    emailField.getStyleClass().add("error");
                }
            } else {
                emailField.getStyleClass().remove("error");
            }
        });

        // Comprendiamo se siamo in signIn o signUp
        if (confermaPasswordField == null) {
            System.out.println("Caricato signIn.fxml");

            passwordField.textProperty().addListener((observable, oldValue, newValue) -> {
                boolean isEmpty = newValue == null || newValue.trim().isEmpty();
                boolean isValid = newValue != null && newValue.length() >= 8;

                if (!isEmpty && !isValid) {
                    if (!passwordField.getStyleClass().contains("error")) {
                        passwordField.getStyleClass().add("error");
                    }
                } else {
                    passwordField.getStyleClass().remove("error");
                }
            });
            confermaButton.disableProperty().bind(isEmailInvalid.or(isPasswordInvalid));
        } else {
            System.out.println("Caricato signUp.fxml");
            BooleanBinding usernameInvalid = Bindings.createBooleanBinding(() -> {
                String username = usernameField.getText();
                return username == null || username.trim().isEmpty();
            }, usernameField.textProperty());

            BooleanBinding matricolaInvalid = Bindings.createBooleanBinding(() -> {
                String matricola = matricolaField.getText();
                return matricola == null || matricola.trim().isEmpty();
            }, matricolaField.textProperty());
            matricolaField.textProperty().addListener((observable, oldValue, newValue) -> {
                boolean isEmpty = newValue == null || newValue.trim().isEmpty();
                boolean isValid = newValue.trim().length() >= 3; // Modificato per coerenza
                if (!isEmpty && !isValid) {
                    if (!matricolaField.getStyleClass().contains("error")) {
                        matricolaField.getStyleClass().add("error");
                    }
                } else {
                    matricolaField.getStyleClass().remove("error");
                }
            });
            usernameField.textProperty().addListener((observable, oldValue, newValue) -> {
                boolean isEmpty = newValue == null || newValue.trim().isEmpty();
                boolean isValid = newValue != null && newValue.length() >= 3;
                if (!isEmpty && !isValid) {
                    if (!usernameField.getStyleClass().contains("error")) {
                        usernameField.getStyleClass().add("error");
                    }
                } else {
                    usernameField.getStyleClass().remove("error");
                }
            });
            BooleanBinding passwordNonMatchano = Bindings.createBooleanBinding(() -> {
                        String password = passwordField.getText();
                        String confermaPassword = confermaPasswordField.getText();
                        return password == null || confermaPassword == null || password.trim().isEmpty() || !password.equals(confermaPassword);
                    },
                    passwordField.textProperty(),
                    confermaPasswordField.textProperty()
            );
            passwordField.textProperty().addListener((obs, oldVal, newVal) -> {
                validatePasswords();
            });

            confermaPasswordField.textProperty().addListener((obs, oldVal, newVal) -> {
                validatePasswords();
            });
            confermaButton.disableProperty().bind(isEmailInvalid.or(isPasswordInvalid).or(matricolaInvalid).
                    or(usernameInvalid).or(passwordNonMatchano)
            );
        }
    }
    private void registraUtente(){
        String username = usernameField.getText();
        String password = passwordField.getText();
        String matricola = matricolaField.getText();
        String email = emailField.getText();
        controllerUninaSwap.creaUtente(username, password, matricola, email);
    }
    private void validatePasswords() {
        String password = passwordField.getText();
        String confermaPassword = confermaPasswordField.getText();
        boolean isPassLengthValid = password != null && password.length() >= 8;
        boolean isPassEmpty = password == null || password.trim().isEmpty();
        boolean doPasswordsMatch = password != null && password.equals(confermaPassword);
        boolean isConfirmEmpty = confermaPassword == null || confermaPassword.trim().isEmpty();

        if (!isPassEmpty && !isPassLengthValid) {
            if (!passwordField.getStyleClass().contains("error")) {
                passwordField.getStyleClass().add("error");
            }
        }
        else if (!isConfirmEmpty && !doPasswordsMatch) {
            if (!passwordField.getStyleClass().contains("error")) {
                passwordField.getStyleClass().add("error");
            }
        }
        else {
            passwordField.getStyleClass().remove("error");
        }
        if (!isConfirmEmpty && !doPasswordsMatch) {
            if (!confermaPasswordField.getStyleClass().contains("error")) {
                confermaPasswordField.getStyleClass().add("error");
            }
        }
        else {
            confermaPasswordField.getStyleClass().remove("error");
        }

    }
}