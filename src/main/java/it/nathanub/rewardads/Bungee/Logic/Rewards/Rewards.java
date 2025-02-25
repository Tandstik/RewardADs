package it.nathanub.rewardads.Bungee.Logic.Rewards;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import it.nathanub.rewardads.Bungee.Tools.Api.Api;
import it.nathanub.rewardads.BungeeMain;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class Rewards {
    private final String code;
    private final Gson gson;

    public Rewards(BungeeMain plugin) {
        this.code = plugin.getConfig().getString("code");
        this.gson = new Gson();
    }

    public String getList() throws ExecutionException, InterruptedException {
        Future<String> future = Api.handle("getbuys/" + this.code);
        List<JsonObject> rewardsList = new ArrayList<>();

        String response = future.get();
        if(response != null) {
            JsonElement jsonElement = this.gson.fromJson(response, JsonElement.class);

            if(jsonElement.isJsonArray()) {
                JsonArray jsonArray = jsonElement.getAsJsonArray();
                processRewardsArray(jsonArray, rewardsList);
            } else if(jsonElement.isJsonObject()) {
                JsonObject jsonObject = jsonElement.getAsJsonObject();

                if(jsonObject.has("rewards") && jsonObject.get("rewards").isJsonArray()) {
                    JsonArray jsonArray = jsonObject.getAsJsonArray("rewards");
                    processRewardsArray(jsonArray, rewardsList);
                }
            }
        }

        return this.gson.toJson(rewardsList);
    }

    private void processRewardsArray(JsonArray jsonArray, List<JsonObject> rewardsList) {
        for(JsonElement element : jsonArray) {
            if(element.isJsonObject()) {
                JsonObject rewardObject = element.getAsJsonObject();
                JsonObject filteredReward = new JsonObject();
                addPropertyIfExists(filteredReward, rewardObject, "id", "id_reward");
                addPropertyIfExists(filteredReward, rewardObject, "name", "name_reward");
                addPropertyIfExists(filteredReward, rewardObject, "cost", "cost_reward");
                addPropertyIfExists(filteredReward, rewardObject, "user", "id_user");
                addPropertyIfExists(filteredReward, rewardObject, "player", "minecraft_user");
                addPropertyIfExists(filteredReward, rewardObject, "status", "status_reward");
                addPropertyIfExists(filteredReward, rewardObject, "code", "id_server");
                addPropertyIfExists(filteredReward, rewardObject, "quantity", "quantity");
                rewardsList.add(filteredReward);
            }
        }
    }

    private void addPropertyIfExists(JsonObject filtered, JsonObject original, String filteredKey, String originalKey) {
        if (original.has(originalKey) && !original.get(originalKey).isJsonNull())
            filtered.addProperty(filteredKey, original.get(originalKey).getAsString());
    }
}
