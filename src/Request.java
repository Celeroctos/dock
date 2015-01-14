import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.GZIPInputStream;

public class Request {

    static {
        System.setProperty("http.agent", "");
    }

    public abstract static class Callback {

        /**
         * THat method will raise after success request sent
         * @param response - Text with message
         */
        public abstract void ok(String response);

        /**
         * Override that method to catch an error asynchronously
         * @param exception - Exception object
         */
        public abstract void error(Exception exception);

        /**
         * Get request which has been appended to current callback
         * @return - Request
         */
        public Request getRequest() {
            return request;
        }

        private Request request;
    }

    public abstract static class Ok extends Callback {
        @Override
        public void error(Exception exception) {
            /* Ignore */
        }
    }

    public abstract static class Error extends Callback {
        @Override
        public void ok(String response) {
            /* Ignore */
        }
    }

    public class Response {

        /**
         * Construct response with message and error code
         * @param message - Response message
         * @param code - Http error code
         */
        public Response(String message, int code) {
            this.message = message;
            this.code = code;
        }

        /**
         * Get message message
         * @return - Response message
         */
        public String getMessage() {
            return message;
        }

        /**
         * Get message code
         * @return - Response code
         * @see java.net.HttpURLConnection#getResponseCode
         */
        public int getCode() {
            return code;
        }

        String message;
        private int code;
    }

    public enum Method {
        GET,
        POST
    }

    /**
     * Construct request with url and hash data, request will be send automatically
     * synchronously
     * @param url - Link to server
     * @param data - Data to send
     * @throws Exception
     */
    public Request(String url, HashMap<String, Object> data) throws Exception {
        this(url, Method.POST, data, null);
    }

    /**
     * Construct request for asynchronous request it will raise "or" or "error" method
     * after perform
     * @param url - Link to server
     * @param method - Send method
     * @param data - Data to send
     * @param callback - Object with 2 callbacks on error and ok, it will be raised
     *                 after perform, if callback is null, then it will be send
     *                 asynchronously
     * @throws Exception
     */
    public Request(String url, Method method, HashMap<String, Object> data, Callback callback) throws Exception {

        if (callback != null) {
            callback.request = this;
        }

        this.url = url;
        this.method = method;
        this.data = data;
        this.callback = callback;

        send();
    }

    /**
     * Send request manually (btw it will be send in constructor, be careful)
     */
    public void send() throws Exception {
        switch (method) {
            case GET:
                if (callback != null) {
                    sendGetAsync(url, data, callback);
                } else {
                    sendGet(url, data);
                }
                break;
            case POST:
                if (callback != null) {
                    sendPostAsync(url, data, callback);
                } else {
                    sendPost(url, data);
                }
                break;
            default:
                throw new Exception("Can't resolve send method (Not-Implemented)");
        }
    }

    /**
     * @return - Request url
     */
    public String getUrl() {
        return url;
    }

    /**
     * @return - Request method
     */
    public Method getMethod() {
        return method;
    }

    /**
     * @return - Request data
     */
    public HashMap<String, Object> getData() {
        return data;
    }

    /**
     * @return - Request callback
     */
    public Callback getCallback() {
        return callback;
    }

    private String url;
    private Method method;
    private HashMap<String, Object> data;
    private Callback callback;

    public static void sendGetAsync(final String url, final HashMap<String, Object> data, final Callback callback) throws Exception {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    callback.ok(sendGet(url, data));
                } catch (Exception e) {
                    callback.error(e);
                }
            }
        }).start();
    }

    public static void sendPostAsync(final String url, final HashMap<String, Object> data, final Callback callback) throws Exception {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    callback.ok(sendPost(url, data));
                } catch (Exception e) {
                    callback.error(e);
                }
            }
        }).start();
    }

    public static String sendGet(String url, HashMap<String, Object> data) throws Exception{

        int responseCode;

        if (!url.endsWith("?")) {
            url += "?";
        }

        // Build get url link
        for (Map.Entry<String, Object> i : data.entrySet()) {
            url += i.getKey() + "=" + i.getValue().toString() + "&";
        }
        if (data.size() > 0) {
            url = url.substring(0, url.length() - 1);
        }

        // Create url with link
        URL requestUrl = new URL(url);

        // Open url connection
        HttpURLConnection httpURLConnection = ((HttpURLConnection) requestUrl.openConnection());

        // Get request method and property
        httpURLConnection.setRequestMethod("GET");
        httpURLConnection.addRequestProperty("User-Agent", USER_AGENT);

        // Get response code
        if ((responseCode = httpURLConnection.getResponseCode()) != 200) {
            throw new Exception(httpURLConnection.getResponseMessage() + " (" + responseCode + ")");
        }

        InputStream inputStream = httpURLConnection.getInputStream();

        if("gzip".equals(httpURLConnection.getContentEncoding())){
            inputStream = new GZIPInputStream(inputStream);
        }

        BufferedReader bufferedReader = new BufferedReader(
            new InputStreamReader(inputStream)
        );
        StringBuilder response = new StringBuilder();
        String line;

        // Read response
        while ((line = bufferedReader.readLine()) != null) {
            response.append(line + "\n");
        }
        bufferedReader.close();

        // Return response
        return response.toString();
    }

    public static String sendPost(String url, HashMap<String, Object> data) throws Exception {

        int responseCode;
        String urlParameters = "";

        // Build post url parameters
        for (Map.Entry<String, Object> i : data.entrySet()) {
            urlParameters += i.getKey() + "=" + i.getValue().toString();
        }
        if (data.size() > 0) {
            urlParameters = urlParameters.substring(0, urlParameters.length() - 1);
        }

        // Create url with link
        URL requestUrl = new URL(url);

        // Open url connection
        HttpURLConnection httpURLConnection = ((HttpURLConnection) requestUrl.openConnection());

        // Add request headers
        httpURLConnection.setRequestMethod("POST");
        httpURLConnection.setRequestProperty("User-Agent", USER_AGENT);
        httpURLConnection.setRequestProperty("Accept-Language", "en-US,en;q=0.5");

        // Send request
        httpURLConnection.setDoOutput(true);

        DataOutputStream dataOutputStream = new DataOutputStream(
            httpURLConnection.getOutputStream()
        );

        dataOutputStream.writeBytes(urlParameters);
        dataOutputStream.flush();
        dataOutputStream.close();

        // Get response code
        if ((responseCode = httpURLConnection.getResponseCode()) != 200) {
            throw new Exception(httpURLConnection.getResponseMessage() + " (" + responseCode + ")");
        }

        BufferedReader bufferedReader = new BufferedReader(
            new InputStreamReader(httpURLConnection.getInputStream())
        );
        StringBuilder response = new StringBuilder();
        String line;

        while ((line = bufferedReader.readLine()) != null) {
            response.append(line);
        }
        bufferedReader.close();

        return response.toString();
    }

    private static final String USER_AGENT = "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:25.0) Gecko/20100101 Firefox/25.0";
}
