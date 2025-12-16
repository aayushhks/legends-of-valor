package common;

import utils.ConsoleColors;

/**
 * Information provider for Legends: Monsters and Heroes game.
 * Implements the Strategy pattern for displaying game-specific information.
 */
public class LegendsGameInfo extends GameInfo {
    
    @Override
    protected String getGameTitle() {
        return "LEGENDS: MONSTERS AND HEROES - GAME GUIDE";
    }
    
    @Override
    protected String getHeaderColor() {
        return ConsoleColors.GREEN;
    }
    
    @Override
    protected void printObjective() {
        String color = getHeaderColor();
        printBoxLine(ConsoleColors.WHITE_BOLD + "OBJECTIVE:" + ConsoleColors.RESET);
        printBoxLine("Explore the world with your party of heroes. Defeat monsters,");
        printBoxLine("collect treasures, and grow stronger. The game continues as long");
        printBoxLine("as at least one hero remains alive.");
        System.out.println(color + "╠════════════════════════════════════════════════════════════════════════╣" + ConsoleColors.RESET);
    }
    
    @Override
    protected void printGameplay() {
        String color = getHeaderColor();
        printBoxLine(ConsoleColors.WHITE_BOLD + "GAMEPLAY:" + ConsoleColors.RESET);
        printBoxLine(ConsoleColors.YELLOW + "> Movement:" + ConsoleColors.RESET + " Use WASD to move your party around the grid.");
        printBoxLine(ConsoleColors.YELLOW + "> Encounters:" + ConsoleColors.RESET + " Random battles occur on common spaces.");
        printBoxLine(ConsoleColors.YELLOW + "> Markets:" + ConsoleColors.RESET + " Visit market spaces (M) to buy/sell items.");
        printBoxLine(ConsoleColors.YELLOW + "> Combat:" + ConsoleColors.RESET + " Turn-based battles with attack, spells, potions.");
        printBoxLine(ConsoleColors.YELLOW + "> Progression:" + ConsoleColors.RESET + " Gain XP and gold to level up and buy equipment.");
        System.out.println(color + "╠════════════════════════════════════════════════════════════════════════╣" + ConsoleColors.RESET);
    }
    
    @Override
    protected void printControls() {
        String color = getHeaderColor();
        printBoxLine(ConsoleColors.WHITE_BOLD + "CONTROLS:" + ConsoleColors.RESET);
        printBoxLine(ConsoleColors.CYAN + "[W]" + ConsoleColors.RESET + " Move Up     " + ConsoleColors.CYAN + "[A]" + ConsoleColors.RESET + " Move Left     " + ConsoleColors.CYAN + "[S]" + ConsoleColors.RESET + " Move Down");
        printBoxLine(ConsoleColors.CYAN + "[D]" + ConsoleColors.RESET + " Move Right  " + ConsoleColors.CYAN + "[M]" + ConsoleColors.RESET + " Market Menu   " + ConsoleColors.CYAN + "[I]" + ConsoleColors.RESET + " Party Info");
        printBoxLine(ConsoleColors.CYAN + "[Q]" + ConsoleColors.RESET + " Quit Game");
        System.out.println(color + "╠════════════════════════════════════════════════════════════════════════╣" + ConsoleColors.RESET);
    }
    
    @Override
    protected void printTips() {
        String color = getHeaderColor();
        printBoxLine(ConsoleColors.WHITE_BOLD + "TIPS:" + ConsoleColors.RESET);
        printBoxLine(ConsoleColors.RED + "> " + ConsoleColors.RESET + "Warriors have high strength and HP bonuses.");
        printBoxLine(ConsoleColors.RED + "> " + ConsoleColors.RESET + "Paladins are balanced with good strength and dexterity.");
        printBoxLine(ConsoleColors.RED + "> " + ConsoleColors.RESET + "Sorcerers have high mana and agility for spell casting.");
        printBoxLine(ConsoleColors.RED + "> " + ConsoleColors.RESET + "Buy potions before tough battles for stat boosts.");
        printBoxLine(ConsoleColors.RED + "> " + ConsoleColors.RESET + "Equip better weapons and armor as you progress.");
    }
}