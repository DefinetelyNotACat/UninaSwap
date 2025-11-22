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
import javafx.scene.text.Text;

import java.net.URL;
import java.util.ResourceBundle;
public class SignBoundary implements Initializable {
    @FXML
    private Text erroreUsername;
    @FXML
    private Text erroreMatricola;
    @FXML
    private Text errorePassword;
    @FXML
    private Text erroreConfermaPassword;
    @FXML
    private Text erroreEmail;
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

    private static final String PASSWORD_REGEX = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z]).{8,}$";

    private ControllerUninaSwap controllerUninaSwap;
    private ControllerCambioBoundary controllerCambioBoundary = new ControllerCambioBoundary();

    public void onConfermaClick(ActionEvent actionEvent) {
        System.out.println("Login premuto! Validazione OK.");
        System.out.println("Email: " + emailField.getText());
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
                    erroreEmail.setVisible(true);
                    erroreEmail.setManaged(true);
                }
            } else {
                emailField.getStyleClass().remove("error");
                emailField.getStyleClass().add("right");
                erroreEmail.setVisible(false);
                erroreEmail.setManaged(false);
            }
        });

        /*===========================================================
               Comprendiamo se siamo in signIn o signUp
        -------------------------------------------------------------*/
        if (confermaPasswordField == null) {
            System.out.println("Caricato signIn.fxml");

            passwordField.textProperty().addListener((observable, oldValue, newValue) -> {
                boolean eVuoto = newValue == null || newValue.trim().isEmpty();
                boolean eValido = newValue != null && newValue.matches(PASSWORD_REGEX);

                if (!eVuoto && !eValido) {
                    if (!passwordField.getStyleClass().contains("error")) {
                        passwordField.getStyleClass().add("error");
                        errorePassword.setVisible(true);
                        errorePassword.setManaged(true);
                    }
                } else {
                    passwordField.getStyleClass().remove("error");
                    passwordField.getStyleClass().add("right");
                    errorePassword.setVisible(false);
                    errorePassword.setManaged(false);
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
                        erroreMatricola.setVisible(true);
                        erroreMatricola.setManaged(true);
                    }
                } else {
                    matricolaField.getStyleClass().remove("error");
                    matricolaField.getStyleClass().add("right");
                    erroreMatricola.setVisible(false);
                    erroreMatricola.setManaged(false);
                }
            });
            usernameField.textProperty().addListener((observable, oldValue, newValue) -> {
                boolean eVuoto = newValue == null || newValue.trim().isEmpty();
                boolean eValido = newValue != null && newValue.length() >= 3;
                if (!eVuoto && !eValido) {
                    if (!usernameField.getStyleClass().contains("error")) {
                        usernameField.getStyleClass().add("error");
                        erroreUsername.setVisible(true);
                        erroreUsername.setManaged(true);
                    }
                } else {
                    usernameField.getStyleClass().remove("error");
                    usernameField.getStyleClass().add("right");
                    erroreUsername.setVisible(false);
                    erroreUsername.setManaged(false);
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
                errorePassword.setVisible(true);
                errorePassword.setManaged(true);
            }
        }
        else if (!confermaPasswordEVuota && !passwordSonoUguali) {
            if (!passwordField.getStyleClass().contains("error")) {
                passwordField.getStyleClass().add("error");
                erroreConfermaPassword.setVisible(true);
                erroreConfermaPassword.setManaged(true);
            }
        } else {
            passwordField.getStyleClass().remove("error");
            passwordField.getStyleClass().add("right");

        }
        if (!confermaPasswordEVuota && !passwordSonoUguali) {
            if (!confermaPasswordField.getStyleClass().contains("error")) {
                confermaPasswordField.getStyleClass().add("error");
                erroreConfermaPassword.setVisible(true);
                erroreConfermaPassword.setManaged(true);
            }
        } else {
            confermaPasswordField.getStyleClass().remove("error");
            confermaPasswordField.getStyleClass().add("right");
            erroreConfermaPassword.setVisible(false);
            erroreConfermaPassword.setManaged(false);
        }
    }
}