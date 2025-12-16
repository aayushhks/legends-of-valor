package items;

/**
 * Represents an equipable weapon with durability system.
 * Data derived from: Weaponry.txt
 */
public class Weapon extends Item {
    private final double baseDamage;
    private final int requiredHands;
    private double durability; // Current durability (0.0 to 100.0)
    private final double maxDurability; // Maximum durability

    public Weapon(String name, double price, int minLevel, double damage, int requiredHands) {
        super(name, price, minLevel);
        this.baseDamage = damage;
        this.requiredHands = requiredHands;
        this.durability = 100.0; // Start at full durability
        this.maxDurability = 100.0;
    }

    public double getDamage() { 
        // Damage scales with durability (minimum 25% damage at 0 durability)
        double durabilityPercent = durability / maxDurability;
        return baseDamage * (0.25 + (0.75 * durabilityPercent));
    }
    
    public double getBaseDamage() { return baseDamage; }
    public int getRequiredHands() { return requiredHands; }
    public double getDurability() { return durability; }
    public double getMaxDurability() { return maxDurability; }
    public boolean isBroken() { return durability <= 0; }
    
    // Degrade weapon slowly (1-2% per round)
    public void degrade() {
        if (durability > 0) {
            durability = Math.max(0, durability - 1.5); // 1.5% degradation per round
        }
    }
    
    // Current value based on durability
    @Override
    public double getPrice() {
        double durabilityPercent = durability / maxDurability;
        return super.getPrice() * durabilityPercent;
    }

    @Override
    public String toString() {
        String durabilityColor = "";
        String resetColor = "";
        
        // Color code durability
        if (durability > 75) {
            durabilityColor = "\u001B[32m"; // Green
        } else if (durability > 50) {
            durabilityColor = "\u001B[33m"; // Yellow  
        } else if (durability > 25) {
            durabilityColor = "\u001B[31m"; // Red
        } else {
            durabilityColor = "\u001B[91m"; // Bright red
        }
        resetColor = "\u001B[0m";
        
        return String.format("%-15s | Lvl: %d | Cost: %.0f | Dmg: %.0f | Hands: %d | Dur: %s%.0f%%" + resetColor,
                name, minLevel, getPrice(), getDamage(), requiredHands, durabilityColor, durability);
    }
}