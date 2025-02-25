package it.nathanub.rewardads.Velocity.Logic.Buy;

import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.ServerConnection;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import it.nathanub.rewardads.Velocity.Tools.Api.Api;
import it.nathanub.rewardads.VelocityMain;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Map;
import java.util.Optional;
import org.slf4j.Logger;

public class Buy {
    public void handle(VelocityMain plugin, Map<String, String> event) {
        Logger logger = plugin.getLogger();
        ProxyServer proxy = plugin.getProxy();
        String code = event.get("code");
        String idReward = event.get("id");
        String nameReward = event.get("name");
        String costReward = event.get("cost");
        String playerName = event.get("player");
        String userId = event.get("user");
        if (userId == null || playerName == null || idReward == null || nameReward == null || costReward == null || code == null) {
            logger.warn("Missing event data: " + event);
            return;
        }
        Optional<Player> optionalPlayer = proxy.getPlayer(playerName);
        if (optionalPlayer.isPresent()) {
            Player player = optionalPlayer.get();
            Optional<RegisteredServer> serverOptional = player.getCurrentServer().map(s -> s.getServer());
            if (serverOptional.isPresent()) {
                RegisteredServer server = serverOptional.get();
                try {
                    ByteArrayOutputStream byteArray = new ByteArrayOutputStream();
                    DataOutputStream out = new DataOutputStream(byteArray);
                    out.writeUTF("OnBuy");
                    out.writeUTF(player.getUsername());
                    out.writeUTF(idReward);
                    out.writeUTF(nameReward);
                    out.writeUTF(costReward);
                    out.writeUTF(code);
                    server.sendPluginMessage(plugin.getMessageChannel(), byteArray.toByteArray());
                    logger.info("Messaggio inviato a Spigot per il player " + player.getUsername());
                    update(event, "ok");
                } catch (IOException e) {
                    logger.error("Errore nell'invio del messaggio al server", e);
                }
            } else {
                logger.warn("Il giocatore {} non connesso a nessun server.", playerName);
                update(event, "Player is not online");
            }
        } else {
            logger.warn("Player is not online: " + userId);
            update(event, "Player is not online");
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
