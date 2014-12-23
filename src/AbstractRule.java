
public abstract class AbstractRule {

    /**
     * Construct loader with your machine instance
     * @param machine - Reference to machine
     */
    public AbstractRule(Machine machine, String folder) {
        this.machine = machine;
        this.folder = folder;
    }

    /**
     * Implement that method to load some specific data from
     * filesystem, similar to receiver and builder
     * @throws Exception
     */
    public abstract void load() throws Exception;

    /**
     * Implement that method to parse your machine's
     * specific data, which you've loaded or received via
     * loader, receiver or something else
     * @throws Exception
     */
    public abstract void parse() throws Exception;

    /**
     * Override this method to build you own rule file, which has been
     * loaded via abstract loader instance or received from some
     * protocol via receiver instance
     * @throws Exception
     */
    public abstract void build() throws Exception;

    /**
     * Every loader have to load data from somewhere, so implement
     * that method to show from which relative path loader have
     * to load data
     * @return - Path to loader's data
     */
    public String getFolder() {
        return folder;
    }

    /**
     * Get loader's machine instance
     * @return - Machine's instance
     */
    public Machine getMachine() {
        return machine;
    }

    private Machine machine;
    private String folder;
}
