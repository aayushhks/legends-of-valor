package entities;

/**
 * Spirit monster type - high dodge chance, lower physical stats
 * Data derived from: Spirits.txt
 */
public class Spirit extends Monster {

    public Spirit(String name, int level, double hp, double baseDamage, double defense, double dodgeChance) {
        super(name, level, hp, baseDamage, defense, dodgeChance, MonsterType.SPIRIT);
    }
}