package com.github.wickoo.disguiseme.versions;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.comphenix.protocol.wrappers.PlayerInfoData;
import com.comphenix.protocol.wrappers.WrappedGameProfile;
import com.github.wickoo.disguiseme.Disguise;
import com.github.wickoo.disguiseme.DisguiseMe;
import com.github.wickoo.disguiseme.packetwrappers.WrapperPlayServerPlayerInfo;
import com.github.wickoo.disguiseme.util.Utils;
import com.mojang.authlib.GameProfile;
import net.minecraft.server.v1_15_R1.PacketPlayOutEntityDestroy;
import net.minecraft.server.v1_15_R1.PacketPlayOutNamedEntitySpawn;
import net.minecraft.server.v1_15_R1.PacketPlayOutPlayerInfo;
import net.minecraft.server.v1_15_R1.PacketPlayOutSpawnEntityLiving;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_15_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

public class DisguiseHandler_1_15 extends DisguiseHandler {

    private DisguiseMe plugin;
    private ProtocolManager manager;

    private Map<UUID, Disguise> disguisedPlayers;
    private Map<String, Disguise> cachedProfiles;

    private Inventory inv;
    private Inventory cached;

    public DisguiseHandler_1_15 (DisguiseMe plugin, ProtocolManager manager) {
        this.plugin = plugin;
        this.manager = manager;
        this.disguisedPlayers = new HashMap<>();
        cachedProfiles = new HashMap<>();
        inv = Bukkit.createInventory(null, 36, Utils.chat("&b&lCurrent Disguised Players"));
        cached = Bukkit.createInventory(null, 36, Utils.chat("&d&lCurrent Cached Disguises"));
    }

    @Override
    public void setDisguiseSkin (Player player) {

        super.setDisguiseSkin(player);
       // CraftPlayer craftPlayer = (CraftPlayer) player;
       //GameProfile gameProfile = craftPlayer.getProfile();
       // PropertyMap propertiesMap = gameProfile.getProperties();

        //Disguise disguise = this.getDisguisedPlayer(player.getUniqueId());
       // disguise.setActualSignature(propertiesMap.get("textures").iterator().next().getSignature());
        //disguise.setActualTexture(propertiesMap.get("textures").iterator().next().getValue());

       // propertiesMap.removeAll("textures");
       // String signature = disguise.getDisguisedSignature();
       // String localTexture = disguise.getDisguisedTexture();

       // propertiesMap.put("textures", new Property("textures", localTexture, signature));

    }

    @Override
    public void clearDisguiseSkin (Player player) {

        super.clearDisguiseSkin(player);
        //CraftPlayer craftPlayer = (CraftPlayer) player;
        //GameProfile gameProfile = craftPlayer.getProfile();
        //PropertyMap propertiesMap = gameProfile.getProperties();
        //propertiesMap.removeAll("textures");

        //Disguise disguise = this.getDisguisedPlayer(player.getUniqueId());

        //String signature = disguise.getActualSignature();
       // String localTexture = disguise.getActualTexture();

        //propertiesMap.put("textures", new Property("textures", localTexture, signature));

    }

