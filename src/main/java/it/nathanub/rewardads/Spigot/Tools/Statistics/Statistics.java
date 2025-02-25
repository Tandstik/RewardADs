package it.nathanub.rewardads.Spigot.Tools.Statistics;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import it.nathanub.rewardads.Spigot.Tools.Accounts.Link;
import it.nathanub.rewardads.Spigot.Tools.Api.Api;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.bukkit.plugin.Plugin;

public class Statistics {
    private final Plugin plugin;
    private final String code;

    public Statistics(Plugin plugin) {
        this.code = plugin.getConfig().getString("code");
        this.plugin = plugin;
    }

    public List<String> topAdbits() throws IOException, ExecutionException, InterruptedException {
        Future<String> future = Api.handle("topadbits");
        Link link = new Link(this.plugin);
        String response = future.get();

        if(response == null || response.trim().isEmpty()) {
            return Collections.emptyList();
        }

        JsonElement jsonElement = new JsonParser().parse(response);

        if(!jsonElement.isJsonArray()) {
            return Collections.emptyList();
        }

        JsonArray jsonArray = jsonElement.getAsJsonArray();
        List<String> result = new ArrayList<>();

        for(JsonElement element : jsonArray) {
            if(!element.isJsonObject()) continue;

            JsonObject jsonObject = element.getAsJsonObject();
            if(!jsonObject.has("id_player") || !jsonObject.has("adbits_player")) continue;

            String idPlayer = jsonObject.get("id_player").getAsString();
            String adbitsPlayer = jsonObject.get("adbits_player").getAsString();

            if(link.isLinked(idPlayer))
                result.add(idPlayer + "," + adbitsPlayer);
        }

        return result;
    }

    public List<String> topBuys() throws IOException, ExecutionException, InterruptedException, TimeoutException {
        Future<String> future = Api.handle("topbuys/" + code);
        String response = future.get(10, TimeUnit.SECONDS);

        if (response == null || response.trim().isEmpty()) {
            return Collections.emptyList();
        }

        JsonElement jsonElement = new JsonParser().parse(response);

        if (!jsonElement.isJsonArray()) {
            return Collections.emptyList();
        }

        JsonArray jsonArray = jsonElement.getAsJsonArray();
        List<String> result = new ArrayList<>();

        for(JsonElement element : jsonArray) {
            if(!element.isJsonObject()) continue;

            JsonObject jsonObject = element.getAsJsonObject();
            if(!jsonObject.has("id_player") || !jsonObject.has("total_buys")) continue;

            String idPlayer = jsonObject.get("id_player").getAsString();
            String totalBuys = jsonObject.get("total_buys").getAsString();

            result.add(idPlayer + "," + totalBuys);
        }

        return result;
    }
}
