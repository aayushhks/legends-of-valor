package items;

/**
 * Ice spell type - defensive/debuff magic spell
 * Data derived from: IceSpells.txt
 */
public class IceSpell extends Spell {

    public IceSpell(String name, double price, int minLevel, double damage, double manaCost) {
        super(name, price, minLevel, damage, manaCost, SpellType.ICE);
    }
}