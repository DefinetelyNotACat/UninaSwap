package boundary;
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
public class SignInBoundary implements Initializable {
    @FXML
    private TextField emailField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private Button confermaButton;
    private static final String EMAIL_REGEX = "^[\\w-_.+]*[\\w-_.]@([\\w]+\\.)+[\\w]+[\\w]$";
    public void onConfermaClick(ActionEvent actionEvent) {
        System.out.println("Login premuto! Validazione OK.");
        System.out.println("Email: " + emailField.getText());
        System.out.println("Password: " + passwordField.getText());
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
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
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
        confermaButton.disableProperty().bind(
                isEmailInvalid.or(isPasswordInvalid)
        );
    }
}