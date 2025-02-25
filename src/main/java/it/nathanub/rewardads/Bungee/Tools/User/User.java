package it.nathanub.rewardads.Bungee.Tools.User;

import com.google.gson.*;
import it.nathanub.rewardads.Bungee.Tools.Api.Api;
import it.nathanub.rewardads.BungeeMain;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.config.YamlConfiguration;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class User {
    private final BungeeMain plugin;

    public User(BungeeMain plugin) {
        this.plugin = plugin;
    }

    public String getId(ProxiedPlayer player) throws IOException {
        if(player == null) return null;
        File userFile = new File(this.plugin.getDataFolder(), "userdata.yml");
        if(!userFile.exists()) userFile.createNewFile();

        Yaml yaml = new Yaml();
        FileInputStream fileInputStream = new FileInputStream(userFile);
        Map<String, Object> data = yaml.load(fileInputStream);

        if(data != null && data.containsKey("users") && data.get("users") instanceof Map) {
            @SuppressWarnings("unchecked")
            Map<String, Object> users = (Map<String, Object>) data.get("users");

            String playerId = (String) users.get(player.getUniqueId().toString());
            return playerId != null ? playerId : "Player ID not found";
        } else {
            return "Users section not found";
        }
    }

    public String getName(ProxiedPlayer player) throws ExecutionException, InterruptedException, ParseException, IOException {
        Future<String> future = Api.handle("getaccountbyid/" + getId(player));
        String response = future.get();

        JSONParser parser = new JSONParser();
        JSONObject jsonObject = (JSONObject)parser.parse(response);
        String name = (String)jsonObject.get("name_user");
        return (name == null) ? "" : name;
    }

    public String getName(String idPlayer) throws ExecutionException, InterruptedException, ParseException {
        Future<String> future = Api.handle("getaccountbyid/" + idPlayer);
        String response = future.get();

        JSONParser parser = new JSONParser();
        JSONObject jsonObject = (JSONObject)parser.parse(response);
        String name = (String)jsonObject.get("name_user");
        return (name == null) ? "" : name;
    }

    public int getAdbits(ProxiedPlayer player) throws IOException {
        Future<String> future = Api.handle("getadbits/" + getId(player));

        try {
            String response = future.get();

            if(response == null || response.isEmpty()) {
                return 0;
            }

            JsonElement jsonElement = new JsonParser().parse(response);

            if(!jsonElement.isJsonArray()) {
                return 0;
            }

            JsonArray jsonArray = jsonElement.getAsJsonArray();
            if(jsonArray.size() == 0) {
                return 0;
            }

            JsonObject jsonObject = jsonArray.get(0).getAsJsonObject();
            return jsonObject.has("adbits_player") ? jsonObject.get("adbits_player").getAsInt() : 0;
        } catch(ExecutionException | InterruptedException e) {
            this.plugin.getLogger().severe("Internal error: " + e.getMessage());
            return 0;
        } catch(JsonSyntaxException | IllegalStateException e) {
            this.plugin.getLogger().severe("Invalid JSON format: " + e.getMessage());
            return 0;
        }
    }

}
