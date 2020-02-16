package com.github.wickoo.disguiseme.versions;

import com.comphenix.protocol.ProtocolManager;
import com.github.wickoo.disguiseme.Disguise;
import com.github.wickoo.disguiseme.DisguiseMe;
import com.github.wickoo.disguiseme.util.Utils;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.mojang.authlib.properties.PropertyMap;
import net.minecraft.server.v1_10_R1.PacketPlayOutEntityDestroy;
import net.minecraft.server.v1_10_R1.PacketPlayOutNamedEntitySpawn;
import net.minecraft.server.v1_10_R1.PacketPlayOutPlayerInfo;
import net.minecraft.server.v1_10_R1.PacketPlayOutSpawnEntityLiving;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_10_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.lang.reflect.Field;
import java.util.*;

@SuppressWarnings("deprecation")
public class DisguiseHandler_1_10 extends DisguiseHandler {

    private DisguiseMe plugin;
    private ProtocolManager manager;

    private Map<UUID, Disguise> disguisedPlayers;
    private Map<String, Disguise> cachedProfiles;

    private Inventory inv;
    private Inventory cached;

    public DisguiseHandler_1_10 (DisguiseMe plugin, ProtocolManager manager) {
        this.plugin = plugin;
        this.manager = manager;
        this.disguisedPlayers = new HashMap<>();
        cachedProfiles = new HashMap<>();
        inv = Bukkit.createInventory(null, 36, Utils.chat("&b&lCurrent Disguised Players"));
        cached = Bukkit.createInventory(null, 36, Utils.chat("&d&lCurrent Cached Disguises"));
    }

    public void setDisguiseSkin (Player player) {

        CraftPlayer craftPlayer = (CraftPlayer) player;
        GameProfile gameProfile = craftPlayer.getProfile();
        PropertyMap propertiesMap = gameProfile.getProperties();

        Disguise disguise = this.getDisguisedPlayer(player.getUniqueId());
        disguise.setActualSignature(propertiesMap.get("textures").iterator().next().getSignature());
        disguise.setActualTexture(propertiesMap.get("textures").iterator().next().getValue());

        propertiesMap.removeAll("textures");
        String signature = disguise.getDisguisedSignature();
        String localTexture = disguise.getDisguisedTexture();

        propertiesMap.put("textures", new Property("textures", localTexture, signature));

    }

    public void clearDisguiseSkin (Player player) {

        CraftPlayer craftPlayer = (CraftPlayer) player;
        GameProfile gameProfile = craftPlayer.getProfile();
        PropertyMap propertiesMap = gameProfile.getProperties();
        propertiesMap.removeAll("textures");

        Disguise disguise = this.getDisguisedPlayer(player.getUniqueId());

        String signature = disguise.getActualSignature();
        String localTexture = disguise.getActualTexture();

        propertiesMap.put("textures", new Property("textures", localTexture, signature));

    }

