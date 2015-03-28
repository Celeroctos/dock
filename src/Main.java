
public class Main {

    public static void main(String[] args) throws Exception {

        // Create machine instance for MEK7222
        final Machine machine = MachineFactory.getFactory().create("Mek7222");

		Logger.getLogger().write(machine, "Loading and building rules for machine");

        // Load rules and build machine tree
        machine.getRule().load();
        machine.getRule().build();

		Logger.getLogger().write(machine, "Starting thread for machine");

        // Run machine's receiver for current format and
        // create new laboratory and run it
        Thread thread = new Thread(machine.getReceiver());

        if (Config.FAKE_EMULATOR) {

            // Start thread asynchronously
            thread.start();

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
            thread.join();

        } else {

            // Start thread synchronously
            thread.run();
        }

		Logger.getLogger().write(machine, "Terminating application");
    }
}