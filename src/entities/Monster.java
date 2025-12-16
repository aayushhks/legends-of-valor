package entities;

/**
 * Represents a Monster enemy.
 * Attributes derived from: Dragons.txt, Exoskeletons.txt, Spirits.txt
 */
public class Monster extends RPGCharacter {

    public enum MonsterType {
        DRAGON, EXOSKELETON, SPIRIT
    }

    private final MonsterType type;
    private double baseDamage;
    private double defense;
    private double dodgeChance;

    // Position Tracking for Valor
    private int row;
    private int col;
    private int lane;

    // Constructor matches file: Name/level/damage/defense/dodge chance
    public Monster(String name, MonsterType type, int level, double baseDamage, double defense, double dodgeChance) {
        super(name, level);
        this.type = type;
        this.baseDamage = baseDamage;
        this.defense = defense;
        this.dodgeChance = dodgeChance;

        // Spec: HP = level * 100
        this.hp = level * 100;
    }
    
    // Constructor with explicit HP for subclasses
    public Monster(String name, int level, double hp, double baseDamage, double defense, double dodgeChance, MonsterType type) {
        super(name, level);
        this.type = type;
        this.baseDamage = baseDamage;
        this.defense = defense;
        this.dodgeChance = dodgeChance;
        this.hp = hp;
    }

    // Implementation of Attack Abstraction
    @Override
    public double attack(RPGCharacter target) {
        // Monster damage is primarily their base damage attribute
        return this.baseDamage;
    }
    
    @Override
    public double getMaxHp() {
        // Monsters' max HP is their starting HP (level * 100)
        return level * 100;
    }

    public MonsterType getType() {
        return type;
    }

    public double getBaseDamage() {
        return baseDamage;
    }

    public double getDefense() {
        return defense;
    }

    public double getDodgeChance() {
        // Spec: Monster's dodge chance = dodge_chance * 0.01 (loaded value is likely 0-100)
        return dodgeChance * 0.01;
    }

    public void reduceDefense(double amount) {
        this.defense = Math.max(0, this.defense - amount);
    }

    public void reduceDamage(double amount) {
        this.baseDamage = Math.max(0, this.baseDamage - amount);
    }

    public void reduceDodgeChance(double amount) {
        this.dodgeChance = Math.max(0, this.dodgeChance - amount);
    }

    // Position Getters/Setters
    public void setPosition(int row, int col) {
        this.row = row;
        this.col = col;
    }

    public int getRow() { return row; }
    public int getCol() { return col; }

    public int getLane() { return lane; }
    public void setLane(int lane) { this.lane = lane; }

    @Override
    public String toString() {
        return String.format("[%s] %s (Lvl %d) | HP: %.0f | Dmg: %.0f",
                type, name, level, hp, baseDamage);
    }
}