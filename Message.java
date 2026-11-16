import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * QuickChat message (Parts 2 and 3).
 * Holds one message plus the shared arrays used by the whole application:
 * sent, disregarded and stored messages, message hashes and message IDs.
 */
public class Message {

    // ---- shared arrays (Part 3) ------------------------------------------
    private static final List<Message> sentMessages = new ArrayList<>();
    private static final List<Message> disregardedMessages = new ArrayList<>();
    private static final List<Message> storedMessages = new ArrayList<>();
    private static final List<String> messageHashes = new ArrayList<>();
    private static final List<String> messageIDs = new ArrayList<>();

    private static int numMessagesSent = 0;          // starts at 0, auto-incremented
    private static String currentSender = "Unknown"; // set to the logged-in user
    private static String jsonFile = "messages.json";
    private static final Random RANDOM = new Random();

    public static final int MAX_MESSAGE_LENGTH = 250;

    // ---- per-message state -----------------------------------------------
    private final String messageID;
    private final int messageNumber;
    private final String sender;
    private final String recipient;
    private final String message;
    private final String messageHash;
    private String flag = "";                        // Sent / Stored / Disregard

    public Message(String recipient, String message) {
        this(generateMessageID(), numMessagesSent, recipient, message);
    }

    /** Lets a developer (or a unit test) supply a known ID and message number. */
    Message(String messageID, int messageNumber, String recipient, String message) {
        this.messageID = messageID;
        this.messageNumber = messageNumber;
        this.sender = currentSender;
        this.recipient = recipient;
        this.message = message;
        this.messageHash = createMessageHash();
    }

    /** Used when reading a stored message back from the JSON file. */
    private Message(String messageID, int messageNumber, String messageHash,
                    String sender, String recipient, String message) {
        this.messageID = messageID;
        this.messageNumber = messageNumber;
        this.messageHash = messageHash;
        this.sender = sender;
        this.recipient = recipient;
        this.message = message;
        this.flag = "Stored";
    }

    // ======================================================================
    //  Part 2 methods
    // ======================================================================

    /** Ensures the message ID is not more than ten characters. */
    public boolean checkMessageID() {
        return messageID != null && messageID.length() <= 10;
    }