    @Override
    public void initiateDisguise (Player player) {

        setDisguiseSkin(player);
        setDisguiseName(player);
        player.setDisplayName(disguisedPlayers.get(player.getUniqueId()).getDisguisedName());

        for (Player otherPlayer : Bukkit.getOnlinePlayers()) {

            if (otherPlayer.getUniqueId() == player.getUniqueId()) {
                return;
            }

            CraftPlayer otherCraftPlayer = (CraftPlayer) otherPlayer;
            CraftPlayer disguisedCraftPlayer = (CraftPlayer) player;

            otherCraftPlayer.getHandle().playerConnection.sendPacket(new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.REMOVE_PLAYER, disguisedCraftPlayer.getHandle()));
            otherCraftPlayer.getHandle().playerConnection.sendPacket(new PacketPlayOutEntityDestroy(player.getEntityId()));
            otherCraftPlayer.getHandle().playerConnection.sendPacket(new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER, disguisedCraftPlayer.getHandle()));
            otherCraftPlayer.getHandle().playerConnection.sendPacket(new PacketPlayOutNamedEntitySpawn(disguisedCraftPlayer.getHandle()));
            otherCraftPlayer.getHandle().playerConnection.sendPacket(new PacketPlayOutSpawnEntityLiving(disguisedCraftPlayer.getHandle()));

        }

    }

    @Override
    public void clearDisguise (Player player) {

        clearDisguisedName(player);
        clearDisguiseSkin(player);
        player.setDisplayName(disguisedPlayers.get(player.getUniqueId()).getActualName());

        for (Player otherPlayer : Bukkit.getOnlinePlayers()) {

            if (otherPlayer.getUniqueId() == player.getUniqueId()) {
                return;
            }

            CraftPlayer otherCraftPlayer = (CraftPlayer) otherPlayer;
            CraftPlayer disguisedCraftPlayer = (CraftPlayer) player;

            otherCraftPlayer.getHandle().playerConnection.sendPacket(new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.REMOVE_PLAYER, disguisedCraftPlayer.getHandle()));
            otherCraftPlayer.getHandle().playerConnection.sendPacket(new PacketPlayOutEntityDestroy(player.getEntityId()));
            otherCraftPlayer.getHandle().playerConnection.sendPacket(new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER, disguisedCraftPlayer.getHandle()));
            otherCraftPlayer.getHandle().playerConnection.sendPacket(new PacketPlayOutNamedEntitySpawn(disguisedCraftPlayer.getHandle()));
            otherCraftPlayer.getHandle().playerConnection.sendPacket(new PacketPlayOutSpawnEntityLiving(disguisedCraftPlayer.getHandle()));

        }

    }

    public void setDisguiseName (Player player) {

        CraftPlayer craftPlayer = (CraftPlayer) player;
        GameProfile gameProfile = craftPlayer.getProfile();
        Disguise disguise = this.getDisguisedPlayer(player.getUniqueId());
        Field field;

        try {
            field = gameProfile.getClass().getDeclaredField("name");
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
            return;
        }

        field.setAccessible(true);

        try {
            field.set(gameProfile, disguise.getDisguisedName());
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            return;
        }

    }

    public void clearDisguisedName (Player player) {

        CraftPlayer craftPlayer = (CraftPlayer) player;
        GameProfile gameProfile = craftPlayer.getProfile();
        Disguise disguise = this.getDisguisedPlayer(player.getUniqueId());
        Field field;

        try {
            field = gameProfile.getClass().getDeclaredField("name");
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
            return;
        }

        field.setAccessible(true);

        try {
            field.set(gameProfile, disguise.getActualName());
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            return;
        }

    }

    @Override
    public void openDisguisedInv (Player player) {

        inv.clear();

        for (Map.Entry<UUID, Disguise> uuid : disguisedPlayers.entrySet()) {

            UUID actualUUID = uuid.getKey();
            Disguise disguise = disguisedPlayers.get(actualUUID);

            ItemStack skull = new ItemStack(Material.getMaterial("SKULL_ITEM"), 1, (short) 3);
            SkullMeta meta = (SkullMeta) skull.getItemMeta();
            meta.setOwner(disguise.getDisguisedName());
            meta.setDisplayName(Utils.chat("&b&l" + disguise.getActualName()));
            List<String> lore = new ArrayList<>();
            lore.add(0, Utils.chat("&r&fDisguised as: " + "&b&l" + disguise.getDisguisedName()));
            lore.add(1, Utils.chat("&r&fDisguise UUID: " + "&b&l" + disguise.getDisguisedUUID()));
            meta.setLore(lore);
            skull.setItemMeta(meta);
            inv.addItem(skull);

        }

        player.openInventory(inv);

    }

    @Override
    public void openCachedInv (Player player) {

        cached.clear();

        for (Map.Entry<String, Disguise> string : cachedProfiles.entrySet()) {

            String disguiseName = string.getKey();
            Disguise disguise = cachedProfiles.get(disguiseName);

            ItemStack skull = new ItemStack(Material.getMaterial("SKULL_ITEM"), 1, (short) 3);
            SkullMeta meta = (SkullMeta) skull.getItemMeta();
            meta.setOwner(disguise.getDisguisedName());
            meta.setDisplayName(Utils.chat("&b&l" + disguise.getDisguisedName()));
            List<String> lore = new ArrayList<>();
            lore.add(0, Utils.chat("&r&fDisguise UUID: " + "&b&l" + disguise.getDisguisedUUID()));
            meta.setLore(lore);
            skull.setItemMeta(meta);
            cached.addItem(skull);

        }

        player.openInventory(cached);

    }
    public Map<String, Disguise> getCachedProfiles () {
        return cachedProfiles;
    }

    public boolean isDisguised (UUID uuid) {
        if (disguisedPlayers.containsKey(uuid)) {
            return true;
        } else {
            return false;
        }
    }

    public Disguise getDisguisedPlayer (UUID uuid) {
        return disguisedPlayers.get(uuid);
    }

    public void addToCachedProfiles (String string, Disguise disguise) {
        cachedProfiles.put(string, disguise);
    }

    public void addDisguised (UUID uuid, Disguise disguise) {
        disguisedPlayers.put(uuid, disguise);
    }

    public void removeDisguisedPlayer (UUID uuid) {
        disguisedPlayers.remove(uuid);
    }
}
