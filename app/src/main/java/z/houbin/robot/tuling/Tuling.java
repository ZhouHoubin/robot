package z.houbin.robot.tuling;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class Tuling {
    private final String URL = "http://openapi.tuling123.com/openapi/api/v2";

    public interface CallBack {
        void onCall(List<String> values);
    }

    public List<String> request(String text) {
        List<String> result = new ArrayList<>();
        try {
            URL url = new URL(URL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            connection.connect();
            OutputStream os = connection.getOutputStream();
            os.write(getRequestJson(text).getBytes());
            os.flush();
            os.close();
            InputStream is = connection.getInputStream();
            String json = getText(is);
            is.close();
            connection.disconnect();
            JSONObject jsonObject = new JSONObject(json);
            JSONArray resultsArray = jsonObject.getJSONArray("results");
            for (int i = 0; i < resultsArray.length(); i++) {
                JSONObject resultObject = resultsArray.getJSONObject(i);
                String values = resultObject.getJSONObject("values").getString("text");
                result.add(values);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return result;
    }

    public void request(final String text, final CallBack callBack) {
        new Thread() {
            @Override
            public void run() {
                super.run();
                callBack.onCall(request(text));
            }
        }.start();
    }

    private String getText(InputStream inputStream) throws IOException {
        int len = 0;
        byte[] buffer = new byte[1204];
        StringBuilder builder = new StringBuilder();
        while ((len = inputStream.read(buffer)) != -1) {
            builder.append(new String(buffer, 0, len));
        }
        return builder.toString();
    }

    private String getRequestJson(String text) {
        return "{\n" +
                "\t\"reqType\":0,\n" +
                "    \"perception\": {\n" +
                "        \"inputText\": {\n" +
                "            \"text\": \"" + text + "\"\n" +
                "        }\n" +
                "    },\n" +
                "    \"userInfo\": {\n" +
                "        \"apiKey\": \"be24ad78b46c5c9549acf1f6d580c3bb\",\n" +
                "        \"userId\": \"20180510\"\n" +
                "    }\n" +
                "}";
    }
}
