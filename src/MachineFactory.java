import java.util.HashMap;

public class MachineFactory implements Factorisable<Machine> {

	/**
	 * @return - Machine factory instance
	 */
	public static MachineFactory getFactory() {
		return factory;
	}

	/**
	 * Can't construct machine factory
	 */
	private MachineFactory() {
		super();
	}

	/**
	 * Construct new machine instance by it's name
	 * @param name - Machine's name
	 * @return - Machine's instance
	 */
	public Machine create(String name) throws Exception {

		// Try to load class with machine
		Class.forName(name);

		// Find key with machine and construct it's instance
		if (map.containsKey(name)) {
			try {
				return map.get(name).getConstructor(name.getClass()).newInstance(name);
			} catch (InstantiationException e) {
				throw ((Exception) e.getCause());
			}
		}

		// Will never happen (i hope)
		return null;
	}

	/**
	 * Register new machine in factory
	 * @param name - Machine's name
	 * @param machine - Machine's class
	 */
	public void register(String name, Class<? extends Machine> machine) {
		if (!map.containsKey(name)) {
			map.put(name, machine);
		}
	}

	private HashMap<String, Class<? extends Machine>> map
		= new HashMap<String, Class<? extends Machine>>();

	private static MachineFactory factory
		= new MachineFactory();
}
