package items;

import entities.Hero;

public class ManaPotion extends Potion {
    public ManaPotion(String name, double price, int minLevel, double attributeIncrease) {
        super(name, price, minLevel, attributeIncrease, "Mana");
    }

    @Override
    public void apply(Hero hero) {
        hero.setMana(hero.getMana() + attributeIncrease);
        System.out.println(hero.getName() + " recovered " + attributeIncrease + " Mana!");
    }
}