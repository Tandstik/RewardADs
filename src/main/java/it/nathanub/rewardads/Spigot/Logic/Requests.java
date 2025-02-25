package it.nathanub.rewardads.Spigot.Logic;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import it.nathanub.rewardads.Spigot.Logic.Buy.Buy;
import it.nathanub.rewardads.Spigot.Logic.Rewards.Rewards;
import it.nathanub.rewardads.Spigot.Tools.Logs.Error;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

public class Requests extends BukkitRunnable {
    private final Rewards rewards;
    private final Plugin plugin;
    private final String code;
    private final Buy buy;
    private final Gson gson;
    private List<Map<String, String>> cachedRewards;


    public Requests(Plugin plugin) {
        this.plugin = plugin;
        this.code = plugin.getConfig().getString("code");
        this.buy = new Buy(plugin);
        this.gson = new Gson();
        this.rewards = new Rewards(plugin);
    }

    @Override
    public void run() {
        new BukkitRunnable() {
            @Override
            public void run() {
                try {
                    Type listType = new TypeToken<List<Map<String, String>>>() {}.getType();
                    List<Map<String, String>> elements = gson.fromJson(rewards.getList(), listType);

                    if(elements != null && !elements.equals(cachedRewards)) {
                        cachedRewards = elements;

                        for(Map<String, String> reward : cachedRewards) {
                            String status = reward.get("status");
                            if(status.equalsIgnoreCase("pending")) {
                                buy.handle(plugin, reward);
                            }
                        }
                    }

                } catch(Exception e) {
                    Error.send(code, e);
                }
            }
        }.runTaskAsynchronously(plugin);
    }
}
