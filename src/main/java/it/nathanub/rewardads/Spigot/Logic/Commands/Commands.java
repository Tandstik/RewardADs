package it.nathanub.rewardads.Spigot.Logic.Commands;

import it.nathanub.rewardads.Spigot.Tools.Accounts.Verify;
import it.nathanub.rewardads.Spigot.Tools.Logs.Error;
import it.nathanub.rewardads.Spigot.Tools.Server.Server;

import it.nathanub.rewardads.Spigot.Tools.Version.Version;
import it.nathanub.rewardads.SpigotMain;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class Commands implements CommandExecutor {
    private final SpigotMain plugin;

    private final FileConfiguration messageConfig;
    private final String code;

    public Commands(SpigotMain plugin, FileConfiguration messageConfig) {
        this.messageConfig = messageConfig;
        this.plugin = plugin;
        this.code = plugin.getConfig().getString("code");
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, String[] args) {
        try {
            String prefix = plugin.safeTranslate(messageConfig.getString("prefix"));

            if(command.getName().equalsIgnoreCase("rewardads")) {
                if(args.length == 0) {
                    if(sender instanceof Player) {
                        String version = plugin.getDescription().getVersion();
                        Server server = new Server(plugin);
                        Player player = (Player) sender;

                        if (server.isValid()) {
                            player.sendMessage(prefix + plugin.safeTranslate(Objects.requireNonNull(messageConfig.getString("server-using-rewardads")).replace("%server%", server.getName()).replace("%version%", version)));
                        } else {
                            player.sendMessage(prefix + "§cRewardADs is not configured, contact an administrator.");
                        }
                    } else {
                        String version = plugin.getDescription().getVersion();
                        Server server = new Server(plugin);

                        if(server.isValid()) {
                            sender.sendMessage("§fYour server is using §6RewardADs §7v" + version + "§f!");
                        } else {
                            sender.sendMessage("§cRewardADs is not configured, do it!");
                        }
                    }

                    return true;
                } else if(args[0].equalsIgnoreCase("reload")) {
                    if(sender instanceof Player) {
                        if(!sender.hasPermission("rewardads.reload")) {
                            sender.sendMessage(plugin.safeTranslate(messageConfig.getString("no-permission")));
                            return true;
                        }
                        Player player = (Player) sender;

                        plugin.reloadConfig();
                        plugin.reloadMessages();
                        player.sendMessage(prefix + plugin.safeTranslate(messageConfig.getString("reloaded")));
                    } else {
                        plugin.reloadConfig();
                        sender.sendMessage(prefix + plugin.safeTranslate(messageConfig.getString("reloaded")));
                    }
                } else if(args[0].equalsIgnoreCase("version")) {
                    Version version = new Version(plugin);
                    Player player = (Player) sender;

                    player.sendMessage(prefix + "§7v" + version.getPlugin());
                } else if(args[0].equalsIgnoreCase("error")) {
                    throw new IllegalStateException("User configuration is not loaded.");
                }
            } else if(command.getName().equalsIgnoreCase("confirm")) {
                if(args.length > 1) {
                    String token = args[0];
                    String platform_id = args[1];
                    Player player = (Player) sender;

                    Verify verify = new Verify(plugin, messageConfig);
                    player.sendMessage(prefix + verify.verifyPlatform(player, token, platform_id));
                    plugin.getLogger().info(player.getName() + " Verified platform!");
                } else {
                    sender.sendMessage(prefix + plugin.safeTranslate(messageConfig.getString("platform.tokenOrId")));
                }
            }

            return false;
        } catch(Exception e) {
            sender.sendMessage(plugin.safeTranslate(messageConfig.getString("error")));
            Error.send(this.code, e);
            return false;
        }
    }
}
