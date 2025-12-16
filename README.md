# CS611-Assignment < Legends of Valor >
## < Legends of Valor >
---------------------------------------------------------------------------
- Name: Patrick Kola 
- Email: kolap@bu.edu
- Student ID: U63346026

- Name: Aayush Kumar
- Email: aayushks@bu.edu
- Student ID: U73761402

---

# Overview
Legends of Valor is a comprehensive RPG suite featuring two distinct game modes:

## **Legends: Monsters and Heroes** (Classic Mode)
A traditional exploration RPG where players assemble a team of up to 3 heroes—Warriors, Sorcerers, or Paladins—and navigate procedurally generated worlds. Your goal is to survive monster encounters, trade in markets, and level up infinitely.

## **Valor: Nexus Defense** (Strategic Mode)  
A tactical tower defense game where 3 heroes defend their Nexus on a fixed 8×8 battlefield. Heroes must prevent monsters from reaching their base while utilizing terrain bonuses and strategic positioning across 3 distinct lanes.

Both games share the same character system, combat mechanics, and progression but offer completely different gameplay experiences.



# File Breakdown

## **Core Application** (`src/`)
- **Main.java**: Entry point that bootstraps the GameRunner

## **Game Framework** (`src/common/`)
- **GameRunner.java**: Main menu system and game selection
- **InputValidator.java**: Safe input handling with validation
- **ErrorHandler.java**: Centralized error reporting
- **RandomGenerator.java**: Singleton for random number generation
- **GameInfo.java**: Abstract base for game information display
- **LegendsGameInfo.java**: Game guide for Legends: Monsters and Heroes
- **ValorGameInfo.java**: Game guide for Legends of Valor

## **Game Engines** (`src/game/`)
- **Game.java**: Abstract template defining universal game lifecycle
- **LegendsGame.java**: Classic exploration RPG with random world generation
- **ValorGame.java**: Strategic tower defense on fixed 8×8 grid
- **BattleController.java**: Turn-based combat with equipment durability and boosts
- **MarketController.java**: Dynamic trading system with level-scaling

## **Entity System** (`src/entities/`)
- **RPGCharacter.java**: Abstract base class for all living entities
- **Hero.java**: Player character with class-based growth and equipment
- **Warrior.java**: Warrior hero class specialization (Strength/Agility focus)
- **Sorcerer.java**: Sorcerer hero class specialization (Dexterity/Agility focus)
- **Paladin.java**: Paladin hero class specialization (Strength/Dexterity focus)
- **Monster.java**: Enemy implementation with type specialization
- **Dragon.java**: Dragon monster type with high damage
- **Exoskeleton.java**: Exoskeleton monster type with high defense
- **Spirit.java**: Spirit monster type with high dodge
- **Party.java**: Hero group management and collective operations

## **Item System** (`src/items/`)
- **Item.java**: Abstract base for all collectible objects
- **Inventory.java**: Type-safe storage with category filtering
- **Weapon.java**: Melee equipment with damage bonuses and durability
- **Armor.java**: Defensive equipment with damage reduction and durability
- **Spell.java**: Magic items with elemental effects
- **FireSpell.java**: Fire spells that reduce enemy defense
- **IceSpell.java**: Ice spells that reduce enemy damage
- **LightningSpell.java**: Lightning spells that reduce enemy dodge
- **Potion.java**: Abstract base for consumable items
- **HealingPotion.java**: Restores hero HP
- **ManaPotion.java**: Restores hero MP
- **StatPotion.java**: Temporary battle stat boosts

## **Board System** (`src/board/`)
- **Board.java**: Abstract board interface with coordinate validation
- **LegendsBoard.java**: Random world generator with procedural terrain
- **ValorBoard.java**: Fixed 8×8 tactical grid with 3 lanes
- **Cell.java**: Individual tile with occupancy and terrain management
- **CellType.java**: Terrain types with bonuses (Bush, Cave, Koulou, Nexus)

## **Data Loading** (`src/utils/`)
- **GameDataLoader.java**: Factory for creating game objects from CSV files
- **ConsoleColors.java**: ANSI color codes for console output

