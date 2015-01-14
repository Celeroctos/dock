import sun.awt.Mutex;

public class Main {

    public static void main(String[] args) throws Exception {

        // Create machine instance for MEK7222
        final Machine machine = MachineFactory.getFactory().create("Mek7222");

        // Load rules and build machine tree
        machine.getRule().load();
        machine.getRule().build();

        // Create fake instance for generator and emulator
        Fake fake = FakeFactory.getFactory().createMek7222(machine);

        // Generate, load and emulate fake data for MEK7222 machine
        fake.getGenerator().generate();
        fake.getEmulator().emulate();

        // Sleep a bit
        try {
            Thread.sleep(100);
        } catch (InterruptedException ignored) {
        }

        // Run receiver for every format
        for (int i = 0; i < 6; i++) {
            new Runnable() {
                @Override
                public void run() {
                    // Run machine's receiver for current format
                    machine.getReceiver().run();
                    // Create new laboratory and run it
                    machine.createLaboratory().run();
                }
            }.run();
        }

        // Wait for emulator
        fake.getEmulator().await();
    }
}