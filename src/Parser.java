
public abstract class Parser {

    /**
     * Construct parser with your machine instance
     * @param machine - Reference to machine
     */
    public Parser(Machine machine) {
        this.machine = machine;
    }



    /**
     * Get parser's machine instance
     * @return - Reference to machine
     */
    public Machine getMachine() {
        return machine;
    }

    private Machine machine;
}
