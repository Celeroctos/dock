
public abstract class Loader {

    /**
     * Construct loader with your machine instance
     * @param machine - Reference to machine
     */
    public Loader(Machine machine) {
        this.machine = machine;
    }

    /**
     * Implement that method to load some specific data from
     * filesystem, similar to receiver and builder
     * @throws Exception
     */
    public abstract void load() throws Exception;

    /**
     * Every loader have to load data from somewhere, so implement
     * that method to show from which relative path loader have
     * to load data
     * @return - Path to loader's data
     * @throws Exception
     */
    public abstract String getFolder() throws Exception;

    /**
     * Get loader's machine instance
     * @return - Machine's instance
     */
    public Machine getMachine() {
        return machine;
    }

    private Machine machine;
}
