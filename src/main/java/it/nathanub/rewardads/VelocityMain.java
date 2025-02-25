package it.nathanub.rewardads;

import com.google.inject.Inject;
import com.velocitypowered.api.command.Command;
import com.velocitypowered.api.command.CommandManager;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.PluginContainer;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.messages.ChannelIdentifier;
import com.velocitypowered.api.proxy.messages.MinecraftChannelIdentifier;
import com.velocitypowered.api.scheduler.ScheduledTask;
import com.velocitypowered.api.scheduler.TaskStatus;
import it.nathanub.rewardads.Velocity.Logic.Commands.Commands;
import it.nathanub.rewardads.Velocity.Logic.Commands.ConfirmCommand;
import it.nathanub.rewardads.Velocity.Logic.Requests;
import it.nathanub.rewardads.Velocity.Tools.Server.Server;
import it.nathanub.rewardads.Velocity.Tools.Version.Version;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.FileAttribute;
import java.util.Optional;
import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.slf4j.Logger;

@Plugin(id = "rewardads", name = "RewardADs", version = "5.5", authors = {"Wiinup, Nathanub"})
public class VelocityMain {
    private static VelocityMain instance;

    private final ProxyServer server;

    private final Logger logger;

    private final Path dataDirectory;

    private Properties messageConfig;

    private Properties userData;

    private Properties config;

    private Path dataFolder;

    private ScheduledTask task;

    private final ChannelIdentifier channel;

    @Inject
    public VelocityMain(ProxyServer server, Logger logger, @DataDirectory Path dataDirectory) {
        this.server = server;
        this.logger = logger;
        this.dataDirectory = dataDirectory;
        this.channel = (ChannelIdentifier)MinecraftChannelIdentifier.create("rewardads", "channel");
        this.server.getChannelRegistrar().register(new ChannelIdentifier[] { this.channel });
        this.logger.info("Canale di messaggi registrato: " + this.channel.getId());
        instance = this;
    }

    public void startTask() {
        this

                .task = this.server.getScheduler().buildTask(this, () -> (new Requests(this)).run()).repeat(1L, TimeUnit.SECONDS).schedule();
    }

