import java.net.Socket;

public class Receiver implements Runnable {

    /**
     * @return - Machine instance
     */
    public Machine getMachine() {
        return machine;
    }

    /**
     * @param machine - Reference to machine
     */
    public void setMachine(Machine machine) {
        this.machine = machine;
    }

    @Override
    public void run() {

        Rule rule = ((Rule) machine.getRule());

        try {
            // Open client socket and connect to machine
            Socket socket = new Socket(
				rule.getReceiveInfo().getHost(),
				rule.getReceiveInfo().getPort()
			);

            // Allocate maximum allowed package
            byte[] bytes = new byte[rule.getLength()];

            // Receive package
            int length = socket.getInputStream().read(bytes);

            if (length < 0) {
                return;
            }

            byte[] result = new byte[length];

            // Copy received package to result package
            System.arraycopy(bytes, 0, result, 0, length);

            // Invoke parser to parse input result
            machine.getParser().parse(result);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Machine machine;
}
