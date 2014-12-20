import java.util.HashMap;

public class Node<N extends Node> {

    public Node(String name, String cast, int length, int max) {
        this.name = name;
        this.cast = cast;
        this.length = length;
        this.max = max;
    }

    public void add(String key, N node) throws Exception {

        assert key != null;
        assert node != null;

        if (nodeMap.containsKey(key)) {
            throw new Exception("Key already exists (" + key + ")");
        }

        nodeMap.put(key, node);
    }

    public N get(String key) throws Exception {

        assert key != null;

        if (!nodeMap.containsKey(key)) {
            throw new Exception("Unresolved key (" + key + ")");
        }

        return nodeMap.get(key);
    }

    public void drop(String key) throws Exception {

        assert key != null;

        if (!nodeMap.containsKey(key)) {
            throw new Exception("Unresolved key (" + key + ")");
        }

        nodeMap.remove(key);
    }

    public Node root() {

        Node node = this;

        while (node.parent != null) {
            node = node.parent;
        }

        return node;
    }

    public String getName() {
        return name;
    }

    public Node getParent() {
        return parent;
    }

    public String getCast() {
        return cast;
    }

    public int getMax() {
        return max;
    }

    public int getLength() {
        return length;
    }

    private HashMap<String, N> nodeMap
            = new HashMap<String, N>();

    private Node parent = null;
    private String name;
    private String cast;

    private int max;
    private int length;
}
