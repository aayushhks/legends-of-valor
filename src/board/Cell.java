package board;

import utils.ConsoleColors;
import entities.Hero;
import entities.Monster;

public class Cell {
    private CellType type; // Removed 'final' to allow modification (Obstacle -> Common)
    private Hero hero;
    private Monster monster;



    public Cell(CellType type) {
        this.type = type;
    }

    public CellType getType() { return type; }

    // Setter for changing terrain (e.g., breaking Obstacles)
    public void setType(CellType type) { this.type = type; }

    public void setHero(Hero hero) { this.hero = hero; }
    public Hero getHero() { return hero; }
    public void removeHero() { this.hero = null; }
    public boolean hasHero() { return hero != null; }

    public void setMonster(Monster monster) { this.monster = monster; }
    public Monster getMonster() { return monster; }
    public void removeMonster() { this.monster = null; }
    public boolean hasMonster() { return monster != null; }

    public boolean isAccessible() { return type != CellType.INACCESSIBLE; }
    public boolean isCommon() { return type == CellType.COMMON; }
    public boolean isMarket() { return type == CellType.MARKET; }

    @Override
    public String toString() {
        // MUST return exactly 4 visible characters to fit the board alignment

        if (hasHero() && hasMonster()) {
            return ConsoleColors.PURPLE + "H&M " + ConsoleColors.RESET;
        } else if (hasHero()) {
            // "[H1]" = 4 chars
            return ConsoleColors.CYAN + "[H" + (hero.getLane() + 1) + "]" + ConsoleColors.RESET;
        } else if (hasMonster()) {
            // "(M1)" = 4 chars
            return ConsoleColors.RED + "(M" + (monster.getLane() + 1) + ")" + ConsoleColors.RESET;
        }

        // Default types like " - ", " N ", " X " are 3 chars, add one space
        return type.getSymbol() + " ";
    }
}