## **Data Assets** (`data/`)
- **Warriors.txt**, **Sorcerers.txt**, **Paladins.txt**: Hero data
- **Dragons.txt**, **Exoskeletons.txt**, **Spirits.txt**: Monster data
- **Weaponry.txt**, **Armory.txt**, **Potions.txt**: Item data
- **FireSpells.txt**, **IceSpells.txt**, **LightningSpells.txt**: Spell data

## **Compilation Output** (`bin/`)
- Compiled `.class` files organized by package structure  



# Installation & Run

## Prerequisites
- Java JDK 8 or higher  
- Terminal with ANSI color support  

## Compile and Run

### Windows CMD:
```cmd
mkdir bin
javac -encoding UTF-8 -d bin src\Main.java src\board\*.java src\common\*.java src\entities\*.java src\game\*.java src\items\*.java src\utils\*.java
java -cp bin Main
```

### Linux/Mac/Unix:
```bash
mkdir -p bin && javac -d bin src/**/*.java && java -cp bin Main
```



# Input/Output Examples

## 🎮 Game Mode Selection

```
Welcome to Legends of Valor!
Please select a game mode:

1. Legends of Valor - Strategic Nexus Defense
2. Monsters and Heroes - Classic Exploration RPG
3. Quit

Choice: 1
```

---

# 🏰 Valor Mode: Nexus Defense

## 🛡️ Team Recruitment

```
=== RECRUIT YOUR TEAM ===
You must select 3 Heroes to defend the Nexus.

Party Size: 0/3
+----+----------------------+-----------+-----+------+------+------+------+------+
| ID | Name                 | Class     | Lvl | HP   | MP   | Str  | Dex  | Agi  |
+----+----------------------+-----------+-----+------+------+------+------+------+
| 1  | Gaerdal_Ironhand     | WARRIOR   | 1   | 100  | 100  | 700  | 600  | 500  |
| 2  | Sehanine_Monnbow     | SORCERER  | 1   | 100  | 600  | 700  | 500  | 800  |
| 3  | Caliber_Heist        | PALADIN   | 1   | 100  | 400  | 400  | 400  | 400  |
+----+----------------------+-----------+-----+------+------+------+------+------+
Select Hero ID: 1
Gaerdal_Ironhand joined the party!
```

---

## ⚔️ Battle for the Nexus

```
Initializing Legends of Valor...
*** Reinforcements! New Monsters have entered the Nexus! ***

The battle for the Nexus begins!

=== ROUND 1 ===

     L-0     L-0     W-1     L-1     L-1     W-2     L-2     L-2  
  +=======+=======+=======+=======+=======+=======+=======+=======+
  |   N   |  (M1) |   X   |   N   |  (M2) |   X   |   N   | (M3)  |
  +-------+-------+-------+-------+-------+-------+-------+-------+
  |   B   |   K   |   X   |   -   |   B   |   X   |   -   |   C   |
  +-------+-------+-------+-------+-------+-------+-------+-------+
  |   -   |   C   |   X   |   B   |   K   |   X   |   -   |   -   |
  +-------+-------+-------+-------+-------+-------+-------+-------+
  |   K   |   -   |   X   |   C   |   K   |   X   |   K   |   C   |
  +-------+-------+-------+-------+-------+-------+-------+-------+
  |   C   |   C   |   X   |   K   |   C   |   X   |   -   |   B   |
  +-------+-------+-------+-------+-------+-------+-------+-------+
  |   -   |   C   |   X   |   -   |   -   |   X   |   -   |   -   |
  +-------+-------+-------+-------+-------+-------+-------+-------+
  |   K   |   -   |   X   |   C   |   C   |   X   |   K   |   K   |
  +-------+-------+-------+-------+-------+-------+-------+-------+
  |  (H1) |   N   |   X   |  (H2) |   N   |   X   |  (H3) |   N   |
  +=======+=======+=======+=======+=======+=======+=======+=======+

Turn: Gaerdal_Ironhand (Lane 0)
CONTROLS: [W]Move [A]ttack [C]ast [T]eleport [R]ecall [M]arket [P]otion [E]quip [I]nfo [Q]uit
```

---

## 🏃 Hero Movement & Terrain Bonus

```
Action: w
Move: [W]Up [A]Left [S]Down [D]Right
Dir: w
Gaerdal_Ironhand moved to (6,0)
Terrain: Koulou increases Strength by 70.0!
```

---

## ⚔️ Combat Turn

