
public abstract  class FakeGenerator {

	public FakeGenerator(Machine machine) {
		this.machine = machine;
	}

	/**
	 * Override that method to generate fake data for
	 * current machine
	 * @throws Exception
	 */
	public abstract void generate() throws Exception;

	/**
	 * @return - Fake generator's machine
	 */
	public Machine getMachine() {
		return machine;
	}

	private Machine machine;
}
