import java.util.HashMap;

public abstract class Node<N extends Node> {

    public Node(String name) {
        this.name = name;
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

    public String getName() {
        return name;
    }

    private HashMap<String, N> nodeMap
            = new HashMap<String, N>();

    private String name;
}
