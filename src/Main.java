
public class Main {

    public static void main(String[] args) throws Exception {

        // Create machine instance for MEK7222
        final Machine machine = MachineFactory.getFactory().create("Mek7222");

        // Load rules and build machine tree
        machine.getRule().load();
        machine.getRule().build();

        // Run machine's receiver for current format and
        // create new laboratory and run it
        new Thread(machine.getReceiver()).start();

        // Sleep a bit
        try {
            Thread.sleep(100);
        } catch (InterruptedException ignored) {
        }

        // Create fake instance for generator and emulator
        Fake fake = FakeFactory.getFactory().createMek7222(machine);

        // Generate, load and emulate fake data for MEK7222 machine
        fake.getGenerator().generate();
        fake.getEmulator().emulate();

        // Wait for emulator
        fake.getEmulator().await();
    }
}