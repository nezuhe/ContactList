package sample.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import sample.Contact;

public class ContactDialogController {

    @FXML
    private TextField firstNameTextField;

    @FXML
    private TextField lastNameTextField;

    @FXML
    private TextField phoneNumberTextField;

    @FXML
    private TextField notesTextField;

    /* Getting contact from Contact Dialog */
    public Contact getContact() {
        String firstName = firstNameTextField.getText();
        String lastName = lastNameTextField.getText();
        String phoneNumber = phoneNumberTextField.getText();
        String notes = notesTextField.getText();
        return new Contact(firstName, lastName, phoneNumber, notes);
    }

    /* Setting contact to Contact Dialog for edit */
    public void setContact(Contact contact) {
        firstNameTextField.setText(contact.getFirstName());
        lastNameTextField.setText(contact.getLastName());
        phoneNumberTextField.setText(contact.getPhoneNumber());
        notesTextField.setText(contact.getNotes());
    }

    /* Check if all necessary fields are filled */
    public boolean isContactComplete() {
        return !firstNameTextField.getText().isEmpty() && !phoneNumberTextField.getText().isEmpty();
    }

    private String checkIfTextFieldIsNull (TextField textField) {
        if (textField == null) {
            return "";
        }
        return textField.getText();
    }
}
