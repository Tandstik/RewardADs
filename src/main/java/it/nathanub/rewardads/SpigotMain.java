package it.nathanub.rewardads;

import it.nathanub.rewardads.Spigot.Logic.Requests;
import it.nathanub.rewardads.Spigot.Logic.BungeeListener;
import it.nathanub.rewardads.Spigot.Logic.Commands.Commands;
import it.nathanub.rewardads.Spigot.Logic.Events;
import it.nathanub.rewardads.Spigot.Tools.Logs.Error;
import it.nathanub.rewardads.Spigot.Tools.Server.Server;
import it.nathanub.rewardads.Spigot.Tools.Version.Version;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

public class SpigotMain extends JavaPlugin {
    private final Server server = new Server(this);
    public FileConfiguration userConfig;
    private final Version version = new Version(this);
    private static SpigotMain instance;

    private File messagesFile;
    public FileConfiguration messageConfig;

    @Override
    public void onEnable() {
        instance = this;

        try {
            saveDefaultConfig();
            saveDefaultMessages();
            loadMessages();
            if(!getConfig().getBoolean("isBungee") && server.isValid()) {
                Objects.requireNonNull(this.getCommand("rewardads")).setExecutor(new Commands(this, messageConfig));
                Objects.requireNonNull(this.getCommand("confirm")).setExecutor(new Commands(this, messageConfig));
                getServer().getPluginManager().registerEvents(new Events(this), this);
                createUserData();
                getLogger().info("Library RewardADs (Spigot) enabled!");
                getLogger().info("Welcome back " + server.getName() + " to RewardADs!");
                new Requests(this).runTaskTimer(this, 0, 200);
            } else if(!server.isValid()) {
                getLogger().severe(safeTranslate(messageConfig.getString("invalid-code")));
            } else {
                getServer().getMessenger().registerIncomingPluginChannel(this, "rewardads:channel", new BungeeListener(this));
                getServer().getMessenger().registerOutgoingPluginChannel(this, "rewardads:channel");

                // Debugging: Print both incoming and outgoing channels
                getLogger().info("Registered incoming channels: " + String.join(", ", this.getServer().getMessenger().getIncomingChannels()));
                getLogger().info("Registered outgoing channels: " + String.join(", ", this.getServer().getMessenger().getOutgoingChannels()));
                getLogger().info("Library RewardADs (Spigot & Bungee) enabled!");
                getLogger().info("Welcome back " + server.getName() + " to RewardADs!");
            }
            version.checkForUpdate();
        } catch(Exception e) {
            Error.send(server.getCode(), e);
        }
    }

    public void saveDefaultMessages() {
        if (messagesFile == null) {
            messagesFile = new File(getDataFolder(), "messages.yml");
        }

        if (!messagesFile.exists()) {
            saveResource("messages.yml", false);
            getLogger().info("messages.yml creato!");
        }
    }

    public void loadMessages() {
        messagesFile = new File(getDataFolder(), "messages.yml");
        messageConfig = YamlConfiguration.loadConfiguration(messagesFile);
    }

    public void saveMessages() {
        try {
            messageConfig.save(messagesFile);
        } catch (IOException e) {
            getLogger().severe("Error to save messages.yml!");
            e.printStackTrace();
        }
    }

    // Ricarica messages.yml da comando o codice
    public void reloadMessages() throws IOException {
        messageConfig = YamlConfiguration.loadConfiguration(messagesFile);

        // Aggiorna Commands e Link con la nuova configurazione
        Objects.requireNonNull(this.getCommand("rewardads")).setExecutor(new Commands(this, messageConfig));
        Objects.requireNonNull(this.getCommand("confirm")).setExecutor(new Commands(this, messageConfig));
        getServer().getPluginManager().registerEvents(new Events(this), this);
        getLogger().info("messages reloaded!");
    }

    private void createUserData() throws IOException {
        File userFile = new File(getDataFolder(), "userdata.yml");

        if(!userFile.exists()) {
            getDataFolder().mkdirs();
            userFile.createNewFile();
        }

        userConfig = YamlConfiguration.loadConfiguration(userFile);
    }

    public String safeTranslate(String message) {
        if (message == null) {
            return ""; // or some default value
        }
        return ChatColor.translateAlternateColorCodes('&', message);
    }

    @Override
    public void onDisable() {
        getLogger().info("Library RewardADs (Spigot) disabled!");
    }

    public static SpigotMain getInstance() {
        return instance;
    }
}
