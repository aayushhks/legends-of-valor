package items;

/**
 * Represents equipable armor.
 * Data derived from: Armory.txt
 */
public class Armor extends Item {
    private final double damageReduction;

    public Armor(String name, double price, int minLevel, double damageReduction) {
        super(name, price, minLevel);
        this.damageReduction = damageReduction;
    }

    public double getDamageReduction() { return damageReduction; }

    @Override
    public String toString() {
        return String.format("%-15s | Lvl: %d | Cost: %.0f | Def: %.0f",
                name, minLevel, price, damageReduction);
    }
}