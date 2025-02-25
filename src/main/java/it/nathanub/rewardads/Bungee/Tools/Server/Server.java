package it.nathanub.rewardads.Bungee.Tools.Server;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import it.nathanub.rewardads.Bungee.Tools.Api.Api;
import it.nathanub.rewardads.BungeeMain;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class Server {
    private final String code;

    public Server(BungeeMain plugin) {
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
