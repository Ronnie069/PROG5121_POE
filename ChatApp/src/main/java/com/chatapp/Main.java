package com.chatapp;

import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Login loginObj = null;
        boolean isLoggedIn = false;
        boolean runProgram = true;

        System.out.println("Welcome to QuickChat.");

        // Load any previously stored messages from JSON
        Message.loadStoredMessages();

        while (runProgram) {

            if (!isLoggedIn) {
                System.out.println("\n1. Register");
                System.out.println("2. Login");
                System.out.println("3. Exit");
                System.out.print("Choose an option: ");
                String choice = scanner.nextLine();

                switch (choice) {
                    case "1":
                        System.out.print("Enter First Name: ");
                        String fName = scanner.nextLine();
                        System.out.print("Enter Last Name: ");
                        String lName = scanner.nextLine();
                        System.out.print("Enter Username: ");
                        String user = scanner.nextLine();
                        System.out.print("Enter Password: ");
                        String pass = scanner.nextLine();
                        System.out.print("Enter Phone Number: ");
                        String phone = scanner.nextLine();

                        loginObj = new Login(user, pass, fName, lName, phone);
                        System.out.println("\n" + loginObj.registerUser());
                        break;

                    case "2":
                        if (loginObj == null) {
                            System.out.println("No user registered yet! Please register first.");
                        } else {
                            System.out.print("Enter Username: ");
                            String loginUser = scanner.nextLine();
                            System.out.print("Enter Password: ");
                            String loginPass = scanner.nextLine();

                            isLoggedIn = loginObj.loginUser(loginUser, loginPass);
                            System.out.println(loginObj.returnLoginStatus(isLoggedIn));
                        }
                        break;

                    case "3":
                        System.out.println("Exiting... Goodbye!");
                        runProgram = false;
                        break;

                    default:
                        System.out.println("Invalid option. Please enter 1, 2, or 3.");
                }

            } else {
                System.out.println("\nWelcome to QuickChat.");
                System.out.println("1. Send Messages");
                System.out.println("2. Show recently sent messages");
                System.out.println("3. Stored Messages");
                System.out.println("4. Quit");
                System.out.print("Choose an option: ");
                String menuChoice = scanner.nextLine();

                switch (menuChoice) {
                    case "1":
                        System.out.print("How many messages would you like to send? ");
                        int numMessages;
                        try {
                            numMessages = Integer.parseInt(scanner.nextLine());
                        } catch (NumberFormatException e) {
                            System.out.println("Invalid number. Returning to menu.");
                            break;
                        }

                        for (int i = 0; i < numMessages; i++) {
                            System.out.println("\n--- Message " + (i + 1) + " ---");

                            System.out.print("Enter recipient cell number: ");
                            String recipient = scanner.nextLine();

                            System.out.print("Enter your message: ");
                            String messageText = scanner.nextLine();

                            Message msg = new Message(recipient, messageText, Message.returnTotalMessages() + 1);

                            System.out.println(msg.checkRecipientCell());

                            String lengthCheck = msg.checkMessageLength();
                            System.out.println(lengthCheck);

                            if (!lengthCheck.equals("Message ready to send.")) {
                                System.out.println("Please shorten your message and try again.");
                                i--;
                                continue;
                            }

                            System.out.println("Message ID  : " + msg.getMessageID());
                            System.out.println("Message Hash: " + msg.createMessageHash());

                            System.out.println("\nWhat would you like to do?");
                            System.out.println("1. Send Message");
                            System.out.println("2. Disregard Message");
                            System.out.println("3. Store Message to send later");
                            System.out.print("Choose: ");
                            int sendChoice;
                            try {
                                sendChoice = Integer.parseInt(scanner.nextLine());
                            } catch (NumberFormatException e) {
                                sendChoice = 2;
                            }

                            System.out.println(msg.sentMessage(sendChoice));
                        }

                        System.out.println(Message.printMessages());
                        System.out.println("Total messages sent: " + Message.returnTotalMessages());
                        break;

                    case "2":
                        System.out.println("Coming Soon.");
                        break;

                    case "3":
                        // ---- STORED MESSAGES MENU ----
                        boolean inStoredMenu = true;
                        while (inStoredMenu) {
                            System.out.println("\n--- Stored Messages Menu ---");
                            System.out.println("a. Display all stored messages");
                            System.out.println("b. Display longest message");
                            System.out.println("c. Search by Message ID");
                            System.out.println("d. Search by recipient");
                            System.out.println("e. Delete message by hash");
                            System.out.println("f. Display full report");
                            System.out.println("q. Back to main menu");
                            System.out.print("Choose: ");
                            String subChoice = scanner.nextLine().toLowerCase();

                            switch (subChoice) {
                                case "a":
                                    System.out.println(Message.displayStoredMessages());
                                    break;
                                case "b":
                                    System.out.println("Longest message: " + Message.getLongestMessage());
                                    break;
                                case "c":
                                    System.out.print("Enter Message ID to search: ");
                                    String searchID = scanner.nextLine();
                                    System.out.println(Message.searchByMessageID(searchID));
                                    break;
                                case "d":
                                    System.out.print("Enter recipient number to search: ");
                                    String searchRecipient = scanner.nextLine();
                                    System.out.println(Message.searchByRecipient(searchRecipient));
                                    break;
                                case "e":
                                    System.out.print("Enter message hash to delete: ");
                                    String hash = scanner.nextLine();
                                    System.out.println(Message.deleteMessageByHash(hash));
                                    break;
                                case "f":
                                    System.out.println(Message.displayReport());
                                    break;
                                case "q":
                                    inStoredMenu = false;
                                    break;
                                default:
                                    System.out.println("Invalid option.");
                            }
                        }
                        break;

                    case "4":
                        System.out.println("Exiting QuickChat. Goodbye!");
                        runProgram = false;
                        break;

                    default:
                        System.out.println("Invalid option. Please enter 1, 2, 3, or 4.");
                }
            }
        }
        scanner.close();
    }
}