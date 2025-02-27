package it.nathanub.rewardads.Spigot.Tools.User;

import com.google.gson.*;
import it.nathanub.rewardads.Spigot.Tools.Api.Api;
import java.io.File;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class User {
    private final Plugin plugin;

    public User(Plugin plugin) {
        this.plugin = plugin;
    }

    public String getId(Player player) {
        String playerName = player.getName();
        Future<String> response = Api.handle("getaccountbyminecraft/" + playerName);

        try {
            String jsonResponse = response.get();
            if (jsonResponse != null && !jsonResponse.isEmpty()) {
                if (jsonResponse.contains("\"id_user\"")) {
                    JsonObject jsonObject = JsonParser.parseString(jsonResponse).getAsJsonObject();

                    if (jsonObject.has("id_user")) {
                        String idUser = jsonObject.get("id_user").getAsString();
                        if(idUser != null && !idUser.trim().isEmpty()){
                            return idUser;
                        }
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return "null";
    }

    public String getUUId(String idPlayer) {
        if(idPlayer == null)
            return null;

        File userFile = new File(this.plugin.getDataFolder(), "userdata.yml");
        YamlConfiguration yamlConfiguration = YamlConfiguration.loadConfiguration(userFile);
        for(String uuid : yamlConfiguration.getConfigurationSection("users").getKeys(false)) {
            String id = yamlConfiguration.getString("users." + uuid);
            if(id != null && id.equals(idPlayer))
                return uuid;
        }

        return null;
    }

    public String getName(Player player) throws ExecutionException, InterruptedException, ParseException {
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

    public int getAdbits(Player player) {
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

    public int getBuys(Player player) {
        Future<String> future = Api.handle("getbuy/" + getId(player));

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
            return jsonObject.has("buys") ? jsonObject.get("buys").getAsInt() : 0;
        } catch(ExecutionException | InterruptedException e) {
            this.plugin.getLogger().severe("Internal error: " + e.getMessage());
            return 0;
        } catch(JsonSyntaxException | IllegalStateException e) {
            this.plugin.getLogger().severe("Invalid JSON format: " + e.getMessage());
            return 0;
        }
    }

}
