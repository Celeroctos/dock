
public class Main {

    public static void main(String[] args) throws Exception {

        // Create machine instance for MEK7222
        Machine machine = MachineFactory.getFactory().createMek7222();

        // Create fake instance for generator and emulator
        Fake fake = FakeFactory.getFactory().createMek7222(machine);

        // Generate, load and emulate fake data for MEK7222 machine
        fake.getGenerator().generate();
        fake.getEmulator().load();
        fake.getEmulator().emulate();
    }
}