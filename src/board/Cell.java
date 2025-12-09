package board;

import entities.Hero;
import entities.Monster;

/**
 * Represents a single tile on the game board.
 * Encapsulates the terrain type and logic for accessibility.
 */
public class Cell {
    private final CellType type;

    // --- NEW: Occupancy ---
    private Hero hero;
    private Monster monster;

    public Cell(CellType type) {
        this.type = type;
    }

    public CellType getType() {
        return type;
    }

    public boolean isAccessible() {
        return type != CellType.INACCESSIBLE;
    }

    public boolean isMarket() {
        return type == CellType.MARKET; // Note: Markets are primarily in LegendsGame, but Nexus acts as one in Valor
    }

    public boolean isCommon() {
        return type == CellType.COMMON;
    }

    // --- NEW: Hero Management ---
    public void setHero(Hero hero) {
        this.hero = hero;
    }

    public Hero getHero() {
        return hero;
    }

    public void removeHero() {
        this.hero = null;
    }

    public boolean hasHero() {
        return hero != null;
    }

    // --- NEW: Monster Management ---
    public void setMonster(Monster monster) {
        this.monster = monster;
    }

    public Monster getMonster() {
        return monster;
    }

    public void removeMonster() {
        this.monster = null;
    }

    public boolean hasMonster() {
        return monster != null;
    }

    @Override
    public String toString() {
        // Priority Render: Hero+Monster -> Hero -> Monster -> Terrain
        if (hasHero() && hasMonster()) {
            return "|H&M|"; // Rare case where both share a cell
        } else if (hasHero()) {
            // Print H1, H2, etc. based on lane if possible, or just H
            return "| H" + (hero.getLane() + 1) + "|";
        } else if (hasMonster()) {
            return "| M" + (monster.getLane() + 1) + "|";
        }
        return type.getSymbol();
    }
}