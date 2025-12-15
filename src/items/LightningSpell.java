package items;

/**
 * Lightning spell type - high damage magic spell
 * Data derived from: LightningSpells.txt
 */
public class LightningSpell extends Spell {

    public LightningSpell(String name, double price, int minLevel, double damage, double manaCost) {
        super(name, price, minLevel, damage, manaCost, SpellType.LIGHTNING);
    }
}