import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class RequestManager {

    public static interface Callback {
        public void success(String response);
        public void error(Exception e);
    }

    public static void sendGetAsync(final String url, final HashMap<String, Object> data, final Callback callback) throws Exception {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    callback.success(sendGet(url, data));
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
                    callback.success(sendPost(url, data));
                } catch (Exception e) {
                    callback.error(e);
                }
            }
        }).start();
    }

    public static String sendGet(String url, HashMap<String, Object> data) throws Exception{

        // Build get url link
        for (Map.Entry<String, Object> i : data.entrySet()) {
            url += i.getKey() + "=" + i.getValue().toString();
        }
        if (data.size() > 0) {
            url = url.substring(url.length() - 1);
        }

        // Create url with link
        URL requestUrl = new URL(url);

        // Open url connection
        HttpURLConnection httpURLConnection = ((HttpURLConnection) requestUrl.openConnection());

        // Get request method and property
        httpURLConnection.setRequestMethod("GET");
        httpURLConnection.setRequestProperty("User-Agent", USER_AGENT);

        // Get response code
        int responseCode = httpURLConnection.getResponseCode();

        BufferedReader bufferedReader = new BufferedReader(
            new InputStreamReader(httpURLConnection.getInputStream())
        );
        StringBuilder response = new StringBuilder();
        String line;

        // Read response
        while ((line = bufferedReader.readLine()) != null) {
            response.append(line);
        }
        bufferedReader.close();

        // Return response
        return response.toString();
    }

    public static String sendPost(String url, HashMap<String, Object> data) throws Exception {

        String urlParameters = "";

        // Build post url parameters
        for (Map.Entry<String, Object> i : data.entrySet()) {
            urlParameters += i.getKey() + "=" + i.getValue().toString();
        }
        if (data.size() > 0) {
            urlParameters = urlParameters.substring(urlParameters.length() - 1);
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
        int responseCode = httpURLConnection.getResponseCode();

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

    private static final String USER_AGENT = "Mozilla/5.0";
}
