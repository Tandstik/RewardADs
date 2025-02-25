package it.nathanub.rewardads.Spigot.Tools.Server;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import it.nathanub.rewardads.Spigot.Tools.Api.Api;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import org.bukkit.plugin.Plugin;

public class Server {
    private final Plugin plugin;

    private final String code;

    public Server(Plugin plugin) {
        this.plugin = plugin;
        this.code = plugin.getConfig().getString("code");
    }

    public String getName() throws ExecutionException, InterruptedException {
        Future<String> future = Api.handle("getserver/" + this.code);
        String response = future.get();

        JsonArray jsonArray = (new JsonParser()).parse(response).getAsJsonArray();
        JsonObject jsonObject = jsonArray.get(0).getAsJsonObject();
        return jsonObject.get("name_server").getAsString();
    }

    public boolean isValid() throws ExecutionException, InterruptedException {
        Future<String> future = Api.handle("getserver/" + this.code);
        String response = future.get();

        JsonArray jsonArray = new JsonParser().parse(response).getAsJsonArray();
        return (jsonArray.size() > 0);
    }

    public String getCode() {
        return this.code;
    }
}
