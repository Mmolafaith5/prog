import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class MessageTest {

    // Test data
    private static final String RECIPIENT_1 = "+2783123456";
    private static final String MESSAGE_1 = "Hi Mike, can you join us for dinner tonight";
    private static final String RECIPIENT_2 = "08575975889";
    private static final String MESSAGE_2 = "Hi Keegan, did you receive the payment?";

    @BeforeEach
    void setUp() throws Exception {
        Message.resetState();
        java.nio.file.Path tmp = java.nio.file.Paths.get(
                System.getProperty("java.io.tmpdir"), "quickchat-test-messages.json");
        java.nio.file.Files.deleteIfExists(tmp);
        Message.setJsonFile(tmp.toString());
        Message.setCurrentSender("Faith");
    }

    // ======================================================================
    //  Part 3 – test data (messages 1 to 5)
    // ======================================================================
    // Message 4: the developer enters the message ID 0838884567 (see brief).
    private static final String MSG1 = "Did you get the cake?";
    private static final String MSG2 = "Where are you? You are late! I have asked you to be on time.";
    private static final String MSG3 = "Yohoooo, I am at your gate.";
    private static final String MSG4 = "It is dinner time!";
    private static final String MSG5 = "Ok, I am leaving without you.";

    private Message m1, m2, m3, m4, m5;

    /** Loads the five test messages into the arrays using their flags. */
    private void loadPart3TestData() {
        m1 = new Message("1000000001", 0, "+27834557896", MSG1);
        m2 = new Message("1000000002", 1, "+27838884567", MSG2);
        m3 = new Message("1000000003", 2, "+27834484567", MSG3);
        m4 = new Message("0838884567", 3, "+27831112222", MSG4);
        m5 = new Message("1000000005", 4, "+27838884567", MSG5);

        m1.sentMessage(1);   // Sent
        m2.sentMessage(3);   // Stored
        m3.sentMessage(2);   // Disregard
        m4.sentMessage(1);   // Sent
        m5.sentMessage(3);   // Stored
    }

    @Test
    void sentMessagesArrayCorrectlyPopulated() {
        loadPart3TestData();
        assertEquals(java.util.List.of("Did you get the cake?", "It is dinner time!"),
                Message.getSentMessageTexts());
    }

    @Test
    void disregardedMessagesArrayCorrectlyPopulated() {
        loadPart3TestData();
        assertEquals(java.util.List.of("Yohoooo, I am at your gate."),
                Message.getDisregardedMessageTexts());
    }

    @Test
    void storedMessagesArrayCorrectlyPopulated() {
        loadPart3TestData();
        assertEquals(java.util.List.of(MSG2, MSG5), Message.getStoredMessageTexts());
    }

    @Test
    void messageHashAndIdArraysPopulated() {
        loadPart3TestData();
        assertEquals(5, Message.getMessageHashes().size());
        assertEquals(5, Message.getMessageIDs().size());
        assertTrue(Message.getMessageIDs().contains("0838884567"));
        assertTrue(Message.getMessageHashes().contains("10:0:DIDCAKE"));
    }

    @Test
    void storedMessagesAreReadBackFromJson() {
        loadPart3TestData();
        Message.resetState();                 // wipe the arrays...
        Message.loadStoredMessages();         // ...and read the JSON file back in
        assertEquals(java.util.List.of(MSG2, MSG5), Message.getStoredMessageTexts());
        assertEquals(2, Message.getMessageHashes().size());
    }

    @Test
    void displayLongestMessage() {
        loadPart3TestData();
        assertEquals("Where are you? You are late! I have asked you to be on time.",
                Message.longestStoredMessage());
    }

    @Test
    void searchForMessageId() {
        loadPart3TestData();
        assertEquals("It is dinner time!", Message.searchByMessageID("0838884567"));
    }

    @Test
    void searchMessagesForRecipient() {
        loadPart3TestData();
        assertEquals(java.util.List.of(
                "Where are you? You are late! I have asked you to be on time.",
                "Ok, I am leaving without you."),
                Message.searchByRecipient("+27838884567"));
    }

    @Test
    void deleteMessageUsingMessageHash() {
        loadPart3TestData();
        String result = Message.deleteByMessageHash(m2.getMessageHash());
        assertEquals("Message \"Where are you? You are late! I have asked you to be on time.\" "
                + "successfully deleted.", result);
        assertEquals(java.util.List.of(MSG5), Message.getStoredMessageTexts());
    }

    @Test
    void deleteWithUnknownHashDoesNothing() {
        loadPart3TestData();
        assertEquals("Message hash not found.", Message.deleteByMessageHash("XX:9:NOPE"));
        assertEquals(2, Message.getStoredMessageTexts().size());
    }

    @Test
    void displayReportShowsHashRecipientAndMessage() {
        loadPart3TestData();
        String report = Message.sentMessagesReport();
        assertTrue(report.contains(m1.getMessageHash()));
        assertTrue(report.contains("+27834557896"));
        assertTrue(report.contains(MSG1));
        assertTrue(report.contains(m4.getMessageHash()));
        assertTrue(report.contains(MSG4));
        assertTrue(!report.contains(MSG2));   // stored, not sent
    }

    @Test
    void storedReportShowsFullDetails() {
        loadPart3TestData();
        String report = Message.storedMessagesReport();
        assertTrue(report.contains(m2.getMessageID()));
        assertTrue(report.contains(m2.getMessageHash()));
        assertTrue(report.contains("Faith"));
        assertTrue(report.contains("+27838884567"));
        assertTrue(report.contains(MSG5));
    }

    @Test
    void senderAndRecipientOfStoredMessages() {
        loadPart3TestData();
        String out = Message.displaySenderAndRecipientOfStored();
        assertTrue(out.contains("Sender: Faith | Recipient: +27838884567"));
    }

    // ---- Message length ---------------------------------------------------
    @Test
    void messageLengthSuccess() {
        assertEquals("Message ready to send.", Message.checkMessageLength(MESSAGE_1));
    }

    @Test
    void messageLengthFailure() {
        String longMessage = "a".repeat(260);
        assertEquals("Message exceeds 250 characters by 10; please reduce the size.",
                Message.checkMessageLength(longMessage));
    }

    // ---- Recipient number -------------------------------------------------
    @Test
    void recipientCellSuccess() {
        Message m = new Message("0012345678", 0, RECIPIENT_1, MESSAGE_1);
        assertEquals("Cell phone number successfully captured.", m.checkRecipientCell());
    }

    @Test
    void recipientCellFailure() {
        Message m = new Message("0012345678", 1, RECIPIENT_2, MESSAGE_2);
        assertEquals("Cell phone number is incorrectly formatted or does not contain an "
                + "international code. Please correct the number and try again.",
                m.checkRecipientCell());
    }

    // ---- Message hash -----------------------------------------------------
    @Test
    void messageHashTestCase1() {
        Message m = new Message("0012345678", 0, RECIPIENT_1, MESSAGE_1);
        assertEquals("00:0:HITONIGHT", m.createMessageHash());
    }

    @Test
    void messageHashRemainingMessagesInLoop() {
        String[] messages = {
            MESSAGE_1,
            MESSAGE_2,
            "Hi Mike, can you join us for dinner tonight",
            "Ok, I am leaving without you.",
            "It is dinner time !"
        };
        String[] expected = {
            "00:0:HITONIGHT",
            "00:1:HIPAYMENT",
            "00:2:HITONIGHT",
            "00:3:OKYOU",
            "00:4:ITTIME"
        };
        for (int i = 0; i < messages.length; i++) {
            Message m = new Message("0012345678", i, RECIPIENT_1, messages[i]);
            assertEquals(expected[i], m.createMessageHash());
        }
    }

    @Test
    void messageHashExampleFromBrief() {
        Message m = new Message("0012345678", 0, RECIPIENT_1, "Hi, thanks");
        assertEquals("00:0:HITHANKS", m.createMessageHash());
    }

    // ---- Message ID -------------------------------------------------------
    @Test
    void messageIdIsCreated() {
        Message m = new Message(RECIPIENT_1, MESSAGE_1);
        assertTrue(m.checkMessageID());
        assertEquals(10, m.getMessageID().length());
        System.out.println("Message ID generated: " + m.getMessageID());
        assertNotNull(m.getMessageID());
    }

    // ---- Message sent / disregard / store ---------------------------------
    @Test
    void messageSentOptions() {
        Message m = new Message(RECIPIENT_1, MESSAGE_1);
        assertEquals("Message successfully sent.", m.sentMessage(1));
        assertEquals("Press 0 to delete the message.", m.sentMessage(2));
        assertEquals("Message successfully stored.", m.sentMessage(3));
    }

    @Test
    void totalMessagesIncrementsOnlyWhenSent() {
        new Message(RECIPIENT_1, MESSAGE_1).sentMessage(1);
        new Message(RECIPIENT_2, MESSAGE_2).sentMessage(2);   // Test Data: Select Discard
        assertEquals(1, Message.returnTotalMessages());
    }

    @Test
    void printMessagesContainsSentMessages() {
        new Message(RECIPIENT_1, MESSAGE_1).sentMessage(1);
        String printed = Message.printMessages();
        assertTrue(printed.contains(MESSAGE_1));
        assertTrue(printed.contains(RECIPIENT_1));
    }
}
