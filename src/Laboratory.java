
public class Laboratory {

	/**
	 * Construct laboratory with your machine
	 * @param machine - Reference to machine
	 */
	public Laboratory(Machine machine) {
		this.machine = machine;
	}

	public void send() {

	}

	/**
	 * @return - Reference to laboratory's machine
	 */
	public Machine getMachine() {
		return machine;
	}

	private Machine machine;
}
