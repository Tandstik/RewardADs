package it.nathanub.rewardads.Velocity.Tools.Version;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import it.nathanub.rewardads.Velocity.Tools.Api.Api;
import it.nathanub.rewardads.Velocity.Tools.Logs.Error;
import it.nathanub.rewardads.VelocityMain;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

public class Version {
    private final VelocityMain plugin;

    private final String code;

    public Version(VelocityMain plugin) {
        this.plugin = plugin;
        this.code = plugin.getConfig("code");
    }

    public void checkForUpdate() {
        CompletableFuture.supplyAsync(() -> {
            try {
                String response = Api.handle("https://api.spigotmc.org/simple/0.2/index.php?action=getResource&id=121867").get();
                JsonElement jsonElement = (new JsonParser()).parse(response);
                JsonObject jsonObject = jsonElement.getAsJsonObject();
                String currentVersion = jsonObject.get("current_version").getAsString();
                return Boolean.valueOf(Objects.equals(currentVersion, getPlugin()));
            } catch (Exception e) {
                Error.send(this.code, e);
                return Boolean.valueOf(false);
            }
        }).thenAccept(upToDate -> {
            if (!upToDate.booleanValue())
                VelocityMain.getInstance().getLogger().warn("Keep me updated! Download the latest version from https://spi.rewardads.it.");
        });
    }

    public String getPlugin() {
        return this.plugin.getVersion();
    }
}
