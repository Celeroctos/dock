import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.Calendar;

public class Backup {

    /**
     * Get backup singleton
     * @return - Backup instance
     */
    public static Backup getBackup() {
        return backup;
    }

    /**
     * Write bytes to bak folder
     * @param machine - From which machine request has been received
     * @param bytes - Array with received bytes
     */
    public synchronized void write(Machine machine, byte[] bytes) {
        try {
            OutputStream stream = new FileOutputStream(
                getFile(machine), true
            );
            stream.write(bytes);
            stream.flush();
            stream.close();
            Logger.getLogger().write(machine, "Backup file has been successfully created for " + machine.getName());
        } catch (Exception e) {
            Logger.getLogger().write(machine, "An error occurred while writing backup file for " + machine.getName() +
                " (" + e.getClass().getName() + "): " + e.getMessage());
        }
        try {
            Thread.sleep(25);
        } catch (InterruptedException ignored) {
        }
    }

    /**
     * Get file handle for current timestamp and current machine
     * @param machine - Machine instance
     * @return - Descriptor with file
     */
    private File getFile(Machine machine) throws Exception {
        File folder = new File(Config.BAK_FOLDER + machine.getName().toLowerCase());
        if (!folder.exists()) {
            folder.mkdirs();
        }
        String strTime = String.format("%s%s%s",
            twoSymbols(calendar.get(Calendar.HOUR_OF_DAY)),
            twoSymbols(calendar.get(Calendar.MINUTE)),
            twoSymbols(calendar.get(Calendar.SECOND))
        );
        File file = new File(Config.BAK_FOLDER + machine.getName().toLowerCase() + File.separator + String.format("%d%s%s",
            calendar.get(Calendar.YEAR), twoSymbols(calendar.get(Calendar.MONTH) + 1), twoSymbols(calendar.get(Calendar.DAY_OF_MONTH)))
                + strTime + ".bak"
        );
        if (!file.exists()) {
            file.createNewFile();
        }
        return file;
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

    private static Calendar calendar = Calendar.getInstance();

    /**
     * Locked constructor
     */
    private Backup() {
        /* Locked */
    }

    private static Backup backup = new Backup();
}
