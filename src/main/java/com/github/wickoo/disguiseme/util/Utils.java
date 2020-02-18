package com.github.wickoo.disguiseme.util;

import com.comphenix.protocol.ProtocolManager;
import com.github.wickoo.disguiseme.DisguiseMe;
import com.github.wickoo.disguiseme.versions.*;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.UUID;

public class Utils {

    public static String getPackageVersion () {
        String nmsver = Bukkit.getServer().getClass().getPackage().getName();
        return nmsver.substring(nmsver.lastIndexOf(".") + 1);
    }

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
            player.sendMessage(Utils.chat("&c&lERROR! &7Server received too many requests!"));
            e.printStackTrace();
            return new String[] {null};
        } catch (IllegalStateException e) {
            System.err.println("Player does not exist!");
            e.printStackTrace();
            return null;
        }

    }

    public static DisguiseHandler getHandlerByVer (String version, ProtocolManager manager, DisguiseMe plugin) {

        if (version.contains("1.15")) {
            return new DisguiseHandler_1_15(plugin, manager);
        } if (version.contains("1.14")) {
            return new DisguiseHandler_1_14(plugin, manager);
        } if (version.contains("1.13")) {
            return new DisguiseHandler_1_13(plugin, manager);
        } if (version.contains("1.12")) {
            return new DisguiseHandler_1_12(plugin, manager);
        } if (version.contains("1.11")) {
            return new DisguiseHandler_1_11(plugin, manager);
        } if (version.contains("1.10")) {
            return new DisguiseHandler_1_10(plugin, manager);
        } if (version.contains("1.9")) {
            return new DisguiseHandler_1_9(plugin, manager);
        } if (version.contains("1.8")) {
            return new DisguiseHandler_1_8(plugin, manager);
        } else {
            return null;
        }

    }

}
