package it.nathanub.rewardads.Bungee.Logic.Buy;

import it.nathanub.rewardads.Bungee.Tools.Api.Api;
import it.nathanub.rewardads.BungeeMain;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;

public class Buy {
    public void handle(BungeeMain plugin, Map<String, String> event) throws IOException {
        String code = event.get("code");
        String idReward = event.get("id");
        String nameReward = event.get("name");
        String costReward = event.get("cost");
        String playerName = event.get("player");
        String userId = event.get("user");

        // Verifica che tutti i dati siano presenti
        if (userId == null || playerName== null || idReward == null || nameReward == null || costReward == null || code == null) {
            plugin.getLogger().log(Level.WARNING, "Missing event data: " + event);
            return;
        }

        ProxiedPlayer player = plugin.getProxy().getPlayer(playerName);

        if (player != null && player.isConnected()) {
            ServerInfo server = player.getServer().getInfo();
            try {
                ByteArrayOutputStream byteArray = new ByteArrayOutputStream();
                DataOutputStream out = new DataOutputStream(byteArray);

                // Scrivi i dati nel messaggio
                out.writeUTF("OnBuy"); // Identificatore del messaggio
                out.writeUTF(player.getName());
                out.writeUTF(idReward);
                out.writeUTF(nameReward);
                out.writeUTF(costReward);
                out.writeUTF(code);

                // Invia il messaggio al server del giocatore
                server.sendData("rewardads:channel", byteArray.toByteArray());
                plugin.getLogger().info("Messaggio inviato a Spigot per il player " + player.getName());
                update(event, "ok");
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            plugin.getLogger().log(Level.WARNING, "Player is not online: " + userId);
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
