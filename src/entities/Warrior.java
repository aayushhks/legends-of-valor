package entities;

import items.Inventory;

/**
 * Warrior hero type - excels in strength and dexterity
 * Data derived from: Warriors.txt
 */
public class Warrior extends Hero {

    public Warrior(String name, double hp, double mp, double strength, double dexterity, double agility, double money, int experience) {
        super(name, hp, mp, strength, dexterity, agility, money, experience, HeroType.WARRIOR);
    }
}