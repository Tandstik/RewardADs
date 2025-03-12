package it.nathanub.rewardads.Velocity.Logic;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import it.nathanub.rewardads.Velocity.Logic.Buy.Buy;
import it.nathanub.rewardads.Velocity.Logic.Rewards.Rewards;
import it.nathanub.rewardads.Velocity.Tools.Logs.Error;
import it.nathanub.rewardads.VelocityMain;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;

public class Requests implements Runnable {
    private final Rewards rewards;

    private final VelocityMain plugin;

    private final String code;

    private final Buy buy;

    private final Gson gson;

    private final Logger logger;

    public List<Map<String, String>> cachedRewards;


    public Requests(VelocityMain plugin) {
        this.plugin = plugin;
        this.logger = plugin.getLogger();
        this.code = plugin.getConfig("code");
        this.buy = new Buy();
        this.gson = new Gson();
        this.rewards = new Rewards(plugin);
    }

    public void run() {
        this.plugin.getProxy().getScheduler().buildTask(this.plugin, () -> {
            try {
                Type listType = (new TypeToken<List<Map<String, String>>>() {

                }).getType();
                List<Map<String, String>> elements = this.gson.fromJson(this.rewards.getList(), listType);
                if (elements != null && !elements.equals(this.cachedRewards)) {
                    this.cachedRewards = elements;
                    for (Map<String, String> reward : this.cachedRewards) {
                        String status = reward.get("status");
                        if ("pending".equalsIgnoreCase(status))
                            this.buy.handle(this.plugin, reward);
                    }
                }
            } catch (Exception e) {
                Error.send(this.code, e);
                this.logger.error("Error during request: ", e);
            }
        }).schedule();
    }
}
