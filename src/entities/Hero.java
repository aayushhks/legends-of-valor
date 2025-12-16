package entities;

import items.Inventory;
import items.Weapon;
import items.Armor;

/**
 * Represents a playable Hero character.
 * Manages stats, inventory, equipment, and grid position.
 * Implements leveling logic based on HeroType (Warrior, Sorcerer, Paladin).
 */
public class Hero extends RPGCharacter {

    public enum HeroType {
        WARRIOR, SORCERER, PALADIN
    }

    private final HeroType type;
    private double mana;
    private double strength;
    private double agility;
    private double dexterity;
    private double money;
    private int experience;

    // Position on the Valor Board
    private int row;
    private int col;
    private int lane; // 0, 1, or 2 (corresponding to Top/Mid/Bot lanes)

    // Composition: Hero "has an" Inventory
    private final Inventory inventory;

    // Equipment Slots
    private Weapon equippedWeapon;
    private Armor equippedArmor;
    
    // Battle state tracking
    private boolean wasFaintedInBattle;
    
    // Temporary battle boosts (reset after each battle)
    private double strengthBoost = 0;
    private double agilityBoost = 0;
    private double dexterityBoost = 0;

    /**
     * Constructs a new Hero.
     * Note: HP is calculated based on level (Level * 100).
     */
    public Hero(String name, HeroType type, double mana, double strength,
                double agility, double dexterity, double money, int experience) {
        super(name, 1); // Default to Level 1 initially

        this.type = type;
        this.mana = mana;
        this.strength = strength;
        this.agility = agility;
        this.dexterity = dexterity;
        this.money = money;
        this.experience = experience;

        this.inventory = new Inventory();
        this.wasFaintedInBattle = false;

        // HP based on class type - more defensive classes get more HP
        if (type == HeroType.WARRIOR) {
            this.hp = this.level * 150;  // Tanky melee fighters
        } else if (type == HeroType.PALADIN) {
            this.hp = this.level * 120;  // Defensive hybrids  
        } else if (type == HeroType.SORCERER) {
            this.hp = this.level * 80;   // Glass cannon mages
        }
    }
    
    // Constructor for subclasses with explicit parameters
    protected Hero(String name, double hp, double mp, double strength, double dexterity, double agility, double money, int experience, HeroType type) {
        super(name, 1); // Default to Level 1 initially
        
        this.type = type;
        this.hp = hp;
        this.mana = mp;
        this.strength = strength;
        this.dexterity = dexterity;
        this.agility = agility;
        this.money = money;
        this.experience = experience;
        
        this.inventory = new Inventory();
        this.wasFaintedInBattle = false;
    }

    // Implementation of Attack Abstraction
    @Override
    public double attack(RPGCharacter target) {
        double weaponDmg = (equippedWeapon != null) ? equippedWeapon.getDamage() : 0;
        // Logic: (Strength + Weapon Damage) * 0.05
        return (this.strength + weaponDmg) * 0.05;
    }

    // Positioning Logic for Valor
    public void setPosition(int row, int col) {
        this.row = row;
        this.col = col;
    }

    public void setLane(int lane) {
        this.lane = lane;
    }

    public int getRow() { return row; }
    public int getCol() { return col; }
    public int getLane() { return lane; }


    public void gainExperience(int amount) {
        this.experience += amount;
        // Experience points to level up = hero_current_level * 10
        if (this.experience >= this.level * 10) {
            levelUp();
        }
    }

    private void levelUp() {
        int oldLevel = this.level;
        double oldStrength = this.strength;
        double oldAgility = this.agility;
        double oldDexterity = this.dexterity;
        double oldHp = this.hp;
        double oldMana = this.mana;
        
        this.level++;
        
        // Subtract the XP threshold from current XP (allow overflow for next level)
        int xpUsed = oldLevel * 10;
        this.experience -= xpUsed;

        // HP based on class type when leveling up (base + level*increment for gradual growth)
        if (type == HeroType.WARRIOR) {
            this.hp = 100 + (this.level * 50);  // 150, 200, 250, 300... (gradual increase)
        } else if (type == HeroType.PALADIN) {
            this.hp = 80 + (this.level * 40);   // 120, 160, 200, 240... (defensive hybrids)  
        } else if (type == HeroType.SORCERER) {
            this.hp = 60 + (this.level * 30);   // 90, 120, 150, 180... (glass cannon mages)
        }

        // Spec Rule: MP of the heroes when they level up = current_mana * 1.1
        this.mana = this.mana * 1.1;

        // Spec Rule: When a hero levels up all of their skills increase by 5%
        // and their favored skills increase by an extra 5% (Total 10%).
        double standardFactor = 1.05;
        double favoredFactor = 1.10;

        // Apply increases based on Type
        if (type == HeroType.WARRIOR) {
            strength *= favoredFactor;
            agility *= favoredFactor;
            dexterity *= standardFactor;
        } else if (type == HeroType.SORCERER) {
            strength *= standardFactor;
            agility *= favoredFactor;
            dexterity *= favoredFactor;
        } else if (type == HeroType.PALADIN) {
            strength *= favoredFactor;
            agility *= standardFactor;
            dexterity *= favoredFactor;
        }

        // Display simple level up notification
        displayLevelUpStats(oldLevel, oldStrength, oldAgility, oldDexterity, oldHp, oldMana);
        
        // Check for consecutive level ups
        if (this.experience >= this.level * 10) {
            levelUp();
        }
    }
    
