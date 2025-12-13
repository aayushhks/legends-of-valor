package items;

import entities.Hero;
import java.util.Arrays;
import java.util.List;

/**
 * Represents a single-use consumable potion.
 * Data derived from: Potions.txt
 */
public abstract class Potion extends Item {
    protected final double attributeIncrease;
    protected final List<String> attributesAffected;

    public Potion(String name, double price, int minLevel, double attributeIncrease, String attributeString) {
        super(name, price, minLevel);
        this.attributeIncrease = attributeIncrease;
        // Parses "Health/Mana" or "All" into a list for logic handling later
        this.attributesAffected = Arrays.asList(attributeString.split("/"));
    }

    public double getAttributeIncrease() { return attributeIncrease; }

    public boolean affects(String statName) {
        if (attributesAffected.contains("All")) return true;
        // Simple case-insensitive check
        for (String attr : attributesAffected) {
            if (attr.equalsIgnoreCase(statName)) return true;
        }
        return false;
    }

    public abstract void apply(Hero hero);

    @Override
    public String toString() {
        return String.format("%-15s | Lvl: %d | Cost: %.0f | Effect: +%.0f to %s",
                name, minLevel, price, attributeIncrease, String.join(",", attributesAffected));
    }
}