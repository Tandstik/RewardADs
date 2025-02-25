package it.nathanub.rewardads.Velocity.Tools.Server;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import it.nathanub.rewardads.Velocity.Tools.Api.Api;
import it.nathanub.rewardads.VelocityMain;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class Server {
    private final String code;

    public Server(VelocityMain plugin) {
        this.code = plugin.getConfig("code");
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
        JsonArray jsonArray = (new JsonParser()).parse(response).getAsJsonArray();
        return (jsonArray.size() > 0);
    }

    public String getCode() {
        return this.code;
    }
}
