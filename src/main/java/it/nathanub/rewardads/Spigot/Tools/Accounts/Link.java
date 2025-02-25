package it.nathanub.rewardads.Spigot.Tools.Accounts;

import java.io.File;
import java.io.IOException;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.Future;

import it.nathanub.rewardads.Spigot.Tools.Api.Api;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;

public class Link {
    private final Plugin plugin;

    private final FileConfiguration userConfig;

    private final File userFile;

    public Link(Plugin plugin) throws IOException {
        File userFile = new File(plugin.getDataFolder(), "userdata.yml");
        if(!userFile.exists())
            if(userFile.createNewFile())
                plugin.getLogger().info("Created new userdata.yml file.");

        YamlConfiguration yamlConfiguration = YamlConfiguration.loadConfiguration(userFile);
        if(!yamlConfiguration.isConfigurationSection("users")) {
            yamlConfiguration.createSection("users");
            yamlConfiguration.save(userFile);
        }

        this.plugin = plugin;
        this.userConfig = yamlConfiguration;
        this.userFile = userFile;
    }


    public boolean isLinked(String value) {
        Future<String> response = Api.handle("getaccountbyminecraft/" + value);

        try {
            String jsonResponse = response.get();
            if (jsonResponse != null && !jsonResponse.isEmpty()) {
                if (jsonResponse.contains("\"id_user\"")) {
                    return true;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }


    public boolean isLinked(Player player) {
        if(this.userConfig == null) {
            throw new IllegalStateException("User configuration is not loaded.");
        }
        if(!this.userConfig.isConfigurationSection("users")) {
            throw new IllegalStateException("'users' section is missing in the configuration.");
        }

        String playerUuid = player.getUniqueId().toString();
        return this.userConfig.contains("users." + playerUuid);
    }

    private void saveUserData() throws IOException {
        this.userConfig.save(this.userFile);
    }

}
