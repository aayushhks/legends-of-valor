package game;

import common.InputValidator;
import common.RandomGenerator;
import entities.Hero;
import entities.Party;
import items.*;
import items.Spell.SpellType;
import utils.GameDataLoader;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

/**
 * Controller responsible for managing Market interactions.
 * Handles the logic for buying and selling items between Heroes and the Shop.
 */
public class MarketController {

    private final List<Item> globalItemCatalog;

    // ANSI Colors
    private static final String ANSI_RESET = "\u001B[0m";
    private static final String ANSI_CYAN = "\u001B[36m";
    private static final String ANSI_YELLOW = "\u001B[33m";
    private static final String ANSI_GREEN = "\u001B[32m";
    private static final String ANSI_RED = "\u001B[31m";
    private static final String ANSI_WHITE_BOLD = "\033[1;37m";

    public MarketController() {
        this.globalItemCatalog = new ArrayList<>();
        initializeCatalog();
    }

    /**
     * Loads all possible items into a master catalog.
     * In a larger app, this might be injected rather than loaded here.
     */
    private void initializeCatalog() {
        globalItemCatalog.addAll(GameDataLoader.loadWeapons("Weaponry.txt"));
        globalItemCatalog.addAll(GameDataLoader.loadArmor("Armory.txt"));
        globalItemCatalog.addAll(GameDataLoader.loadPotions("Potions.txt"));
        globalItemCatalog.addAll(GameDataLoader.loadSpells("FireSpells.txt", SpellType.FIRE));
        globalItemCatalog.addAll(GameDataLoader.loadSpells("IceSpells.txt", SpellType.ICE));
        globalItemCatalog.addAll(GameDataLoader.loadSpells("LightningSpells.txt", SpellType.LIGHTNING));

        if (globalItemCatalog.isEmpty()) {
            System.err.println("Warning: Market initialized with no items. Check data files.");
        }
    }

    /**
     * Starts the market interaction loop.
     * Generates a random subset of items for this specific market visit.
     */
    public void enterMarket(Scanner scanner, Party party) {
        // Generate a unique inventory for this market session (e.g., 5-10 random items)
        List<Item> marketInventory = generateMarketInventory();

        boolean inMarket = true;
        while (inMarket) {
            System.out.println("\n" + ANSI_YELLOW + "--- Market Menu ---" + ANSI_RESET);
            System.out.println("1. Buy Items");
            System.out.println("2. Sell Items");
            System.out.println("3. Exit Market");

            int choice = InputValidator.getValidInt(scanner, "Choose action: ", 1, 3);

            switch (choice) {
                case 1: buyLoop(scanner, party, marketInventory); break;
                case 2: sellLoop(scanner, party); break;
                case 3: inMarket = false; break;
            }
        }
        System.out.println(ANSI_GREEN + "You leave the market." + ANSI_RESET);
    }

    private List<Item> generateMarketInventory() {
        List<Item> inventory = new ArrayList<>();
        if (globalItemCatalog.isEmpty()) return inventory;

        // Create a shuffled copy of the catalog to pick unique random items
        List<Item> shuffledCatalog = new ArrayList<>(globalItemCatalog);
        Collections.shuffle(shuffledCatalog);

        // Select the first N items (e.g., 10)
        int stockSize = Math.min(10, shuffledCatalog.size());
        for (int i = 0; i < stockSize; i++) {
            inventory.add(shuffledCatalog.get(i));
        }

        return inventory;
    }

    // BUYING LOGIC
    private void buyLoop(Scanner scanner, Party party, List<Item> marketInventory) {
        Hero shopper = selectHero(scanner, party, "Who is buying?");
        if (shopper == null) return;

        while (true) {
            System.out.println("\n" + ANSI_WHITE_BOLD + "--- Items for Sale (Shopper: " + shopper.getName() + " | Gold: " + shopper.getMoney() + ") ---" + ANSI_RESET);
            printItemTable(marketInventory);
            System.out.println((marketInventory.size() + 1) + ". Back");

            int choice = InputValidator.getValidInt(scanner, "Select item to buy: ", 1, marketInventory.size() + 1);
            if (choice == marketInventory.size() + 1) break;

            Item item = marketInventory.get(choice - 1);
            processPurchase(shopper, item);
        }
    }

    private void processPurchase(Hero hero, Item item) {
        // Rule: Hero cannot buy item if level is too low
        if (hero.getLevel() < item.getMinLevel()) {
            System.out.println(ANSI_RED + "Cannot buy! Required Level: " + item.getMinLevel() + ANSI_RESET);
            return;
        }

        // Rule: Hero cannot buy if insufficient gold
        if (hero.getMoney() < item.getPrice()) {
            System.out.println(ANSI_RED + "Insufficient Gold! Cost: " + item.getPrice() + ANSI_RESET);
            return;
        }

        // Transaction
        hero.deductMoney(item.getPrice());
        hero.getInventory().addItem(item);
        System.out.println(ANSI_GREEN + "Purchase successful! " + item.getName() + " added to inventory." + ANSI_RESET);
    }

    // SELLING LOGIC
    private void sellLoop(Scanner scanner, Party party) {
        Hero seller = selectHero(scanner, party, "Who is selling?");
        if (seller == null) return;

        while (true) {
            List<Item> sellableItems = seller.getInventory().getItems();
            if (sellableItems.isEmpty()) {
                System.out.println(ANSI_YELLOW + seller.getName() + " has nothing to sell." + ANSI_RESET);
                break;
            }

            System.out.println("\n" + ANSI_WHITE_BOLD + "--- Your Inventory (Seller: " + seller.getName() + ") ---" + ANSI_RESET);
            // Show items with their resale value (50% of price)
            printSellableItemTable(sellableItems);
            System.out.println((sellableItems.size() + 1) + ". Back");

            int choice = InputValidator.getValidInt(scanner, "Select item to sell: ", 1, sellableItems.size() + 1);
            if (choice == sellableItems.size() + 1) break;

            Item itemToSell = sellableItems.get(choice - 1);
            processSale(seller, itemToSell);
        }
    }

