package items;

import java.util.Arrays;
import java.util.List;

/**
 * Represents a single-use consumable potion.
 * Data derived from: Potions.txt
 */
public class Potion extends Item {
    private final double attributeIncrease;
    private final List<String> attributesAffected;

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

    @Override
    public String toString() {
        return String.format("%-15s | Lvl: %d | Cost: %.0f | Effect: +%.0f to %s",
                name, minLevel, price, attributeIncrease, String.join(",", attributesAffected));
    }
}