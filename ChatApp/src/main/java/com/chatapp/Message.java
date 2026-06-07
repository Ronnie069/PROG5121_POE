package com.chatapp;

import java.util.ArrayList;
import java.util.Random;
import java.util.regex.Pattern;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import java.io.FileWriter;
import java.io.FileReader;
import java.io.IOException;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;


 // Message class handling sending, storing and displaying messages

public class Message {

    private final String messageID;
    private final String recipient;
    private final String message;
    private final int messageNumber;
    private String flag; // "Sent", "Stored", "Disregarded"

    // Arrays to store all messages by category
    private static final ArrayList<Message> sentMessages = new ArrayList<>();
    private static final ArrayList<Message> disregardedMessages = new ArrayList<>();
    private static final ArrayList<Message> storedMessages = new ArrayList<>();
    private static final ArrayList<String> messageHashes = new ArrayList<>();
    private static final ArrayList<String> messageIDs = new ArrayList<>();

    private static int totalMessagesSent = 0;


     // Constructor for Message

    public Message(String recipient, String message, int messageNumber) {
        this.messageID = generateMessageID();
        this.recipient = recipient;
        this.message = message;
        this.messageNumber = messageNumber;
        this.flag = "";
    }


     // to Generates a random 10-digit message ID.

    private String generateMessageID() {
        Random rand = new Random();
        long id = (long) (rand.nextDouble() * 9_000_000_000L) + 1_000_000_000L;
        return String.valueOf(id);
    }


     // Checks if the message ID is no more than 10 characters.

    public boolean checkMessageID() {
        return messageID != null && messageID.length() <= 10;
    }


     // Checks if the recipient cell number is correctly formatted.

    public String checkRecipientCell() {
        String regex = "^\\+[0-9]{9,12}$";
        if (recipient != null && Pattern.compile(regex).matcher(recipient).matches()) {
            return "Cell phone number successfully captured.";
        } else {
            return "Cell phone number is incorrectly formatted or does not contain an international code. Please correct the number and try again.";
        }
    }


     // Validates the message length (max 250 characters).

    public String checkMessageLength() {
        if (message.length() <= 250) {
            return "Message ready to send.";
        } else {
            int over = message.length() - 250;
            return "Message exceeds 250 characters by " + over + "; please reduce the size.";
        }
    }

    /**
     * Creates and returns the Message Hash.
     * Format: first 2 digits of ID : message number : FIRSTWORD+LASTWORD in caps
     */
    public String createMessageHash() {
        String idPrefix = messageID.substring(0, 2);
        String[] words = message.trim().split("\\s+");
        String firstWord = words[0].toUpperCase().replaceAll("[^A-Z]", "");
        String lastWord = words[words.length - 1].toUpperCase().replaceAll("[^A-Z]", "");
        return idPrefix + ":" + messageNumber + ":" + firstWord + lastWord;
    }

    /**
     * Handles sending, storing, or disregarding a message.
     * 1 = Send, 2 = Disregard, 3 = Store
     */
    public String sentMessage(int choice) {
        String hash = createMessageHash();
        switch (choice) {
            case 1:
                this.flag = "Sent";
                sentMessages.add(this);
                messageHashes.add(hash);
                messageIDs.add(messageID);
                totalMessagesSent++;
                return "Message successfully sent.";
            case 2:
                this.flag = "Disregarded";
                disregardedMessages.add(this);
                return "Press 0 to delete the message.";
            case 3:
                this.flag = "Stored";
                storedMessages.add(this);
                messageHashes.add(hash);
                messageIDs.add(messageID);
                storeMessage();
                return "Message successfully stored.";
            default:
                return "Invalid option.";
        }
    }


      //Returns all sent messages as a formatted string.

    public static String printMessages() {
        if (sentMessages.isEmpty()) {
            return "No messages sent yet.";
        }
        StringBuilder sb = new StringBuilder();
        sb.append("\n--- Sent Messages ---\n");
        for (Message m : sentMessages) {
            sb.append("Message ID   : ").append(m.messageID).append("\n");
            sb.append("Message Hash : ").append(m.createMessageHash()).append("\n");
            sb.append("Recipient    : ").append(m.recipient).append("\n");
            sb.append("Message      : ").append(m.message).append("\n");
            sb.append("---------------------\n");
        }
        return sb.toString();
    }


     // Returns total number of messages sent.

    public static int returnTotalMessages() {
        return totalMessagesSent;
    }


     //Stores the message to a JSON file.

    @SuppressWarnings("unchecked")
    public void storeMessage() {
        JSONObject obj = new JSONObject();
        obj.put("messageID", messageID);
        obj.put("messageNumber", messageNumber);
        obj.put("recipient", recipient);
        obj.put("message", message);
        obj.put("messageHash", createMessageHash());
        obj.put("flag", flag);

        JSONArray list = new JSONArray();
        list.add(obj);

        try (FileWriter file = new FileWriter("messages.json", true)) {
            file.write(list.toJSONString());
            file.write(System.lineSeparator());
        } catch (IOException e) {
            System.out.println("Error storing message: " + e.getMessage());
        }
    }


     // Loads stored messages from JSON file into storedMessages array.

