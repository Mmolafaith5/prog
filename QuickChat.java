import java.util.Scanner;

/**
 * QuickChat – Part 2: Sending Messages.
 * Run this class. Users must log in before they can send messages.
 */
public class QuickChat {

    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);

        // 1. Only allow access after a successful login
        Login login = new Login();
        login.registerUsers(input);
        if (!login.loginUser(input)) {
            input.close();
            return;
        }

        // 2. Welcome message
        System.out.println("Welcome to QuickChat.");

        // 5. User defines how many messages they wish to enter
        int maxMessages = readInt(input, "How many messages do you wish to enter? ", 1, Integer.MAX_VALUE);
        int messagesEntered = 0;

        // 4. Run until the user selects quit
        boolean running = true;
        while (running) {
            System.out.println("\nMenu");
            System.out.println("1) Send Messages");
            System.out.println("2) Show recently sent messages");
            System.out.println("3) Quit");
            int option = readInt(input, "Choose an option: ", 1, 3);

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
                    // Total accumulated and displayed once all messages have been sent
                    System.out.println("Total number of messages sent: " + Message.returnTotalMessages());
                    break;
                case 2:
                    System.out.println("Coming Soon.");
                    break;
                case 3:
                    running = false;
                    System.out.println("Goodbye.");
                    break;
                default:
                    break;
            }
        }
        input.close();
    }

    private static void sendOneMessage(Scanner input) {
        // Recipient
        String recipient;
        while (true) {
            System.out.print("Enter recipient cell number (international code, max 10 digits, e.g. +27831234): ");
            recipient = input.nextLine().trim();
            if (Message.isValidRecipient(recipient)) {
                System.out.println("Cell phone number successfully captured.");
                break;
            }
            System.out.println("Cell phone number is incorrectly formatted or does not contain an "
                    + "international code. Please correct the number and try again.");
        }

        // Message text (max 250 characters)
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

        // Send / Disregard / Store
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
