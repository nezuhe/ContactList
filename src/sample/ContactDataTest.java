package sample;

import org.junit.Assert;
import org.junit.Test;

public class ContactDataTest {

//    private ObservableList<Contact> contactList = FXCollections.observableArrayList();

    @Test
    public void editContact() {
//        contactList.add(new Contact("Name", "Lastname", "Phonenumber", "Notes"));
        Contact contact1 = new Contact("Name", "Last name", "Phonenumber", "Notes");
        Contact contact2 = new Contact("Other name", "Different last name", "Phonenumber changed", "Noting note");
        ContactData contactData = new ContactData();
        contactData.addContact(contact1);
        int index1 = contactData.getContactList().indexOf(contact1);
        contactData.editContact(contact1, contact2);
        Assert.assertTrue(index1 == contactData.getContactList().indexOf(contact2));
    }
}