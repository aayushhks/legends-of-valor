package utils;

import entities.*;
import entities.Hero.HeroType;
import entities.Monster.MonsterType;
import items.*;
import items.Spell.SpellType;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Utility class responsible for parsing game data from external text files.
 * Acts as the centralized Asset Manager / Factory for the game.
 * Robustly handles missing files and malformed lines.
 */
public class GameDataLoader {

    private static final String DATA_DIR = "data";

    // ENTITY LOADERS
    public static List<Hero> loadHeroes(String fileName, HeroType type) {
        List<Hero> heroes = new ArrayList<>();
        File file = new File(DATA_DIR + File.separator + fileName);

        if (!file.exists()) {
            System.err.println("Warning: Config file not found: " + fileName);
            return heroes;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (shouldSkip(line)) continue;
                try {
                    String[] parts = line.trim().split("\\s+");
                    // Format: Name/mana/strength/agility/dexterity/starting money/starting experience
                    if (parts.length < 7) continue;

                    Hero hero = createHero(parts[0], type,
                            Double.parseDouble(parts[1]), // Mana as HP
                            Double.parseDouble(parts[1]), // Mana
                            Double.parseDouble(parts[2]), // Str
                            Double.parseDouble(parts[4]), // Dex
                            Double.parseDouble(parts[3]), // Agi
                            Double.parseDouble(parts[5]), // Money
                            Integer.parseInt(parts[6])    // XP
                    );
                    if (hero != null) heroes.add(hero);
                } catch (Exception e) {
                    logError(fileName, line);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return heroes;
    }

    public static List<Monster> loadMonsters(String fileName, MonsterType type) {
        List<Monster> monsters = new ArrayList<>();
        File file = new File(DATA_DIR + File.separator + fileName);

        if (!file.exists()) {
            System.err.println("Warning: Config file not found: " + fileName);
            return monsters;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (shouldSkip(line)) continue;
                try {
                    String[] parts = line.trim().split("\\s+");
                    // Format: Name/level/damage/defense/dodge chance
                    if (parts.length < 5) continue;

                    Monster monster = createMonster(parts[0], type,
                            Integer.parseInt(parts[1]),   // Level
                            Integer.parseInt(parts[1]) * 100.0, // HP = level * 100
                            Double.parseDouble(parts[2]), // Damage
                            Double.parseDouble(parts[3]), // Defense
                            Double.parseDouble(parts[4])  // Dodge
                    );
                    if (monster != null) monsters.add(monster);
                } catch (Exception e) {
                    logError(fileName, line);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return monsters;
    }

    // ITEM LOADERS
    public static List<Weapon> loadWeapons(String fileName) {
        List<Weapon> weapons = new ArrayList<>();
        File file = new File(DATA_DIR + File.separator + fileName);

        if (!file.exists()) return weapons;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (shouldSkip(line)) continue;
                try {
                    String[] parts = line.trim().split("\\s+");
                    // Format: Name/cost/level/damage/required hands
                    if (parts.length < 5) continue;

                    weapons.add(new Weapon(
                            parts[0],                     // Name
                            Double.parseDouble(parts[1]), // Cost
                            Integer.parseInt(parts[2]),   // Min Level
                            Double.parseDouble(parts[3]), // Damage
                            Integer.parseInt(parts[4])    // Hands
                    ));
                } catch (Exception e) {
                    logError(fileName, line);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return weapons;
    }

    public static List<Armor> loadArmor(String fileName) {
        List<Armor> armorList = new ArrayList<>();
        File file = new File(DATA_DIR + File.separator + fileName);

        if (!file.exists()) return armorList;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (shouldSkip(line)) continue;
                try {
                    String[] parts = line.trim().split("\\s+");
                    // Format: Name/cost/required level/damage reduction
                    if (parts.length < 4) continue;

                    armorList.add(new Armor(
                            parts[0],                     // Name
                            Double.parseDouble(parts[1]), // Cost
                            Integer.parseInt(parts[2]),   // Min Level
                            Double.parseDouble(parts[3])  // Damage Reduction
                    ));
                } catch (Exception e) {
                    logError(fileName, line);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return armorList;
    }

    // --- UPDATED METHOD FOR POTION FACTORY LOGIC ---
    public static List<Potion> loadPotions(String fileName) {
        List<Potion> potions = new ArrayList<>();
        File file = new File(DATA_DIR + File.separator + fileName);

        if (!file.exists()) return potions;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (shouldSkip(line)) continue;
                try {
                    String[] parts = line.trim().split("\\s+");
                    // Format: Name/cost/required level/attribute increase/attribute affected
                    if (parts.length < 5) continue;

                    String name = parts[0];
                    double cost = Double.parseDouble(parts[1]);
                    int lvl = Integer.parseInt(parts[2]);
                    double val = Double.parseDouble(parts[3]);
                    String attr = parts[4];

                    // Factory Logic
                    if (attr.equalsIgnoreCase("Health")) {
                        potions.add(new HealingPotion(name, cost, lvl, val));
                    } else if (attr.equalsIgnoreCase("Mana")) {
                        potions.add(new ManaPotion(name, cost, lvl, val));
                    } else {
                        potions.add(new StatPotion(name, cost, lvl, val, attr));
                    }

                } catch (Exception e) {
                    logError(fileName, line);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return potions;
    }

    public static List<Spell> loadSpells(String fileName, SpellType type) {
        List<Spell> spells = new ArrayList<>();
        File file = new File(DATA_DIR + File.separator + fileName);

        if (!file.exists()) return spells;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (shouldSkip(line)) continue;
                try {
                    String[] parts = line.trim().split("\\s+");
                    // Format: Name/cost/required level/damage/mana cost
                    if (parts.length < 5) continue;

                    Spell spell = createSpell(parts[0], type,
                            Double.parseDouble(parts[1]), // Cost
                            Integer.parseInt(parts[2]),   // Min Level
                            Double.parseDouble(parts[3]), // Damage
                            Double.parseDouble(parts[4])  // Mana Cost
                    );
                    if (spell != null) spells.add(spell);
                } catch (Exception e) {
                    logError(fileName, line);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return spells;
    }

    private static boolean shouldSkip(String line) {
        // Skip empty lines or header lines that start with "Name/"
        return line.trim().isEmpty() || line.trim().startsWith("Name/");
    }

    private static void logError(String fileName, String line) {
        System.err.println("Skipping malformed line in " + fileName + ": " + line);
    }

    // Factory method to create specific hero subclasses
    private static Hero createHero(String name, HeroType type, double hp, double mp, 
                                  double strength, double dexterity, double agility, 
                                  double money, int experience) {
        switch (type) {
            case WARRIOR:
                return new Warrior(name, hp, mp, strength, dexterity, agility, money, experience);
            case SORCERER:
                return new Sorcerer(name, hp, mp, strength, dexterity, agility, money, experience);
            case PALADIN:
                return new Paladin(name, hp, mp, strength, dexterity, agility, money, experience);
            default:
                return null;
        }
    }

    // Factory method to create specific monster subclasses
    private static Monster createMonster(String name, MonsterType type, int level, 
                                        double hp, double baseDamage, double defense, 
                                        double dodgeChance) {
        switch (type) {
            case DRAGON:
                return new Dragon(name, level, hp, baseDamage, defense, dodgeChance);
            case EXOSKELETON:
                return new Exoskeleton(name, level, hp, baseDamage, defense, dodgeChance);
            case SPIRIT:
                return new Spirit(name, level, hp, baseDamage, defense, dodgeChance);
            default:
                return null;
        }
    }

    // Public factory method for runtime monster creation
    public static Monster createMonsterFromTemplate(Monster template, int level) {
        double levelMultiplier = level / (double)Math.max(1, template.getLevel());
        return createMonster(
            template.getName(),
            template.getType(), 
            level,
            level * 100.0, // HP = level * 100
            template.getBaseDamage() * levelMultiplier,
            template.getDefense() * levelMultiplier,
            template.getDodgeChance() * 100
        );
    }

    // Factory method to create specific spell subclasses
    private static Spell createSpell(String name, SpellType type, double price, 
                                    int minLevel, double damage, double manaCost) {
        switch (type) {
            case FIRE:
                return new FireSpell(name, price, minLevel, damage, manaCost);
            case ICE:
                return new IceSpell(name, price, minLevel, damage, manaCost);
            case LIGHTNING:
                return new LightningSpell(name, price, minLevel, damage, manaCost);
            default:
                return null;
        }
    }
}