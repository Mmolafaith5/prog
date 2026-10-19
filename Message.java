import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Part 2: a single QuickChat message plus the shared state for all messages
 * sent while the program is running.
 */
public class Message {

    // ---- shared (class-level) state --------------------------------------
    private static final List<Message> sentMessages = new ArrayList<>();
    private static final List<Message> storedMessages = new ArrayList<>();
    private static int numMessagesSent = 0;      // starts at 0, auto-incremented
    private static final Random RANDOM = new Random();

    public static final int MAX_MESSAGE_LENGTH = 250;
    public static final String JSON_FILE = "messages.json";

    // ---- per-message state -----------------------------------------------
    private final String messageID;
    private final int messageNumber;
    private final String recipient;
    private final String message;
    private final String messageHash;

    public Message(String recipient, String message) {
        this.messageID = generateMessageID();
        this.messageNumber = numMessagesSent;     // number of this message (0-based)
        this.recipient = recipient;
        this.message = message;
        this.messageHash = createMessageHash();
    }

    /** Package-private constructor so unit tests can supply a known ID / number. */
    Message(String messageID, int messageNumber, String recipient, String message) {
        this.messageID = messageID;
        this.messageNumber = messageNumber;
        this.recipient = recipient;
        this.message = message;
        this.messageHash = createMessageHash();
    }

    // ---- required methods ------------------------------------------------

    /** Ensures the message ID is not more than ten characters. */
    public boolean checkMessageID() {
        return messageID != null && messageID.length() <= 10;
    }

    /**
     * Ensures the recipient number is no more than ten characters long and
     * starts with an international code (+ followed by digits).
     */
    public String checkRecipientCell() {
        if (isValidRecipient(recipient)) {
            return "Cell phone number successfully captured.";
        }
        return "Cell phone number is incorrectly formatted or does not contain an "
                + "international code. Please correct the number and try again.";
    }

    /** Creates and returns the hash, e.g. 00:0:HITHANKS. */
    public String createMessageHash() {
        String id = messageID == null ? "" : messageID;
        String firstTwo = id.length() >= 2 ? id.substring(0, 2) : id;

        // Ignore tokens that are only punctuation (e.g. a trailing "!")
        List<String> words = new ArrayList<>();
        for (String token : message.trim().split("\\s+")) {
            String cleaned = stripPunctuation(token);
            if (!cleaned.isEmpty()) words.add(cleaned);
        }
        String first = words.isEmpty() ? "" : words.get(0);
        String last = words.isEmpty() ? "" : words.get(words.size() - 1);

        return (firstTwo + ":" + messageNumber + ":" + first + last).toUpperCase();
    }

    /**
     * Lets the user choose to send, disregard or store the message.
     * 1 = send, 2 = disregard, 3 = store.
     */
    public String sentMessage(int choice) {
        switch (choice) {
            case 1:
                sentMessages.add(this);
                numMessagesSent++;
                return "Message successfully sent.";
            case 2:
                return "Press 0 to delete the message.";
            case 3:
                storeMessage();
                return "Message successfully stored.";
            default:
                return "Invalid option. Please choose 1, 2 or 3.";
        }
    }

    /** Returns all the messages sent while the program is running. */
    public static String printMessages() {
        StringBuilder sb = new StringBuilder();
        for (Message m : sentMessages) {
            sb.append(m.fullDetails()).append(System.lineSeparator());
        }
        return sb.toString();
    }

    /** Returns the total number of messages sent. */
    public static int returnTotalMessages() {
        return numMessagesSent;
    }

    /** Stores the message in the JSON file (messages.json). */
    public void storeMessage() {
        storedMessages.add(this);
        writeStoredMessagesToJson();
    }

    // ---- helpers ---------------------------------------------------------

    /** Returns the message-length result text used by the length test. */
    public static String checkMessageLength(String text) {
        if (text.length() <= MAX_MESSAGE_LENGTH) {
            return "Message ready to send.";
        }
        int excess = text.length() - MAX_MESSAGE_LENGTH;
        return "Message exceeds 250 characters by " + excess + "; please reduce the size.";
    }

    public static boolean isValidRecipient(String number) {
        return number != null && number.matches("\\+\\d{1,10}");
    }

    /** Randomly generated ten-digit number (first digit never 0). */
    public static String generateMessageID() {
        long id = 1_000_000_000L + (long) (RANDOM.nextDouble() * 9_000_000_000L);
        return String.valueOf(id);
    }

    private static String stripPunctuation(String word) {
        return word.replaceAll("[^A-Za-z0-9]", "");
    }

    /** Message ID, Message Hash, Recipient, Message – in that order. */
    public String fullDetails() {
        return "Message ID: " + messageID
                + "\nMessage Hash: " + messageHash
                + "\nRecipient: " + recipient
                + "\nMessage: " + message
                + "\n";
    }

    private static void writeStoredMessagesToJson() {
        StringBuilder json = new StringBuilder("[\n");
        for (int i = 0; i < storedMessages.size(); i++) {
            Message m = storedMessages.get(i);
            json.append("  {\n")
                .append("    \"messageID\": \"").append(escape(m.messageID)).append("\",\n")
                .append("    \"messageHash\": \"").append(escape(m.messageHash)).append("\",\n")
                .append("    \"recipient\": \"").append(escape(m.recipient)).append("\",\n")
                .append("    \"message\": \"").append(escape(m.message)).append("\"\n")
                .append("  }").append(i < storedMessages.size() - 1 ? "," : "").append("\n");
        }
        json.append("]\n");
        try (FileWriter writer = new FileWriter(JSON_FILE)) {
            writer.write(json.toString());
        } catch (IOException e) {
            System.out.println("Could not write to " + JSON_FILE + ": " + e.getMessage());
        }
    }

    private static String escape(String s) {
        return s.replace("\\", "\\\\").replace("\"", "\\\"")
                .replace("\n", "\\n").replace("\r", "\\r").replace("\t", "\\t");
    }

    // ---- getters ---------------------------------------------------------
    public String getMessageID() { return messageID; }
    public String getMessageHash() { return messageHash; }
    public String getRecipient() { return recipient; }
    public String getMessage() { return message; }

    /** Clears shared state (used by unit tests). */
    static void resetState() {
        sentMessages.clear();
        storedMessages.clear();
        numMessagesSent = 0;
    }
}
