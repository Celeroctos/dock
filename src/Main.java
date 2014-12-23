
public class Main {

    public static void main(String[] args) throws Exception {

        // Create machine instance for MEK7222
        Machine machine = MachineFactory.getFactory().createMek7222();

        // Create fake instance for generator and emulator
        Fake fake = FakeFactory.getFactory().createMek7222(machine);

        // Generate, load and emulate fake data for MEK7222 machine
        fake.getGenerator().generate();
        fake.getEmulator().emulate();

        // Sleep a bit
        try {
            Thread.sleep(250);
        } catch (InterruptedException ignored) {
        }

        // Run receiver for every format
        for (int i = 0; i < 6; i++) {
            new Thread(machine.getReceiver()).start();
        }

        // Wait for emulator (it will provide infinity loop)
        fake.getEmulator().await();
    }
}