package boundary;
import Controller.ControllerUninaSwap;
import com.example.uninaswap.Costanti;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import java.net.URL;
import java.util.ResourceBundle;
public class SignBoundary implements Initializable {
    @FXML
    private TextField usernameField;
    @FXML
    private TextField emailField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private PasswordField confermaPasswordField;
    @FXML
    private TextField matricolaField;
    @FXML
    private Button confermaButton;
    @FXML
    private Button registraButton;
    @FXML
    private Button accediButton;

    private static final String EMAIL_REGEX_UNINA = "^[\\w-_.+]+@studenti\\.unina\\.it$";
    // Regex: Almeno 1 numero, 1 minuscola, 1 maiuscola, e lunghezza minima 8
    private static final String PASSWORD_REGEX = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z]).{8,}$";

    private ControllerUninaSwap controllerUninaSwap;
    private ControllerCambioBoundary controllerCambioBoundary = new ControllerCambioBoundary();

    public void onConfermaClick(ActionEvent actionEvent) {
        System.out.println("Login premuto! Validazione OK.");
        System.out.println("Email: " + emailField.getText());
        // Non stampare la password in chiaro in un'app reale per sicurezza

        try {
            if (confermaPasswordField == null) {
                accediUtente();
            } else {
                registraUtente();
            }
            controllerCambioBoundary.CambiaScena(Costanti.pathHomePage, Costanti.homepage, actionEvent);
        } catch (Exception e) {
            System.out.println("Errore! " + e.getMessage());
        }
    }

    private void accediUtente() throws Exception{
        String email = emailField.getText();
        String password = passwordField.getText();
        controllerUninaSwap.accediUtente(email, password);
    }

    public void onRegistraClick(ActionEvent actionEvent) {
        System.out.println(actionEvent + "\n" + actionEvent.getSource().toString());
        controllerCambioBoundary.CambiaScena(Costanti.pathSignUp, Costanti.registrati, actionEvent);
    }

    public void onAccediClick(ActionEvent actionEvent) {
        System.out.println(actionEvent);
        controllerCambioBoundary.CambiaScena(Costanti.pathSignIn, Costanti.accedi, actionEvent);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        this.controllerUninaSwap = new ControllerUninaSwap();

        BooleanBinding emailNonValida = Bindings.createBooleanBinding(() -> {
            String email = emailField.getText();
            return email == null || email.trim().isEmpty() || !email.matches(EMAIL_REGEX_UNINA);
        }, emailField.textProperty());

        // MODIFICATO: Ora controlla il Regex completo (Maiuscola, Minuscola, Numero, 8 chars)
        BooleanBinding passwordNonValida = Bindings.createBooleanBinding(() -> {
            String password = passwordField.getText();
            return password == null || !password.matches(PASSWORD_REGEX);
        }, passwordField.textProperty());

        emailField.textProperty().addListener((observable, oldValue, newValue) -> {
            boolean vuoto = newValue == null || newValue.trim().isEmpty();
            boolean valido = newValue != null && newValue.matches(EMAIL_REGEX_UNINA);
            if (!vuoto && !valido) {
                if (!emailField.getStyleClass().contains("error")) {
                    emailField.getStyleClass().add("error");
                }
            } else {
                emailField.getStyleClass().remove("error");
            }
        });

        /*===========================================================
               Comprendiamo se siamo in signIn o signUp
        -------------------------------------------------------------*/
        if (confermaPasswordField == null) {
            System.out.println("Caricato signIn.fxml");

            passwordField.textProperty().addListener((observable, oldValue, newValue) -> {
                boolean eVuoto = newValue == null || newValue.trim().isEmpty();
                // MODIFICATO: Usa il Regex anche qui per il bordo rosso
                boolean eValido = newValue != null && newValue.matches(PASSWORD_REGEX);

                if (!eVuoto && !eValido) {
                    if (!passwordField.getStyleClass().contains("error")) {
                        passwordField.getStyleClass().add("error");
                    }
                } else {
                    passwordField.getStyleClass().remove("error");
                }
            });
            confermaButton.disableProperty().bind(emailNonValida.or(passwordNonValida));
        } else {
            System.out.println("Caricato signUp.fxml");
            BooleanBinding usernameNonValido = Bindings.createBooleanBinding(() -> {
                String username = usernameField.getText();
                return username == null || username.trim().isEmpty();
            }, usernameField.textProperty());

            BooleanBinding matricolaNonValida = Bindings.createBooleanBinding(() -> {
                String matricola = matricolaField.getText();
                return matricola == null || matricola.trim().isEmpty();
            }, matricolaField.textProperty());

            matricolaField.textProperty().addListener((observable, oldValue, newValue) -> {
                boolean eVuoto = newValue == null || newValue.trim().isEmpty();
                boolean eValido = newValue.trim().length() >= 3;
                if (!eVuoto && !eValido) {
                    if (!matricolaField.getStyleClass().contains("error")) {
                        matricolaField.getStyleClass().add("error");
                    }
                } else {
                    matricolaField.getStyleClass().remove("error");
                }
            });
            usernameField.textProperty().addListener((observable, oldValue, newValue) -> {
                boolean eVuoto = newValue == null || newValue.trim().isEmpty();
                boolean eValido = newValue != null && newValue.length() >= 3;
                if (!eVuoto && !eValido) {
                    if (!usernameField.getStyleClass().contains("error")) {
                        usernameField.getStyleClass().add("error");
                    }
                } else {
                    usernameField.getStyleClass().remove("error");
                }
            });
            BooleanBinding passwordNonCombaciano = Bindings.createBooleanBinding(() -> {
                        String password = passwordField.getText();
                        String confermaPassword = confermaPasswordField.getText();
                        return password == null || confermaPassword == null || password.trim().isEmpty() || !password.equals(confermaPassword);
                    },
                    passwordField.textProperty(),
                    confermaPasswordField.textProperty()
            );

            passwordField.textProperty().addListener((obs, oldVal, newVal) -> {
                validaPasswords();
            });

            confermaPasswordField.textProperty().addListener((obs, oldVal, newVal) -> {
                validaPasswords();
            });

            confermaButton.disableProperty().bind(emailNonValida.or(passwordNonValida).or(matricolaNonValida).
                    or(usernameNonValido).or(passwordNonCombaciano)
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

    private void validaPasswords() {
        String password = passwordField.getText();
        String confermaPassword = confermaPasswordField.getText();
        boolean passwordComplessa = password != null && password.matches(PASSWORD_REGEX);
        boolean passwordVuota = password == null || password.trim().isEmpty();
        boolean passwordSonoUguali = password != null && password.equals(confermaPassword);
        boolean confermaPasswordEVuota = confermaPassword == null || confermaPassword.trim().isEmpty();

        if (!passwordVuota && !passwordComplessa) {
            if (!passwordField.getStyleClass().contains("error")) {
                passwordField.getStyleClass().add("error");
            }
        }
        else if (!confermaPasswordEVuota && !passwordSonoUguali) {
            if (!passwordField.getStyleClass().contains("error")) {
                passwordField.getStyleClass().add("error");
            }
        } else {
            passwordField.getStyleClass().remove("error");
        }
        if (!confermaPasswordEVuota && !passwordSonoUguali) {
            if (!confermaPasswordField.getStyleClass().contains("error")) {
                confermaPasswordField.getStyleClass().add("error");
            }
        } else {
            confermaPasswordField.getStyleClass().remove("error");
        }
    }
}