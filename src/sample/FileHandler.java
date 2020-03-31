package sample;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/* FileHandler class is responsible for operating on file:
   writing contact to new file or existing one, and reading from chosen file. */
public class FileHandler {

    private File file;

    private boolean changed;

    public FileHandler() {
        changed = false;
    }

    /* Saving serializable list of contacts to the *.dat file */
    public void saveContactListToFile(ObservableList<Contact> contactList, File file) {
        try (ObjectOutputStream outputStream =
                new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(file)))) {
            outputStream.writeObject(getContactArrayList(contactList));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /* Loading serializable list of contacts from the default *.dat file */
    public ObservableList<Contact> loadContactListFromFile() {
        ObservableList<Contact> list = loadContactListFromFile(this.file);
        return list;
    }

    /* Loading serializable list of contacts from the given *.dat file */
    public ObservableList<Contact> loadContactListFromFile(File file) {
        ArrayList<SerializableContact> contactArrayList = new ArrayList<>();
        try (ObjectInputStream inputStream =
                     new ObjectInputStream(new BufferedInputStream(new FileInputStream(file)))) {
            contactArrayList = (ArrayList<SerializableContact>) inputStream.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return FXCollections.observableArrayList(getContactObservableList(contactArrayList));
    }

    public boolean isContactListSaved(ObservableList<Contact> contactList) {
        ObservableList<Contact> currentSortedContactList = contactList.sorted();
        ObservableList<Contact> savedSortedContactList = loadContactListFromFile().sorted();
        return currentSortedContactList.equals(savedSortedContactList);
    }

    /* Converts ObservableList<Contact> to serializable ArrayList<SerializableContact> */
    private List<SerializableContact> getContactArrayList(ObservableList<Contact> contactObservableList) {
        ArrayList<SerializableContact> contactArrayList = new ArrayList<>();
        for (Contact contact : contactObservableList) {
            contactArrayList.add(convertContactToSerializableContact(contact));
        }
        return contactArrayList;
    }

    /* Converts ArrayList<SerializableContact> read from file to ObservableList<Contact> */
    private ObservableList<Contact> getContactObservableList(ArrayList<SerializableContact> contactArrayList) {
        ObservableList<Contact> observableList = FXCollections.observableArrayList();
        for (SerializableContact serializableContact : contactArrayList) {
            observableList.add(convertSerializableContactToContact(serializableContact));
        }
        return observableList;
    }

    /* Converts Contact object to SerializableContact object */
    private SerializableContact convertContactToSerializableContact(Contact contact) {
        String firstName = contact.getFirstName();
        String lastName = contact.getLastName();
        String phoneNumber = contact.getPhoneNumber();
        String notes = contact.getNotes();
        return new SerializableContact(firstName, lastName, phoneNumber, notes);
    }

    /* Converts SerializableContact object to Contact object */
    private Contact convertSerializableContactToContact(SerializableContact serializableContact) {
        String firstName = serializableContact.getFirstName();
        String lastName = serializableContact.getLastName();
        String phoneNumber = serializableContact.getPhoneNumber();
        String notes = serializableContact.getNotes();
        return new Contact(firstName, lastName, phoneNumber, notes);
    }

    public File getFile() {
        return file;
    }

    public boolean isChanged() {
        return changed;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public void setChanged(boolean changed) {
        this.changed = changed;
    }
}
