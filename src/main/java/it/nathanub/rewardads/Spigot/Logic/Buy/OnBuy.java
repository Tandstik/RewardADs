package it.nathanub.rewardads.Spigot.Logic.Buy;

import it.nathanub.rewardads.SpigotMain;
import it.nathanub.rewardads.Velocity.Tools.Api.Api;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.Plugin;

import java.util.HashMap;
import java.util.Map;

public class OnBuy extends Event {
    private static final HandlerList HANDLERS = new HandlerList();
    private final SpigotMain plugin;
    private final Player player;
    private final String nameReward;
    private final String idReward;
    private final String code;
    private final String costReward;
    private final String user;
    private final String quantity;
    public Map<String, String> events = new HashMap<>();

    public OnBuy(SpigotMain plugin, Player player, String idReward, String nameReward, String costReward, String code, String user, String quantity) {
        this.plugin = plugin;
        this.nameReward = nameReward;
        this.idReward = idReward;
        this.player = player;
        this.code = code;
        this.user = user;
        this.quantity = quantity;
        this.costReward = costReward;

    }



    public Plugin getPlugin() {
        return plugin;
    }
    public Map<String, String> getEvents(){
        events.put("code", code);
        events.put("id", idReward);
        events.put("user", user);
        events.put("name", nameReward);
        events.put("quantity", quantity);
        events.put("cost", costReward);
        return events;
    }

    public void update(Map<String, String> event, String status) {
        String idReward = event.get("id");
        String userId = event.get("user");
        String code = event.get("code");
        String quantity = event.get("quantity");
        Api.handle("updatebuy/" + idReward + "/" + userId + "/" + code + "/" + quantity + "/" + status);
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
