package com.chatapp;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class MessageTest {

    // Test data from Part 2 spec
    Message msg1 = new Message("+27718693002", "Hi Mike, can you join us for dinner tonight?", 1);
    Message msg2 = new Message("0857597889", "Hi Keegan, did you receive the payment?", 2);

    // Part 3 test data
    Message testMsg1 = new Message("+27834557896", "Did you get the cake?", 1);
    Message testMsg2 = new Message("+27838884567", "Where are you? You are late! I have asked you to be on time.", 2);
    Message testMsg3 = new Message("+27838484567", "Yohooo, I am at your gate.", 3);
    Message testMsg4 = new Message("0838884567", "It is dinner time !", 4);
    Message testMsg5 = new Message("+27838884567", "Ok, I am leaving without you.", 5);

    @BeforeEach
    public void setUp() {
        // Clear all arrays before each test
        Message.getSentMessages().clear();
        Message.getStoredMessages().clear();
        Message.getDisregardedMessages().clear();
        Message.getMessageHashes().clear();
        Message.getMessageIDs().clear();

        // Populate arrays with test data
        testMsg1.sentMessage(1); // Sent
        testMsg2.sentMessage(3); // Stored
        testMsg3.sentMessage(2); // Disregarded
        testMsg4.sentMessage(1); // Sent
        testMsg5.sentMessage(3); // Stored
    }

    // --- Part 2 tests ---

    @Test
    public void testMessageLengthValid() {
        assertEquals("Message ready to send.", msg1.checkMessageLength());
    }

    @Test
    public void testMessageLengthTooLong() {
        Message longMsg = new Message("+27718693002", "A".repeat(260), 1);
        String result = longMsg.checkMessageLength();
        assertTrue(result.startsWith("Message exceeds 250 characters by"));
    }

    @Test
    public void testRecipientCellValid() {
        assertEquals("Cell phone number successfully captured.", msg1.checkRecipientCell());
    }

    @Test
    public void testRecipientCellInvalid() {
        assertTrue(msg2.checkRecipientCell().contains("incorrectly formatted"));
    }

    @Test
    public void testMessageHashCorrect() {
        String hash = msg1.createMessageHash();
        assertTrue(hash.endsWith(":HITONIGHT"), "Hash should end with :HITONIGHT but was: " + hash);
    }

    @Test
    public void testMessageIDGenerated() {
        assertTrue(msg1.checkMessageID());
        System.out.println("Message ID generated: " + msg1.getMessageID());
    }

    @Test
    public void testSentMessageSend() {
        Message m = new Message("+27718693002", "Hi Mike, can you join us for dinner tonight?", 1);
        assertEquals("Message successfully sent.", m.sentMessage(1));
    }

    @Test
    public void testSentMessageDisregard() {
        Message m = new Message("+27718693002", "Hi Mike, can you join us for dinner tonight?", 1);
        assertEquals("Press 0 to delete the message.", m.sentMessage(2));
    }

    @Test
    public void testSentMessageStore() {
        Message m = new Message("+27718693002", "Hi Mike, can you join us for dinner tonight?", 1);
        assertEquals("Message successfully stored.", m.sentMessage(3));
    }

    // --- Part 3 tests ---

    @Test
    public void testSentMessagesArrayPopulated() {
        // Sent messages array should contain msg1 and msg4
        assertEquals(2, Message.getSentMessages().size());
        assertEquals("Did you get the cake?", Message.getSentMessages().get(0).getMessage());
        assertEquals("It is dinner time !", Message.getSentMessages().get(1).getMessage());
    }

    @Test
    public void testGetLongestMessage() {
        // Longest message should be testMsg2
        String longest = Message.getLongestMessage();
        assertEquals("Where are you? You are late! I have asked you to be on time.", longest);
    }

    @Test
    public void testSearchByMessageID() {
        // Search using testMsg4's ID
        String result = Message.searchByMessageID(testMsg4.getMessageID());
        assertTrue(result.contains("It is dinner time !"));
    }

    @Test
    public void testSearchByRecipient() {
        // +27838884567 has testMsg2 and testMsg5
        String result = Message.searchByRecipient("+27838884567");
        assertTrue(result.contains("Where are you? You are late! I have asked you to be on time."));
        assertTrue(result.contains("Ok, I am leaving without you."));
    }

    @Test
    public void testDeleteMessageByHash() {
        // Delete testMsg2 using its hash
        String hash = testMsg2.createMessageHash();
        String result = Message.deleteMessageByHash(hash);
        assertTrue(result.contains("successfully deleted"));
    }

    @Test
    public void testDisplayReport() {
        // Report should contain sent messages with hash, recipient, message
        String report = Message.displayReport();
        assertTrue(report.contains("Message Hash"));
        assertTrue(report.contains("Recipient"));
        assertTrue(report.contains("Message"));
    }
}