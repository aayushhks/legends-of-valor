# CS611-Assignment < Legends of Valor >
## < Legends of Valor >
---------------------------------------------------------------------------
- Name: Patrick Kola 
- Email: kolap@bu.edu
- Student ID: 

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
- **GameRunner.java**: Main menu system, game selection, and restart logic
- **InputValidator.java**: Safe input handling with validation and error recovery
- **ErrorHandler.java**: Centralized error reporting and logging
- **RandomGenerator.java**: Singleton for consistent random number generation

## **Game Engines** (`src/game/`)
- **Game.java**: Abstract template defining the universal game lifecycle
- **LegendsGame.java**: Classic exploration RPG with random world generation
  - Random 4×4 to 20×20 grid worlds
  - Market trading system
  - Monster ambush encounters (50% chance)
  - Infinite progression and party management
- **ValorGame.java**: Strategic tower defense on fixed 8×8 grid
  - 3-lane warfare with hero positioning
  - Real-time terrain bonuses (Bush, Cave, Koulou)
  - Hero-Monster collision detection
  - Nexus defense objectives
- **BattleController.java**: Turn-based combat engine
  - Physical attacks with weapon scaling
  - Spell casting with elemental effects
  - Dodge/defense calculations
  - Experience and gold rewards
- **MarketController.java**: Dynamic trading system
  - Buy/Sell with 50% return rate
  - Hero-specific inventory management
  - Item categorization and filtering

## **Entity System** (`src/entities/`)
- **RPGCharacter.java**: Abstract base class for all living entities
  - Core stats (HP, Mana, Level)
  - Position tracking (Row, Col, Lane)
  - Faint/Revive mechanics
- **Hero.java**: Player character implementation
  - Class-based stat growth (Warrior, Sorcerer, Paladin)
  - Equipment system (Weapon, Armor)
  - Inventory management
  - Leveling with 5%/10% stat increases
  - Terrain bonus tracking and application
- **Monster.java**: Enemy implementation
  - Type-based specialization (Dragon, Exoskeleton, Spirit)
  - Level-based stat scaling
  - Combat abilities (damage, defense, dodge)
  - Spell effect susceptibility
- **Party.java**: Hero group management
  - Multi-hero operations
  - Collective faint/defeat detection
  - Party-wide stat display

## **Item System** (`src/items/`)
- **Item.java**: Abstract base for all collectible objects
- **Inventory.java**: Type-safe storage with category filtering
  - Separate collections for weapons, armor, spells, potions
  - Add/remove operations
  - Category-specific retrieval methods
- **Weapon.java**: Melee equipment with damage bonuses
- **Armor.java**: Defensive equipment with damage reduction
- **Spell.java**: Magic items with elemental effects
  - **Fire**: Reduces enemy defense by 10%
  - **Ice**: Reduces enemy damage by 10%
  - **Lightning**: Reduces enemy dodge by 10%
  - Mana cost system
  - Dexterity-based damage scaling
- **Potion.java**: Consumables with stat modifications
  - Temporary/permanent effects
  - Multiple attribute targeting
  - Single-use consumption

## **Board System** (`src/board/`)
- **Board.java**: Abstract board interface
  - Coordinate validation
  - Cell access methods
  - Abstract rendering
- **LegendsBoard.java**: Random world generator
  - Procedural terrain generation
  - Market placement (15% density)
  - Inaccessible walls (20% density)
  - Party position tracking with ANSI colors
- **ValorBoard.java**: Fixed tactical grid
  - 8×8 grid with 3 lanes separated by walls
  - Enhanced ASCII graphics with box-drawing characters
  - Terrain distribution (Bush, Cave, Koulou)
  - Hero/Monster position visualization
  - Real-time terrain bonus application
- **Cell.java**: Individual tile implementation
  - Occupancy management (Hero, Monster)
  - Terrain type storage
  - Accessibility logic
  - Visual representation with ANSI colors
- **CellType.java**: Terrain type enumeration
  - Visual symbols and color codes
  - Accessibility rules
  - Terrain-specific bonuses:
    - **BUSH**: +10% Dexterity (improves spell damage)
    - **CAVE**: +10% Agility (improves dodge chance)
    - **KOULOU**: +10% Strength (improves attack damage)
    - **NEXUS**: Hero spawn/market access point
    - **INACCESSIBLE**: Impassable barriers

## **Data Loading** (`src/utils/`)
- **GameDataLoader.java**: Factory for creating game objects from text files
  - **Heroes**: `Warriors.txt`, `Sorcerers.txt`, `Paladins.txt`
  - **Monsters**: `Dragons.txt`, `Exoskeletons.txt`, `Spirits.txt`
  - **Items**: `Weaponry.txt`, `Armory.txt`, `Potions.txt`
  - **Spells**: `FireSpells.txt`, `IceSpells.txt`, `LightningSpells.txt`
  - CSV parsing with error handling
  - Type-safe object instantiation

## **Data Assets** (`data/`)
Text files containing game data in CSV format:
- **Character Data**: Pre-defined heroes and monsters with base stats
- **Item Data**: Equipment and consumables with prices and effects
- **Spell Data**: Magic abilities with damage, cost, and elemental types

## **Compilation Output** (`bin/`)
- Compiled `.class` files organized by package structure
- Ready for execution with `java -cp bin Main`  


