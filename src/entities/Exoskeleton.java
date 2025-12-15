package entities;

/**
 * Exoskeleton monster type - high defense, moderate damage
 * Data derived from: Exoskeletons.txt
 */
public class Exoskeleton extends Monster {

    public Exoskeleton(String name, int level, double hp, double baseDamage, double defense, double dodgeChance) {
        super(name, level, hp, baseDamage, defense, dodgeChance, MonsterType.EXOSKELETON);
    }
}