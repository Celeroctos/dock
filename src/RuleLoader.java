import java.io.File;
import java.io.FileInputStream;

public class RuleLoader extends Loader implements HasProtocol {

    public RuleLoader(Machine machine) {
        super(machine);
    }

    @Override
    public void load() throws Exception {

        File handle = new File(getFolder() + getMachine().getName() + ".json");

        if (!handle.exists()) {
            throw new Exception("Rule file doesn't exists (" + getMachine().getName() + ")");
        }

        byte[] bytes = new byte[(int) handle.length()];

        FileInputStream stream = new FileInputStream(handle);

        if (stream.read(bytes) != bytes.length) {
            throw new Exception("Can't read (" + bytes.length + ") from rule file");
        }

        stream.close();

        json = new String(bytes);
    }

    @Override
    public String getProtocol() {
        return "file";
    }

    @Override
    public String getFolder() throws Exception {
        return "rule";
    }

    public String getJson() {
        return json;
    }

    private String json;
}
