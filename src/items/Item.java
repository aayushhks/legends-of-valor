package items;

/**
 * Abstract base class for all tradeable items in the game.
 * Encapsulates shared attributes: Name, Cost, and Minimum Level requirement.
 */
public abstract class Item {
    protected String name;
    protected double price;
    protected int minLevel;

    public Item(String name, double price, int minLevel) {
        this.name = name;
        this.price = price;
        this.minLevel = minLevel;
    }

    public String getName() { return name; }
    public double getPrice() { return price; }
    public int getMinLevel() { return minLevel; }

    /**
     * Abstract method to ensure every item type has a formatted display string.
     */
    @Override
    public abstract String toString();
}