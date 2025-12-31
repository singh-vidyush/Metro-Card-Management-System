package org.vidyush;

import org.vidyush.model.SmartCard;
import org.vidyush.model.Station;
import org.vidyush.model.CardTransaction;
import org.vidyush.service.CardService;
import org.vidyush.service.MetroService;
import org.vidyush.exception.MetroException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Scanner;

public class Main {

    private static final Scanner scanner = new Scanner(System.in);

    private static final CardService cardService = new CardService();
    private static final MetroService metroService = new MetroService();

    public static void main(String[] args) {

        System.out.println("=================================");
        System.out.println("   Metro Smart Card System");
        System.out.println("=================================");

        boolean running = true;

        while (running) {
            printMenu();
            int choice = getUserChoice();

            try {
                switch (choice) {
                    case 1 -> createCard();
                    case 2 -> rechargeCard();
                    case 3 -> enterMetro();
                    case 4 -> exitMetro();
                    case 5 -> viewTransactions();
                    case 6 -> {
                        System.out.println("Exiting system. Thank you!");
                        running = false;
                    }
                    default -> System.out.println("Invalid choice. Please try again.");
                }
            } catch (MetroException e) {
                System.out.println("ERROR: " + e.getMessage());
            } catch (IllegalArgumentException e) {
                System.out.println("ERROR: Invalid input. " + e.getMessage());
            }
        }

        scanner.close();
    }

    // ================= MENU =================

    private static void printMenu() {
        System.out.println("\nSelect an option:");
        System.out.println("1. Create Smart Card");
        System.out.println("2. Recharge Card");
        System.out.println("3. Enter Metro");
        System.out.println("4. Exit Metro");
        System.out.println("5. View Transactions");
        System.out.println("6. Exit");
        System.out.print("Enter choice: ");
    }

    private static int getUserChoice() {
        while (!scanner.hasNextInt()) {
            System.out.print("Please enter a valid number: ");
            scanner.next();
        }
        return scanner.nextInt();
    }

    // ================= BUSINESS OPERATIONS =================

    private static void createCard() throws MetroException {
        System.out.print("Enter initial balance: ");
        double balance = scanner.nextDouble();

        SmartCard card = cardService.createCard(balance);
        System.out.println("Card created successfully. Card ID: " + card.getId());
    }

    private static void rechargeCard() throws MetroException {
        System.out.print("Enter card ID: ");
        long cardId = scanner.nextLong();

        System.out.print("Enter recharge amount: ");
        double amount = scanner.nextDouble();

        cardService.rechargeCard(cardId, amount);
        System.out.println("Card recharged successfully.");
    }

    private static void enterMetro() throws MetroException {
        System.out.print("Enter card ID: ");
        long cardId = scanner.nextLong();

        System.out.print("Enter source station (A/B/C/D): ");
        Station source = Station.valueOf(scanner.next().toUpperCase());

        SmartCard card = cardService.getCard(cardId);
        metroService.swipeIn(card, source, LocalDateTime.now());

        System.out.println("Swipe-in successful at station " + source);
    }

    private static void exitMetro() throws MetroException {
        System.out.print("Enter card ID: ");
        long cardId = scanner.nextLong();

        System.out.print("Enter destination station (A1/ A2/ A3/ A4/ A5/ A6/ A7/ A8/ A9/ A10): ");
        Station destination = Station.valueOf(scanner.next().toUpperCase());

        SmartCard card = cardService.getCard(cardId);
        metroService.swipeOut(card, destination, LocalDateTime.now());

        System.out.println("Swipe-out successful at station " + destination);
        System.out.println("Remaining balance: " + card.getBalance());
    }

    private static void viewTransactions() throws MetroException {
        System.out.print("Enter card ID: ");
        long cardId = scanner.nextLong();

        SmartCard card = cardService.getCard(cardId);
        List<CardTransaction> transactions = metroService.cardReport(card);

        if (transactions.isEmpty()) {
            System.out.println("No transactions found.");
            return;
        }

        System.out.println("\nTransaction History:");
        for (CardTransaction trx : transactions) {
            System.out.println(
                    "From " + trx.getSource() +
                    " to " + trx.getDestination() +
                    " | Fare: " + trx.getFare() +
                    " | Balance After: " + trx.getBalance()
            );
        }
    }
}
