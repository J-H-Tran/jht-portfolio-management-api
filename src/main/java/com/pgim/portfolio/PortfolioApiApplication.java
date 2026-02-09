package com.pgim.portfolio;

import com.pgim.portfolio.dto.TradeDTO;
import com.pgim.portfolio.service.TradeService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import java.util.Scanner;

@SpringBootApplication(scanBasePackages = "com.pgim.portfolio")
@EnableJpaRepositories(basePackages = "com.pgim.portfolio.repository")
public class PortfolioApiApplication /*implements CommandLineRunner*/ {
    private final TradeService tradeService;

    public PortfolioApiApplication(TradeService tradeService) {
        this.tradeService = tradeService;
    }

    public static void main(String[] args) {
        SpringApplication.run(PortfolioApiApplication.class, args);
    }

//    @Override
//    public void run(String... args) {
//        Scanner sc = new Scanner(System.in);
//        System.out.println("Welcome to the Trade CLI.");
//        while (true) {
//            System.out.println("\nChoose an operation:");
//            System.out.println("1. List Trades by Portfolio ID");
//            System.out.println("2. Add a Trade");
//            System.out.println("3. Update a Trade");
//            System.out.println("4. Delete a Trade");
//            System.out.println("5. Exit");
//
//            int choice = sc.nextInt();
//            sc.nextLine(); // Consume newline
//            try {
//                switch (choice) {
//                    case 1 -> listTrades(sc);
//                    case 2 -> addTrade(sc);
//                    case 3 -> updateTrade(sc);
//                    case 4 -> deleteTrade(sc);
//                    case 5 -> {
//                        System.out.println("Exiting CLI. Goodbye...");
//                        return;
//                    }
//                    default -> System.out.println("Invalid choice. Please try again.");
//                }
//            } catch (Exception e) {
//                System.out.println("Error: " + e.getMessage());
//            }
//        }
//    }

    private void listTrades(Scanner sc) {
        System.out.println("Enter Portfolio ID: ");
        Long pID = sc.nextLong();
        System.out.println("Enter page number: ");
        int page = sc.nextInt();
        System.out.println("Enter page size: ");
        int size = sc.nextInt();

        Page<TradeDTO> trades = tradeService.getTradesByPortfolioId(pID, PageRequest.of(page, size));
        trades.forEach(System.out::println);
    }

    private void addTrade(Scanner sc) {
        System.out.println("Enter Portfolio ID: ");
        Long pID = sc.nextLong();
        sc.nextLine(); // consume newline
        System.out.println("Enter Trade Details: ");
        String details = sc.nextLine();

        TradeDTO tradeDTO = new TradeDTO();
        tradeDTO.setPortfolioId(pID);
        tradeDTO.setDetails(details);

        TradeDTO savedTrade = tradeService.addTrade(tradeDTO);
        System.out.println("Trade added: " + savedTrade);
    }

    private void updateTrade(Scanner sc) {
        System.out.println("Enter Trade ID: ");
        Long tradeId = sc.nextLong();
        sc.nextLine();
        System.out.println("Enter Updated Trade Details: ");
        String updatedDetails = sc.nextLine();

        TradeDTO updateTradeDTO = new TradeDTO();
        updateTradeDTO.setDetails(updatedDetails);

        TradeDTO updatedTrade = tradeService.updateTrade(tradeId, updateTradeDTO);
        System.out.println("Trade updated: " + updatedTrade);
    }

    private void deleteTrade(Scanner sc) {
        System.out.println("Enter Trade ID: ");
        Long tradeId = sc.nextLong();

        tradeService.deleteTrade(tradeId);
        System.out.println("Trade deleted successfully.");
    }
}