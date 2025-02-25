package it.nathanub.rewardads.Spigot.Logic.Buy;

import it.nathanub.rewardads.Spigot.Tools.Accounts.Link;
import it.nathanub.rewardads.Spigot.Tools.Api.Api;
import it.nathanub.rewardads.Spigot.Tools.User.User;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;

public class Buy {
    private final Plugin plugin;

    public Buy(Plugin plugin) {
        this.plugin = plugin;
    }

    public void handle(Plugin plugin, Map<String, String> event) throws IOException {
        Link link = new Link(plugin);
        User user = new User(plugin);

        String code = event.get("code");
        String idReward = event.get("id");
        String nameReward = event.get("name");
        String playerName = event.get("player");
        String costReward = event.get("cost");
        String userId = event.get("user");
        if(userId == null || playerName == null || idReward == null || nameReward == null || costReward == null || code == null) return;

        Player player = Bukkit.getPlayer(playerName);
        if(link.isLinked(playerName)) {
            if(player != null && player.isOnline()) {
                OnBuy onBuyEvent = new OnBuy(plugin, player, idReward, nameReward, costReward, code);
                try {
                    Bukkit.getScheduler().runTask(plugin, () -> {
                        Bukkit.getServer().getPluginManager().callEvent(onBuyEvent);
                    });
                } catch (Exception e) {
                    plugin.getLogger().severe("Error during callEvent: " + e.getMessage());
                    e.printStackTrace();
                }
                update(event,"ok");
            } else {
                update(event, "You're not online!");
            }
        } else {
            update(event, "You're not linked to this server!");
        }
    }


    private void update(Map<String, String> event, String status) {
        String idReward = event.get("id");
        String userId = event.get("user");
        String code = event.get("code");
        String quantity = event.get("quantity");
        Api.handle("updatebuy/" + idReward + "/" + userId + "/" + code + "/" + quantity + "/" + status);
    }
}