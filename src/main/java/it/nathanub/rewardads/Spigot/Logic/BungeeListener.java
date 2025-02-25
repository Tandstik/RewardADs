package it.nathanub.rewardads.Spigot.Logic;

import it.nathanub.rewardads.Spigot.Logic.Buy.OnBuy;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.messaging.PluginMessageListener;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;

public class BungeeListener implements PluginMessageListener {
    private final Plugin plugin;

    public BungeeListener(Plugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void onPluginMessageReceived(String channel, Player player, byte[] message) {
        if (!channel.equals("rewardads:channel")) {
            return;
        }

        try {
            DataInputStream in = new DataInputStream(new ByteArrayInputStream(message));
            String subChannel = in.readUTF(); // Legge il primo campo (identificatore)

            if (subChannel.equals("OnBuy")) {
                String playerName = in.readUTF();
                String idReward = in.readUTF();
                String nameReward = in.readUTF();
                String costReward = in.readUTF();
                String code = in.readUTF();

                Player targetPlayer = Bukkit.getPlayer(playerName);
                if (targetPlayer != null && targetPlayer.isOnline()) {
                    // Chiamare l'evento OnBuy su Spigot
                    OnBuy onBuyEvent = new OnBuy(plugin, targetPlayer, idReward, nameReward, costReward, code);
                    Bukkit.getScheduler().runTask(plugin, () -> {
                        Bukkit.getServer().getPluginManager().callEvent(onBuyEvent);
                    });
                    plugin.getLogger().info("Event OnBuy called for " + playerName);
                } else {
                    plugin.getLogger().warning("Player " + playerName + " is not online!");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
