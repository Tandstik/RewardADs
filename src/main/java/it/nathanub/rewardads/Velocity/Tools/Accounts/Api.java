package it.nathanub.rewardads.Velocity.Tools.Accounts;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public class Api {
    private static final String BASE_URL = "https://apiaccounts.rewardads.it/api/";

    public static JsonObject handleApi(String endpoint, Map<String, Object> formData) throws IOException {
        URL url = new URL("https://apiaccounts.rewardads.it/api/" + endpoint);
        HttpURLConnection connection = (HttpURLConnection)url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setDoOutput(true);
        Gson gson = new Gson();
        String jsonInputString = gson.toJson(formData);
        OutputStream os = connection.getOutputStream();
        byte[] input = jsonInputString.getBytes(StandardCharsets.UTF_8);
        os.write(input, 0, input.length);
        BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8));
        StringBuilder response = new StringBuilder();
        String responseLine;
        while ((responseLine = br.readLine()) != null)
            response.append(responseLine.trim());
        br.close();
        return (JsonObject)(new Gson()).fromJson(response.toString(), JsonObject.class);
    }
}
