package it.nathanub.rewardads.Bungee.Logic.Buy;

import it.nathanub.rewardads.BungeeMain;
import it.nathanub.rewardads.Velocity.Tools.Api.Api;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Plugin;

import java.util.Map;

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

    public void update(Map<String, String> event, String status) {
        String idReward = event.get("id");
        String userId = event.get("user");
        String code = event.get("code");
        String quantity = event.get("quantity");
        Api.handle("updatebuy/" + idReward + "/" + userId + "/" + code + "/" + quantity + "/" + status);
    }

    public ProxiedPlayer getPlayer() {
        return player;
    }

    public String getNameReward() {
        return nameReward;
    }

    public String getIdReward() {
        return idReward;
    }

    public String getCostReward() {
        return costReward;
    }

    public Plugin getPlugin() {
        return plugin;
    }

    public String getCode() {
        return code;
    }
}
