package items;

/**
 * Represents an equipable weapon.
 * Data derived from: Weaponry.txt
 */
public class Weapon extends Item {
    private final double damage;
    private final int requiredHands;

    public Weapon(String name, double price, int minLevel, double damage, int requiredHands) {
        super(name, price, minLevel);
        this.damage = damage;
        this.requiredHands = requiredHands;
    }

    public double getDamage() { return damage; }
    public int getRequiredHands() { return requiredHands; }

    @Override
    public String toString() {
        return String.format("%-15s | Lvl: %d | Cost: %.0f | Dmg: %.0f | Hands: %d",
                name, minLevel, price, damage, requiredHands);
    }
}