    public void stopTask() {
        if (this.task != null && this.task.status() == TaskStatus.SCHEDULED)
            this.task.cancel();
    }

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) throws ExecutionException, InterruptedException {
        this.logger.info("Library RewardADs (Velocity) enabled!");
        createConfigFiles();
        loadConfig();
        loadMessages();
        loadUserData();
        registerCommands();
        startTask();
        Server platform = new Server(this);
        Version version = new Version(this);
        this.logger.info("Checking for 'rewardads' plugin in PluginManager...");
        Optional<PluginContainer> pluginOptional = this.server.getPluginManager().getPlugin("rewardads");
        if (pluginOptional.isEmpty()) {
            this.logger.error("Error: The 'rewardads' plugin was not found in PluginManager!");
            this.dataFolder = this.dataDirectory;
            return;
        }
        PluginContainer plugin = pluginOptional.get();
        Optional<Path> sourceOptional = plugin.getDescription().getSource();
        if (sourceOptional.isEmpty()) {
            this.logger.warn("Warning: Plugin source folder is empty. Using the default data directory.");
            this.dataFolder = this.dataDirectory;
        } else {
            this.dataFolder = sourceOptional.get();
            this.logger.info("Data folder set to: " + this.dataFolder.toString());
        }
        if (platform.isValid()) {
            this.logger.info("Welcome back " + platform.getName() + " to RewardADs!");
            version.checkForUpdate();
        } else {
            String message = getMessage("invalid-code");
            this.logger.info(message + "\033[0m");
        }
    }

    private void createConfigFiles() {
        try {
            Files.createDirectories(this.dataDirectory, (FileAttribute<?>[])new FileAttribute[0]);
            copyDefaultConfig("velocity.yml");
            copyDefaultConfig("messages_velocity.yml");
            copyDefaultConfig("userdata.yml");
        } catch (IOException e) {
            this.logger.error("Error creating configuration files", e);
        }
    }

    private void copyDefaultConfig(String fileName) throws IOException {
        Path filePath = this.dataDirectory.resolve(fileName);
        if (Files.notExists(filePath, new java.nio.file.LinkOption[0])) {
            InputStream inputStream = getClass().getResourceAsStream("/" + fileName);
            try {
                if (inputStream != null) {
                    Files.copy(inputStream, filePath, new CopyOption[] { StandardCopyOption.REPLACE_EXISTING });
                } else {
                    this.logger.warn("Default " + fileName + " not found in the plugin JAR.");
                }
                if (inputStream != null)
                    inputStream.close();
            } catch (Throwable throwable) {
                if (inputStream != null)
                    try {
                        inputStream.close();
                    } catch (Throwable throwable1) {
                        throwable.addSuppressed(throwable1);
                    }
                throw throwable;
            }
        }
    }

    public void loadMessages() {
        Path messagesPath = this.dataDirectory.resolve("messages_velocity.yml");
        this.messageConfig = new Properties();
        if (Files.exists(messagesPath, new java.nio.file.LinkOption[0]))
            try {
                InputStream inputStream = Files.newInputStream(messagesPath, new java.nio.file.OpenOption[0]);
                try {
                    this.messageConfig.load(inputStream);
                    if (inputStream != null)
                        inputStream.close();
                } catch (Throwable throwable) {
                    if (inputStream != null)
                        try {
                            inputStream.close();
                        } catch (Throwable throwable1) {
                            throwable.addSuppressed(throwable1);
                        }
                    throw throwable;
                }
            } catch (IOException e) {
                this.logger.error("Failed to load messages_velocity.yml", e);
            }
    }

    public void loadConfig() {
        Path configPath = this.dataDirectory.resolve("velocity.yml");
        this.config = new Properties();
        if (Files.exists(configPath, new java.nio.file.LinkOption[0]))
            try {
                InputStream inputStream = Files.newInputStream(configPath, new java.nio.file.OpenOption[0]);
                try {
                    this.config.load(inputStream);
                    if (inputStream != null)
                        inputStream.close();
                } catch (Throwable throwable) {
                    if (inputStream != null)
                        try {
                            inputStream.close();
                        } catch (Throwable throwable1) {
                            throwable.addSuppressed(throwable1);
                        }
                    throw throwable;
                }
            } catch (IOException e) {
                this.logger.error("Failed to load velocity.yml", e);
            }
    }

    public void loadUserData() {
        Path userPath = this.dataDirectory.resolve("userdata.yml");
        this.userData = new Properties();
        if (Files.exists(userPath, new java.nio.file.LinkOption[0]))
            try {
                InputStream inputStream = Files.newInputStream(userPath, new java.nio.file.OpenOption[0]);
                try {
                    this.userData.load(inputStream);
                    if (inputStream != null)
                        inputStream.close();
                } catch (Throwable throwable) {
                    if (inputStream != null)
                        try {
                            inputStream.close();
                        } catch (Throwable throwable1) {
                            throwable.addSuppressed(throwable1);
                        }
                    throw throwable;
                }
            } catch (IOException e) {
                this.logger.error("Failed to load velocity.yml", e);
            }
    }

    public ProxyServer getProxy() {
        return this.server;
    }

    public ChannelIdentifier getMessageChannel() {
        return this.channel;
    }

    private void registerCommands() {
        CommandManager commandManager = this.server.getCommandManager();
        commandManager.register("confirm", (Command)new ConfirmCommand(this, this.messageConfig, this.userData), new String[0]);
        commandManager.register("rewardads", (Command)new Commands(this, this.messageConfig), new String[0]);
    }

    public Path getDataFolder() {
        return this.dataDirectory;
    }

    public static VelocityMain getInstance() {
        return instance;
    }

    public Logger getLogger() {
        return this.logger;
    }

    public String getVersion() {
        return getConfig("version");
    }

    public Component safeTranslate(String message) {
        if (message == null || message.isEmpty())
            return (Component)Component.empty();
        return (Component)LegacyComponentSerializer.legacyAmpersand().deserialize(message);
    }

    public String getMessage(String key) {
        return this.messageConfig.getProperty(key, "Message not found");
    }

    public String getConfig(String key) {
        return this.config.getProperty(key, "Config not found");
    }
}
