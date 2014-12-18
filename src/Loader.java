import java.io.File;
import java.io.FileInputStream;

public class Loader {

    public Loader(Machine machine) {
        this.machine = machine;
    }

    public void load() throws Exception {

        handle = new File(Config.RULE_PATH + machine.getName() + Config.RULE_EXTENSION);

        if (!handle.exists()) {
            throw new Exception("Rule file doesn't exists (" + machine.getName() + ")");
        }

        byte[] bytes = new byte[(int) handle.length()];

        FileInputStream stream = new FileInputStream(handle);

        if (stream.read(bytes) != bytes.length) {
            throw new Exception("Can't read (" + bytes.length + ") from rule file");
        }

        stream.close();

        json = new String(bytes);
    }

    public Machine getMachine() {
        return machine;
    }

    public File getHandle() {
        return handle;
    }

    public String getJson() {
        return json;
    }

    private File handle;
    private Machine machine;
    private String json;
}
