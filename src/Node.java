import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Node implements Cloneable {

    public Node(Node parent, String name, String cast, int length, int max, int fixed) {
        this.parent = parent;
        this.name = name;
        this.cast = cast;
        this.length = length;
        this.max = max;
        this.fixed = fixed;
    }

    public void add(Node node) throws Exception {
        add(node.getName(), node);
    }

    public void add(String key, Node node) throws Exception {

        assert key != null;
        assert node != null;

        if (nodeMap.containsKey(key)) {
            throw new Exception("Key already exists (" + key + ")");
        }

        nodeMap.put(key, node);
    }

    public boolean has(String key) {
        return key != null && nodeMap.containsKey(key);
    }

    public Node get(String key) throws Exception {

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

    public Node clone(Node parent) throws Exception {

        Node node = new Node(parent,
            getName(),
            getCast(),
            getLength(),
            getMax(),
            getFixed()
        );

        for (Node n : node.nodeMap.values()) {
            try {
                node.add(n.getName(), n.clone(node));
            } catch (Exception ignored) {
            }
        }

        return node;
    }

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

    public Node root() {

        Node node = this;

        while (node.parent != null) {
            node = node.parent;
        }

        return node;
    }

    public Collection<Node> getChildren() {
        return nodeMap.values();
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

        int nodeLength = 0;

        if (length == -1) {

            for (Node node : getChildren()) {
                nodeLength += node.getLength();
            }

            length = nodeLength;
        }

        return length;
    }

    public int getFixed() {
        return fixed;
    }

    private Map<String, Node> nodeMap
            = new LinkedHashMap<String, Node>();

    private Node parent;
    private String name;
    private String cast;

    private int max;
    private int length;
    private int fixed;
}
