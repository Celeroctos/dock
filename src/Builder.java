
public abstract class Builder {

    /**
     * Construct builder with current machine
     * @param machine - Reference to current it's machine instance
     */
    public Builder(Machine machine) {
        this.machine = machine;
    }

    /**
     * Override this method to build you own rule file, which has been
     * loaded via abstract loader instance or received from some
     * protocol via receiver instance
     * @throws Exception
     */
    public abstract void build() throws Exception;

    /**
     * Get builder's machine instance
     * @return - Reference to machine
     */
    public Machine getMachine() {
        return machine;
    }

    private Machine machine;
}
