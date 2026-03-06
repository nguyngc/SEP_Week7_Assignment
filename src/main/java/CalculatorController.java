import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class CalculatorController {

    @FXML private TextField number1Field;
    @FXML private TextField number2Field;@FXML
    private Label sumLabel;

    @FXML
    private Label productLabel;

    @FXML
    private Label subtractLabel;

    @FXML
    private Label divisionLabel;

    @FXML
    private void onCalculateClick() {
        try {
            double num1 = Double.parseDouble(number1Field.getText());
            double num2 = Double.parseDouble(number2Field.getText());

            double sum = num1 + num2;
            double product = num1 * num2;
            double subtract = num1 - num2;

            Double divisionValue = null;
            String divisionText;

            if (num2 == 0) {
                divisionText = "Cannot divide by 0";
            } else {
                divisionValue = num1 / num2;
                divisionText = String.valueOf(divisionValue);
            }

            sumLabel.setText("Sum: " + sum);
            productLabel.setText("Product: " + product);
            subtractLabel.setText("Subtract: " + subtract);
            divisionLabel.setText("Division: " + divisionText);

            // Save to DB
            ResultService.saveResult(num1, num2, sum, product, subtract, divisionValue);

        } catch (NumberFormatException e) {
            showAlert("Invalid input", "Please enter valid numbers.");
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", e.getMessage());
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}