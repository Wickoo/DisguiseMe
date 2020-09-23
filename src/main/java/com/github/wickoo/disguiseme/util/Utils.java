package com.github.wickoo.disguiseme.util;

import com.comphenix.protocol.ProtocolManager;
import com.github.wickoo.disguiseme.DisguiseMe;
import com.github.wickoo.disguiseme.versions.DisguiseHandler;
import com.github.wickoo.disguiseme.versions.DisguiseHandler_New;
import com.github.wickoo.disguiseme.versions.DisguiseHandler_Old;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class Utils {

    public static String getPackageVersion () {
        String nmsver = Bukkit.getServer().getClass().getPackage().getName();
        return nmsver.substring(nmsver.lastIndexOf(".") + 1);
    }

    public static List<String> supportedVersions = Arrays.asList("1.9","1.10","1.11","1.12","1.13","1.14","1.15","1.16","1.17");

    public static boolean isSupported () {

        for (String s : supportedVersions) {

            Bukkit.broadcastMessage(s);
            Bukkit.broadcastMessage(getPackageVersion());

            if (Bukkit.getVersion().contains(s)) {

                return true;

            }

        }

        return false;

    }

    public static String chat (String s) {
        return ChatColor.translateAlternateColorCodes('&',s);
    }

    public static String[] fetch(UUID uuid) {

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
            e.printStackTrace();
            return new String[] {null};
        } catch (IllegalStateException e) {
            System.err.println("Player does not exist!");
            e.printStackTrace();
            return null;
        }

    }

    public static DisguiseHandler getHandlerByVer (String version, ProtocolManager manager, DisguiseMe plugin) {

        if (version.contains("1.8")) {
            return new DisguiseHandler_Old(plugin, manager);
        } if (isSupported()) {
            return new DisguiseHandler_New(plugin, manager);
        } else {
            return null;
        }
    }

    public static int getPing (Player player) {

        Class<?> craftPlayerClass = player.getClass();

        try {
            Method getHandle = craftPlayerClass.getDeclaredMethod("getHandle");
            getHandle.setAccessible(true);
            Object entityPlayer = getHandle.invoke(player);
            Field ping = entityPlayer.getClass().getDeclaredField("ping");
            return ping.getInt(entityPlayer);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException | NoSuchFieldException e) {
            e.printStackTrace();
            return 5;
        }

    }


    public static ItemStack getSkullByVer () {

        String version = Bukkit.getVersion().substring(Bukkit.getVersion().lastIndexOf(':') + 1).replace(')', ' ');

        ItemStack oldSkull = new ItemStack(Material.getMaterial("SKULL_ITEM"), 1, (short) 3);
        ItemStack newSkull = new ItemStack(Material.PLAYER_HEAD);

        if (version.contains("1.16")) {
            return newSkull;
        } if (version.contains("1.15")) {
            return newSkull;
        } if (version.contains("1.14")) {
            return newSkull;
        } if (version.contains("1.13")) {
            return newSkull;
        } if (version.contains("1.12")) {
            return oldSkull;
        } if (version.contains("1.11")) {
            return oldSkull;
        } if (version.contains("1.10")) {
            return oldSkull;
        } if (version.contains("1.9")) {
            return oldSkull;
        } if (version.contains("1.8")) {
            return oldSkull;
        } else {
            return null;
        }

    }

    public static ItemStack buildStack (Material material, String name, String... lores) {
        List<String> lore = Arrays.asList(lores);
        ItemStack itemStack = new ItemStack(material);
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setLore(lore);
        itemMeta.setDisplayName(chat(name));
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }

}
