import java.io.InputStream;
import java.net.Socket;
import java.nio.ByteBuffer;

public class Receiver implements HasProtocol {

    public Receiver(Machine machine) {
        this.machine = machine;
    }

    @Override
    public String getProtocol() {
        return "TCP/IP";
    }

    public void connect(String host, int port) throws Exception {
        socket = new Socket(host, port);
    }

    public ByteBuffer receive() throws Exception {
        return null;
    }

    public InputStream getInputStream() throws Exception {
        if (socket != null) {
            return socket.getInputStream();
        } else {
            return null;
        }
    }

    public Machine getMachine() {
        return machine;
    }

    public Socket getSocket() {
       return socket;
    }

    private Socket socket = null;
    private Machine machine;
}
