package it.nathanub.rewardads.Bungee.Tools.Accounts;

import com.google.gson.JsonObject;
import it.nathanub.rewardads.BungeeMain;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.config.Configuration;
import org.bukkit.command.Command;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Verify {
    private final FileConfiguration userConfig;
    private final Configuration messageConfig;
    private final BungeeMain plugin;
    private final File userFile;

    public Verify(BungeeMain plugin, Configuration messageConfig) throws IOException {
        File userFile = new File(plugin.getDataFolder(), "userdata.yml");
        if (!userFile.exists()) {
            if (userFile.createNewFile()) {
                plugin.getLogger().info("Created new userdata.yml file.");
            } else {
                plugin.getLogger().warning("Failed to create userdata.yml file.");
            }
        }

        YamlConfiguration yamlConfiguration = YamlConfiguration.loadConfiguration(userFile);
        if (!yamlConfiguration.isConfigurationSection("users")) {
            yamlConfiguration.createSection("users");
            yamlConfiguration.save(userFile);
        }

        this.plugin = plugin;
        this.userConfig = yamlConfiguration;
        this.messageConfig = messageConfig;
        this.userFile = userFile;
    }

    public String verifyPlatform(ProxiedPlayer player, String token, String platform_id) throws IOException {
        Map<String, Object> formData = new HashMap<>();
        formData.put("platform_id", platform_id);
        formData.put("token", token);

        JsonObject response = Api.handleApi("verify-platform", formData);
        if (response == null) {
            throw new IllegalStateException("API response is null");
        }

        String message = response.has("message") ? response.get("message").getAsString() : "Unknown error";
        String platformId = response.has("platform_id") ? response.get("platform_id").getAsString() : "";

        if (platformId.isEmpty()) {
            throw new IllegalStateException("Platform ID not found in response");
        }

        UUID uuid = player.getUniqueId();
        String userPath = "users." + uuid;

        // Check if the player is already verified
        if (this.userConfig.contains(userPath + ".verified") && this.userConfig.getBoolean(userPath + ".verified")) {
            message = plugin.safeTranslate(messageConfig.getString("platform.alreadyVerified"));
            return message;
        }

        // Set the verification data
        this.userConfig.set(userPath + ".id", platformId);
        this.userConfig.set(userPath + ".verified", true);
        saveUserData();

        return message;
    }

    private void saveUserData() throws IOException {
        this.userConfig.save(this.userFile);
    }
}
