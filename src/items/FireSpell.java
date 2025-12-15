package items;

/**
 * Fire spell type - offensive magic spell
 * Data derived from: FireSpells.txt
 */
public class FireSpell extends Spell {

    public FireSpell(String name, double price, int minLevel, double damage, double manaCost) {
        super(name, price, minLevel, damage, manaCost, SpellType.FIRE);
    }
}