import org.json.JSONArray;
import org.json.JSONObject;

public class Builder {

    public Builder(Machine machine) {
        this.machine = machine;
    }

    public void build() throws Exception {

        JSONObject root = new JSONObject(
            getMachine().getLoader().getJson()
        );

        JSONArray array = root.getJSONArray("data");

        int packLength = 0;

        for (int i = 0; i < array.length(); i++) {
            if (array.getJSONObject(i).has("max")) {
                packLength += array.getJSONObject(i).getInt("max");
            }
        }

        System.out.println(packLength);

//        build(root);
    }

    private boolean build(JSONObject root) {

        String name = null;
        String type = null;

        int length = 0;
        int max = -1;

        if (root.has("name")) {
            name = root.getString("name");
        }

        if (root.has("type")) {
            type = root.getString("type");
        }

        if (root.has("length")) {
            length = root.getInt("length");
        }

        if (root.has("max")) {
            max = root.getInt("max");
        }

        System.out.format(" + %s %s %d %d\n", name, type, length, max);

        if (!root.has("data")) {
            return false;
        }

        JSONArray data = root.getJSONArray("data");

        for (int i = 0; i < data.length(); i++) {
            build(data.getJSONObject(i));
        }

        return true;
    }

    public Machine getMachine() {
        return machine;
    }

    private Machine machine;
}