    private void displayLevelUpStats(int oldLevel, double oldStrength, double oldAgility, double oldDexterity, double oldHp, double oldMana) {
        System.out.println("\n" + utils.ConsoleColors.GREEN + "LEVEL UP! " + this.name + " (" + this.type + ") Level " + oldLevel + " -> " + this.level + utils.ConsoleColors.RESET);
        
        // Show HP and MP first
        System.out.printf("HP: " + utils.ConsoleColors.GREEN + "%.0f -> %.0f" + utils.ConsoleColors.RESET, oldHp, this.hp);
        System.out.println();
        System.out.printf("MP: " + utils.ConsoleColors.BLUE + "%.0f -> %.0f" + utils.ConsoleColors.RESET, oldMana, this.mana);
        System.out.println();
        
        // Show stats with favored ones colored
        if (type == HeroType.WARRIOR || type == HeroType.PALADIN) {
            System.out.printf("Strength: " + utils.ConsoleColors.YELLOW + "%.0f -> %.0f" + utils.ConsoleColors.RESET, oldStrength, this.strength);
        } else {
            System.out.printf("Strength: %.0f -> %.0f", oldStrength, this.strength);
        }
        System.out.println();
        
        if (type == HeroType.WARRIOR || type == HeroType.SORCERER) {
            System.out.printf("Agility: " + utils.ConsoleColors.CYAN + "%.0f -> %.0f" + utils.ConsoleColors.RESET, oldAgility, this.agility);
        } else {
            System.out.printf("Agility: %.0f -> %.0f", oldAgility, this.agility);
        }
        System.out.println();
        
        if (type == HeroType.SORCERER || type == HeroType.PALADIN) {
            System.out.printf("Dexterity: " + utils.ConsoleColors.PURPLE + "%.0f -> %.0f" + utils.ConsoleColors.RESET, oldDexterity, this.dexterity);
        } else {
            System.out.printf("Dexterity: %.0f -> %.0f", oldDexterity, this.dexterity);
        }
        System.out.println();
        System.out.println();
    }

    public double getMaxHp() {
        if (type == HeroType.WARRIOR) {
            return 100 + (this.level * 50);  // Matches level-up calculation
        } else if (type == HeroType.PALADIN) {
            return 80 + (this.level * 40);   // Matches level-up calculation
        } else if (type == HeroType.SORCERER) {
            return 60 + (this.level * 30);   // Matches level-up calculation
        }
        return 100 + (this.level * 30); // Default fallback
    }
    
    public double getMaxMana() {
        // Base mana from constructor * level up multiplier (1.1^(level-1))
        double baseMana;
        switch (type) {
            case WARRIOR: baseMana = 300; break; // Muamman_Duathall's base
            case SORCERER: baseMana = 1000; break; // Average sorcerer base
            case PALADIN: baseMana = 300; break; // Average paladin base
            default: baseMana = 300; break;
        }
        return baseMana * Math.pow(1.1, level - 1);
    }

    public void revive() {
        this.hp = (this.level * 100) / 2.0; // Revive with half HP
        this.mana = 0;
    }

    public void equipWeapon(Weapon weapon) {
        this.equippedWeapon = weapon;
        System.out.println(this.name + " equipped " + weapon.getName());
    }

    public void equipArmor(Armor armor) {
        this.equippedArmor = armor;
        System.out.println(this.name + " equipped " + armor.getName());
    }

    public Weapon getEquippedWeapon() {
        return equippedWeapon;
    }

    public Armor getEquippedArmor() {
        return equippedArmor;
    }

    public void unequipWeapon() {
        this.equippedWeapon = null;
    }