    /** Checks the recipient number has an international code and a valid length. */
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
     * Send, disregard or store the message.
     * 1 = send, 2 = disregard, 3 = store.
     */
    public String sentMessage(int choice) {
        switch (choice) {
            case 1:
                flag = "Sent";
                sentMessages.add(this);
                numMessagesSent++;
                recordIdentifiers();
                return "Message successfully sent.";
            case 2:
                flag = "Disregard";
                disregardedMessages.add(this);
                recordIdentifiers();
                return "Press 0 to delete the message.";
            case 3:
                flag = "Stored";
                storeMessage();
                recordIdentifiers();
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

    /** Adds the message to the stored array and saves all stored messages to JSON. */
    public void storeMessage() {
        if (!storedMessages.contains(this)) {
            storedMessages.add(this);
        }
        writeStoredMessagesToJson();
    }

    // ======================================================================
    //  Part 3 methods
    // ======================================================================

    /** Texts of all sent messages (the "Sent Messages" array). */
    public static List<String> getSentMessageTexts() {
        return textsOf(sentMessages);
    }

    public static List<String> getDisregardedMessageTexts() {
        return textsOf(disregardedMessages);
    }

    public static List<String> getStoredMessageTexts() {
        return textsOf(storedMessages);
    }

    public static List<String> getMessageHashes() {
        return new ArrayList<>(messageHashes);
    }

    public static List<String> getMessageIDs() {
        return new ArrayList<>(messageIDs);
    }

    /** a. Sender and recipient of every stored message. */
    public static String displaySenderAndRecipientOfStored() {
        if (storedMessages.isEmpty()) return "There are no stored messages.";
        StringBuilder sb = new StringBuilder();
        for (Message m : storedMessages) {
            sb.append("Sender: ").append(m.sender)
              .append(" | Recipient: ").append(m.recipient)
              .append(System.lineSeparator());
        }
        return sb.toString();
    }

    /** b. The longest stored message (returns just the message text). */
    public static String longestStoredMessage() {
        String longest = "";
        for (Message m : storedMessages) {
            if (m.message.length() > longest.length()) {
                longest = m.message;
            }
        }
        return longest.isEmpty() ? "There are no stored messages." : longest;
    }

    /** c. Search for a message ID; returns the message text, or a not-found notice. */
    public static String searchByMessageID(String id) {
        Message m = findByMessageID(id);
        return m == null ? "Message ID not found." : m.message;
    }

    /** c. Finds a message by ID in the sent, stored and disregarded arrays. */
    public static Message findByMessageID(String id) {
        for (List<Message> list : allLists()) {
            for (Message m : list) {
                if (m.messageID.equals(id)) return m;
            }
        }
        return null;
    }

    /** d. All the messages sent or stored for a particular recipient. */
    public static List<String> searchByRecipient(String recipient) {
        List<String> results = new ArrayList<>();
        for (Message m : sentMessages) {
            if (m.recipient.equals(recipient)) results.add(m.message);
        }
        for (Message m : storedMessages) {
            if (m.recipient.equals(recipient)) results.add(m.message);
        }
        return results;
    }

    /** e. Deletes a stored message using its message hash. */
    public static String deleteByMessageHash(String hash) {
        for (int i = 0; i < storedMessages.size(); i++) {
            Message m = storedMessages.get(i);
            if (m.messageHash.equalsIgnoreCase(hash)) {
                storedMessages.remove(i);
                messageHashes.remove(m.messageHash);
                messageIDs.remove(m.messageID);
                writeStoredMessagesToJson();
                return "Message \"" + m.message + "\" successfully deleted.";
            }
        }
        return "Message hash not found.";
    }

    /** f. Full details of all the stored messages. */
    public static String storedMessagesReport() {
        return buildReport("STORED MESSAGES REPORT", storedMessages);
    }

    /** Report of all the sent messages (hash, recipient and message). */
    public static String sentMessagesReport() {
        return buildReport("SENT MESSAGES REPORT", sentMessages);
    }

    /** Reads messages.json into the stored-messages array. */
    public static void loadStoredMessages() {
        storedMessages.clear();
        Path path = Paths.get(jsonFile);
        if (!Files.exists(path)) return;

        String json;
        try {
            json = new String(Files.readAllBytes(path), StandardCharsets.UTF_8);
        } catch (IOException e) {
            System.out.println("Could not read " + jsonFile + ": " + e.getMessage());
            return;
        }

        Pattern field = Pattern.compile("\"(\\w+)\"\\s*:\\s*(?:\"((?:\\\\.|[^\"\\\\])*)\"|(\\d+))");
        Matcher matcher = field.matcher(json);

        String id = null, hash = null, sender = null, recipient = null, text = null;
        int number = 0;
        boolean open = false;
        while (matcher.find()) {
            String key = matcher.group(1);
            String value = matcher.group(2) != null ? unescape(matcher.group(2)) : matcher.group(3);
            if (key.equals("messageID")) {
                if (open) addLoaded(id, number, hash, sender, recipient, text);
                id = value; hash = sender = recipient = text = null; number = 0; open = true;
            } else if (key.equals("messageNumber")) {
                number = Integer.parseInt(value);
            } else if (key.equals("messageHash")) {
                hash = value;
            } else if (key.equals("sender")) {
                sender = value;
            } else if (key.equals("recipient")) {
                recipient = value;
            } else if (key.equals("message")) {
                text = value;
            }
        }
        if (open) addLoaded(id, number, hash, sender, recipient, text);
    }

    // ======================================================================
    //  Helpers
    // ======================================================================

    /** Returns the message-length result text used by the length test. */
    public static String checkMessageLength(String text) {
        if (text.length() <= MAX_MESSAGE_LENGTH) {
            return "Message ready to send.";
        }
        int excess = text.length() - MAX_MESSAGE_LENGTH;
        return "Message exceeds 250 characters by " + excess + "; please reduce the size.";
    }

    /**
     * A valid recipient starts with "+" and an international code, followed by
     * the number: up to 3 code digits and up to 10 number digits.
     */
    public static boolean isValidRecipient(String number) {
        return number != null && number.matches("\\+[1-9]\\d{1,12}");
    }

    /** Randomly generated ten-digit number (first digit never 0). */
    public static String generateMessageID() {
        long id = 1_000_000_000L + (long) (RANDOM.nextDouble() * 9_000_000_000L);
        return String.valueOf(id);
    }

    public static void setCurrentSender(String username) {
        currentSender = username;
    }

    public static void setJsonFile(String path) {
        jsonFile = path;
    }

    private void recordIdentifiers() {
        if (!messageHashes.contains(messageHash)) messageHashes.add(messageHash);
        if (!messageIDs.contains(messageID)) messageIDs.add(messageID);
    }

    private static void addLoaded(String id, int number, String hash, String sender,
                                  String recipient, String text) {
        if (id == null || text == null) return;
        Message m = new Message(id, number, hash == null ? "" : hash,
                sender == null ? "Unknown" : sender,
                recipient == null ? "" : recipient, text);
        storedMessages.add(m);
        if (!messageHashes.contains(m.messageHash)) messageHashes.add(m.messageHash);
        if (!messageIDs.contains(m.messageID)) messageIDs.add(m.messageID);
    }

    private static List<List<Message>> allLists() {
        List<List<Message>> lists = new ArrayList<>();
        lists.add(sentMessages);
        lists.add(storedMessages);
        lists.add(disregardedMessages);
        return lists;
    }

    private static List<String> textsOf(List<Message> list) {
        List<String> texts = new ArrayList<>();
        for (Message m : list) texts.add(m.message);
        return texts;
    }

    private static String buildReport(String title, List<Message> list) {
        StringBuilder sb = new StringBuilder();
        sb.append("===== ").append(title).append(" =====").append(System.lineSeparator());
        if (list.isEmpty()) {
            sb.append("No messages to display.").append(System.lineSeparator());
        }
        for (Message m : list) {
            sb.append(m.fullDetails()).append(System.lineSeparator());
        }
        return sb.toString();
    }

    private static String stripPunctuation(String word) {
        return word.replaceAll("[^A-Za-z0-9]", "");
    }

    /** Message ID, Message Hash, Sender, Recipient, Message. */
    public String fullDetails() {
        return "Message ID: " + messageID
                + "\nMessage Hash: " + messageHash
                + "\nSender: " + sender
                + "\nRecipient: " + recipient
                + "\nMessage: " + message
                + "\n";
    }

    /** Writes every message in the stored array to the JSON file. */
    private static void writeStoredMessagesToJson() {
        StringBuilder json = new StringBuilder("[\n");
        for (int i = 0; i < storedMessages.size(); i++) {
            Message m = storedMessages.get(i);
            json.append("  {\n")
                .append("    \"messageID\": \"").append(escape(m.messageID)).append("\",\n")
                .append("    \"messageNumber\": ").append(m.messageNumber).append(",\n")
                .append("    \"messageHash\": \"").append(escape(m.messageHash)).append("\",\n")
                .append("    \"sender\": \"").append(escape(m.sender)).append("\",\n")
                .append("    \"recipient\": \"").append(escape(m.recipient)).append("\",\n")
                .append("    \"message\": \"").append(escape(m.message)).append("\"\n")
                .append("  }").append(i < storedMessages.size() - 1 ? "," : "").append("\n");
        }
        json.append("]\n");
        try {
            Files.write(Paths.get(jsonFile), json.toString().getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            System.out.println("Could not write to " + jsonFile + ": " + e.getMessage());
        }
    }

    private static String escape(String s) {
        return s.replace("\\", "\\\\").replace("\"", "\\\"")
                .replace("\n", "\\n").replace("\r", "\\r").replace("\t", "\\t");
    }

    private static String unescape(String s) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (c == '\\' && i + 1 < s.length()) {
                char n = s.charAt(++i);
                switch (n) {
                    case 'n': sb.append('\n'); break;
                    case 'r': sb.append('\r'); break;
                    case 't': sb.append('\t'); break;
                    default:  sb.append(n);
                }
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    // ---- getters ---------------------------------------------------------
    public String getMessageID() { return messageID; }
    public String getMessageHash() { return messageHash; }
    public String getSender() { return sender; }
    public String getRecipient() { return recipient; }
    public String getMessage() { return message; }
    public String getFlag() { return flag; }

    /** Clears all shared state (used by unit tests). */
    static void resetState() {
        sentMessages.clear();
        disregardedMessages.clear();
        storedMessages.clear();
        messageHashes.clear();
        messageIDs.clear();
        numMessagesSent = 0;
    }
}
