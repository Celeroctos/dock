import org.json.JSONArray;
import org.json.JSONObject;

public class RuleBuilder extends Builder implements HasProtocol {

    public RuleBuilder(Machine machine) {
        super(machine);
    }

    @Override
    public void build() throws Exception {

        JSONObject json = new JSONObject(
            getMachine().getLoader().getJson()
        );

        if (!json.has("name")) {
            throw new Exception("Unable to find key (name) in root node");
        }

        root = new Node(json.getString("name"), null, -1, -1);

        if (!json.has("protocol")) {
            throw new Exception("Unable to find key (protocol) in root node");
        }

        JSONObject protocolNode = json.getJSONObject("protocol");

        if (!protocolNode.has("name")) {
            throw new Exception("Unable to find key (name) in protocol node");
        }

        protocol = protocolNode.getString("name");

        if (!json.has("data")) {
            throw new Exception("Unable to find key (data) in root node");
        }

        JSONArray data = json.getJSONArray("data");

        for (int i = 0; i < data.length(); i++) {
            build(data.getJSONObject(i));
        }
    }

    private boolean build(JSONObject root) {

        String name = null;
        String cast = null;

        int length = -1;
        int max = -1;

        if (root.has("name")) {
            name = root.getString("name");
        }

        if (root.has("cast")) {
            cast = root.getString("cast");
        }

        if (root.has("length")) {
            length = root.getInt("length");
        }

        if (root.has("max")) {
            max = root.getInt("max");
        }

        Node node = new Node(
            name, cast, length, max
        );

        if (!root.has("data")) {
            return false;
        }

        JSONArray data = root.getJSONArray("data");

        for (int i = 0; i < data.length(); i++) {
            build(data.getJSONObject(i));
        }

        return true;
    }

    @Override
    public String getProtocol() throws Exception {
        return protocol;
    }

    public Node getRoot() {
        return root;
    }

    private String protocol = null;
    private Node root = null;
}
