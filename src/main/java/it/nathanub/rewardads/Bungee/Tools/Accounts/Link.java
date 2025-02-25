package it.nathanub.rewardads.Bungee.Tools.Accounts;

import it.nathanub.rewardads.BungeeMain;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.Objects;
import java.util.Set;

public class Link {
    private final BungeeMain plugin;
    private final Configuration userConfig;
    private final File userFile;

    public Link(BungeeMain plugin) throws IOException {
        File userFile = new File(plugin.getDataFolder(), "userdata.yml");
        if (!userFile.exists()) {
            // Ensure the parent directories exist
            if (userFile.getParentFile().mkdirs() || userFile.getParentFile().exists()) {
                if (userFile.createNewFile()) {
                    plugin.getLogger().info("Created new userdata.yml file.");
                } else {
                    plugin.getLogger().warning("Failed to create userdata.yml file.");
                }
            }
        }

        // Load the configuration using BungeeCord's API
        Configuration yamlConfiguration = ConfigurationProvider.getProvider(YamlConfiguration.class).load(userFile);
        if (yamlConfiguration.get("users") == null) {
            yamlConfiguration.set("users", new Configuration());
            ConfigurationProvider.getProvider(YamlConfiguration.class).save(yamlConfiguration, userFile);
        }

        this.plugin = plugin;
        this.userConfig = yamlConfiguration;
        this.userFile = userFile;
    }


    public boolean isLinked(Object value) {
        if (this.userConfig == null) {
            throw new IllegalStateException("User configuration is not loaded.");
        }
        if (this.userConfig.get("users") == null) {
            throw new IllegalStateException("'users' section is missing in the configuration.");
        }

        String targetValue;
        if (value instanceof ProxiedPlayer) {
            targetValue = ((ProxiedPlayer) value).getUniqueId().toString();
        } else if (value instanceof String) {
            targetValue = (String) value;
        } else {
            throw new IllegalArgumentException("Invalid type passed to isLinked method");
        }

        // Iterate over the keys in the 'users' section
        Configuration usersSection = this.userConfig.getSection("users");
        Set<String> keys = (Set<String>) usersSection.getKeys();
        for (String key : keys) {
            if (Objects.equals(usersSection.getString(key), targetValue)) {
                return true;
            }
        }
        return false;
    }

    private void saveUserData() throws IOException {
        ConfigurationProvider.getProvider(YamlConfiguration.class).save(this.userConfig, this.userFile);
    }
}