```
Action: a
Select Target:
1. Ancient_Dragon
Target: 1
Gaerdal_Ironhand dealt 42 damage!
Ancient_Dragon HP: 58/100
```

---

# 🗺️ Legends Mode: Classic RPG

## 🌍 World Generation

```
Initializing Game Engine...
Loading Game Data...

--- World Generation ---
Enter board size (4-20): 8

--- Hero Selection ---
Enter party size (1-3): 2
```

---

## 🧝 Hero Selection

```
Select Hero #1:
1. Warrior (Favors Strength/Agility)
2. Sorcerer (Favors Dexterity/Agility) 
3. Paladin (Favors Strength/Dexterity)
Choose class: 2
```

---

## 🌎 World Exploration

```
The party enters the world...
+---+---+---+---+---+---+---+---+
| . | X | M | . | X | . | . | . |
+---+---+---+---+---+---+---+---+
| . | . | . | X | . | M | X | . |
+---+---+---+---+---+---+---+---+
| X | . | . | . | . | . | . | X |
+---+---+---+---+---+---+---+---+
| . | . | X | . | . | . | . | . |
+---+---+---+---+---+---+---+---+
| . | X | . | . | X | . | . | . |
+---+---+---+---+---+---+---+---+
| . | . | . | . | . | . | X | . |
+---+---+---+---+---+---+---+---+
| X | . | . | . | . | . | . | . |
+---+---+---+---+---+---+---+---+
| P | . | . | X | . | M | . | . |
+---+---+---+---+---+---+---+---+

CONTROLS: [W]Up [A]Left [S]Down [D]Right  [M]Market [I]Info [Q]Quit
Action: a
Action: w
Action: w
```

---

## 🐉 Monster Encounter

```
*** AMBUSH! You have encountered monsters! ***

*** Battle Started! Enemies approaching: ***
- [DRAGON] Nefarian (Lvl 2) | HP: 200 | Dmg: 150
- [SPIRIT] Whisperwind (Lvl 1) | HP: 100 | Dmg: 120
```

---

## 🎯 Spell Combat

```
Turn: Sehanine_Monnbow [SORCERER]
Action: 2 (Cast Spell)

--- Spellbook ---
1. Lightning Strike (Dmg: 80, Cost: 30)
2. Ice Shard (Dmg: 65, Cost: 25) 
3. Fireball (Dmg: 90, Cost: 35)
Select Spell: 1

Select Target:
1. Nefarian
2. Whisperwind
Target: 1

Sehanine_Monnbow casts Lightning Strike on Nefarian for 88 damage!
Nefarian's dodge reduced by Lightning!
Nefarian HP: 112/200
```

---

## 🏪 Market Trading

```
=== NEXUS MARKET ===
Welcome, Gaerdal_Ironhand! Gold: 2850

1. Buy Items
2. Sell Items  
3. Exit

Choice: 1

=== WEAPONS ===
1. Steel Sword          | Dmg: +25  | Price: 500
2. Mystic Blade         | Dmg: +40  | Price: 800
3. Dragon Slayer        | Dmg: +60  | Price: 1200

Purchase: 2
You bought Mystic Blade for 800 gold!
```

---

## 🎊 Victory & Progression

```
*** VICTORY! ***
Gaerdal_Ironhand reached the Monster Nexus!

Level Up! Gaerdal_Ironhand is now Level 2!
+5% All Stats, +10% Favored Stats (Strength/Agility)

Final Status:
+------------------------------------------------------------+
|                        PARTY STATUS                        |
+----------------------+-------+--------+--------+-----------+
| NAME                 | LVL   | HP     | MP     | GOLD      |
+----------------------+-------+--------+--------+-----------+
| Gaerdal_Ironhand     | 2     | 115    | 115    | 3200      |
| Sehanine_Monnbow     | 2     | 110    | 330    | 2950      |
| Caliber_Heist        | 2     | 120    | 140    | 2800      |
+------------------------------------------------------------+
```

---

## 🔄 Game Selection

```
Do you want to play again? (y/n): y

Please select a game mode:
1. Legends of Valor - Strategic Nexus Defense  
2. Monsters and Heroes - Classic Exploration RPG
3. Quit

Choice: 2
```

Enjoy mastering both legendary adventures!
