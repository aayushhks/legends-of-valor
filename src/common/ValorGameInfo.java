package common;

import utils.ConsoleColors;

/**
 * Information provider for Legends of Valor game.
 * Implements the Strategy pattern for displaying game-specific information.
 */
public class ValorGameInfo extends GameInfo {
    
    @Override
    protected String getGameTitle() {
        return "LEGENDS OF VALOR - GAME GUIDE";
    }
    
    @Override
    protected String getHeaderColor() {
        return ConsoleColors.RED;
    }
    
    @Override
    protected void printObjective() {
        String color = getHeaderColor();
        printBoxLine(ConsoleColors.WHITE_BOLD + "OBJECTIVE:" + ConsoleColors.RESET);
        printBoxLine(ConsoleColors.GREEN + "WIN:" + ConsoleColors.RESET + " Move any hero to the Monster Nexus (Row 0).");
        printBoxLine(ConsoleColors.RED + "LOSE:" + ConsoleColors.RESET + " If any monster reaches your Hero Nexus (Row 7).");
        printBoxLine("Battle across 3 lanes in this strategic tower defense RPG.");
        System.out.println(color + "╠════════════════════════════════════════════════════════════════════════╣" + ConsoleColors.RESET);
    }
    
    @Override
    protected void printGameplay() {
        String color = getHeaderColor();
        printBoxLine(ConsoleColors.WHITE_BOLD + "GAMEPLAY:" + ConsoleColors.RESET);
        printBoxLine(ConsoleColors.YELLOW + "> Board:" + ConsoleColors.RESET + " 8x8 grid with 3 lanes (columns 0-2, 3-5, 6-7).");
        printBoxLine(ConsoleColors.YELLOW + "> Movement:" + ConsoleColors.RESET + " Adjacent tiles only (North/South/East/West).");
        printBoxLine(ConsoleColors.YELLOW + "> Combat:" + ConsoleColors.RESET + " Attack range includes diagonal tiles.");
        printBoxLine(ConsoleColors.YELLOW + "> Terrain:" + ConsoleColors.RESET + " Bush(+Dex), Cave(+Agi), Koulou(+Str) bonuses.");
        printBoxLine(ConsoleColors.YELLOW + "> Spawning:" + ConsoleColors.RESET + " New monsters appear every 8 rounds.");
        System.out.println(color + "╠════════════════════════════════════════════════════════════════════════╣" + ConsoleColors.RESET);
    }
    
    @Override
    protected void printControls() {
        String color = getHeaderColor();
        printBoxLine(ConsoleColors.WHITE_BOLD + "CONTROLS:" + ConsoleColors.RESET);
        printBoxLine(ConsoleColors.CYAN + "[W]" + ConsoleColors.RESET + " Move        " + ConsoleColors.CYAN + "[A]" + ConsoleColors.RESET + " Attack      " + ConsoleColors.CYAN + "[C]" + ConsoleColors.RESET + " Cast Spell");
        printBoxLine(ConsoleColors.CYAN + "[T]" + ConsoleColors.RESET + " Teleport    " + ConsoleColors.CYAN + "[R]" + ConsoleColors.RESET + " Recall      " + ConsoleColors.CYAN + "[M]" + ConsoleColors.RESET + " Market");
        printBoxLine(ConsoleColors.CYAN + "[P]" + ConsoleColors.RESET + " Use Potion  " + ConsoleColors.CYAN + "[E]" + ConsoleColors.RESET + " Equip       " + ConsoleColors.CYAN + "[I]" + ConsoleColors.RESET + " Hero Info");
        printBoxLine(ConsoleColors.CYAN + "[Q]" + ConsoleColors.RESET + " Quit Game");
        System.out.println(color + "╠════════════════════════════════════════════════════════════════════════╣" + ConsoleColors.RESET);
    }
    
    @Override
    protected void printTips() {
        String color = getHeaderColor();
        printBoxLine(ConsoleColors.WHITE_BOLD + "STRATEGY TIPS:" + ConsoleColors.RESET);
        printBoxLine(ConsoleColors.RED + "> " + ConsoleColors.RESET + "Use Teleport to quickly support other lanes.");
        printBoxLine(ConsoleColors.RED + "> " + ConsoleColors.RESET + "Recall to your Nexus to access the market safely.");
        printBoxLine(ConsoleColors.RED + "> " + ConsoleColors.RESET + "Position heroes on terrain matching their strengths.");
        printBoxLine(ConsoleColors.RED + "> " + ConsoleColors.RESET + "Balance offense and defense across all three lanes.");
        printBoxLine(ConsoleColors.RED + "> " + ConsoleColors.RESET + "Monitor monster spawn timing to prepare defenses.");
    }
}