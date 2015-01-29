import java.io.File;

public class Config {

    /**
     * How many milliseconds repeater will sleep before repeat
     * all requests sending
     */
    public static final int REPEATER_DELAY = 60000;

    /**
     * LOG_FOLDER - Folder which will contains all logs for every day
     * BAK_FOLDER - Folder which will contains all backups of every sending request
     */
    public static final String LOG_FOLDER = "log" + File.separator;
    public static final String BAK_FOLDER = "bak" + File.separator;

    /**
     * Log message limit after what logger will flush all stuff to
     * file
     */
    public static final int LOG_LIMIT = 100;

    /**
     * Use fake emulator to emulate requests with fake data
     */
    public static final boolean FAKE_EMULATOR = false;
}
