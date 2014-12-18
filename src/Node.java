import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.HashMap;

public abstract class Node<K, N extends Node> {

    public Node(Machine machine, Receiver receiver, String name, int length) {
        this.machine = machine;
        this.receiver = receiver;
        this.name = name;
        this.length = length;
    }

    public abstract boolean parse(ByteBuffer buffer) throws Exception;

    public ByteBuffer receive() throws Exception{

        assert receiver != null;
        assert length != 0;

        InputStream stream = receiver.getInputStream();

        byte[] buffer = new byte[length];

        if (stream.read(buffer) != length) {
            throw new Exception("Can't read byte sequence from socket's stream (" + length + " bytes)");
        }

        return ByteBuffer.wrap(buffer);
    }

    public void add(K key, N node) throws Exception {

        assert key != null;
        assert node != null;

        if (nodeMap.containsKey(key)) {
            throw new Exception("Key already exists (" + key + ")");
        }

        nodeMap.put(key, node);
    }

    public N get(K key) throws Exception {

        assert key != null;

        if (!nodeMap.containsKey(key)) {
            throw new Exception("Unresolved key (" + key + ")");
        }

        return nodeMap.get(key);
    }

    public void delete(K key) throws Exception {

        assert key != null;

        if (!nodeMap.containsKey(key)) {
            throw new Exception("Unresolved key (" + key + ")");
        }

        nodeMap.remove(key);
    }

    public Machine getMachine() {
        return machine;
    }

    public Receiver getReceiver() {
        return receiver;
    }

    public String getName() {
        return name;
    }

    public int getLength() {
        return length;
    }

    private HashMap<K, N> nodeMap
            = new HashMap<K, N>();

    private Machine machine;
    private Receiver receiver;
    private String name;

    private int length;
}
