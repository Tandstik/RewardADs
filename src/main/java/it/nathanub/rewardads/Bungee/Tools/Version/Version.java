package it.nathanub.rewardads.Bungee.Tools.Version;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import it.nathanub.rewardads.Bungee.Tools.Api.Api;
import it.nathanub.rewardads.Bungee.Tools.Logs.Error;
import it.nathanub.rewardads.BungeeMain;
import net.md_5.bungee.api.plugin.Plugin;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;

public class Version {
    private final Plugin plugin;
    private final String code;

    public Version(BungeeMain plugin) {
        this.plugin = plugin;
        this.code = plugin.getConfig().getString("code");
    }

    public void checkForUpdate() {
        CompletableFuture.supplyAsync(() -> {
            try {
                String response = Api.handle("https://api.spigotmc.org/simple/0.2/index.php?action=getResource&id=121867").get();
                JsonElement jsonElement = new JsonParser().parse(response);
                JsonObject jsonObject = jsonElement.getAsJsonObject();
                String currentVersion = jsonObject.get("current_version").getAsString();
                return Objects.equals(currentVersion, getPlugin());
            } catch(Exception e) {
                Error.send(this.code, e);
                return false;
            }
        }).thenAccept(upToDate -> {
            if(!upToDate)
                plugin.getLogger().warning("Keep me updated! Download the latest version from https://spi.rewardads.it.");
        });
    }

    public String getPlugin() {
        return this.plugin.getDescription().getVersion();
    }
}
