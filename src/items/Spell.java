package items;

/**
 * Represents a magic spell scroll.
 * Data derived from: FireSpells.txt, IceSpells.txt, LightningSpells.txt
 */
public class Spell extends Item {

    public enum SpellType {
        ICE, FIRE, LIGHTNING
    }

    private final double damage;
    private final double manaCost;
    private final SpellType type;

    public Spell(String name, double price, int minLevel, double damage, double manaCost, SpellType type) {
        super(name, price, minLevel);
        this.damage = damage;
        this.manaCost = manaCost;
        this.type = type;
    }

    public double getDamage() { return damage; }
    public double getManaCost() { return manaCost; }
    public SpellType getType() { return type; }

    @Override
    public String toString() {
        return String.format("%-15s | Type: %s | Lvl: %d | Cost: %.0f | Dmg: %.0f | MP: %.0f",
                name, type, minLevel, price, damage, manaCost);
    }
}