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
        // Cap between 0 and maximum HP for this character
        this.hp = Math.max(0, Math.min(hp, getMaxHp()));
    }
    
    // Abstract method for maximum HP calculation (implemented by subclasses)
    public abstract double getMaxHp();

    public boolean isFainted() {
        return hp <= 0;
    }

    public abstract double attack(RPGCharacter target);

    /**
     * Abstract method forcing subclasses to define their specific string representation.
     */
    @Override
    public abstract String toString();
}