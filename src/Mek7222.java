import org.json.JSONObject;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class Mek7222 extends Machine {

	static {
		MachineFactory.getFactory().register(
			Mek7222.class
		);
	}

	/**
	 * Construct machine with default constructor
	 */
	public Mek7222(String name) {
		super(name);
	}

	/**
	 * Override that method to create parser for your machine's data (it have to implement rule data parse)
	 * @return - Parser's instance
	 */
	@Override
	public Parser createParser() {

		final Map<Integer, String> formatMap = new LinkedHashMap<Integer, String>() {{
			put(10, "order-information");
			put(11, "order-information-ok");
			put(12, "order-information-ng");
			put(20, "measurement-data");
			put(21, "measurement-data-ok");
			put(22, "measurement-data-ng");
		}};

		return new Parser() {
			@Override
			public void parse(byte[] bytes) throws Exception {

				Node root = getRule().getRoot();
				ByteBuffer buffer = ByteBuffer.wrap(bytes);

				byte[] lengthBuffer = new byte[
						root.find("outline/length").getLength()
					];

				byte[] formatBuffer = new byte[
						root.find("outline/format").getLength()
					];

				buffer.get(lengthBuffer);
				buffer.get(formatBuffer);

				int length = Integer.parseInt(new String(lengthBuffer));
				int format = Integer.parseInt(new String(formatBuffer));

				root.find("outline/format").setValue(Integer.toString(format));
				root.find("outline/length").setValue(Integer.toString(length));

				root.get(formatMap.get(format)).read(buffer);
			}
		};
	}

	/**
	 * Override that method to create your own rule for current machine
	 * @return - Rule's instance
	 */
	@Override
	public AbstractRule createRule() {
		return new Rule(this);
	}

	/**
	 * Override that method to create machine's laboratory instance, it will create new laboratory to send received and
	 * parsed data from machine to LIS
	 * @return - Laboratory instance
	 */
	@Override
	public Laboratory createLaboratory() {

		return new Laboratory(this) {

			@Override
			public void send() throws Exception {

				// Write log
				Logger.getLogger().write(getMachine(), "Sending request to server");

				// Open connection with server
				Connection connection = new Connection(
					getMachine(), getMachine().getRule().getHost()
				);

				// Login with login and password from rule
				connection.login(
					getMachine().getRule().getLogin(),
					getMachine().getRule().getPassword()
				);

				if (connection.getSession() == null) {
					throw new Exception("Unable to connect to server API");
				}

				// Clone root node
				root = getMachine().getRule().getRoot().clone();

				JSONObject json = connection.make("laboratory/register",
					new HashMap<String, Object>() {{
						put("model", root.serialize());
					}}
				);

				if (json.has("status") && !json.getBoolean("status")) {
					Logger.getLogger().write(getMachine(), "Error occurred while sending data to laboratory");
				}
			}
		};
	}

	/**
	 * Override that method to create machine's receiver, which will receive data from DMS-PC
	 * @return - Receiver's instance
	 */
	@Override
	public Receiver createReceiver() {
		return new Receiver(this);
	}
}
