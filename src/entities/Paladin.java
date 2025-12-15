package entities;

import items.Inventory;

/**
 * Paladin hero type - excels in strength and agility
 * Data derived from: Paladins.txt
 */
public class Paladin extends Hero {

    public Paladin(String name, double hp, double mp, double strength, double dexterity, double agility, double money, int experience) {
        super(name, hp, mp, strength, dexterity, agility, money, experience, HeroType.PALADIN);
    }
}