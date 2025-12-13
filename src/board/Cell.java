package board;

import entities.Hero;
import entities.Monster;

public class Cell {
    private final CellType type;
    private Hero hero;
    private Monster monster;

    // ANSI Colors
    private static final String ANSI_RESET = "\u001B[0m";
    private static final String ANSI_CYAN = "\u001B[36m";
    private static final String ANSI_RED = "\u001B[31m";
    private static final String ANSI_PURPLE = "\u001B[35m";

    public Cell(CellType type) {
        this.type = type;
    }

    public CellType getType() { return type; }

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
        // MUST return exactly 3 visible characters to fit the board alignment

        if (hasHero() && hasMonster()) {
            return ANSI_PURPLE + "H&M" + ANSI_RESET;
        } else if (hasHero()) {
            // " H1" (Space + H + Number) = 3 chars
            return ANSI_CYAN + " H" + (hero.getLane() + 1) + ANSI_RESET;
        } else if (hasMonster()) {
            // " M1" (Space + M + Number) = 3 chars
            return ANSI_RED + " M" + (monster.getLane() + 1) + ANSI_RESET;
        }

        // Default types like " - ", " N ", " X " are already 3 chars
        return type.getSymbol();
    }
}