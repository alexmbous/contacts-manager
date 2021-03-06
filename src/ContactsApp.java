import util.Input;

import java.io.IOException;
import java.nio.file.*;
import java.text.NumberFormat;
import java.text.ParsePosition;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class ContactsApp {
    private static ArrayList<Contact> contacts = new ArrayList<>();
    private static String directory = "data";
    private static String filename = "contacts.txt";
    private static Path dataDirectory = Paths.get(directory);
    private static Path dataFile = Paths.get(directory, filename);

    public static void main(String[] args) {
        initializeData();
        options();
    }

    protected static void initializeData() {
        if (Files.notExists(dataDirectory)) {
            Input input = new Input();
            System.out.print("Data directory \"" + dataDirectory.toAbsolutePath() +
                    "\" does not exist. Do you wish to create it? (yes/no) > ");
            boolean choice = input.yesNo();
            if (choice) {
                createDirectory();
            } else {
                System.out.println("If there is no data file, then there is no reason to continue the program.");
                exit();
            }
        } else {
            System.out.println("Data directory \"" + dataDirectory.toAbsolutePath() + "\" exists, checking for the data file now...");
        }
        if (!Files.exists(dataFile)) {
            System.out.println("The file \"" + dataFile.toAbsolutePath() + "\" does not exist.\nWant to create a new data file?");
            Input input = new Input();
            boolean shouldCreate = input.yesNo();
            if (shouldCreate) {
                createDataFile();
            } else options();
        } else {
            System.out.println("Data file \"" + dataFile.toAbsolutePath() + "\" exists, reading from the file...");
            loadFile();
        }
    }
    public static void loadFile() {
        try {
            List<String> loadContacts = Files.readAllLines(dataFile);
            for (String item : loadContacts) {
                String[] words = item.split("\\W+");
                if (words.length == 4) {
                    String name = words[0] + " " + words[1];
                    String number = words[2] + words[3];
                    number = String.valueOf(number).replaceFirst("(\\d{3})(\\d+)","$1-$2");
                    createContact(name, number);
                } else if (words.length == 5) {
                    String name = words[0] + " " + words[1];
                    String number = words[2]+words[3]+words[4];
                    number = String.valueOf(number).replaceFirst("(\\d{3})(\\d{3})(\\d+)", "($1) $2-$3");
                    createContact(name, number);
                }

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static void options() {
        Input input = new Input();
        System.out.println("\n1. View contacts");
        System.out.println("2. Add a new contact");
        System.out.println("3. Search for a contact by name");
        System.out.println("4. Delete an existing contact");
        System.out.println("5. Save and Exit");
        System.out.println("Enter an option (1, 2, 3, 4, or 5) > ");
        int choice = input.getInt(1, 5);
        switch (choice) {
            case 1:
                viewContacts();
                options();
                break;
            case 2:
                makeContact();
                break;
            case 3:
                searchContact();
                break;
            case 4:
                deleteContact();
                break;
            case 5:
                exit();
                break;
            default: {
                System.out.println("Sorry, that is not a valid option. Please try again.");
                options();
            }
        }
    }
    protected static void createDirectory() {
        try {
            Files.createDirectories(dataDirectory);
            System.out.println("Creating \"" + dataDirectory.toAbsolutePath() + "\"");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    protected static void createDataFile() {
        try {
            Files.createFile(dataFile);
            System.out.println("Creating data file\"" + dataFile.toAbsolutePath() + "\"...");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    protected static void viewContacts() {
        if (contacts.isEmpty()) {
            System.out.println("There are currently no contacts");
        } else {
                // Print the list objects in tabular format.
                System.out.println("|----------------------------------------------------|");
                System.out.printf("%-5s %-18s %-5s %-20s %-5s","|", "Contact Name", "++", "Contact Number"," |");
                System.out.println();
                System.out.println("|----------------------------------------------------|");
                for(Contact contact : contacts){
                    System.out.format("%-5s %-18s %-5s %-20s %-5s","|>", contact.getName(),"<>", contact.getPhone(), "<|");
                    System.out.println();
                }
                System.out.println("|----------------------------------------------------|");
            }
            }


    protected static void makeContact() {
        String name = getNewContactName();
        String number = getPhoneNumber();
        createContact(name, number);
        viewContacts();
        options();
    }
    protected static String getNewContactName() {
        Input input = new Input();
        String newName;
        boolean confirm;
        System.out.print("New contact name: > ");
        newName = input.getString();
        if (isLetters(newName)) {
            System.out.print("Name: \"" + newName + "\" is correct. Confirm? (yes/no) > ");
            confirm = input.yesNo();
            if (confirm) {
                return newName;
            } else if (!confirm) {
                return getNewContactName();
            }
        } else if (isLetters(newName)) {
            System.out.println("Only letters are valid!");
            return getNewContactName();
        }
        return "Broken :(";
    }
    protected static String getPhoneNumber () {
        Input input = new Input();
        String newPhone;
        boolean confirm;
        System.out.print("New contact number: > ");
        newPhone = input.getString();
        if (!isNumbers(newPhone)) {
            System.out.println("Only numbers are valid, either 7 or 10 digits!");
            return getPhoneNumber();
        } else {
            System.out.println("New contact number: " + newPhone + "\nConfirm? (yes/no) >");
            confirm = input.yesNo();
            if (!confirm) {
                getPhoneNumber();
            } else {
                return formatNumber(newPhone);
            }
        }
        return "Broken";
    }
    public static boolean isNumbers(String phone)
    {
        if (phone.length() == 7 || phone.length() == 10) {
            NumberFormat formatter = NumberFormat.getInstance();
            ParsePosition position = new ParsePosition(0);
            formatter.parse(phone, position);
            return phone.length() == position.getIndex();
        } else {
            return false;
        }
    }
    public static String formatNumber (String phone) {
        if (phone.length() == 7) {
            return String.valueOf(phone).replaceFirst("(\\d{3})(\\d+)","$1-$2");
        } else if (phone.length() == 10) {
            return String.valueOf(phone).replaceFirst("(\\d{3})(\\d{3})(\\d+)", "($1)-$2-$3");
        } else {
            System.out.println("Broken in formatNumber!!! HELP!!");
            return "broken";
        }
    }
    public static boolean isLetters( String str ) {
        int counter = 0;
        for (char c : str.toCharArray()) {
            if (Character.isLetter(c) || c == ' ') {
                counter += 1;
            }
        }
        if (counter != str.length()) {
            return false;
        } else return true;
    }
    public static void createContact(String name, String number) {
        Contact contact = new Contact();
        contact.setName(name);
        contact.setPhone(number);
        contacts.add(contact);
    }
    protected static void searchContact () {
        int entriesMatching = 0;
        Input input = new Input();
        System.out.print("Contact name: > ");
        String search = input.getString();
        search = search.toLowerCase();
        for (Contact contact : contacts) {
            if (String.valueOf(contact.getName()).contains(search)) {
                System.out.println(contact.getName());
                entriesMatching++;
            }
        }
        if (entriesMatching == 1) {
            System.out.println(entriesMatching + " entry matches the given search: " + search);
            options();
        } else if (entriesMatching > 0) {
            System.out.println(entriesMatching + " entries match the given search: " + search);
            options();
        } else {
            System.out.println("The contact " + search + " does not exist.");
            options();
        }
    }
    protected static void deleteContact () {
        if (contacts.isEmpty()) {
            System.out.println("There are no contacts, unable to delete. Trying adding contacts first.");
            options();
        }
        Input input = new Input();
        CopyOnWriteArrayList<Contact> tempList = new CopyOnWriteArrayList<>();
        for (Contact contact : contacts) {
            tempList.add(contact);
        }
        viewContacts();
        System.out.println("Contact to delete (type exact name): > ");
        String contactToDelete = input.getString();
        for (Contact item : tempList) {
            if (item.getName().equalsIgnoreCase(contactToDelete)) {
                System.out.println("Confirm delete " + item.getName() + "? (yes/no) > ");
                boolean confirm = input.yesNo();
                if (confirm) {
                    System.out.println("Removing " + item.getName() + "...");
                    tempList.remove(item);
                } else {
                    System.out.println("Cancelled deletion of " + item.getName());
                    options();
                }
            } else if (tempList.indexOf(item) == -1) {
                System.out.println("Unable to find contact, please try again.");
                deleteContact();
            }
        }
        contacts.clear();
        for (Contact item : tempList) {
            contacts.add(item);
        }
        options();
    }
    protected static void saveContactFile() {
        List<String> contactsAsStrings = new ArrayList<>();
        System.out.println("Saving contact file...");
        for (Contact contact : contacts) {
            contactsAsStrings.add(contact.getName() + " " + contact.getPhone());
        }
        try {
            Files.write(dataFile,contactsAsStrings);
            System.out.println("Contact file save successful!");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private static void exit () {
        saveContactFile();
        System.exit(0);
    }
}