    @SuppressWarnings("unchecked")
    public static void loadStoredMessages() {
        JSONParser parser = new JSONParser();
        try (FileReader reader = new FileReader("messages.json")) {
            // Read line by line since we append JSON arrays
            java.io.BufferedReader br = new java.io.BufferedReader(reader);
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                JSONArray arr = (JSONArray) parser.parse(line);
                for (Object o : arr) {
                    JSONObject obj = (JSONObject) o;
                    String recipient = (String) obj.get("recipient");
                    String message = (String) obj.get("message");
                    int msgNum = ((Long) obj.get("messageNumber")).intValue();
                    Message m = new Message(recipient, message, msgNum);
                    m.flag = "Stored";
                    if (!storedMessages.contains(m)) {
                        storedMessages.add(m);
                    }
                }
            }
        } catch (IOException | ParseException e) {
            System.out.println("No stored messages found or error reading file.");
        }
    }

    // ===================== PART 3 METHODS =====================


     // a. Display sender and recipient of all stored messages.

    public static String displayStoredMessages() {
        if (storedMessages.isEmpty()) {
            return "No stored messages found.";
        }
        StringBuilder sb = new StringBuilder();
        sb.append("\n--- Stored Messages ---\n");
        for (Message m : storedMessages) {
            sb.append("Recipient : ").append(m.recipient).append("\n");
            sb.append("Message   : ").append(m.message).append("\n");
            sb.append("-----------------------\n");
        }
        return sb.toString();
    }


     //b. Display the longest stored message.

    public static String getLongestMessage() {
        ArrayList<Message> all = new ArrayList<>();
        all.addAll(sentMessages);
        all.addAll(storedMessages);
        if (all.isEmpty()) {
            return "No messages found.";
        }
        Message longest = all.get(0);
        for (Message m : all) {
            if (m.message.length() > longest.message.length()) {
                longest = m;
            }
        }
        return longest.message;
    }


     // c. Search for a message ID and display the corresponding recipient and message.

    public static String searchByMessageID(String searchID) {
        ArrayList<Message> all = new ArrayList<>();
        all.addAll(sentMessages);
        all.addAll(storedMessages);
        for (Message m : all) {
            if (m.messageID.equals(searchID)) {
                return "Recipient : " + m.recipient + "\nMessage   : " + m.message;
            }
        }
        return "Message ID not found.";
    }


     // d. Search all messages stored or sent for a particular recipient.

    public static String searchByRecipient(String searchRecipient) {
        ArrayList<Message> all = new ArrayList<>();
        all.addAll(sentMessages);
        all.addAll(storedMessages);
        StringBuilder sb = new StringBuilder();
        for (Message m : all) {
            if (m.recipient.equals(searchRecipient)) {
                sb.append("Message : ").append(m.message).append("\n");
            }
        }
        if (sb.length() == 0) {
            return "No messages found for recipient: " + searchRecipient;
        }
        return sb.toString();
    }

    /**
     * e. Delete a message using the message hash.
     */
    public static String deleteMessageByHash(String hash) {
        // Check sent messages
        for (int i = 0; i < sentMessages.size(); i++) {
            if (sentMessages.get(i).createMessageHash().equals(hash)) {
                String msg = sentMessages.get(i).message;
                sentMessages.remove(i);
                messageHashes.remove(hash);
                return "Message: \"" + msg + "\" successfully deleted.";
            }
        }
        // Check stored messages
        for (int i = 0; i < storedMessages.size(); i++) {
            if (storedMessages.get(i).createMessageHash().equals(hash)) {
                String msg = storedMessages.get(i).message;
                storedMessages.remove(i);
                messageHashes.remove(hash);
                return "Message: \"" + msg + "\" successfully deleted.";
            }
        }
        return "Message hash not found.";
    }


     // f. Display a full report of all stored messages including hash, recipient, message.

    public static String displayReport() {
        ArrayList<Message> all = new ArrayList<>();
        all.addAll(sentMessages);
        all.addAll(storedMessages);
        if (all.isEmpty()) {
            return "No messages to display.";
        }
        StringBuilder sb = new StringBuilder();
        sb.append("\n========== FULL REPORT ==========\n");
        for (Message m : all) {
            sb.append("Message Hash : ").append(m.createMessageHash()).append("\n");
            sb.append("Recipient    : ").append(m.recipient).append("\n");
            sb.append("Message      : ").append(m.message).append("\n");
            sb.append("Flag         : ").append(m.flag).append("\n");
            sb.append("---------------------------------\n");
        }
        return sb.toString();
    }

    // Getters
    public String getMessageID() { return messageID; }
    public String getRecipient() { return recipient; }
    public String getMessage() { return message; }
    public int getMessageNumber() { return messageNumber; }
    public String getFlag() { return flag; }
    public static ArrayList<Message> getSentMessages() { return sentMessages; }
    public static ArrayList<Message> getStoredMessages() { return storedMessages; }
    public static ArrayList<Message> getDisregardedMessages() { return disregardedMessages; }
    public static ArrayList<String> getMessageHashes() { return messageHashes; }
    public static ArrayList<String> getMessageIDs() { return messageIDs; }
}