package items;

import entities.Hero;

public class HealingPotion extends Potion {
    public HealingPotion(String name, double price, int minLevel, double attributeIncrease) {
        super(name, price, minLevel, attributeIncrease, "Health");
    }

    @Override
    public void apply(Hero hero) {
        hero.setHp(hero.getHp() + attributeIncrease);
        System.out.println(hero.getName() + " healed for " + attributeIncrease + " HP!");
    }
}