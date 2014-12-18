package Core;

import java.io.InputStream;
import java.net.Socket;
import java.nio.ByteBuffer;

public class Receiver extends Socket implements Receivable {

    public Receiver(String host, int port) throws Exception {
        super(host, port);
    }

    @Override
    public ByteBuffer receive() throws Exception {
        return null;
    }

    public static void error(String message) throws Exception {
        throw new Exception(message);
    }
}
