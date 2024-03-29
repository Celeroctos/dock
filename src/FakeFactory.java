import com.sun.org.apache.xerces.internal.impl.dv.util.HexBin;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Vector;

public class FakeFactory {

	/**
	 * Locked constructor
	 */
	private FakeFactory() {
		/* Locked */
	}

	/**
	 * @return - Fake factory
	 */
	public static FakeFactory getFactory() {
		return fakeFactory;
	}

	/**
	 * Create fake generator for MEK7222
	 * @param machine - MEK7222 machine
	 * @return - Generator
	 * @throws Exception
	 */
	public Fake createMek7222(final Machine machine) throws Exception {

		if (!machine.getName().toUpperCase().equals("MEK7222")) {
			throw new Exception("Illegal machine \"" + machine.getName() + "\"");
		}

		final Map<Integer, String> formatMap = new LinkedHashMap<Integer, String>() {{
			put(10, "order-information");
			put(11, "order-information-ok");
			put(12, "order-information-ng");
			put(20, "measurement-data");
			put(21, "measurement-data-ok");
			put(22, "measurement-data-ng");
		}};

		final Node root = machine.getRule().getRoot();

		Generator generator = new Generator() {

			public void generate(int f) throws Exception {

				ByteArrayOutputStream stream
					= new ByteArrayOutputStream();

				Node outline = root.get("outline");
				Node format = root.get(formatMap.get(f));

				write(stream, outline.get("length"), Integer.toString(format.getLength()));
				write(stream, outline.get("format"), Integer.toString(f));

				for (Node node : format.getChildren()) {
					write(stream, node);
				}

				flush(machine, stream, Integer.toString(f));
			}

			@Override
			public void generate() throws Exception {

				for (int format : formatMap.keySet()) {
					generate(format);
				}
			}
		};

		Rule rule = ((Rule) machine.getRule());

		final Rule.SocketInfo receiveInfo = rule.getReceiveInfo();
		final Rule.SocketInfo sendInfo = rule.getSendInfo();

		Emulator emulator = new Emulator() {

			class Session implements Runnable {

				public Session(Socket socket, int format) {
					this.socket = socket;
					this.format = format;
				}

				@Override
				public void run() {
					try {
						socket.getOutputStream().write(read(machine,
							Integer.toString(format)
						));
					} catch (Exception ignored) {
					}
				}

				private Socket socket;
				private int format;
			}

			@Override
			public void run() {
				Vector<Thread> threads = new Vector<Thread>(formatMap.size());
				for (int format : formatMap.keySet()) {
					try {
						Thread t = new Thread(new Session(
							new Socket(
								receiveInfo.getHost(),
								receiveInfo.getPort()
							), format
						));
						t.start();
						threads.add(t);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				for (Thread t : threads) {
					t.run();
				}
			}

			@Override
			public void emulate() throws Exception {
				run();
			}

			@Override
			public void interrupt() throws Exception {
				/* Ignore */
			}

			@Override
			public void await() throws Exception {
				/* Ignore */
			}
		};

		return new Fake(generator, emulator);
	}

	/**
	 * Read fake data from file
	 * @param machine - Machine instance
	 * @param name - Fake data name
	 * @throws Exception
	 */
	private byte[] read(Machine machine, String name) throws Exception {

		File dir = new File("fake" + File.separator + machine.getName().toLowerCase());

		if (!dir.exists()) {
			throw new Exception("Can't load fake data file for \"" + machine.getName() + "\"");
		}

		File handle = new File(dir.getPath() + File.separator + name);

		if (!handle.exists()) {
			throw new Exception("Can't load fake data file for \"" + machine.getName() + "\"");
		}

		InputStream file = new FileInputStream(
			handle
		);

		byte[] bytes = new byte[(int) handle.length()];

		if (file.read(bytes) != bytes.length) {
			throw new Exception("Can't load fake data file for \"" + machine.getName() + "\"");
		}

		return bytes;
	}

	/**
	 * Write result stream to output file
	 * @param machine- Machine instance
	 * @param stream - ByteArray stream
	 * @param name - Fake data name
	 * @throws Exception
	 */
	private void flush(Machine machine, ByteArrayOutputStream stream, String name) throws Exception {

		File dir = new File("fake" + File.separator + machine.getName().toLowerCase());

		if (!dir.exists() && !dir.mkdirs()) {
			throw new Exception("Can't create directories for fake data for \"" + machine.getName() + "\"");
		}

		OutputStream file = new FileOutputStream(new File(
			dir.getPath() + File.separator + name
		));
		stream.writeTo(file);

		file.flush();
		file.close();
	}

	/**
	 * Write random data in stream
	 * @param stream - Stream
	 * @param node - Node
	 * @throws Exception
	 */
	private void write(OutputStream stream, Node node) throws Exception {

		if (node.getChildren().size() > 0) {
			for (Node child : node.getChildren()) {
				write(stream, child);
			}
		} else {
			if (node.getFixed() >= 0) {
				write(stream, node, Integer.toString(node.getFixed()));
			} else {
				write(stream, node, "");
			}
		}
	}

	/**
	 * Write random data with default value
	 * @param stream - Stream
	 * @param node - Node
	 * @param value - Default value
	 * @throws Exception
	 */
	private void write(OutputStream stream, Node node, String value) throws Exception {

		if (value.length() < node.getLength()) {
			if (node.getCast().toLowerCase().equals("string") || value.length() == 0) {
				if ((node.getLength() % 2) != 0) {
					value = randomFake(node.getLength())
						.substring(value.length()) + value + "#";
				} else {
					value = randomFake(node.getLength())
						.substring(value.length()) + value;
				}
			} else {
				value = zeroFake(node.getLength()).substring(value.length()) + value;
			}
		}

		stream.write(value.getBytes());
	}

	/**
	 * Generate random zero values
	 * @param length - Count of zero values
	 * @return - Generated data
	 * @throws Exception
	 */
	public static String zeroFake(int length) throws Exception {
		byte[] bytes = new byte[length];
		Arrays.fill(bytes, (byte) '0');
		return new String(bytes);
	}

	/**
	 * Generate random bytes
	 * @param length - Count of random bytes
	 * @return - Generated data
	 * @throws Exception
	 */
	public static String randomFake(int length) throws Exception {
		SecureRandom secureRandom = new SecureRandom();
		byte[] bytes = new byte[length / 2];
		secureRandom.nextBytes(bytes);
		byte[] seed = secureRandom.generateSeed(length / 2);
		return HexBin.encode(seed);
	}

	private static FakeFactory fakeFactory
		= new FakeFactory();
}
