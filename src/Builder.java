
public abstract class Builder {

    /**
     * Construct builder with current machine
     * @param machine - Reference to current it's machine instance
     */
    public Builder(Machine machine) {
        this.machine = machine;
    }


    /**
     * Get builder's machine instance
     * @return - Reference to machine
     */
    public Machine getMachine() {
        return machine;
    }

    private Machine machine;
}
