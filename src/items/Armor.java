package items;

/**
 * Represents equipable armor with durability system.
 * Data derived from: Armory.txt
 */
public class Armor extends Item {
    private final double baseDamageReduction;
    private double durability; // Current durability (0.0 to 100.0)
    private final double maxDurability; // Maximum durability

    public Armor(String name, double price, int minLevel, double damageReduction) {
        super(name, price, minLevel);
        this.baseDamageReduction = damageReduction;
        this.durability = 100.0; // Start at full durability
        this.maxDurability = 100.0;
    }

    public double getDamageReduction() { 
        // Defense scales with durability (minimum 25% defense at 0 durability)
        double durabilityPercent = durability / maxDurability;
        return baseDamageReduction * (0.25 + (0.75 * durabilityPercent));
    }
    
    public double getBaseDamageReduction() { return baseDamageReduction; }
    public double getDurability() { return durability; }
    public double getMaxDurability() { return maxDurability; }
    public boolean isBroken() { return durability <= 0; }
    
    // Degrade armor per hit (2-3% per hit)
    public void degrade() {
        if (durability > 0) {
            durability = Math.max(0, durability - 2.5); // 2.5% degradation per hit
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
        
        return String.format("%-15s | Lvl: %d | Cost: %.0f | Def: %.0f | Dur: %s%.0f%%" + resetColor,
                name, minLevel, getPrice(), getDamageReduction(), durabilityColor, durability);
    }
}