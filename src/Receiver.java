import java.net.Socket;
import java.nio.ByteBuffer;

public class Receiver extends Socket {

    public Receiver(String host, int port) throws Exception {
        super(host, port);
    }

    public ByteBuffer receive() throws Exception {
        return null;
    }
}
