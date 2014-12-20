
public abstract class Parser {

    /**
     * Construct parser with your machine instance
     * @param machine - Reference to machine
     */
    public Parser(Machine machine) {
        this.machine = machine;
    }

    /**
     * Implement that method to parse your machine's
     * specific data, which you've loaded or received via
     * loader, receiver or something else
     * @throws Exception
     */
    public abstract void parse() throws Exception;

    /**
     * Get parser's machine instance
     * @return - Reference to machine
     */
    public Machine getMachine() {
        return machine;
    }

    private Machine machine;
}
