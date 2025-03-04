package it.nathanub.rewardads.Velocity.Tools.Version;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import it.nathanub.rewardads.Spigot.Tools.Api.Api;
import it.nathanub.rewardads.Velocity.Tools.Logs.Error;
import it.nathanub.rewardads.VelocityMain;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

public class Version {
    private final VelocityMain plugin;

    private final String code;
    private String versionNumber;

    public Version(VelocityMain plugin) {
        this.plugin = plugin;
        this.code = plugin.getConfig("code");
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
            } catch (Exception e) {
                Error.send(this.code, e);
                return Boolean.FALSE;
            }
        }).thenAccept(upToDate -> {
            if (!upToDate)
                VelocityMain.getInstance().getLogger().warn("Keep me updated! Download the latest version from https://spi.rewardads.it.");
            else
                VelocityMain.getInstance().getLogger().info(ANSI_GREEN + "You're UpToDate to latest version: " + ANSI_YELLOW + "v" + versionNumber + ANSI_RESET);
        });
    }

    public String getPlugin() {
        return this.plugin.getVersion();
    }
}