    @Override
    public void initiateDisguise (Player player) {

        setDisguiseSkin(player);
        setDisguiseName(player);
        player.setDisplayName(disguisedPlayers.get(player.getUniqueId()).getDisguisedName());

        BukkitTask task = new BukkitRunnable() {
            @Override
            public void run() {

                for (Player otherPlayer : Bukkit.getOnlinePlayers()) {

                    if (otherPlayer.getUniqueId() == player.getUniqueId()) {
                        continue;
                    }

                    WrappedGameProfile disguisedPlayerGP = WrappedGameProfile.fromPlayer(player);

                    PacketContainer packetRemovePlayer = new PacketContainer(PacketType.Play.Server.PLAYER_INFO);
                    packetRemovePlayer.getGameProfiles().write(0, disguisedPlayerGP);
                    packetRemovePlayer.getIntegers().write(0, 1);
                    packetRemovePlayer.getPlayerInfoAction().write(1, EnumWrappers.PlayerInfoAction.REMOVE_PLAYER);

                    PacketContainer packetDestroyEntity = new PacketContainer(PacketType.Play.Server.ENTITY_DESTROY);
                    packetDestroyEntity.getIntegers().write(0,1);
                    int[] entityID = {player.getEntityId()};
                    packetDestroyEntity.getIntegerArrays().write(1, entityID);

                    PacketContainer packetAddPlayer = new PacketContainer(PacketType.Play.Server.PLAYER_INFO);
                    packetAddPlayer.getGameProfiles().write(0, disguisedPlayerGP);
                    packetAddPlayer.getIntegers().write(0, 1);
                    packetAddPlayer.getPlayerInfoAction().write(1, EnumWrappers.PlayerInfoAction.ADD_PLAYER);

                    PacketContainer namedEntitySpawn = new PacketContainer(PacketType.Play.Server.NAMED_ENTITY_SPAWN);
                    namedEntitySpawn.getIntegers().write(0, player.getEntityId());
                    namedEntitySpawn.getUUIDs().write(0, player.getUniqueId());
                    namedEntitySpawn.getDoubles().write(0, player.getLocation().getX());
                    namedEntitySpawn.getDoubles().write(1, player.getLocation().getY());
                    namedEntitySpawn.getDoubles().write(2, player.getLocation().getZ());


                    try {
                        manager.sendServerPacket(otherPlayer, packetRemovePlayer);
                        manager.sendServerPacket(otherPlayer, packetDestroyEntity);
                        manager.sendServerPacket(otherPlayer, packetAddPlayer);
                        manager.sendServerPacket(otherPlayer, namedEntitySpawn);
                    } catch (InvocationTargetException e) {
                        e.printStackTrace();
                    }


                    //CraftPlayer otherCraftPlayer = (CraftPlayer) otherPlayer;
                    //CraftPlayer disguisedCraftPlayer = (CraftPlayer) player;

                    //otherCraftPlayer.getHandle().playerConnection.sendPacket(new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.REMOVE_PLAYER, disguisedCraftPlayer.getHandle()));
                    //otherCraftPlayer.getHandle().playerConnection.sendPacket(new PacketPlayOutEntityDestroy(disguisedCraftPlayer.getEntityId()));
                    //otherCraftPlayer.getHandle().playerConnection.sendPacket(new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER, disguisedCraftPlayer.getHandle()));
                    //otherCraftPlayer.getHandle().playerConnection.sendPacket(new PacketPlayOutNamedEntitySpawn(disguisedCraftPlayer.getHandle()));
                    //otherCraftPlayer.getHandle().playerConnection.sendPacket(new PacketPlayOutSpawnEntityLiving(disguisedCraftPlayer.getHandle()));

                }

            }
        }.runTaskLater(plugin, 20);

    }

