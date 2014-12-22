
public abstract class Loader implements HasProtocol {

    /**
     * Construct loader with your machine instance
     * @param machine - Reference to machine
     */
    public Loader(Machine machine, String folder) {
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
     * Override that method to return loaded result
     * @return - Loaded result
     * @throws Exception
     */
    public abstract String getResult() throws Exception;

    /**
     * If your class implements some protocol features then implement
     * that method to return protocol name and register your class
     * in ProtocolCollection, for example receiver implements TCP/IP
     * protocol data transfer
     * @return - Name of your class's protocol, which it's implements
     */
    @Override
    public String getProtocol() {
        return "file";
    }

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
