package it.nathanub.rewardads.Spigot.Logic.Buy;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.Plugin;

public class OnBuy extends Event {
    private static final HandlerList HANDLERS = new HandlerList();
    private final Plugin plugin;
    private final Player player;
    private final String nameReward;
    private final String idReward;
    private final String code;
    private final String costReward;

    public OnBuy(Plugin plugin, Player player, String idReward, String nameReward, String costReward, String code) {
        this.plugin = plugin;
        this.nameReward = nameReward;
        this.idReward = idReward;
        this.player = player;
        this.code = code;
        this.costReward = costReward;
    }

    public Plugin getPlugin() {
        return plugin;
    }

    public Player getPlayer() {
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

    public String getCode() {
        return code;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}
