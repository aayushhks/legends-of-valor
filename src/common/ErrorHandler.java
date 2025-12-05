package common;

/**
 * Centralized error handling utility.
 * Responsible for logging critical failures and providing user-friendly feedback.
 */
public class ErrorHandler {

    /**
     * Handles uncaught exceptions that terminate the application.
     *
     * @param e The exception that caused the crash.
     */
    public static void handleFatalError(Exception e) {
        System.err.println("CRITICAL SYSTEM FAILURE");
        System.err.println("The application encountered an unexpected error and must close.");
        System.err.println("Error Details: " + e.getMessage());

        System.exit(1);
    }
}