package game;

import utils.ConsoleColors;
import common.InputValidator;
import common.RandomGenerator;
import entities.Hero;
import entities.Party;
import items.*;
import items.Spell.SpellType;
import utils.GameDataLoader;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Controller responsible for managing Market interactions.
 * Handles the logic for buying and selling items between Heroes and the Shop.
 */
public class MarketController {

    private final List<Item> globalItemCatalog;
    private Map<String, List<Item>> positionBasedInventories; // Cache inventories by position
    private int currentPage = 0;
    private final int itemsPerPage = 8;

    public MarketController() {
        this.positionBasedInventories = new HashMap<>();
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
        // Find highest level hero in party
        int highestLevel = party.getHeroes().stream()
                .mapToInt(Hero::getLevel)
                .max().orElse(1);
        
        // Generate inventory based on highest level hero
        List<Item> marketInventory = generateMarketInventoryForLevel(highestLevel);

        boolean inMarket = true;
        while (inMarket) {
            System.out.println("\n" + ConsoleColors.YELLOW + "--- Market Menu ---" + ConsoleColors.RESET);
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
        System.out.println(ConsoleColors.GREEN + "You leave the market." + ConsoleColors.RESET);
    }

    /**
     * Position-based market for Legends game - inventory scales with highest level hero.
     */
    public void enterMarketAtPosition(Scanner scanner, Party party, int row, int col) {
        // Find highest level hero in party
        int highestLevel = party.getHeroes().stream()
                .mapToInt(Hero::getLevel)
                .max().orElse(1);
        
        // Include highest level in cache key so markets update when party levels up
        String positionKey = row + "," + col + "," + highestLevel;
        Hero hero = party.getHeroes().get(0); // Assuming single hero for Legends
        
        // Get or generate inventory for this position and level
        List<Item> marketInventory = positionBasedInventories.computeIfAbsent(
            positionKey, k -> generateMarketInventoryForPosition(highestLevel, row, col)
        );
        
        this.currentPage = 0; // Reset to first page
        enterMarketWithPagination(scanner, hero, marketInventory);
    }

    /**
     * Overloaded method for single hero market access (for Legends of Valor).
     * No hero selection needed - directly uses the provided hero.
     */
    public void enterMarket(Scanner scanner, Hero hero) {
        List<Item> marketInventory = generateMarketInventoryForLevel(hero.getLevel());
        enterMarketWithPagination(scanner, hero, marketInventory);
    }
    
    private void enterMarketWithPagination(Scanner scanner, Hero hero, List<Item> marketInventory) {
        boolean inMarket = true;
        while (inMarket) {
            System.out.println("\n" + ConsoleColors.YELLOW + "--- Market Menu ---" + ConsoleColors.RESET);
            System.out.println(ConsoleColors.CYAN + "Hero: " + hero.getName() + " | Level: " + hero.getLevel() + " | Gold: " + ConsoleColors.YELLOW + hero.getMoney() + ConsoleColors.RESET);
            System.out.println("1. Buy Items");
            System.out.println("2. Sell Items");
            System.out.println("3. Exit Market");

            int choice = InputValidator.getValidInt(scanner, "Choose action: ", 1, 3);

            switch (choice) {
                case 1: buyLoopWithPagination(scanner, hero, marketInventory); break;
                case 2: sellLoopSingleHero(scanner, hero); break;
                case 3: inMarket = false; break;
            }
        }
        System.out.println(ConsoleColors.GREEN + hero.getName() + " leaves the market." + ConsoleColors.RESET);
    }

    private List<Item> generateMarketInventoryForLevel(int heroLevel) {
        List<Item> inventory = new ArrayList<>();
        if (globalItemCatalog.isEmpty()) return inventory;

        // Filter items by level (hero's level - 2 to hero's level + 2)
        int minLevel = Math.max(1, heroLevel - 2);
        int maxLevel = heroLevel + 2;
        
        List<Item> levelAppropriateItems = globalItemCatalog.stream()
            .filter(item -> item.getMinLevel() >= minLevel && item.getMinLevel() <= maxLevel)
            .collect(java.util.stream.Collectors.toList());
        
        // If not enough level-appropriate items, add some from wider range
        if (levelAppropriateItems.size() < 10) {
            levelAppropriateItems = globalItemCatalog.stream()
                .filter(item -> item.getMinLevel() <= heroLevel + 3)
                .collect(java.util.stream.Collectors.toList());
        }
        
        // Shuffle and select items
        Collections.shuffle(levelAppropriateItems);
        int stockSize = Math.min(10, levelAppropriateItems.size());
        for (int i = 0; i < stockSize; i++) {
            inventory.add(levelAppropriateItems.get(i));
        }

        return inventory;
    }
    
    private List<Item> generateMarketInventoryForPosition(int heroLevel, int row, int col) {
        List<Item> inventory = new ArrayList<>();
        if (globalItemCatalog.isEmpty()) return inventory;

        // Use position as seed for consistent randomness at each location
        Random positionRandom = new Random((long) row * 1000 + col);
        
        // Create a copy and shuffle with position-based seed
        List<Item> shuffledCatalog = new ArrayList<>(globalItemCatalog);
        Collections.shuffle(shuffledCatalog, positionRandom);

        // Create variety by limiting items per level and type
        Map<Integer, List<Item>> itemsByLevel = new HashMap<>();
        Map<String, Integer> typeCount = new HashMap<>();
        
        // Group items by level
        for (Item item : shuffledCatalog) {
            int level = item.getMinLevel();
            itemsByLevel.computeIfAbsent(level, k -> new ArrayList<>()).add(item);
        }
        
        // Select items with variety constraints
        int minDesiredLevel = Math.max(1, heroLevel - 2);
        
        // First, add items from hero's usable range (limited per level)
        for (int level = minDesiredLevel; level <= heroLevel && inventory.size() < 12; level++) {
            List<Item> levelItems = itemsByLevel.get(level);
            if (levelItems != null) {
                // Limit to 3-4 items per level to avoid flooding
                int maxFromThisLevel = Math.min(4, levelItems.size());
                for (int i = 0; i < maxFromThisLevel && inventory.size() < 12; i++) {
                    Item item = levelItems.get(i);
                    String itemType = item.getClass().getSimpleName();
                    
                    // Limit items of same type (max 2 weapons, 2 armor, etc.)
                    if (typeCount.getOrDefault(itemType, 0) < 2) {
                        inventory.add(item);
                        typeCount.put(itemType, typeCount.getOrDefault(itemType, 0) + 1);
                    }
                }
            }
        }
        
        // Fill remaining slots with higher level items (preview of future upgrades)
        for (int level = heroLevel + 1; level <= heroLevel + 3 && inventory.size() < 16; level++) {
            List<Item> levelItems = itemsByLevel.get(level);
            if (levelItems != null) {
                // Only 1-2 items from higher levels
                int maxFromThisLevel = Math.min(2, levelItems.size());
                for (int i = 0; i < maxFromThisLevel && inventory.size() < 16; i++) {
                    inventory.add(levelItems.get(i));
                }
            }
        }

        return inventory;
    }

    // BUYING LOGIC
    private void buyLoop(Scanner scanner, Party party, List<Item> marketInventory) {
        Hero shopper = selectHero(scanner, party, "Who is buying?");
        if (shopper == null) return;
        buyLoopSingleHero(scanner, shopper, marketInventory);
    }

    private void buyLoopWithPagination(Scanner scanner, Hero shopper, List<Item> marketInventory) {
        while (true) {
            List<Item> currentPageItems = getCurrentPageItems(marketInventory);
            int totalPages = (int) Math.ceil((double) marketInventory.size() / itemsPerPage);
            
            System.out.println("\n" + ConsoleColors.WHITE_BOLD + "--- Items for Sale (Page " + (currentPage + 1) + "/" + totalPages + ") (Shopper: " + shopper.getName() + " | Gold: " + ConsoleColors.YELLOW + shopper.getMoney() + ConsoleColors.RESET + ") ---" + ConsoleColors.RESET);
            printItemTable(currentPageItems);
            
            int optionNum = currentPageItems.size() + 1;
            if (currentPage > 0) {
                System.out.println(optionNum + ". Previous Page");
                optionNum++;
            }
            if (currentPage < totalPages - 1) {
                System.out.println(optionNum + ". Next Page");
                optionNum++;
            }
            System.out.println(optionNum + ". Back");

            int choice = InputValidator.getValidInt(scanner, "Select item to buy: ", 1, optionNum);
            
            if (choice <= currentPageItems.size()) {
                // Buying an item
                buyItem(scanner, shopper, currentPageItems.get(choice - 1));
            } else {
                // Navigation options
                int navChoice = choice - currentPageItems.size();
                if (currentPage > 0 && navChoice == 1) {
                    currentPage--; // Previous page
                } else if (currentPage < totalPages - 1 && ((currentPage > 0 && navChoice == 2) || (currentPage == 0 && navChoice == 1))) {
                    currentPage++; // Next page
                } else {
                    break; // Back
                }
            }
        }
    }
    
    private void buyLoopSingleHero(Scanner scanner, Hero shopper, List<Item> marketInventory) {
        while (true) {
            System.out.println("\n" + ConsoleColors.WHITE_BOLD + "--- Items for Sale (Shopper: " + shopper.getName() + " | Gold: " + ConsoleColors.YELLOW + shopper.getMoney() + ConsoleColors.RESET + ") ---" + ConsoleColors.RESET);
            printItemTable(marketInventory);
            System.out.println((marketInventory.size() + 1) + ". Back");

            int choice = InputValidator.getValidInt(scanner, "Select item to buy: ", 1, marketInventory.size() + 1);
            if (choice == marketInventory.size() + 1) break;

            buyItem(scanner, shopper, marketInventory.get(choice - 1));
        }
    }
    
    private List<Item> getCurrentPageItems(List<Item> allItems) {
        int startIndex = currentPage * itemsPerPage;
        int endIndex = Math.min(startIndex + itemsPerPage, allItems.size());
        return allItems.subList(startIndex, endIndex);
    }
    
    private void buyItem(Scanner scanner, Hero shopper, Item item) {
        processPurchase(shopper, item);
    }

    private void processPurchase(Hero hero, Item item) {
        // Rule: Hero cannot buy item if level is too low
        if (hero.getLevel() < item.getMinLevel()) {
            System.out.println(ConsoleColors.RED + "Cannot buy! Required Level: " + item.getMinLevel() + ConsoleColors.RESET);
            return;
        }

        // Rule: Hero cannot buy if insufficient gold
        if (hero.getMoney() < item.getPrice()) {
            System.out.println(ConsoleColors.RED + "Insufficient Gold! Cost: " + item.getPrice() + ConsoleColors.RESET);
            return;
        }

        // Transaction
        hero.deductMoney(item.getPrice());
        hero.getInventory().addItem(item);
        System.out.println(ConsoleColors.GREEN + "Purchase successful! " + item.getName() + " added to inventory." + ConsoleColors.RESET);
    }

    // SELLING LOGIC
    private void sellLoop(Scanner scanner, Party party) {
        Hero seller = selectHero(scanner, party, "Who is selling?");
        if (seller == null) return;
        sellLoopSingleHero(scanner, seller);
    }

    private void sellLoopSingleHero(Scanner scanner, Hero seller) {
        while (true) {
            List<Item> sellableItems = seller.getInventory().getItems();
            if (sellableItems.isEmpty()) {
                System.out.println(ConsoleColors.YELLOW + seller.getName() + " has nothing to sell." + ConsoleColors.RESET);
                break;
            }

            System.out.println("\n" + ConsoleColors.WHITE_BOLD + "--- Your Inventory (Seller: " + seller.getName() + ") ---" + ConsoleColors.RESET);
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

        System.out.println(ConsoleColors.GREEN + "Sold " + item.getName() + " for " + resaleValue + " gold." + ConsoleColors.RESET);
    }

    // HELPERS
    private Hero selectHero(Scanner scanner, Party party, String prompt) {
        System.out.println(ConsoleColors.CYAN + prompt + ConsoleColors.RESET);
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
        System.out.println(ConsoleColors.CYAN + "+----+----------------------+-----+----------+--------------------------------+" + ConsoleColors.RESET);
        System.out.printf(ConsoleColors.CYAN + "|" + ConsoleColors.RESET + " %-2s " + ConsoleColors.CYAN + "|" + ConsoleColors.RESET + " %-20s " + ConsoleColors.CYAN + "|" + ConsoleColors.RESET + " %-3s " + ConsoleColors.CYAN + "|" + ConsoleColors.RESET + " %-8s " + ConsoleColors.CYAN + "|" + ConsoleColors.RESET + " %-30s " + ConsoleColors.CYAN + "|\n" + ConsoleColors.RESET, "ID", "NAME", "LVL", "COST", "TYPE / STATS");
        System.out.println(ConsoleColors.CYAN + "+----+----------------------+-----+----------+--------------------------------+" + ConsoleColors.RESET);

        for (int i = 0; i < items.size(); i++) {
            Item item = items.get(i);
            String stats = extractStats(item); // Helper to get simplified stats
            System.out.printf(ConsoleColors.CYAN + "|" + ConsoleColors.RESET + " %-2d " + ConsoleColors.CYAN + "|" + ConsoleColors.RESET + " %-20s " + ConsoleColors.CYAN + "|" + ConsoleColors.RESET + " %-3d " + ConsoleColors.CYAN + "|" + ConsoleColors.RESET + " " + ConsoleColors.YELLOW + "%-8.0f" + ConsoleColors.RESET + " " + ConsoleColors.CYAN + "|" + ConsoleColors.RESET + " %-30s " + ConsoleColors.CYAN + "|\n" + ConsoleColors.RESET,
                    (i + 1), item.getName(), item.getMinLevel(), item.getPrice(), stats);
        }
        System.out.println(ConsoleColors.CYAN + "+----+----------------------+-----+----------+--------------------------------+" + ConsoleColors.RESET);
    }

    private void printSellableItemTable(List<Item> items) {
        System.out.println(ConsoleColors.CYAN + "+----+----------------------+-----+----------+--------------------------------+" + ConsoleColors.RESET);
        System.out.printf(ConsoleColors.CYAN + "|" + ConsoleColors.RESET + " %-2s " + ConsoleColors.CYAN + "|" + ConsoleColors.RESET + " %-20s " + ConsoleColors.CYAN + "|" + ConsoleColors.RESET + " %-3s " + ConsoleColors.CYAN + "|" + ConsoleColors.RESET + " %-8s " + ConsoleColors.CYAN + "|" + ConsoleColors.RESET + " %-30s " + ConsoleColors.CYAN + "|\n" + ConsoleColors.RESET, "ID", "NAME", "LVL", "SELL", "TYPE / STATS");
        System.out.println(ConsoleColors.CYAN + "+----+----------------------+-----+----------+--------------------------------+" + ConsoleColors.RESET);

        for (int i = 0; i < items.size(); i++) {
            Item item = items.get(i);
            String stats = extractStats(item);
            double sellPrice = item.getPrice() * 0.5;
            System.out.printf(ConsoleColors.CYAN + "|" + ConsoleColors.RESET + " %-2d " + ConsoleColors.CYAN + "|" + ConsoleColors.RESET + " %-20s " + ConsoleColors.CYAN + "|" + ConsoleColors.RESET + " %-3d " + ConsoleColors.CYAN + "|" + ConsoleColors.RESET + " " + ConsoleColors.YELLOW + "%-8.0f" + ConsoleColors.RESET + " " + ConsoleColors.CYAN + "|" + ConsoleColors.RESET + " %-30s " + ConsoleColors.CYAN + "|\n" + ConsoleColors.RESET,
                    (i + 1), item.getName(), item.getMinLevel(), sellPrice, stats);
        }
        System.out.println(ConsoleColors.CYAN + "+----+----------------------+-----+----------+--------------------------------+" + ConsoleColors.RESET);
    }


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
