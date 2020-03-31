package sample.controllers;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.TableView;
import javafx.scene.layout.BorderPane;
import javafx.stage.FileChooser;
import sample.Contact;
import sample.ContactData;
import sample.FileHandler;

import java.io.File;
import java.io.IOException;
import java.util.Optional;

public class MainController {

    private ContactData contactData;

    private FileHandler fileHandler;

    @FXML
    private BorderPane mainBorderPane;

    @FXML
    private TableView<Contact> contactTableView;

    public void initialize() {
        contactData = new ContactData();
        fileHandler = new FileHandler();
        contactTableView.setItems(contactData.getContactList());
    }

    @FXML
    public void handleNewMenuItem() {
        showContactDialog("add");
    }

    @FXML
    public void handleEditMenuItem() {
        Contact contact = contactTableView.getSelectionModel().getSelectedItem();
        if (contact != null) {
            showContactDialog("edit", contact);
        }
    }

    @FXML
    public void handleDeleteMenuItem() {
        Contact contact = contactTableView.getSelectionModel().getSelectedItem();
        verifyAndDeleteContact(contact);
    }

    @FXML
    public void handleSaveMenuItem() {
        if (fileHandler.getFile() == null) {
            if (!showSaveAsChooser()) return;
        }
        saveContactList();
    }

    @FXML
    public void handleSaveAsMenuItem() {
        if (showSaveAsChooser()) {
            saveContactList();
        }
    }

    @FXML
    public void handleLoadMenuItem() {
        showLoadChooser();
    }

    @FXML
    public void handleCloseMenuItem() {
        checkIsSavedAndClose();
    }

    public void showContactDialog(String action) {
        showContactDialog(action, null);
    }

    public void showContactDialog(String action, Contact contact) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.initOwner(mainBorderPane.getScene().getWindow());
        FXMLLoader loader = new FXMLLoader(getClass().getResource("../contactDialog.fxml"));
        dialog.setTitle(setDialogTitle(action));

        try {
            dialog.getDialogPane().setContent(loader.load());
        } catch (IOException e) {
            e.printStackTrace();
        }

        dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CANCEL);

        ContactDialogController dialogController = loader.getController();
        if (contact != null) {
            dialogController.setContact(contact);
        }
        boolean dialogDataAreCorrect;
        do {
            dialogDataAreCorrect = true;
            Optional<ButtonType> result = dialog.showAndWait();
            if (result.isPresent() && (result.get() == ButtonType.OK)) {
                if (action.equals("add")) {
                    dialogDataAreCorrect = verifyAndAddContact(dialogController);
                } else if (action.equals("edit")) {
                    dialogDataAreCorrect = verifyAndEditContact(dialogController, contact);
                }
            } else if (result.isPresent() && (result.get() == ButtonType.CANCEL)) {
                dialog.close();
                break;
            }
        } while (!dialogDataAreCorrect);
    }

    /* Adds new contact if required dialog's fields are filled
     * and given number isn't in contact list already. */
    private boolean verifyAndAddContact(ContactDialogController dialogController) {
        Contact newContact = dialogController.getContact();
        if (!dialogController.isContactComplete()) {
            showMissingDataAlert();
            return false;
        } else if (contactData.isNumberInList(newContact.getPhoneNumber())) {
            showOccupiedPhoneNumberAlert();
            return false;
        }
        contactData.addContact(newContact);
        fileHandler.setChanged(true);
        return true;
    }

    /* Edits contact if it isn't null and required dialog's fields are filled. */
    private boolean verifyAndEditContact(ContactDialogController dialogController, Contact contact) {
        if (contact == null) {
            return false;
        }
        Contact newContact = dialogController.getContact();
        if (!dialogController.isContactComplete()) {
            showMissingDataAlert();
            return false;
        }
        contactData.editContact(contact, newContact);
        fileHandler.setChanged(true);
        return true;
    }

    /* Deletes contact if it isn't null. */
    private void verifyAndDeleteContact(Contact contact) {
        if (contact != null) {
            contactData.removeContact(contact);
            fileHandler.setChanged(true);
        }
    }

    /* Saves current ContactData.contactList to file defined in FileHandler.
    *  If file does not exist (null) then calling FileChooser method. */
    private void saveContactList() {
        if (fileHandler.getFile() != null) {
            fileHandler.saveContactListToFile(contactData.getContactList(), fileHandler.getFile());
            fileHandler.setChanged(false);
        } else {
            if (showSaveAsChooser()) {
                fileHandler.saveContactListToFile(contactData.getContactList(), fileHandler.getFile());
                fileHandler.setChanged(false);
            }
        }
    }

    /* Loads contact list from file defined in FileHandler. */
    private void loadContactList() {
        contactData.setContactList(fileHandler.loadContactListFromFile());
        fileHandler.setChanged(false);
        contactTableView.refresh();
    }

    /* Saves contacts to the file and closes app */
    public void checkIsSavedAndClose() {
        if (!verifyBeforeClose()) {
            Platform.exit();
        } else {
            if (showNotSavedDialogAndDecide()) {
                Platform.exit();
            }
        }
    }

    /* Set Dialog title depending on action taken by handle methods (add or edit) */
    private String setDialogTitle(String action) {
        if (action.equals("add")) {
            return "Add new contact";
        } else {
            return "Edit contact";
        }
    }

    /* Show Alert window if 'Phone number' is already in contact list */
    private void showOccupiedPhoneNumberAlert() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Phone number already exists");
        alert.setHeaderText("Phone number is already in list");
        alert.setContentText("Given phone number has been already assigned to other contact.");
        alert.showAndWait();
    }

    /* Show Alert window if 'First name' or 'Phone number' are missing */
    private void showMissingDataAlert() {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Missing data");
        alert.setHeaderText("Fill necessary fields");
        alert.setContentText("Please fill following fields: First name and Phone Number");
        alert.showAndWait();
    }

    private boolean showReplaceContactsDialog() {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Contact List");
        dialog.getDialogPane().setHeaderText("Are you sure?");
        dialog.getDialogPane().setContentText("If you click 'Yes' current list will be replaced.");

        dialog.getDialogPane().getButtonTypes().add(ButtonType.YES);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.NO);

        Optional<ButtonType> result = dialog.showAndWait();
        return result.isPresent() && result.get() == ButtonType.YES;
    }

    /* Returns false if Cancel button is clicked in dialog */
    private boolean showSaveAsChooser() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save As...");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Contact files","*.dat"));

        File file = fileChooser.showSaveDialog(mainBorderPane.getScene().getWindow());
        if (file == null) {
            return false;
        }
        fileHandler.setFile(file);
        return true;
    }

    /* Opens FileChooser to define file in FileHandler.
     * If window is closed by 'Cancel' button then file is null and method stops.
     * Asks for confirmation that current list will be replaced otherwise method stops. */
    private void showLoadChooser() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Load file");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Contact files", "*.dat"));

        File file = fileChooser.showOpenDialog(mainBorderPane.getScene().getWindow());
        if (file == null) {
            return;
        }
        if (!showReplaceContactsDialog()) {
            return;
        }
        fileHandler.setFile(file);
        loadContactList();
        contactTableView.setItems(contactData.getContactList());
    }

    /* Returns true if current contact list is equal to list saved in File,
     * otherwise returns false */
    private boolean verifyBeforeClose() {
        return fileHandler.isChanged();
    }

    private boolean showNotSavedDialogAndDecide() {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Contact list");
        dialog.setContentText("Do you want to save changes?");
        dialog.getDialogPane().getButtonTypes().add(ButtonType.YES);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.NO);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CANCEL);

        Optional<ButtonType> result = dialog.showAndWait();
        if (result.isPresent()) {
            if (result.get() == ButtonType.YES) {
                saveContactList();
                return true;
            } else if (result.get() == ButtonType.NO) {
                return true;
            }
        }
        return false;
    }
}
