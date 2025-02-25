package it.nathanub.rewardads.Velocity.Logic.Commands;

import com.velocitypowered.api.command.CommandInvocation;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.proxy.Player;
import it.nathanub.rewardads.Velocity.Tools.Accounts.Verify;
import it.nathanub.rewardads.Velocity.Tools.Logs.Error;
import it.nathanub.rewardads.VelocityMain;
import java.util.List;
import java.util.Properties;
import net.kyori.adventure.text.Component;

public class ConfirmCommand implements SimpleCommand {
    private final VelocityMain plugin;

    private final Properties messageConfig;

    private final Properties userConfig;

    public ConfirmCommand(VelocityMain plugin, Properties messageConfig, Properties userConfig) {
        this.plugin = plugin;
        this.messageConfig = messageConfig;
        this.userConfig = userConfig;
    }

    public void execute(SimpleCommand.Invocation invocation) {
        CommandSource sender = invocation.source();
        String[] args = (String[])invocation.arguments();
        try {
            Component prefix = this.plugin.safeTranslate(this.plugin.getMessage("prefix"));
            if (args.length < 2) {
                sender.sendMessage(prefix.append(this.plugin.safeTranslate(this.plugin.getMessage("platform-tokenOrId"))));
                return;
            }
            String token = args[0];
            String platform_id = args[1];
            if (!(sender instanceof Player)) {
                sender.sendMessage(prefix.append(this.plugin.safeTranslate("&cThis command is only for players.")));
                return;
            }
            if (!sender.hasPermission("rewardads.confirm")) {
                sender.sendMessage(this.plugin.safeTranslate(this.plugin.getMessage("no-permission")));
                return;
            }
            Player player = (Player)sender;
            Verify verify = new Verify(this.plugin, this.messageConfig, this.userConfig);
            player.sendMessage(prefix.append(verify.verifyPlatform(player, token, platform_id)));
            this.plugin.getLogger().info(player.getUsername() + " Verified platform!");
        } catch (Exception e) {
            sender.sendMessage(this.plugin.safeTranslate(this.plugin.getMessage("error")));
            Error.send(this.plugin.getConfig("code"), e);
        }
    }

    public List<String> suggest(SimpleCommand.Invocation invocation) {
        return List.of();
    }
}
