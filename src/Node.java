import org.json.JSONArray;
import org.json.JSONObject;

import java.nio.ByteBuffer;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Node implements Cloneable {

    /**
     * Construct node
     * @param parent - Node's parent
     * @param name - Name
     * @param cast - Type to cast
     * @param length - Node's length
     * @param max - Max length
     * @param fixed - Fixed value
     */
    public Node(Node parent, String name, String cast, int length, int max, int fixed) {
        this.parent = parent;
        this.name = name;
        this.cast = cast;
        this.length = length;
        this.max = max;
        this.fixed = fixed;
    }

    /**
     * Insert node in current with it's own name as key
     * @param node - Node to insert
     * @throws Exception
     */
    public void add(Node node) throws Exception {
        add(node.getName(), node);
    }

    /**
     * Insert node in current with another name
     * @param key - Node's key
     * @param node - Node instance
     * @throws Exception
     */
    public void add(String key, Node node) throws Exception {

        assert key != null;
        assert node != null;

        if (nodeMap.containsKey(key)) {
            throw new Exception("Key already exists (" + key + ")");
        }

        nodeMap.put(key, node);
    }

    /**
     * Check node's key for existence
     * @param key - Key to check
     * @return - True if exists
     */
    public boolean has(String key) {
        return key != null && nodeMap.containsKey(key);
    }

    /**
     * Get node from current
     * @param key - Node's key
     * @return - Found node
     * @throws Exception
     */
    public Node get(String key) throws Exception {

        assert key != null;

        if (!nodeMap.containsKey(key)) {
            throw new Exception("Unresolved key (" + key + ")");
        }

        return nodeMap.get(key);
    }

    /**
     * Remove node from current
     * @param key - Key to remove
     * @throws Exception
     */
    public void remove(String key) throws Exception {

        assert key != null;

        if (!nodeMap.containsKey(key)) {
            throw new Exception("Unresolved key (" + key + ")");
        }

        nodeMap.remove(key).parent = null;
    }

    /**
     * Clone node with without parent
     * @return - Cloned node with all children
     * @throws CloneNotSupportedException
     */
    @Override
    public Node clone() throws CloneNotSupportedException {

        try {
            super.clone();
        } catch (CloneNotSupportedException ignored) {
        }

        try {
            return clone(null);
        } catch (Exception ignored) {
            return null;
        }
    }

    /**
     * Clone node and set it's new parent (won't be appended)
     * @param parent - New node's parent
     * @return - Cloned node
     * @throws Exception
     */
    public Node clone(Node parent) throws Exception {

        Node node = new Node(parent,
            getName(),
            getCast(),
            getLength(),
            getMax(),
            getFixed()
        );

        node.setValue(getValue());

        for (Node n : nodeMap.values()) {
            try {
                node.add(n.getName(), n.clone(node));
            } catch (Exception ignored) {
            }
        }

        return node;
    }

    /**
     * Find node by specific path. You can simply set node's name
     * to get it likes via <code>get<code/> method, but also you
     * can go to backward nodes <code>..<code/> and get nodes by it's
     * index via <code>.[0]</code> path
     * @param path - Path to find
     * @return - Founded node or it will raise an exception
     * @throws Exception
     */
    public Node find(String path) throws Exception {

        int index = path.indexOf('/');

        String name;
        Node node;

        if (index >= 0) {
            name = path.substring(0, index);
        } else {
            name = path;
        }

        if (name.matches("\\.\\[\\d+\\]")) {

            Matcher matcher = Pattern.compile("\\[(.*?)\\]")
                .matcher(name.substring(1));

            if (!matcher.find()) {
                throw new Exception("No match found \"" + path + "\"");
            }

            int offset = Integer.parseInt(matcher.group(1));

            if (offset < nodeMap.size()) {
                node = (Node) nodeMap.values().toArray()[offset];
            } else {
                throw new Exception("Can't find node at index \"" + offset + "\" in \"" + this.getName() + "\"");
            }

            if (index < 0) {
                return node;
            }

        } else {

            if (index < 0) {
                return get(path);
            }

            if (name.matches("\\.{2}")) {
                node = parent;
            } else {
                node = get(name);
            }
        }

        return node.find(path.substring(index + 1));
    }

    /**
     * Find root node from current
     * @return - Root node
     */
    public Node root() {
        Node node = this;
        while (node.parent != null) {
            node = node.parent;
        }
        return node;
    }

    /**
     * Read nodes values from buffer
     * @param buffer - Buffer with bytes to read
     */
    public void read(ByteBuffer buffer) {
        if (getChildren().size() > 0) {
            for (Node child : getChildren()) {
                child.read(buffer);
            }
        } else {
            byte[] bytes = new byte[getLength()];
            buffer.get(bytes);
            setValue(new String(bytes));
        }
    }

    /**
     * @return - Node's children
     */
    public Collection<Node> getChildren() {
        return nodeMap.values();
    }

    /**
     * @return - Name
     */
    public String getName() {
        return name;
    }

    /**
     * @return - Parent node or null
     */
    public Node getParent() {
        return parent;
    }

    /**
     * @return - Type to cast node
     */
    public String getCast() {
        return cast;
    }

    /**
     * @return - Max allowed node's children length
     */
    public int getMax() {
        return max;
    }

    /**
     * @return - Current node's length or total children
     * length, only if hasn't been set
     */
    public int getLength() {
        int nodeLength = 0;
        if (length == -1) {
            for (Node node : getChildren()) {
                nodeLength += node.getLength();
            }
            length = nodeLength;
        }
        return length;
    }

    /**
     * Serialize node and all it's children
     * @return - Serialized string to json
     */
    public String serialize() {
        return toJson().toString();
    }

    /**
     * Convert node to json
     * @return - Json node
     */
    public JSONObject toJson() {

        return new JSONObject(new HashMap<String, Object>() {{

            put("fixed", fixed);
            put("value", value);
            put("name", name);
            put("cast", cast);

            JSONArray array = new JSONArray();

            for (Node node : nodeMap.values()) {
                array.put(node.toJson());
            }

            put("children", array);
        }});
    }

    /**
     * @return - Default node value
     */
    public int getFixed() {
        return fixed;
    }

    /**
     * @param value - Node's value after receive data
     */
    public void setValue(String value) {
        this.value = value;
    }

    /**
     * @return - Node's value after receive data
     */
    public String getValue() {
        return value;
    }

    private Map<String, Node> nodeMap
        = new LinkedHashMap<String, Node>();

    private Node parent;
    private String value;
    private String name;
    private String cast;

    private int max;
    private int length;
    private int fixed;
}
