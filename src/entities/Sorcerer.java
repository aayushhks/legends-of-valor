package entities;

import items.Inventory;

/**
 * Sorcerer hero type - excels in dexterity and agility
 * Data derived from: Sorcerers.txt
 */
public class Sorcerer extends Hero {

    public Sorcerer(String name, double hp, double mp, double strength, double dexterity, double agility, double money, int experience) {
        super(name, hp, mp, strength, dexterity, agility, money, experience, HeroType.SORCERER);
    }
}