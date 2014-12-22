
public class MachineFactory {

    private MachineFactory() {
        /* Locked */
    }

    /**
     * Get singleton factory instance
     * @return - Factory instance
     */
    public static MachineFactory getFactory() {
        return factory;
    }

    /**
     * Build machine for MEK7222
     * @return - Machine's instance
     * @throws Exception
     */
    public Machine createMek7222() throws Exception {

        Machine machine = new Machine("MEK7222");

        // Load rules for MEK7222
        machine.getRule().load();

        // Build node tree with parsed rules
        machine.getRule().build();

        return machine;
    }

    private static MachineFactory factory
            = new MachineFactory();
}
