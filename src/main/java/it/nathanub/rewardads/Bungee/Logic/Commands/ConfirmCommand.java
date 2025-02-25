package it.nathanub.rewardads.Bungee.Logic.Commands;

import it.nathanub.rewardads.Bungee.Tools.Accounts.Verify;
import it.nathanub.rewardads.Bungee.Tools.Logs.Error;
import it.nathanub.rewardads.BungeeMain;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.config.Configuration;

public class ConfirmCommand extends Command {
    private final BungeeMain plugin;
    private final Configuration messageConfig;

    public ConfirmCommand(BungeeMain plugin, Configuration messageConfig) {
        super("confirm"); // Registra direttamente il comando /confirm
        this.plugin = plugin;
        this.messageConfig = messageConfig;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        try {
            String prefix = plugin.safeTranslate(messageConfig.getString("prefix"));

            if (args.length < 2) {
                sender.sendMessage(new TextComponent(prefix + plugin.safeTranslate(messageConfig.getString("platform.tokenOrId"))));
                return;
            }

            if(!(sender.hasPermission("rewardads.confirm"))){
                sender.sendMessage(new TextComponent(plugin.safeTranslate(messageConfig.getString("no-permission"))));
                return;
            }

            String token = args[0];
            String platform_id = args[1];

            if (!(sender instanceof ProxiedPlayer)) {
                sender.sendMessage(new TextComponent(prefix + "§cThis command is only for players."));
                return;
            }

            ProxiedPlayer player = (ProxiedPlayer) sender;
            Verify verify = new Verify(plugin, messageConfig);
            player.sendMessage(new TextComponent(prefix + verify.verifyPlatform(player, token, platform_id)));
            plugin.getLogger().info(player.getName() + " Verified platform!");

        } catch (Exception e) {
            sender.sendMessage(new TextComponent(plugin.safeTranslate(messageConfig.getString("error"))));
            Error.send(plugin.getConfig().getString("code"), e);
        }
    }
}