    public void unequipArmor() {
        this.equippedArmor = null;
    }

    public Inventory getInventory() {
        return inventory;
    }

    public HeroType getType() { return type; }

    public double getMana() { return mana; }
    public void setMana(double mana) { 
        this.mana = Math.max(0, Math.min(mana, getMaxMana())); // Cap between 0 and max mana
    }

    public double getStrength() { return strength + strengthBoost; }
    public void setStrength(double strength) { this.strength = strength; }
    public double getBaseStrength() { return strength; }
    public void addStrengthBoost(double boost) { this.strengthBoost += boost; }
    public boolean hasStrengthBoost() { return strengthBoost > 0; }

    public double getAgility() { return agility + agilityBoost; }
    public void setAgility(double agility) { this.agility = agility; }
    public double getBaseAgility() { return agility; }
    public void addAgilityBoost(double boost) { this.agilityBoost += boost; }
    public boolean hasAgilityBoost() { return agilityBoost > 0; }

    public double getDexterity() { return dexterity + dexterityBoost; }
    public void setDexterity(double dexterity) { this.dexterity = dexterity; }
    public double getBaseDexterity() { return dexterity; }
    public void addDexterityBoost(double boost) { this.dexterityBoost += boost; }
    public boolean hasDexterityBoost() { return dexterityBoost > 0; }

    public double getMoney() { return money; }
    public void setMoney(double money) { this.money = money; }

    public void addMoney(double amount) { this.money += amount; }

    public boolean deductMoney(double amount) {
        if (this.money >= amount) {
            this.money -= amount;
            return true;
        }
        return false;
    }

    public int getExperience() { return experience; }

    // Battle state management
    public boolean wasFaintedInBattle() { return wasFaintedInBattle; }
    public void markFaintedInBattle() { this.wasFaintedInBattle = true; }
    public void resetBattleState() { 
        this.wasFaintedInBattle = false; 
        // Clear all temporary battle boosts
        this.strengthBoost = 0;
        this.agilityBoost = 0;
        this.dexterityBoost = 0;
    }

    @Override
    public String toString() {
        String strDisplay = hasStrengthBoost() ? 
            String.format("%.0f->" + utils.ConsoleColors.RED + "%.0f" + utils.ConsoleColors.RESET, getBaseStrength(), getStrength()) : 
            String.format("%-4.0f", getStrength());
        String dexDisplay = hasDexterityBoost() ? 
            String.format("%.0f->" + utils.ConsoleColors.PURPLE + "%.0f" + utils.ConsoleColors.RESET, getBaseDexterity(), getDexterity()) : 
            String.format("%-4.0f", getDexterity());
        String agiDisplay = hasAgilityBoost() ? 
            String.format("%.0f->" + utils.ConsoleColors.CYAN + "%.0f" + utils.ConsoleColors.RESET, getBaseAgility(), getAgility()) : 
            String.format("%-4.0f", getAgility());
        
        StringBuilder result = new StringBuilder();
        result.append(String.format(
                "[%s] %-15s | Lvl: %d | HP: %-4.0f | MP: %-4.0f | Str: %s | Dex: %s | Agi: %s | Gold: %.0f",
                type, name, level, hp, mana, strDisplay, dexDisplay, agiDisplay, money
        ));
        
        // Show equipment if any
        if (equippedWeapon != null || equippedArmor != null) {
            result.append("\nEquipped: ");
            if (equippedWeapon != null) {
                result.append(utils.ConsoleColors.RED).append(equippedWeapon.getName()).append(utils.ConsoleColors.RESET);
                if (equippedArmor != null) result.append(" | ");
            }
            if (equippedArmor != null) {
                result.append(utils.ConsoleColors.BLUE).append(equippedArmor.getName()).append(utils.ConsoleColors.RESET);
            }
        }
        
        // Show active boosts
        if (hasStrengthBoost() || hasDexterityBoost() || hasAgilityBoost()) {
            result.append("\n");
            if (hasStrengthBoost()) {
                result.append(utils.ConsoleColors.RED).append("[Strength Boost] ").append(utils.ConsoleColors.RESET);
            }
            if (hasDexterityBoost()) {
                result.append(utils.ConsoleColors.PURPLE).append("[Dexterity Boost] ").append(utils.ConsoleColors.RESET);
            }
            if (hasAgilityBoost()) {
                result.append(utils.ConsoleColors.CYAN).append("[Agility Boost]").append(utils.ConsoleColors.RESET);
            }
        }
        
        return result.toString();
    }
}