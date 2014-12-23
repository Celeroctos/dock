import java.nio.ByteBuffer;

public abstract class Parser {

    /**
     * Override that method to parse received data
     * @param bytes - Bytes to parse
     * @throws Exception
     */
    public abstract void parse(byte[] bytes) throws Exception;

    /**
     * Parse current node and store in it
     * @param buffer - Buffer with received bytes
     * @param node - Node
     * @throws Exception
     */
    public synchronized void parse(ByteBuffer buffer, Node node) throws Exception {

        if (node.getChildren().size() > 0) {
            for (Node child : node.getChildren()) {
                parse(buffer, child);
            }
        } else {

            byte[] bytes = new byte[node.getLength()];

            buffer.get(bytes);

        }
    }
}
