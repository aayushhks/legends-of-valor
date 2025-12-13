package items;

import entities.Hero;

public class StatPotion extends Potion {
    public StatPotion(String name, double price, int minLevel, double attributeIncrease, String attributeString) {
        super(name, price, minLevel, attributeIncrease, attributeString);
    }

    @Override
    public void apply(Hero hero) {
        if (affects("Health")) hero.setHp(hero.getHp() + attributeIncrease);
        if (affects("Mana")) hero.setMana(hero.getMana() + attributeIncrease);
        if (affects("Strength")) hero.setStrength(hero.getStrength() + attributeIncrease);
        if (affects("Dexterity")) hero.setDexterity(hero.getDexterity() + attributeIncrease);
        if (affects("Agility")) hero.setAgility(hero.getAgility() + attributeIncrease);

        System.out.println(hero.getName() + " used " + getName() + "!");
    }
}