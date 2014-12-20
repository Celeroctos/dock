import java.util.HashMap;

public class ProtocolCollection {

    public ProtocolCollection() {
    }

    public HasProtocol add(String key, HasProtocol protocolable) throws Exception {

        if (protocolHashMap.containsKey(key)) {
            throw new Exception("Protocol with that key already exists (" + key + ")");
        }

        protocolHashMap.put(key, protocolable);

        return protocolable;
    }

    public HasProtocol get(String key) throws Exception {

        if (!protocolHashMap.containsKey(key)) {
            throw new Exception("Unresolved protocol key (" + key + ")");
        }

        return protocolHashMap.get(key);
    }

    public HasProtocol drop(String key) throws Exception {

        if (!protocolHashMap.containsKey(key)) {
            throw new Exception("Unresolved protocol key (" + key + ")");
        }

        return protocolHashMap.remove(key);
    }

    private HashMap<String, HasProtocol> protocolHashMap
            = new HashMap<String, HasProtocol>();
}
