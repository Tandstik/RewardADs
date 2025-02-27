package it.nathanub.rewardads.Spigot.Tools.Version;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import it.nathanub.rewardads.Spigot.Tools.Api.Api;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

import it.nathanub.rewardads.Spigot.Tools.Logs.Error;
import it.nathanub.rewardads.SpigotMain;
import org.bukkit.Bukkit;

public class Version {
    private final SpigotMain plugin;
    private final String code;
    private String versionNumber;

    public Version(SpigotMain plugin) {
        this.plugin = plugin;
        this.code = plugin.getConfig().getString("code");
    }

    public void checkForUpdate() {
        String ANSI_GREEN = "\u001B[32m";
        String ANSI_YELLOW = "\u001B[33m";
        String ANSI_RESET = "\u001B[0m";
        CompletableFuture.supplyAsync(() -> {
            try {
                String response = Api.handle("https://api.spiget.org/v2/resources/121867/versions/latest").get();
                JsonElement jsonElement = new JsonParser().parse(response);
                JsonObject jsonObject = jsonElement.getAsJsonObject();
                String currentVersion = jsonObject.get("name").getAsString();
                this.versionNumber = currentVersion.split(" ")[0].substring(1);

                return Objects.equals(versionNumber, getPlugin());
            } catch(Exception e) {
                Error.send(this.code, e);
                return false;
            }
        }).thenAccept(upToDate -> {
            if(!upToDate)
                plugin.getLogger().warning("Keep me updated! Download the latest version from https://spi.rewardads.it.");
            else
                plugin.getLogger().info(plugin.safeTranslate(ANSI_GREEN + "You're UpToDate to latest version: " + ANSI_YELLOW + "v" + versionNumber + ANSI_RESET));
        });
    }

    public boolean isServerHigher() {
        String version = Bukkit.getBukkitVersion().split("-")[0];
        String[] parts = version.split("\\.");

        int ver = Integer.parseInt(parts[1]);
        return ver >= 13;
    }

    public String getPlugin() {
        return this.plugin.getDescription().getVersion();
    }
}