    private void processSale(Hero hero, Item item) {
        double resaleValue = item.getPrice() * 0.5;

        hero.getInventory().removeItem(item);
        hero.addMoney(resaleValue);

        System.out.println(ANSI_GREEN + "Sold " + item.getName() + " for " + resaleValue + " gold." + ANSI_RESET);
    }

    // HELPERS
    private Hero selectHero(Scanner scanner, Party party, String prompt) {
        System.out.println(ANSI_CYAN + prompt + ANSI_RESET);
        for (int i = 0; i < party.getSize(); i++) {
            System.out.println((i + 1) + ". " + party.getHero(i).getName());
        }
        System.out.println((party.getSize() + 1) + ". Cancel");

        int choice = InputValidator.getValidInt(scanner, "Select Hero: ", 1, party.getSize() + 1);
        if (choice == party.getSize() + 1) return null;

        return party.getHero(choice - 1);
    }

    // PRETTY TABLE PRINTING
    private void printItemTable(List<Item> items) {
        System.out.println(ANSI_CYAN + "+----+----------------------+-----+----------+--------------------------------+" + ANSI_RESET);
        System.out.printf(ANSI_CYAN + "|" + ANSI_RESET + " %-2s " + ANSI_CYAN + "|" + ANSI_RESET + " %-20s " + ANSI_CYAN + "|" + ANSI_RESET + " %-3s " + ANSI_CYAN + "|" + ANSI_RESET + " %-8s " + ANSI_CYAN + "|" + ANSI_RESET + " %-30s " + ANSI_CYAN + "|\n" + ANSI_RESET, "ID", "NAME", "LVL", "COST", "TYPE / STATS");
        System.out.println(ANSI_CYAN + "+----+----------------------+-----+----------+--------------------------------+" + ANSI_RESET);

        for (int i = 0; i < items.size(); i++) {
            Item item = items.get(i);
            String stats = extractStats(item); // Helper to get simplified stats
            System.out.printf(ANSI_CYAN + "|" + ANSI_RESET + " %-2d " + ANSI_CYAN + "|" + ANSI_RESET + " %-20s " + ANSI_CYAN + "|" + ANSI_RESET + " %-3d " + ANSI_CYAN + "|" + ANSI_RESET + " " + ANSI_YELLOW + "%-8.0f" + ANSI_RESET + " " + ANSI_CYAN + "|" + ANSI_RESET + " %-30s " + ANSI_CYAN + "|\n" + ANSI_RESET,
                    (i + 1), item.getName(), item.getMinLevel(), item.getPrice(), stats);
        }
        System.out.println(ANSI_CYAN + "+----+----------------------+-----+----------+--------------------------------+" + ANSI_RESET);
    }

    private void printSellableItemTable(List<Item> items) {
        System.out.println(ANSI_CYAN + "+----+----------------------+-----+----------+--------------------------------+" + ANSI_RESET);
        System.out.printf(ANSI_CYAN + "|" + ANSI_RESET + " %-2s " + ANSI_CYAN + "|" + ANSI_RESET + " %-20s " + ANSI_CYAN + "|" + ANSI_RESET + " %-3s " + ANSI_CYAN + "|" + ANSI_RESET + " %-8s " + ANSI_CYAN + "|" + ANSI_RESET + " %-30s " + ANSI_CYAN + "|\n" + ANSI_RESET, "ID", "NAME", "LVL", "SELL", "TYPE / STATS");
        System.out.println(ANSI_CYAN + "+----+----------------------+-----+----------+--------------------------------+" + ANSI_RESET);

        for (int i = 0; i < items.size(); i++) {
            Item item = items.get(i);
            String stats = extractStats(item);
            double sellPrice = item.getPrice() * 0.5;
            System.out.printf(ANSI_CYAN + "|" + ANSI_RESET + " %-2d " + ANSI_CYAN + "|" + ANSI_RESET + " %-20s " + ANSI_CYAN + "|" + ANSI_RESET + " %-3d " + ANSI_CYAN + "|" + ANSI_RESET + " " + ANSI_YELLOW + "%-8.0f" + ANSI_RESET + " " + ANSI_CYAN + "|" + ANSI_RESET + " %-30s " + ANSI_CYAN + "|\n" + ANSI_RESET,
                    (i + 1), item.getName(), item.getMinLevel(), sellPrice, stats);
        }
        System.out.println(ANSI_CYAN + "+----+----------------------+-----+----------+--------------------------------+" + ANSI_RESET);
    }

    // Helper to format item details concisely for the table
    private String extractStats(Item item) {
        if (item instanceof Weapon) {
            Weapon w = (Weapon) item;
            return String.format("Weapon (Dmg: %.0f)", w.getDamage());
        } else if (item instanceof Armor) {
            Armor a = (Armor) item;
            return String.format("Armor (Def: %.0f)", a.getDamageReduction());
        } else if (item instanceof Spell) {
            Spell s = (Spell) item;
            return String.format("Spell (%s, Dmg: %.0f)", s.getType(), s.getDamage());
        } else if (item instanceof Potion) {
            Potion p = (Potion) item;
            return String.format("Potion (+%.0f)", p.getAttributeIncrease());
        }
        return "Item";
    }
}