# Features

## Dual Game Modes
- **Legends**: Classic exploration RPG with randomly generated worlds (4×4 to 20×20)
- **Valor**: Strategic tower defense on fixed 8×8 tactical battlefield
- Seamless game selection and restart system

## Dynamic Worlds
### Legends Mode:
- Procedurally generated grid maps
- Market towns for trading
- Monster ambush encounters (50% chance)
- Party exploration with position tracking

### Valor Mode:
- Fixed 8×8 battlefield with 3 distinct lanes
- Strategic terrain bonuses:
  - **Bush (B)**: +10% Dexterity (spell damage)
  - **Cave (C)**: +10% Agility (dodge chance)
  - **Koulou (K)**: +10% Strength (attack damage)
- Hero-Monster collision detection
- Nexus defense objectives

## Turn-Based Combat
- Physical attacks with weapon scaling
- Elemental spells (Fire, Ice, Lightning) with tactical effects
- Consumable potions for healing and stat boosts
- Mid-battle equipment changes
- Monster specializations:
  - **Dragons** → High base damage
  - **Exoskeletons** → High defense
  - **Spirits** → High dodge chance

## Market System
- Dynamic buy/sell economy (50% return rate)
- Equipment trading: Weapons, Armor
- Magic system: Elemental spells with mana costs
- Consumables: Healing and stat-boosting potions
- Nexus-based trading in Valor mode

## RPG Progression
- Class-based stat growth (Warriors, Sorcerers, Paladins)
- Favored stats grow faster (+10% vs +5%)
- Automatic HP/MP regeneration
- Experience and gold rewards from combat
- Real-time terrain bonuses in Valor

## Enhanced UI
- ANSI color-coded messages and terrain
- Enhanced ASCII graphics with proper alignment
- Comprehensive stat tables and inventory displays
- Robust input validation with error handling
- Instant restart system for continuous gameplay  

---

# Game Structure

## Game Modes

| Mode     | Objective              | Map Type        | Strategy                    |
|----------|------------------------|-----------------|-----------------------------|
| Legends  | Survive & Level Up     | Random 4×4-20×20| Exploration & Resource Mgmt |
| Valor    | Defend Your Nexus      | Fixed 8×8       | Tactical Positioning        |

## Terrain Types

### Legends Mode:
| Tile Type     | Symbol | Description                      |
|---------------|--------|---------------------------------|
| Common        | .      | Normal tile with 50% ambush chance |
| Market        | M      | Safe trade zone                  |
| Inaccessible  | X      | Wall tile                        |
| Party         | P      | Shows your current location      |

### Valor Mode:
| Terrain       | Symbol | Effect                          |
|---------------|--------|---------------------------------|
| Bush          | B      | +10% Dexterity (spell damage)   |
| Cave          | C      | +10% Agility (dodge chance)     |
| Koulou        | K      | +10% Strength (attack damage)   |
| Nexus         | N      | Hero spawn & market access      |
| Walls         | X      | Lane barriers (inaccessible)    |
| Common        | -      | Plain terrain (no bonuses)      |
| Heroes        | H1-H3  | Player characters by lane       |
| Monsters      | M1-M3  | Enemies advancing by lane       |


## Heroes

| Class     | Str  | Dex  | Agi  | Description                  |
|-----------|------|------|------|------------------------------|
| Warrior   | High | Med  | High | Strong melee fighter         |
| Sorcerer  | Low  | High | High | Spell specialist             |
| Paladin   | High | High | Med  | Balanced tank/DPS hybrid     |


## Monsters

| Type        | Specialty        |
|-------------|------------------|
| Dragon      | High base damage |
| Exoskeleton | High defense     |
| Spirit      | High dodge chance |

---

# How to Play

## Controls

### Legends Mode:
| Key | Action     | Description              |
|-----|------------|---------------------------|
| W   | Move Up    | Move north               |
| A   | Move Left  | Move west                |
| S   | Move Down  | Move south               |
| D   | Move Right | Move east                |
| M   | Market     | Enter shop (only on M)   |
| I   | Info       | Show stats and inventory |
| Q   | Quit       | Exit game                |

### Valor Mode:
| Key | Action     | Description              |
|-----|------------|---------------------------|
| W   | Move       | Choose direction to move |
| A   | Attack     | Physical combat          |
| C   | Cast Spell | Use magic abilities      |
| T   | Teleport   | Move to ally's lane      |
| R   | Recall     | Return to Nexus          |
| M   | Market     | Shop at Nexus            |
| P   | Potion     | Use consumables          |
| E   | Equip      | Change gear              |
| I   | Info       | Show character details   |
| Q   | Quit       | Exit game                |

## Combat System

| Action     | Description                                |
|------------|--------------------------------------------|
| Attack     | Physical damage (Strength + Weapon)        |
| Cast Spell | Uses Mana (Dexterity scales damage)        |
| Use Potion | Heal or boost stats                        |
| Equip      | Change gear mid-battle                     |

### Spell Types:
- **Fire** → Lowers enemy defense  
- **Ice** → Lowers enemy damage  
- **Lightning** → Lowers enemy dodge  

### Mechanics & Balance
- Dodge scales from Agility (capped at ~60–75%)  
- Level Ups increase stats by 5% (favored +10%)  
- Selling returns 50% of item value  


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
