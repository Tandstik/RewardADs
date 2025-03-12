package it.nathanub.rewardads.Bungee.Logic;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import it.nathanub.rewardads.Bungee.Logic.Buy.Buy;
import it.nathanub.rewardads.Bungee.Logic.Rewards.Rewards;
import it.nathanub.rewardads.Bungee.Tools.Logs.Error;
import it.nathanub.rewardads.BungeeMain;
import net.md_5.bungee.config.Configuration;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

public class Requests implements Runnable {
    private final Rewards rewards;
    private final BungeeMain plugin;
    private final String code;
    private final Buy buy;
    private final Gson gson;
    public List<Map<String, String>> cachedRewards;

    String prefix = "§7[§6R§7] §f";

    public Requests(BungeeMain plugin) {
        this.plugin = plugin;
        Configuration config = plugin.getConfig();
        this.code = config.getString("code", "default-code");
        this.buy = new Buy();
        this.gson = new Gson();
        this.rewards = new Rewards(plugin);
    }

    @Override
    public void run() {
        new Thread(() -> {
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

            } catch (Exception e) {
                Error.send(code, e);
            }
        }).start();
    }
}
