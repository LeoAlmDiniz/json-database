package Logger;

public class Logger {

    public static void log(String message, Level level) {
        StringBuilder logMessage = new StringBuilder();
        if (level.equals(Level.INFO)) {
            logMessage.append("[INFO]");
        } else if (level.equals(Level.WARNING)) {
            logMessage.append("[WARNING]");
        } else {
            logMessage.append("[ERROR]");
        }
        logMessage.append(" ");
        logMessage.append(message);
        System.out.println(logMessage);
    }

    public static void log(String message) {
        log(message, Level.INFO);
    }

}
