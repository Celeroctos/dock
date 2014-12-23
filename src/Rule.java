import com.sun.org.apache.xerces.internal.impl.dv.util.HexBin;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.security.SecureRandom;
import java.util.Collection;
import java.util.Vector;

public class Rule extends AbstractRule {

    public Rule(Machine machine) {
        super(machine, "rule");
    }

    /**
     * Implement that method to load some specific data from
     * filesystem, similar to receiver and builder
     * @throws Exception
     */
    @Override
    public void load() throws Exception {

        File handle = new File(getFolder() + File.separator + getMachine().getName() + ".json");

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

    /**
     * Implement that method to parse just loaded file
     * @throws Exception
     */
    @Override
    public void parse() throws Exception {
        /* Not needed */
    }

    /**
     * Override this method to build you own rule file, which has been loaded via abstract loader instance or received
     * from some protocol via receiver instance
     * @throws Exception
     */
    @Override
    public void build() throws Exception {

        JSONObject json = new JSONObject(
            this.json
        );

        if (!json.has("name")) {
            throw new Exception("Unable to find key \"name\" in \"root\" node");
        }

        root = new Node(null, json.getString("name"), null, -1, -1, -1);

        if (!json.has("protocol")) {
            throw new Exception("Unable to find key \"protocol\" in \"root\" node");
        }

        JSONObject protocolNode = json.getJSONObject("protocol");

        if (!protocolNode.has("send") || !protocolNode.has("receive")) {
            throw new Exception("Unable to find key \"send\" or \"receive\" in \"protocol\" node");
        }

        receiveInfo = new SocketInfo(
            protocolNode.getJSONObject("receive").getString("host"),
            protocolNode.getJSONObject("receive").getInt("port")
        );

        sendInfo = new SocketInfo(
            protocolNode.getJSONObject("send").getString("host"),
            protocolNode.getJSONObject("send").getInt("port")
        );

        if (!json.has("data")) {
            throw new Exception("Unable to find key \"data\" in \"root\" node");
        }

        JSONArray data = json.getJSONArray("data");

        for (int i = 0; i < data.length(); i++) {
            build(root, data.getJSONObject(i), true);
        }

        for (Reference reference : references) {
            reference.getParent().add(
                root.get(reference.getName())
            );
        }
    }

    /**
     * Calculate maximum package length
     * @return - Maximum package length
     */
    public int getLength() {
        return getLength(root);
    }

    /**
     * Calculate maximum package length with node
     * @param node - Node to calculate
     * @return - Maximum length for current node
     */
    private int getLength(Node node) {

        int length = node.getLength();

        for (Node child : node.getChildren()) {
            length += getLength(child);
        }

        return length;
    }

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

    private SocketInfo receiveInfo = null;
    private SocketInfo sendInfo = null;

    private boolean build(Node parent, JSONObject json, boolean strict) throws Exception {

        String name;
        String cast;

        int length = -1;
        int max = -1;
        int fixed = -1;

        if (json.has("reference")) {
            return references.add(new Reference(
                parent, json.getString("reference")
            ));
        }

        if (json.has("name")) {
            name = json.getString("name");
        } else {
            name = generateName();
        }

        if (json.has("cast")) {
            cast = json.getString("cast");
        } else {
            cast = "none";
        }

        if (json.has("length")) {
            length = json.getInt("length");
        } else if (!json.has("data") && strict) {
            throw new Exception("Unable to find key \"length\" in \"" + name + "\" node ");
        }

        if (json.has("max")) {
            max = json.getInt("max");
        }

        if (json.has("fixed")) {
            fixed = json.getInt("fixed");
        }

        Node node = new Node(
            parent, name, cast, length, max, fixed
        );

        parent.add(node.getName(), node);

        if (!json.has("data")) {
            return false;
        }

        JSONArray data = json.getJSONArray("data");

        for (int i = 0; i < data.length(); i++) {
            build(node, data.getJSONObject(i), strict);
        }

        return true;
    }

    private static class Reference {

        /**
         * Construct reference
         * @param parent - Here will be nodes after clone
         * @param name - Node name
         */
        public Reference(Node parent, String name) {
            this.parent = parent;
            this.name = name;
        }

        /**
         * @return - Reference's parent
         */
        public Node getParent() {
            return parent;
        }

        /**
         * @return - Reference's name
         */
        public String getName() {
            return name;
        }

        private Node parent;
        private String name;
    }

    private static String generateName() throws Exception {

        // Initialize secure random with default PRGN
        SecureRandom secureRandom = new SecureRandom();

        // Create 20 bytes block
        byte[] bytes = new byte[10];

        // Engine this bytes
        secureRandom.nextBytes(bytes);

        // Generate seed
        byte[] seed = secureRandom.generateSeed(10);

        // Encode and return as string
        return HexBin.encode(seed);
    }

    public Node getRoot() {
        return root;
    }

    private String json;
    private Node root;

    private Collection<Reference> references
        = new Vector<Reference>();
}
