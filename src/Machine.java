
public class Machine {

    /**
     * Construct machine instance with it's name, it can be used later,
     * for example, to load config files or receive data from specific
     * source, etc
     * @param name - Machine's name
     */
    public Machine(String name) {
        this.name = name;
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
    public RuleLoader getLoader() {
        return loader;
    }

    /**
     * @return - Builder
     */
    public RuleBuilder getBuilder() {
        return builder;
    }

    /**
     * @return - Name
     */
    public String getName() {
        return name;
    }

    private Receiver receiver
        = new Receiver(this);

    private RuleLoader loader
        = new RuleLoader(this);

    private RuleBuilder builder
        = new RuleBuilder(this);

    private String name;
}
