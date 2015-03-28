import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class Connection {

	public enum Action {

		UNKNOWN("unknown"),
		LOGIN("login"),
		LOGOUT("logout"),
		DO("do"),
		TEST("test");

		/**
		 * Construct enumeration with it's name
		 * @param name - Name of action
		 */
		private Action(String name) {
			this.name = name;
		}

		/**
		 * Get name of action
		 * @return - Action's name
		 */
		public String getName() {
			return name;
		}

		private String name;
	}

	/**
	 * Construct connection class without any actions
	 * @param host - Server's host with API controller
	 * @throws Exception
	 */
	public Connection(Machine machine, String host) throws Exception {
		this(machine, host, null, null);
	}

	/**
	 * Construct connection class with host, login and password
	 * @param host - Server's host with API controller
	 * @param login - User's login
	 * @param password - User's password
	 * @throws Exception
	 */
	public Connection(Machine machine, String host, String login, String password) throws Exception {

		action = Action.UNKNOWN;

		if (!host.endsWith("/")) {
			host = host + "/";
		}

		this.machine = machine;
		this.host = host;
		this.password = password;
		this.login = login;

		if (password != null && login != null) {
			login(login, password);
		}
	}

	/**
	 * Connect to laboratory server
	 * @param login - User's login
	 * @param password - User's password
	 * @throws Exception
	 */
	public synchronized boolean login(String login, String password) throws Exception {

		this.login = login;
		this.password = password;

		String response = Request.sendGet(getUrl(Action.LOGIN),
			new HashMap<String, Object>() {{
				put("login", Connection.this.login);
				put("password", Connection.this.password);
			}}
		);

		JSONObject json = getResponse(response);

		if (json.has("session")) {
			session = json.getString("session");
		} else {
			session = null;
		}

		return json.has("status") && json.getBoolean("status");
	}

	/**
	 * Prepare json object with parsed response from server
	 * @param response - Response message
	 * @return - Json object with parsed response
	 * @throws Exception
	 */
	private JSONObject getResponse(String response) throws Exception {

		int brace;
		if ((brace = response.indexOf("{")) != -1 && response.charAt(0) != '{') {
			response = response.substring(brace);
		}

		JSONObject json = new JSONObject(response);

		if (json.has("message")) {
			message = json.getString("message");
		}

		if (json.has("status") && !json.getBoolean("status")) {
			throw new Exception("Error received from API server \"" + json.getString("message") + "\"");
		} else if (json.has("message")) {
			Logger.getLogger().write(getMachine(), json.getString("message") + " - " + host);
		}

		return json;
	}

	/**
	 * Close connection with API server
	 * @throws Exception
	 */
	public synchronized void logout() throws Exception {

		if (session == null) {
			throw new Exception("Session hasn't been started");
		}

		String response = Request.sendGet(getUrl(Action.LOGOUT),
			new HashMap<String, Object>() {{
				put("session", session);
			}}
		);

		JSONObject json = getResponse(response);

		session = null;
	}

	/**
	 * Send fake request to server to test current connection and it's rights
	 * @throws Exception
	 */
	public synchronized boolean test() throws Exception {

		if (session == null) {
			throw new Exception("Session hasn't been started");
		}

		String response = Request.sendGet(getUrl(Action.TEST),
			new HashMap<String, Object>() {{
				put("session", session);
			}}
		);

		return getResponse(response).getBoolean("status");
	}

	/**
	 * Send request to server's API to provide some action
	 * @param path - Path to action to provide
	 * @param parameters - Map with parameters to receive
	 * @return - Json object with response
	 * @throws Exception
	 */
	public synchronized JSONObject make(final String path, final Map<String, Object> parameters) throws Exception {

		if (session == null) {
			throw new Exception("Session hasn't been started");
		}

		// Put connection's parameters to URL to send it via GET method
		String url = Request.buildUrlForGet(getUrl(Action.DO), new HashMap<String, Object>() {{
			put("session", getSession());
			put("path", path);
		}});

		// Send response to server via GET method
		return getResponse(Request.sendPost(url, parameters));
	}

	/**
	 * Create url for current action
	 * @param action - Action's type
	 * @return - Url for current action
	 */
	private String getUrl(Action action) {
		return host + (this.action = action).getName();
	}

	/**
	 * Get name of server's host
	 * @return - Server's host name
	 */
	public String getHost() {
		return host;
	}

	/**
	 * Get user's password
	 * @return - User's password
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * Get user's login
	 * @return - User's login
	 */
	public String getLogin() {
		return login;
	}

	/**
	 * Get session's identifier after login
	 * @return - Session's identifier
	 */
	public String getSession() {
		return session;
	}

	/**
	 * Get name of last performed action
	 * @return - Last performed action
	 */
	public Action getAction() {
		return action;
	}

	/**
	 * Get message with last error or something else
	 * @return - Last received message
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * Get machine for current connection (only for logs)
	 * @return - Reference to machine
	 */
	public Machine getMachine() {
		return machine;
	}

	private Machine machine;
	private String host;
	private String password;
	private String login;
	private String session;
	private Action action;
	private String message;
}
