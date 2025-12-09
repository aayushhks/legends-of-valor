import common.GameRunner;

public class Main {
    public static void main(String[] args) {
        // Delegate execution to the GameRunner which handles the menu and errors
        GameRunner.run();
    }
}