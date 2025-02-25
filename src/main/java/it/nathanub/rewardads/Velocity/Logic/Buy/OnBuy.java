package it.nathanub.rewardads.Velocity.Logic.Buy;

import it.nathanub.rewardads.BungeeMain;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Plugin;

public class OnBuy {
    private final ProxiedPlayer player;

    private final String nameReward;

    private final String idReward;

    private final String costReward;

    private final BungeeMain plugin;

    private final String code;

    public OnBuy(BungeeMain plugin, ProxiedPlayer player, String nameReward, String idReward, String costReward, String code) {
        this.player = player;
        this.nameReward = nameReward;
        this.idReward = idReward;
        this.costReward = costReward;
        this.plugin = plugin;
        this.code = code;
    }

    public ProxiedPlayer getPlayer() {
        return this.player;
    }

    public String getNameReward() {
        return this.nameReward;
    }

    public String getIdReward() {
        return this.idReward;
    }

    public String getCostReward() {
        return this.costReward;
    }

    public Plugin getPlugin() {
        return (Plugin)this.plugin;
    }

    public String getCode() {
        return this.code;
    }
}
