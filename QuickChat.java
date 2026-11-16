import java.util.List;
import java.util.Scanner;

/**
 * QuickChat – Parts 1, 2 and 3.
 * Run this class. Users must log in before they can send or view messages.
 */
public class QuickChat {

    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);

        // Part 1: only allow access after a successful login
        Login login = new Login();
        login.registerUsers(input);
        if (!login.loginUser(input)) {
            input.close();
            return;
        }
        Message.setCurrentSender(login.getLoggedInUser());

        // Part 3: populate the stored-messages array from the JSON file
        Message.loadStoredMessages();

        System.out.println("Welcome to QuickChat.");

        int maxMessages = readInt(input, "How many messages do you wish to enter? ", 1, Integer.MAX_VALUE);
        int messagesEntered = 0;

        boolean running = true;
        while (running) {
            System.out.println("\nMenu");
            System.out.println("1) Send Messages");
            System.out.println("2) Show recently sent messages");
            System.out.println("3) Quit");
            System.out.println("4) Stored Messages");
            int option = readInt(input, "Choose an option: ", 1, 4);

            switch (option) {
                case 1:
                    if (messagesEntered >= maxMessages) {
                        System.out.println("You have already entered all " + maxMessages + " messages.");
                        break;
                    }
                    while (messagesEntered < maxMessages) {
                        sendOneMessage(input);
                        messagesEntered++;
                    }
                    System.out.println("Total number of messages sent: " + Message.returnTotalMessages());
                    break;
                case 2:
                    System.out.println("Coming Soon.");
                    break;
                case 3:
                    running = false;
                    System.out.println("Goodbye.");
                    break;
                case 4:
                    storedMessagesMenu(input);
                    break;
                default:
                    break;
            }
        }
        input.close();
    }

    // ---- Part 3: Stored Messages menu ------------------------------------
    private static void storedMessagesMenu(Scanner input) {
        boolean back = false;
        while (!back) {
            System.out.println("\nStored Messages");
            System.out.println("1) Display the sender and recipient of all stored messages");
            System.out.println("2) Display the longest stored message");
            System.out.println("3) Search for a message ID");
            System.out.println("4) Search for all messages for a recipient");
            System.out.println("5) Delete a message using the message hash");
            System.out.println("6) Display a report of all stored messages");
            System.out.println("7) Display a report of all sent messages");
            System.out.println("0) Back to main menu");
            int option = readInt(input, "Choose an option: ", 0, 7);

            switch (option) {
                case 1:
                    System.out.println(Message.displaySenderAndRecipientOfStored());
                    break;
                case 2:
                    System.out.println("Longest stored message: " + Message.longestStoredMessage());
                    break;
                case 3:
                    System.out.print("Enter the message ID: ");
                    Message found = Message.findByMessageID(input.nextLine().trim());
                    if (found == null) {
                        System.out.println("Message ID not found.");
                    } else {
                        System.out.println("Recipient: " + found.getRecipient());
                        System.out.println("Message: " + found.getMessage());
                    }
                    break;
                case 4:
                    System.out.print("Enter the recipient number (e.g. +27838884567): ");
                    List<String> results = Message.searchByRecipient(input.nextLine().trim());
                    if (results.isEmpty()) {
                        System.out.println("No messages found for that recipient.");
                    } else {
                        for (String text : results) {
                            System.out.println("\"" + text + "\"");
                        }
                    }
                    break;
                case 5:
                    System.out.print("Enter the message hash: ");
                    System.out.println(Message.deleteByMessageHash(input.nextLine().trim()));
                    break;
                case 6:
                    System.out.println(Message.storedMessagesReport());
                    break;
                case 7:
                    System.out.println(Message.sentMessagesReport());
                    break;
                case 0:
                    back = true;
                    break;
                default:
                    break;
            }
        }
    }

    // ---- Part 2: sending a message ----------------------------------------
    private static void sendOneMessage(Scanner input) {
        String recipient;
        while (true) {
            System.out.print("Enter recipient cell number (international code, e.g. +27834557896): ");
            recipient = input.nextLine().trim();
            if (Message.isValidRecipient(recipient)) {
                System.out.println("Cell phone number successfully captured.");
                break;
            }
            System.out.println("Cell phone number is incorrectly formatted or does not contain an "
                    + "international code. Please correct the number and try again.");
        }

        String text;
        while (true) {
            System.out.print("Enter your message: ");
            text = input.nextLine().trim();
            if (text.isEmpty()) {
                System.out.println("Message cannot be empty.");
            } else if (text.length() > Message.MAX_MESSAGE_LENGTH) {
                System.out.println("Please enter a message of less than 250 characters.");
            } else {
                System.out.println("Message sent");
                break;
            }
        }

        Message msg = new Message(recipient, text);
        System.out.println("Message ID generated: " + msg.getMessageID());

        System.out.println("1) Send Message");
        System.out.println("2) Disregard Message");
        System.out.println("3) Store Message to send later");
        int choice = readInt(input, "Choose an option: ", 1, 3);
        System.out.println(msg.sentMessage(choice));

        if (choice == 1) {
            System.out.println(msg.fullDetails());
        } else if (choice == 2) {
            String confirm = input.nextLine().trim();
            if (confirm.equals("0")) {
                System.out.println("Message deleted.");
            }
        }
    }

    private static int readInt(Scanner input, String prompt, int min, int max) {
        while (true) {
            System.out.print(prompt);
            String line = input.nextLine().trim();
            try {
                int value = Integer.parseInt(line);
                if (value >= min && value <= max) return value;
            } catch (NumberFormatException ignored) { }
            System.out.println("Invalid input. Please enter a number between " + min
                    + (max == Integer.MAX_VALUE ? " and above." : " and " + max + "."));
        }
    }
}
