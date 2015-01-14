import java.io.File;

public class Config {

    /**
     * How many milliseconds repeater will sleep before repeat
     * all requests sending
     */
    public static final int REPEATER_DELAY = 1000;

    /**
     * Folder which will contains all logs for every day
     */
    public static final String LOG_FOLDER = "log" + File.separator;

    /**
     * Log message limit after what logger will flush all stuff to
     * file
     */
    public static final int LOG_LIMIT = 100;
}
