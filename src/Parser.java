
public class Parser {

    /**
     * Construct parser with your machine instance
     * @param machine - Reference to machine
     */
    public Parser(Machine machine) {
        this.machine = machine;
    }

    /**
     * Parse input bytes
     * @param bytes - Bytes to parse
     */
    public void parse(byte[] bytes) {

        Rule rule = ((Rule) getMachine().getRule());
        Node root = rule.getRoot();


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
