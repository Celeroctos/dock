
public abstract class Laboratory implements Runnable {

	/**
	 * Construct laboratory with your machine
	 * @param machine - Reference to machine
	 */
	public Laboratory(Machine machine) {
		this.machine = machine;
	}

	/**
	 * When an object implementing interface <code>Runnable</code> is used to create a thread, starting the thread
	 * causes the object's <code>run</code> method to be called in that separately executing thread.
	 * <p/>
	 * The general contract of the method <code>run</code> is that it may take any action whatsoever.
	 * @see Thread#run()
	 */
	@Override
	public void run() {
		try {
			synchronized (getMachine()) {
				send();
			}
		} catch (Exception e) {
			e.printStackTrace();
			Logger.getLogger().write(getMachine(), e.getMessage());
		}
	}

	/**
	 * Override that method to send all received information
	 * from machine to LIS (laboratory module)
	 */
	public abstract void send() throws Exception;

	/**
	 * @return - Reference to laboratory's machine
	 */
	public Machine getMachine() {
		return machine;
	}

	/**
	 * @return - Root node
	 */
	public Node getRoot() {
		return root;
	}

	private Machine machine;
	protected Node root;
}
