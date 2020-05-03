package tm.fantom.simpledrawer.net;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import tm.fantom.simpledrawer.BuildConfig;


public class NetMenu {

    private static byte[] streamToByteArray(InputStream inputStream) throws IOException {
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            while (true) {
                int r = inputStream.read(buffer);
                if (r == -1) break;
                out.write(buffer, 0, r);
            }
            return out.toByteArray();
        } finally {
            inputStream.close();
        }
    }

    private static byte[] getByteArrayFromRemote() throws IOException, HttpException {
        URL url = new URL("https://www.dropbox.com/s/fk3d5kg6cptkpr6/menu.json?dl=1");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setRequestMethod("GET");
        try {
            int responseCode = connection.getResponseCode();
            if (responseCode != HttpURLConnection.HTTP_OK) {
                final String errorResponse = streamToString(connection.getErrorStream());
                if (BuildConfig.DEBUG) System.out.println("STREAMS ERROR: " + errorResponse);
                throw new HttpException(responseCode, errorResponse);
            }
            final byte[] byteArray = streamToByteArray(connection.getInputStream());
            if (BuildConfig.DEBUG) System.out.println("STREAMS RESPONSE " + new String(byteArray));
            return byteArray;
        } finally {
            connection.disconnect();
        }
    }

    private static String streamToString(InputStream inputStream) throws IOException {
        return new String(streamToByteArray(inputStream));
    }

    private static String getString() throws IOException, HttpException {
        return new String(getByteArrayFromRemote());
    }

    private static JSONObject getJsonObject() throws IOException, JSONException, HttpException {
        return new JSONObject(getString());
    }

    public static List<NetMenuItem> getMenuItems() throws IOException, JSONException, HttpException {
        final JSONObject response = getJsonObject();
        final List<NetMenuItem> list = new ArrayList<>();
        if (response.has("menu")) {
            JSONArray jsonArray = response.getJSONArray("menu");
            if (jsonArray.length() > 0) {
                for (int i = 0; i < jsonArray.length(); i++) {
                    list.add(NetMenuItem.fromJsonObj(jsonArray.getJSONObject(i)));
                }
            }
        }
        return list;
    }
}
