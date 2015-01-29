import java.net.ServerSocket;
import java.net.Socket;

public class Receiver implements Runnable {

    public class Session implements Runnable {

        /**
         * Construct client's session with socket and rule
         * @param socket - Client's socket
         * @param rule - Rule to parse
         */
        public Session(Socket socket, Rule rule) {
            this.socket = socket;
            this.rule = rule;
        }

        /**
         * When an object implementing interface <code>Runnable</code> is used to create a thread, starting the thread
         * causes the object's <code>run</code> method to be called in that separately executing thread.
         * <p/>
         * The general contract of the method <code>run</code> is that it may take any action whatsoever.
         * @see Thread#run()
         */
        @Override
        public void run() {
            try {
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
                machine.getParser().parse(result);
                machine.getLaboratory().run();

            } catch (Exception e) {
                Logger.getLogger().write(getMachine(), e.getMessage());
            }
        }

        private Socket socket;
        private Rule rule;
    }

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
            Socket client;

            // Open client socket and connect to machine
            ServerSocket socket = new ServerSocket(
				rule.getReceiveInfo().getPort()
			);

            while ((client = socket.accept()) != null) {
                Logger.getLogger().write(getMachine(), "Accepted client (" + client.toString() + ")");
                new Thread(new Session(client, rule)).start();
            }

        } catch (Exception e) {
            Logger.getLogger().write(getMachine(), e.getMessage());
        }
    }

    private Machine machine;
}
