package sample;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class ContactData  {

    private ObservableList<Contact> contactList;

    public ContactData() {
        this.contactList = FXCollections.observableArrayList();
    }

    public void addContact(Contact newContact) {
        contactList.add(newContact);
    }

    public void editContact(Contact contact, Contact newContact) {
        contactList.set(contactList.indexOf(contact), newContact);
    }

    public void removeContact(Contact contact) {
        if (contact != null) {
            contactList.remove(contact);
        }
    }

    /* Check if phone number is already in list */
    public boolean isNumberInList(String phoneNumber) {
        return !contactList.stream()
                .noneMatch(contact -> contact.getPhoneNumber().equals(phoneNumber));
    }

    /* Getters & Setters */
    public ObservableList<Contact> getContactList() {
        return contactList;
    }

    public void setContactList(ObservableList<Contact> contactList) {
        this.contactList = contactList;
    }
}
