
public class Factory {

    private Factory() {
        /* Locked */
    }

    /**
     * Get singleton factory instance
     * @return - Factory instance
     */
    public static Factory getFactory() {
        return factory;
    }

    /**
     * Build machine for MEK7222
     * @return - Machine's instance
     * @throws Exception
     */
    public Machine createMek7222() throws Exception {

        Machine machine = new Machine("MEK7222");

        machine.getLoader().load();
        machine.getBuilder().build();

        return machine;
    }

    private static Factory factory
            = new Factory();
}
