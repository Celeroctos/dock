
public class Machine {

    /**
     * Construct machine instance with default receiver and rule
     * @param name - Machine's name
     */
    public Machine(String name) {
        this(name, null, null);
    }

    /**
     * Construct machine instance with it's name, it can be used later,
     * for example, to load config files or receive data from specific
     * source, etc
     * @param name - Machine's name
     */
    public Machine(String name, Receiver receiver, AbstractRule rule) {

        this.name = name;

        if (receiver == null) {
            this.receiver = new Receiver(this);
        } else        {
            this.receiver = receiver;
        }

        if (rule == null) {
            this.rule = new Rule(this);
        } else {
            this.rule = rule;
        }
    }

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

    private Receiver receiver;
    private AbstractRule rule;
    private String name;
}
