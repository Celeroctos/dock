import java.net.Socket;

public class Receiver implements Runnable {

    /**
     * Construct receiver with machine
     * @param machine - Machine
     */
    public Receiver(Machine machine) {
        this.machine = machine;
    }

    /**
     * @return - Machine instance
     */
    public Machine getMachine() {
        return machine;
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

            // Make backup of all received stuff
            Backup.getBackup().write(getMachine(), result);

            // Invoke parser to parse input result
            try {
                machine.getParser().parse(result);
            } catch (Exception ignored) {
                /* Backup.getBackup().write(getMachine(), result); */
            }

        } catch (Exception e) {
            Logger.getLogger().write(getMachine(), e.getMessage());
        }
    }

    private Machine machine;
}
