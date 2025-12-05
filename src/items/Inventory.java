package items;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Manages a collection of Items for a Hero.
 * Provides methods to add, remove, and filter items by type.
 */
public class Inventory {
    private final List<Item> items;

    public Inventory() {
        this.items = new ArrayList<>();
    }

    public void addItem(Item item) {
        items.add(item);
    }

    public boolean removeItem(Item item) {
        return items.remove(item);
    }

    public boolean contains(Item item) {
        return items.contains(item);
    }

    public List<Item> getItems() {
        return new ArrayList<>(items); // Return copy to protect internal list
    }

    public List<Weapon> getWeapons() {
        return items.stream()
                .filter(i -> i instanceof Weapon)
                .map(i -> (Weapon) i)
                .collect(Collectors.toList());
    }

    public List<Armor> getArmor() {
        return items.stream()
                .filter(i -> i instanceof Armor)
                .map(i -> (Armor) i)
                .collect(Collectors.toList());
    }

    public List<Potion> getPotions() {
        return items.stream()
                .filter(i -> i instanceof Potion)
                .map(i -> (Potion) i)
                .collect(Collectors.toList());
    }

    public List<Spell> getSpells() {
        return items.stream()
                .filter(i -> i instanceof Spell)
                .map(i -> (Spell) i)
                .collect(Collectors.toList());
    }

    public boolean isEmpty() {
        return items.isEmpty();
    }

    public void printInventory() {
        if (items.isEmpty()) {
            System.out.println("Inventory is empty.");
            return;
        }
        System.out.println("--- Inventory ---");
        for (int i = 0; i < items.size(); i++) {
            System.out.println((i + 1) + ". " + items.get(i).toString());
        }
    }
}