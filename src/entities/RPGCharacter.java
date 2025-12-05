package entities;

/**
 * Abstract base class representing any character in the RPG world (Hero or Monster).
 * Encapsulates shared attributes like Name, Level, and Health.
 */
public abstract class RPGCharacter {
    protected String name;
    protected int level;
    protected double hp;

    // Using double for HP to handle percentage-based calculations precisely,
    // though display is usually rounded.

    public RPGCharacter(String name, int level) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Character name cannot be null or empty.");
        }
        if (level < 1) {
            throw new IllegalArgumentException("Level must be at least 1.");
        }
        this.name = name;
        this.level = level;
        // HP is set by specific subclasses based on their formulas
    }

    public String getName() {
        return name;
    }

    public int getLevel() {
        return level;
    }

    public double getHp() {
        return hp;
    }

    public void setHp(double hp) {
        // Ensure HP never drops below 0
        this.hp = Math.max(0, hp);
    }

    public boolean isFainted() {
        return hp <= 0;
    }

    /**
     * Abstract method forcing subclasses to define their specific string representation.
     */
    @Override
    public abstract String toString();
}