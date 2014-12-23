
public interface Emulator extends Runnable {

	/**
	 * Override that method to emulate fake machine work
	 * @throws Exception
	 */
	public void emulate() throws Exception;

	/**
	 * Override that method to interrupt emulation process
	 * @throws Exception
	 */
	public void interrupt() throws Exception;

	/**
	 * Override that method to await while emulator finish
	 * @throws Exception
	 */
	public void await() throws Exception;
}