    @Override
    public void clearDisguise (Player player) {

        clearDisguisedName(player);
        clearDisguiseSkin(player);
        player.setDisplayName(disguisedPlayers.get(player.getUniqueId()).getActualName());

        BukkitTask task = new BukkitRunnable() {
            @Override
            public void run() {

                for (Player otherPlayer : Bukkit.getOnlinePlayers()) {

                    if (otherPlayer.getUniqueId() == player.getUniqueId()) {
                        continue;
                    }

                    WrappedGameProfile disguisedPlayerGP = WrappedGameProfile.fromPlayer(player);

                    PacketContainer packetRemovePlayer = new PacketContainer(PacketType.Play.Server.PLAYER_INFO);
                    packetRemovePlayer.getGameProfiles().write(0, disguisedPlayerGP);
                    packetRemovePlayer.getIntegers().write(0, 1);
                    packetRemovePlayer.getPlayerInfoAction().write(1, EnumWrappers.PlayerInfoAction.REMOVE_PLAYER);

                    PacketContainer packetDestroyEntity = new PacketContainer(PacketType.Play.Server.ENTITY_DESTROY);
                    packetDestroyEntity.getIntegers().write(0,1);
                    int[] entityID = {player.getEntityId()};
                    packetDestroyEntity.getIntegerArrays().write(1, entityID);

                    PacketContainer packetAddPlayer = new PacketContainer(PacketType.Play.Server.PLAYER_INFO);
                    packetAddPlayer.getGameProfiles().write(0, disguisedPlayerGP);
                    packetAddPlayer.getIntegers().write(0, 1);
                    packetAddPlayer.getPlayerInfoAction().write(1, EnumWrappers.PlayerInfoAction.ADD_PLAYER);

                    PacketContainer namedEntitySpawn = new PacketContainer(PacketType.Play.Server.NAMED_ENTITY_SPAWN);
                    namedEntitySpawn.getIntegers().write(0, player.getEntityId());
                    namedEntitySpawn.getUUIDs().write(0, player.getUniqueId());
                    namedEntitySpawn.getDoubles().write(0, player.getLocation().getX());
                    namedEntitySpawn.getDoubles().write(1, player.getLocation().getY());
                    namedEntitySpawn.getDoubles().write(2, player.getLocation().getZ());


                    try {
                        manager.sendServerPacket(otherPlayer, packetRemovePlayer);
                        manager.sendServerPacket(otherPlayer, packetDestroyEntity);
                        manager.sendServerPacket(otherPlayer, packetAddPlayer);
                        manager.sendServerPacket(otherPlayer, namedEntitySpawn);
                    } catch (InvocationTargetException e) {
                        e.printStackTrace();
                    }

                    //otherCraftPlayer.getHandle().playerConnection.sendPacket(new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.REMOVE_PLAYER, disguisedCraftPlayer.getHandle()));
                    //otherCraftPlayer.getHandle().playerConnection.sendPacket(new PacketPlayOutEntityDestroy(disguisedCraftPlayer.getEntityId()));
                    //otherCraftPlayer.getHandle().playerConnection.sendPacket(new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER, disguisedCraftPlayer.getHandle()));
                    //otherCraftPlayer.getHandle().playerConnection.sendPacket(new PacketPlayOutNamedEntitySpawn(disguisedCraftPlayer.getHandle()));
                    //otherCraftPlayer.getHandle().playerConnection.sendPacket(new PacketPlayOutSpawnEntityLiving(disguisedCraftPlayer.getHandle()));

                }

            }
        }.runTaskLater(plugin, 20);

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
        }

    }

    @Override
    public void openDisguisedInv (Player player) {

        inv.clear();

        for (Map.Entry<UUID, Disguise> uuid : disguisedPlayers.entrySet()) {

            UUID actualUUID = uuid.getKey();
            Disguise disguise = disguisedPlayers.get(actualUUID);

            ItemStack skull = new ItemStack(Material.PLAYER_HEAD);
            SkullMeta meta = (SkullMeta) skull.getItemMeta();
            setSkin(meta, disguise.getDisguisedTexture());
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

            ItemStack skull = new ItemStack(Material.PLAYER_HEAD);
            SkullMeta meta = (SkullMeta) skull.getItemMeta();
            setSkin(meta, disguise.getDisguisedTexture());
            meta.setDisplayName(Utils.chat("&b&l" + disguise.getDisguisedName()));
            List<String> lore = new ArrayList<>();
            lore.add(0, Utils.chat("&r&fUUID: " + "&b&l" + disguise.getDisguisedUUID()));
            meta.setLore(lore);
            skull.setItemMeta(meta);
            cached.addItem(skull);

        }

        player.openInventory(cached);

    }

    @Override
    public void setCachedDisguise(String name, Player player) {
        super.setCachedDisguise(name, player);
    }

    @Override
    public void asyncDisguise(Player disguiseTarget, UUID disguisedUUID, UUID actualUUID, String disguisedName, String actualName, DisguiseMe plugin) {
        super.asyncDisguise(disguiseTarget, disguisedUUID, actualUUID, disguisedName, actualName, plugin);
    }

    @Override
    public void setSkin (SkullMeta meta, String texture) {
        super.setSkin(meta, texture);
    }

    public Map<String, Disguise> getCachedProfiles () {
        return cachedProfiles;
    }

    @Override
    public Map<UUID, Disguise> getDisguisedPlayers () {
        return disguisedPlayers;
    }

    public boolean isDisguised (UUID uuid) {
        return disguisedPlayers.containsKey(uuid);
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
