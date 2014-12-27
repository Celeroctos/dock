
public abstract class AbstractRule {

    /**
     * Construct loader with your machine instance
     * @param folder - Path to folder with rules
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

    public static class SocketInfo {

        /**
         * Construct socket with host and port
         * @param host - Socket's host
         * @param port - Socket's port
         */
        public SocketInfo(String host, int port) {
            this.host = host;
            this.port = port;
        }

        /**
         * @return - Socket's host name
         */
        public String getHost() {
            return host;
        }

        /**
         * @return - Socket's connect port
         */
        public int getPort() {
            return port;
        }

        private String host;
        private int port;
    }

    /**
     * @return - Receive socket's info
     */
    public SocketInfo getReceiveInfo() {
        return receiveInfo;
    }

    /**
     * @return - Send socket's info
     */
    public SocketInfo getSendInfo() {
        return sendInfo;
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

    /**
     * Get rule's root node
     * @return - Root node
     */
    public Node getRoot() {
        return root;
    }

    /**
     * Get laboratory API key
     * @return - API key
     */
    public String getKey() {
        return key;
    }

    /**
     * Get laboratory host
     * @return - Laboratory host
     */
    public String getHost() {
        return host;
    }

    protected Node root = null;
    protected SocketInfo receiveInfo = null;
    protected SocketInfo sendInfo = null;
    protected String key = null;
    protected String host = null;

    private Machine machine;
    private String folder;
}
