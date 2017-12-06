import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.management.ManagementFactory;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Logger {
    private final String subsystem;
    private int logLevel;
    PrintWriter out;

    public static int CRITICAL = 0;
    public static int INFO = 1;
    public static int DEBUG = 2;

    public Logger(String subsystem, String logFile, int logLevel) {
        logFile += "_" + ManagementFactory.getRuntimeMXBean().getName();

        this.subsystem = subsystem;
        this.logLevel = logLevel;

        try {
            out = new PrintWriter(new BufferedWriter(new FileWriter(logFile, true)));
        } catch (IOException e) {
            System.out.println("Could not initialize logging!");
        }
    }

    public void critical(String s) {
        if (logLevel < CRITICAL)
            return;
        log(s);
    }

    public void info(String s) {
        if (logLevel < INFO)
            return;
        log(s);
    }

    public void debug(String s) {
        if (logLevel < DEBUG)
            return;
        log(s);
    }

    private String getTimestamp() {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S").format(new Date());
    }

    private void log(String s) {
        String message = "[" + getTimestamp() + "] (" + subsystem + "): " + s;
        out.println(message);
        out.flush();
    }
}
