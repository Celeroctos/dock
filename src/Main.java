
public class Main {

    public static void main(String[] args) throws Exception {

        Machine machine = MachineFactory.getFactory().createMek7222();
        FakeGenerator fake = FakeFactory.getFactory().createMek7222(machine);

        fake.generate();
    }
}