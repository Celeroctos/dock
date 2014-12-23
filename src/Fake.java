
public class Fake {

	/**
	 * Construct fake class with generator and emulator
	 * @param generator
	 * @param emulator
	 */
	public Fake(Generator generator, Emulator emulator) {
		this.generator = generator;
		this.emulator = emulator;
	}

	/**
	 * @return - Generator
	 */
	public Generator getGenerator() {
		return generator;
	}

	/**
	 * @return - Emulator
	 */
	public Emulator getEmulator() {
		return emulator;
	}

	private Generator generator;
	private Emulator emulator;
}
