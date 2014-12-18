public class Machine {

    public Machine(String name) {
        this.name = name;
    }

    public Loader getLoader() {
        return loader;
    }

    public Builder getBuilder() {
        return builder;
    }

    public String getName() {
        return name;
    }

    private Loader loader
        = new Loader(this);

    private Builder builder
        = new Builder(this);

    private String name;
}
