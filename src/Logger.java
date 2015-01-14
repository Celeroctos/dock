import java.io.*;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Vector;

public class Logger {

    /**
     * Get logger instance
     * @return - Logger singleton instance
     */
    public static Logger getLogger() {
        return logger;
    }

    /**
     * Write log to output stream
     * @param message - Text message
     * @return - Self instance
     */
    public Logger write(String message) {
        return write(null, message);
    }

    /**
     * Write log to output stream
     * @param machine - Reference to machine
     * @param message - Text message
     * @return - Self instance
     */
    public synchronized Logger write(Machine machine, String message) {
        String strTime = String.format("%s:%s:%s",
            twoSymbols(calendar.get(Calendar.HOUR_OF_DAY)),
            twoSymbols(calendar.get(Calendar.MINUTE)),
            twoSymbols(calendar.get(Calendar.SECOND))
        );
        try {
            String name = machine != null ? machine.getName() : "null";
            vector.add(strTime + "(" + name + ") - [" + message + "]\n");
            if (vector.size() >= Config.LOG_LIMIT) {
                flush();
            }
        } catch (Exception ignored) {
        }
        return this;
    }

    /**
     * Load all log message from file and associate it with it's date
     * @return - Ordered map with loaded logs
     */
    public HashMap<String, Vector<String>> load() {
        String[] files = new File(Config.LOG_FOLDER).list();
        HashMap<String, Vector<String>> map
            = new HashMap<String, Vector<String>>();
        for (String name : files) {
            String date = name.replace("\\.txt", "");
            map.put(date, load(date));
        }
        return map;
    }

    /**
     * Load log from file
     * @param date - Log's date
     * @return - Vector with log messages
     */
    public synchronized Vector<String> load(String date) {
        Vector<String> stringVector = new Vector<String>();
        try {
            BufferedReader bufferedReader = new BufferedReader(
                new FileReader(Config.LOG_FOLDER + date + ".txt")
            );
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                stringVector.add(line + "\n");
            }
        } catch (FileNotFoundException ignored) {
        } catch (IOException ignored) {
        }
        return stringVector;
    }

    /**
     * Flush log messages and write to file
     */
    public synchronized void flush() {
        try {
            File handle = getFile();
            if (!handle.exists() && !handle.createNewFile()) {
                throw new Exception("Unable to create log file");
            }
            FileWriter fileWriter = new FileWriter(
                handle, true
            );
            for (String s : vector) {
                fileWriter.write(s);
            }
            fileWriter.flush();
            vector.clear();
        } catch (Exception ignored) {
        }
    }

    /**
     * @param number - Integer number
     * @return - Formatted string
     */
    private String twoSymbols(int number) {
        if (number < 10) {
            return "0" + Integer.toString(number);
        }
        return Integer.toString(number);
    }

    /**
     * Open file descriptor for current date
     * @return - File descriptor
     */
    private File getFile() {
        return new File(Config.LOG_FOLDER + String.format("%d-%s-%s.txt", calendar.get(Calendar.YEAR),
            twoSymbols(calendar.get(Calendar.MONTH) + 1),
            twoSymbols(calendar.get(Calendar.DAY_OF_MONTH))
        ));
    }

    private Calendar calendar
        = Calendar.getInstance();

    private Vector<String> vector
        = new Vector<String>();

    /**
     * Locked constructor
     */
    private Logger() {
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                try {
                    flush();
                } catch (Throwable ignored) {
                }
            }
        });
    }

    private static Logger logger
        = new Logger();
}
