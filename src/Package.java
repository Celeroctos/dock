public class Package {

    public Package(Machine machine, Receiver receiver) {
        this.machine = machine;
        this.receiver = receiver;
    }

    public Machine getMachine() {
        return machine;
    }

    public Receiver getReceiver() {
        return receiver;
    }

    private Receiver receiver;
    private Machine machine;
}
