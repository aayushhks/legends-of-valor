package entities;

/**
 * Dragon monster type - high damage, moderate defense
 * Data derived from: Dragons.txt
 */
public class Dragon extends Monster {

    public Dragon(String name, int level, double hp, double baseDamage, double defense, double dodgeChance) {
        super(name, level, hp, baseDamage, defense, dodgeChance, MonsterType.DRAGON);
    }
}