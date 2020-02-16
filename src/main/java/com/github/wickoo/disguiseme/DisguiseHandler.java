package com.github.wickoo.disguiseme;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.mojang.authlib.properties.PropertyMap;
import net.minecraft.server.v1_15_R1.PacketPlayOutEntityDestroy;
import net.minecraft.server.v1_15_R1.PacketPlayOutNamedEntitySpawn;
import net.minecraft.server.v1_15_R1.PacketPlayOutPlayerInfo;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_15_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.lang.reflect.Field;
import java.util.*;

public class DisguiseHandler {

    private DisguiseMe plugin;

    private Map<UUID, Disguise> disguisedPlayers;
    private Map<UUID, GameProfile> cachedProfiles;

    private Inventory inv;

    public DisguiseHandler (DisguiseMe plugin) {
        this.plugin = plugin;
        disguisedPlayers = new HashMap<>();
        cachedProfiles = new HashMap<>();
        inv = Bukkit.createInventory(null, 36, DMUtil.chat("&b&lCurrent Disguised Players"));
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

        }

    }

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

    public void openDisguisedInv (Player player) {

        inv.clear();

        for (Map.Entry<UUID, Disguise> uuid : disguisedPlayers.entrySet()) {

            UUID actualUUID = uuid.getKey();
            Disguise disguise = disguisedPlayers.get(actualUUID);

            ItemStack skull = new ItemStack(Material.PLAYER_HEAD);
            SkullMeta meta = (SkullMeta) skull.getItemMeta();
            meta.setOwningPlayer(Bukkit.getOfflinePlayer(actualUUID));
            meta.setDisplayName(DMUtil.chat("&b&l" + disguise.getActualName()));
            List<String> lore = new ArrayList<>();
            lore.add(0, DMUtil.chat("&r&fDisguised as: " + "&b&l" + disguise.getDisguisedName()));
            lore.add(1, DMUtil.chat("&r&fDisguise UUID: " + "&b&l" + disguise.getDisguisedUUID()));
            meta.setLore(lore);
            skull.setItemMeta(meta);
            inv.addItem(skull);

        }

        player.openInventory(inv);

    }

    public void addPacketListener (ProtocolManager manager) {

        manager.addPacketListener (new PacketAdapter(plugin, PacketType.Play.Server.PLAYER_INFO) {
            @Override
            public void onPacketSending(PacketEvent event) {
                // Item packets (id: 0x29)
                if (event.getPacketType() == PacketType.Play.Server.PLAYER_INFO) {

                    if(!isDisguised(event.getPlayer().getUniqueId())) {
                        return;
                    }

                    Player player = event.getPlayer();

                    setDisguiseSkin(player);
                    setDisguiseName(player);
                }

            }
        });

        manager.addPacketListener (new PacketAdapter(plugin, PacketType.Play.Server.NAMED_ENTITY_SPAWN) {
            @Override
            public void onPacketSending(PacketEvent event) {
                // Item packets (id: 0x29)
                if (event.getPacketType() == PacketType.Play.Server.NAMED_ENTITY_SPAWN) {

                    if (!isDisguised(event.getPlayer().getUniqueId())) {
                        return;
                    }

                    setDisguiseName(event.getPlayer());

                }

            }
        });



    }

    public Map<UUID, GameProfile> getCachedProfiles () {
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

    public void addToCachedProfiles (UUID uuid, GameProfile gameProfile) {
        cachedProfiles.put(uuid, gameProfile);
    }

    public void addDisguised (UUID uuid, Disguise disguise) {
        disguisedPlayers.put(uuid, disguise);
    }

    public void removeDisguisedPlayer (UUID uuid) {
        disguisedPlayers.remove(uuid);
    }
}
