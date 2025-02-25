package it.nathanub.rewardads.Bungee.Logic.Commands;

import it.nathanub.rewardads.Bungee.Tools.Accounts.Verify;
import it.nathanub.rewardads.Bungee.Tools.Logs.Error;
import it.nathanub.rewardads.Bungee.Tools.Server.Server;
import it.nathanub.rewardads.Bungee.Tools.Version.Version;
import it.nathanub.rewardads.BungeeMain;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.config.Configuration;

import java.util.Objects;

public class Commands extends Command {
    private final BungeeMain plugin;
    private final String code;
    private final Configuration messageConfig;

    public Commands(BungeeMain plugin, Configuration messageConfig) {
        super("rewardads");
        this.plugin = plugin;
        this.messageConfig = messageConfig;
        this.code = plugin.getConfig().getString("code");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        try {
            String prefix = plugin.safeTranslate(messageConfig.getString("prefix"));

            if(args.length == 0) {
                String version = plugin.getDescription().getVersion();
                Server server = new Server(plugin);

                if(sender instanceof ProxiedPlayer) {
                    ProxiedPlayer player = (ProxiedPlayer) sender;

                    if(server.isValid()) {
                        player.sendMessage(new TextComponent(prefix + plugin.safeTranslate(Objects.requireNonNull(messageConfig.getString("server-using-rewardads")).replace("%server%", server.getName()).replace("%version%", version))));
                    } else {
                        player.sendMessage(new TextComponent(prefix + "§cRewardADs is not configured, please contact an administrator."));
                    }
                } else {
                    if(server.isValid()) {
                        sender.sendMessage(new TextComponent(("§fYour server is using §6RewardADs §7v" + version + "§f!")));
                    } else {
                        sender.sendMessage(new TextComponent("§cRewardADs is not configured, do it!"));
                    }
                }
            } else if (args[0].equalsIgnoreCase("version")) {
                Version version = new Version(plugin);
                sender.sendMessage(new TextComponent(prefix + "§7v" + version.getPlugin()));
            } else if(args[0].equalsIgnoreCase("reload")){
                    if (!(sender.hasPermission("rewardads.reload"))) {
                        sender.sendMessage(plugin.safeTranslate(messageConfig.getString("no-permission")));
                        return;
                    }

                    plugin.loadConfig();
                    plugin.loadMessages();
                    sender.sendMessage(prefix + plugin.safeTranslate(messageConfig.getString("reloaded")));
            } else {
                sender.sendMessage(new TextComponent(prefix + "§cUnknown command."));
            }
        } catch (Exception e) {
            sender.sendMessage(new TextComponent(plugin.safeTranslate(messageConfig.getString("error"))));
            Error.send(this.code, e);
        }
    }

}
