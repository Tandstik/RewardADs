package it.nathanub.rewardads.Velocity.Tools.Accounts;

import com.google.gson.JsonObject;
import com.velocitypowered.api.proxy.Player;
import it.nathanub.rewardads.VelocityMain;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;

public class Verify {
    private final Properties messageConfig;

    private final VelocityMain plugin;

    private final Properties userConfig;

    private final File userFile;

    public Verify(VelocityMain plugin, Properties messageConfig, Properties userConfig) throws IOException {
        this.plugin = plugin;
        this.messageConfig = messageConfig;
        this.userConfig = userConfig;
        this.userFile = new File(String.valueOf(plugin.getDataFolder()), "userdata.yml");
    }

    public Component verifyPlatform(Player player, String token, String platform_id) throws IOException {
        Component component;
        Map<String, Object> formData = new HashMap<>();
        formData.put("platform_id", platform_id);
        formData.put("token", token);
        JsonObject response = Api.handleApi("verify-platform", formData);
        if (response == null)
            throw new IllegalStateException("API response is null");
        TextComponent textComponent = response.has("message") ? Component.text(response.get("message").getAsString()) : Component.text("Unknown error");
        String platformId = response.has("platform_id") ? response.get("platform_id").getAsString() : "";
        if (platformId.isEmpty())
            throw new IllegalStateException("Platform ID not found in response");
        UUID uuid = player.getUniqueId();
        String userPath = "users." + uuid.toString();
        if (this.userConfig.containsKey(userPath + ".verified") && Boolean.parseBoolean(this.userConfig.getProperty(userPath + ".verified"))) {
            component = this.plugin.safeTranslate(this.plugin.getMessage("platform-alreadyVerified"));
            return component;
        }
        this.userConfig.setProperty(userPath + ".id", platformId);
        this.userConfig.setProperty(userPath + ".verified", "true");
        saveUserData();
        return textComponent;
    }

    private void saveUserData() throws IOException {
        FileWriter writer = new FileWriter(this.userFile);
        try {
            this.userConfig.store(writer, "User Data");
            writer.close();
        } catch (Throwable throwable) {
            try {
                writer.close();
            } catch (Throwable throwable1) {
                throwable.addSuppressed(throwable1);
            }
            throw throwable;
        }
    }
}
