package entities;

import java.util.ArrayList;
import java.util.List;

/**
 * Manages the group of Heroes adventuring together.
 * Tracks the party's location on the grid and their collective state.
 */
public class Party {
    private final List<Hero> heroes;
    private int row; // Current Board Row
    private int col; // Current Board Column

    public Party() {
        this.heroes = new ArrayList<>();
        this.row = 0;
        this.col = 0;
    }

    public void addHero(Hero hero) {
        if (heroes.size() < 3) {
            heroes.add(hero);
        } else {
            System.out.println("Party is full! (Max 3 heroes)");
        }
    }

    public List<Hero> getHeroes() {
        return heroes;
    }

    public Hero getHero(int index) {
        if (index >= 0 && index < heroes.size()) {
            return heroes.get(index);
        }
        return null;
    }

    public boolean isPartyWipedOut() {
        return heroes.stream().allMatch(Hero::isFainted);
    }

    public int getSize() {
        return heroes.size();
    }

    // Location Management
    public void setLocation(int row, int col) {
        this.row = row;
        this.col = col;
    }

    public int getRow() { return row; }
    public int getCol() { return col; }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("--- Party Status ---\n");
        for (Hero h : heroes) {
            sb.append(h.toString()).append("\n");
        }
        return sb.toString();
    }
}