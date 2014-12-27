
public abstract class Machine {

    /**
     * Construct machine with name and parser, don't send
     * machine's name from adapter to super class
     * @param name - Machine's name
     */
    protected Machine(String name) {
        this.name = name;
        this.laboratory = createLaboratory();
        this.parser = createParser();
        this.receiver = createReceiver();
        this.rule = createRule();
    }

    /**
     * Override that method to create parser for your machine's
     * data (it have to implement rule data parse)
     * @return - Parser's instance
     */
    public abstract Parser createParser();

    /**
     * Override that method to create your own rule for
     * current machine
     * @return - Rule's instance
     */
    public abstract AbstractRule createRule();

    /**
     * Override that method to create machine's laboratory instance,
     * it will create new laboratory to send received and parsed data
     * from machine to LIS
     * @return - Laboratory instance
     */
    public abstract Laboratory createLaboratory();

    /**
     * Override that method to create machine's receiver, which will receive
     * data from DMS-PC
     * @return - Receiver's instance
     */
    public abstract Receiver createReceiver();

    /**
     * @return - Receiver
     */
    public Receiver getReceiver() {
        return receiver;
    }

    /**
     * @return - Loader
     */
    public AbstractRule getRule() {
        return rule;
    }

    /**
     * @return - Name
     */
    public String getName() {
        return name;
    }

    /**
     * @return - Parser
     */
    public Parser getParser() {
        return parser;
    }

    /**
     * @return - Laboratory
     */
    public Laboratory getLaboratory() {
        return laboratory;
    }

    private Receiver receiver;
    private AbstractRule rule;
    private String name;
    private Parser parser;
    private Laboratory laboratory;
}
