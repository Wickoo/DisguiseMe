package com.github.wickoo.disguiseme;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.UUID;

public class DMUtil {

    public static String chat (String s) {
        return ChatColor.translateAlternateColorCodes('&',s);
    }

    public static String[] fetch(UUID uuid, Player player) {

        String uuidFixed = uuid.toString().replace("-", "");

        try {

            URL url_1 = new URL("https://sessionserver.mojang.com/session/minecraft/profile/" + uuidFixed + "?unsigned=false");
            InputStreamReader reader_1 = new InputStreamReader(url_1.openStream());
            JsonObject textureProperty = new JsonParser().parse(reader_1).getAsJsonObject().get("properties").getAsJsonArray().get(0).getAsJsonObject();
            String texture = textureProperty.get("value").getAsString();
            String signature = textureProperty.get("signature").getAsString();

            return new String[] {texture, signature};

        } catch (IOException e) {
            System.err.println("Could not get skin data from session servers!");
            player.sendMessage(DMUtil.chat("&c&lERROR! &7Server received too many requests!"));
            e.printStackTrace();
            return new String[] {null};
        } catch (IllegalStateException e) {
            System.err.println("Player does not exist!");
            e.printStackTrace();
            return null;
        }

    }

}
