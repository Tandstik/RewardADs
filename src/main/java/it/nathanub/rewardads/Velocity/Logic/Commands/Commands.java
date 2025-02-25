package it.nathanub.rewardads.Velocity.Logic.Commands;

import com.velocitypowered.api.command.CommandInvocation;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.proxy.Player;
import it.nathanub.rewardads.Velocity.Tools.Logs.Error;
import it.nathanub.rewardads.Velocity.Tools.Server.Server;
import it.nathanub.rewardads.Velocity.Tools.Version.Version;
import it.nathanub.rewardads.VelocityMain;
import java.util.List;
import java.util.Objects;
import java.util.Properties;
import net.kyori.adventure.text.Component;

public class Commands implements SimpleCommand {
    private final VelocityMain plugin;

    private final String code;

    private final Properties messageConfig;

    public Commands(VelocityMain plugin, Properties messageConfig) {
        this.plugin = plugin;
        this.messageConfig = messageConfig;
        this.code = plugin.getConfig("code");
    }

    public void execute(SimpleCommand.Invocation invocation) {
        CommandSource sender = invocation.source();
        String[] args = (String[])invocation.arguments();
        try {
            Component prefix = this.plugin.safeTranslate(this.plugin.getMessage("prefix"));
            if (args.length == 0) {
                String version = this.plugin.getVersion();
                Server server = new Server(this.plugin);
                if (sender instanceof Player) {
                    Player player = (Player)sender;
                    if (server.isValid()) {
                        player.sendMessage(prefix.append(this.plugin.safeTranslate((
                                (String)Objects.<String>requireNonNull(this.plugin.getMessage("server-using-rewardads")))
                                .replace("%server%", server.getName())
                                .replace("%version%", version))));
                    } else {
                        player.sendMessage(prefix.append(this.plugin.safeTranslate("&cRewardADs is not configured, please contact an administrator")));
                    }
                } else if (server.isValid()) {
                    sender.sendMessage(this.plugin.safeTranslate("&fYour server is using &6RewardADs &7v" + version + "!"));
                } else {
                    sender.sendMessage(this.plugin.safeTranslate("&cRewardADs is not configured, do it!"));
                }
            } else if (args[0].equalsIgnoreCase("version")) {
                Version version = new Version(this.plugin);
                sender.sendMessage(this.plugin.safeTranslate("" + prefix + "&7v" + prefix));
            } else if (args[0].equalsIgnoreCase("reload")) {
                if (!sender.hasPermission("rewardads.reload")) {
                    sender.sendMessage(this.plugin.safeTranslate(this.plugin.getMessage("no-permission")));
                    return;
                }
                this.plugin.loadConfig();
                this.plugin.loadMessages();
                sender.sendMessage(prefix.append(this.plugin.safeTranslate(this.plugin.getMessage("reloaded"))));
            } else {
                sender.sendMessage(prefix.append(this.plugin.safeTranslate("&cUnknown command.")));
            }
        } catch (Exception e) {
            sender.sendMessage(this.plugin.safeTranslate(this.plugin.getMessage("error")));
            Error.send(this.code, e);
        }
    }

    public List<String> suggest(SimpleCommand.Invocation invocation) {
        return List.of();
    }
}
