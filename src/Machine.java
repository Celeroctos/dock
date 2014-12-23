
public class Machine {

    /**
     * Construct machine with name and parser
     * @param name - Machine's name
     */
    public Machine(String name) {
        this(name, null, null, null);
    }

    /**
     * Construct machine instance with it's name, it can be used later,
     * for example, to load config files or receive data from specific
     * source, etc
     * @param name - Machine's name
     */
    public Machine(String name, Receiver receiver, AbstractRule rule, Parser parser) {

        this.name = name;
        this.parser = parser;

        if (receiver == null) {
            this.receiver = new Receiver();
        } else {
            this.receiver = receiver;
        }
        this.receiver.setMachine(this);

        if (rule == null) {
            this.rule = new Rule();
        } else {
            this.rule = rule;
        }
        this.rule.setMachine(this);
    }

    /**
     * @return - Receiver
     */
    public Receiver getReceiver() {
        return receiver;
    }

    /**
     * @param receiver - Receiver
     */
    public void setReceiver(Receiver receiver) {
        this.receiver = receiver;
    }

    /**
     * @return - Loader
     */
    public AbstractRule getRule() {
        return rule;
    }

    /**
     * @param rule - Rule
     */
    public void setRule(AbstractRule rule) {
        this.rule = rule;
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
     * @param parser - Parser
     */
    public void setParser(Parser parser) {
        this.parser = parser;
    }

    private Receiver receiver;
    private AbstractRule rule;
    private String name;
    private Parser parser;
}
