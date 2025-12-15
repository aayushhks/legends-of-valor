package entities;

import items.Inventory;
import items.Weapon;
import items.Armor;

/**
 * Represents a playable Hero character.
 * Manages stats, inventory, equipment, and grid position.
 * Implements leveling logic based on HeroType (Warrior, Sorcerer, Paladin).
 */
public class Hero extends RPGCharacter {

    public enum HeroType {
        WARRIOR, SORCERER, PALADIN
    }

    private final HeroType type;
    private double mana;
    private double strength;
    private double agility;
    private double dexterity;
    private double money;
    private int experience;

    // Position on the Valor Board
    private int row;
    private int col;
    private int lane; // 0, 1, or 2 (corresponding to Top/Mid/Bot lanes)

    // Composition: Hero "has an" Inventory
    private final Inventory inventory;

    // Equipment Slots
    private Weapon equippedWeapon;
    private Armor equippedArmor;

    /**
     * Constructs a new Hero.
     * Note: HP is calculated based on level (Level * 100).
     */
    public Hero(String name, HeroType type, double mana, double strength,
                double agility, double dexterity, double money, int experience) {
        super(name, 1); // Default to Level 1 initially

        this.type = type;
        this.mana = mana;
        this.strength = strength;
        this.agility = agility;
        this.dexterity = dexterity;
        this.money = money;
        this.experience = experience;

        this.inventory = new Inventory();

        // HP of heroes = level * 100
        this.hp = this.level * 100;
    }
    
    // Constructor for subclasses with explicit parameters
    protected Hero(String name, double hp, double mp, double strength, double dexterity, double agility, double money, int experience, HeroType type) {
        super(name, 1); // Default to Level 1 initially
        
        this.type = type;
        this.hp = hp;
        this.mana = mp;
        this.strength = strength;
        this.dexterity = dexterity;
        this.agility = agility;
        this.money = money;
        this.experience = experience;
        
        this.inventory = new Inventory();
    }

    // Implementation of Attack Abstraction
    @Override
    public double attack(RPGCharacter target) {
        double weaponDmg = (equippedWeapon != null) ? equippedWeapon.getDamage() : 0;
        // Logic: (Strength + Weapon Damage) * 0.05
        return (this.strength + weaponDmg) * 0.05;
    }

    // Positioning Logic for Valor
    public void setPosition(int row, int col) {
        this.row = row;
        this.col = col;
    }

    public void setLane(int lane) {
        this.lane = lane;
    }

    public int getRow() { return row; }
    public int getCol() { return col; }
    public int getLane() { return lane; }


    public void gainExperience(int amount) {
        this.experience += amount;
        // Experience points to level up = hero_current_level * 10
        if (this.experience >= this.level * 10) {
            levelUp();
        }
    }

    private void levelUp() {
        this.level++;
        // Reset XP (or keep accumulated? Standard RPGs keep total, but rules imply a threshold)
        this.experience = 0;

        // Spec Rule: When a hero levels up, this formula is used to reset their HP.
        this.hp = this.level * 100;

        // Spec Rule: MP of the heroes when they level up = current_mana * 1.1
        this.mana = this.mana * 1.1;

        // Spec Rule: When a hero levels up all of their skills increase by 5%
        // and their favored skills increase by an extra 5% (Total 10%).
        double standardFactor = 1.05;
        double favoredFactor = 1.10;

        // Apply increases based on Type
        if (type == HeroType.WARRIOR) {
            strength *= favoredFactor;
            agility *= favoredFactor;
            dexterity *= standardFactor;
        } else if (type == HeroType.SORCERER) {
            strength *= standardFactor;
            agility *= favoredFactor;
            dexterity *= favoredFactor;
        } else if (type == HeroType.PALADIN) {
            strength *= favoredFactor;
            agility *= standardFactor;
            dexterity *= favoredFactor;
        }

        System.out.println(this.name + " leveled up to " + this.level + "!");
    }

    public void revive() {
        this.hp = (this.level * 100) / 2.0; // Revive with half HP
        this.mana = 0;
    }

    public void equipWeapon(Weapon weapon) {
        this.equippedWeapon = weapon;
        System.out.println(this.name + " equipped " + weapon.getName());
    }

    public void equipArmor(Armor armor) {
        this.equippedArmor = armor;
        System.out.println(this.name + " equipped " + armor.getName());
    }

    public Weapon getEquippedWeapon() {
        return equippedWeapon;
    }

    public Armor getEquippedArmor() {
        return equippedArmor;
    }

    public Inventory getInventory() {
        return inventory;
    }

    public HeroType getType() { return type; }

    public double getMana() { return mana; }
    public void setMana(double mana) { this.mana = mana; }

    public double getStrength() { return strength; }
    public void setStrength(double strength) { this.strength = strength; }

    public double getAgility() { return agility; }
    public void setAgility(double agility) { this.agility = agility; }

    public double getDexterity() { return dexterity; }
    public void setDexterity(double dexterity) { this.dexterity = dexterity; }

    public double getMoney() { return money; }
    public void setMoney(double money) { this.money = money; }

    public void addMoney(double amount) { this.money += amount; }

    public boolean deductMoney(double amount) {
        if (this.money >= amount) {
            this.money -= amount;
            return true;
        }
        return false;
    }

    public int getExperience() { return experience; }

    @Override
    public String toString() {
        return String.format(
                "[%s] %-15s | Lvl: %d | HP: %-4.0f | MP: %-4.0f | Str: %-4.0f | Dex: %-4.0f | Agi: %-4.0f | Gold: %.0f",
                type, name, level, hp, mana, strength, dexterity, agility, money
        );
    }
}