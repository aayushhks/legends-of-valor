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

    @Override
    public String toString() {
        return String.format("[%s] %s (Lvl %d) | HP: %.0f | Dmg: %.0f",
                type, name, level, hp, baseDamage);
    }
}