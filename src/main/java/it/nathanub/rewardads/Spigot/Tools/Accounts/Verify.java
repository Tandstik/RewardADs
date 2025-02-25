package it.nathanub.rewardads.Spigot.Tools.Accounts;

import com.google.gson.JsonObject;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import it.nathanub.rewardads.SpigotMain;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class Verify {
    private final FileConfiguration userConfig;
    private final FileConfiguration messageConfig;
    private final File userFile;
    private final SpigotMain plugin;

    public Verify(SpigotMain plugin, FileConfiguration messageConfig) throws IOException {
        File userFile = new File(plugin.getDataFolder(), "userdata.yml");
        if(!userFile.exists())
                if(userFile.createNewFile())
                    plugin.getLogger().info("Created new userdata.yml file.");

        YamlConfiguration yamlConfiguration = YamlConfiguration.loadConfiguration(userFile);
        if(!yamlConfiguration.isConfigurationSection("users")) {
            yamlConfiguration.createSection("users");
            yamlConfiguration.save(userFile);
        }

        this.userConfig = yamlConfiguration;
        this.plugin = plugin;
        this.messageConfig = messageConfig;
        this.userFile = userFile;
    }

    public String verifyPlatform(Player player, String token, String platform_id) throws IOException {
        Map<String, Object> formData = new HashMap<>();
        formData.put("platform_id", platform_id);
        formData.put("token", token);

        JsonObject response = Api.handleApi("verify-platform", formData);
        if(response == null) {
            throw new IllegalStateException("API response is null");
        }

        String message = response.has("message") ? response.get("message").getAsString() : "Unknown error";

        String platformId = response.get("platform_id").getAsString();
        UUID uuid = player.getUniqueId();
        String userPath = "users." + uuid;

        if(this.userConfig.contains(userPath + ".verified") && this.userConfig.getBoolean(userPath + ".verified")) {
            message = plugin.safeTranslate(messageConfig.getString("platform.alreadyVerified"));
            return message;
        }

        this.userConfig.set(userPath + ".id", platformId);
        this.userConfig.set(userPath + ".verified", true);
        saveUserData();

        return message;
    }


    private void saveUserData() throws IOException {
        this.userConfig.save(this.userFile);
    }
}
