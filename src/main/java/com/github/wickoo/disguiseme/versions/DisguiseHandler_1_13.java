package com.github.wickoo.disguiseme.versions;

import com.comphenix.protocol.ProtocolManager;
import com.github.wickoo.disguiseme.Disguise;
import com.github.wickoo.disguiseme.DisguiseMe;
import com.github.wickoo.disguiseme.util.Utils;
import net.minecraft.server.v1_13_R2.PacketPlayOutEntityDestroy;
import net.minecraft.server.v1_13_R2.PacketPlayOutNamedEntitySpawn;
import net.minecraft.server.v1_13_R2.PacketPlayOutPlayerInfo;
import net.minecraft.server.v1_13_R2.PacketPlayOutSpawnEntityLiving;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_13_R2.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;

public class DisguiseHandler_1_13 extends DisguiseHandler {

    private DisguiseMe plugin;
    private ProtocolManager manager;

    private Map<UUID, Disguise> disguisedPlayers;
    private Map<String, Disguise> cachedProfiles;

    private Inventory inv;
    private Inventory cached;

    public DisguiseHandler_1_13 (DisguiseMe plugin, ProtocolManager manager) {
        this.plugin = plugin;
        this.manager = manager;
        this.disguisedPlayers = new HashMap<>();
        cachedProfiles = new HashMap<>();
        inv = Bukkit.createInventory(null, 36, Utils.chat("&b&lCurrent Disguised Players"));
        cached = Bukkit.createInventory(null, 36, Utils.chat("&d&lCurrent Cached Disguises"));
    }

    @Override
    public void setDisguiseSkin (Player player) { super.setDisguiseSkin(player); }

    @Override
    public void clearDisguiseSkin (Player player, String signature) { super.clearDisguiseSkin(player, signature); }

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

                    CraftPlayer otherCraftPlayer = (CraftPlayer) otherPlayer;
                    CraftPlayer disguisedCraftPlayer = (CraftPlayer) player;

                    otherCraftPlayer.getHandle().playerConnection.sendPacket(new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.REMOVE_PLAYER, disguisedCraftPlayer.getHandle()));
                    otherCraftPlayer.getHandle().playerConnection.sendPacket(new PacketPlayOutEntityDestroy(disguisedCraftPlayer.getEntityId()));
                    otherCraftPlayer.getHandle().playerConnection.sendPacket(new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER, disguisedCraftPlayer.getHandle()));
                    otherCraftPlayer.getHandle().playerConnection.sendPacket(new PacketPlayOutNamedEntitySpawn(disguisedCraftPlayer.getHandle()));
                    otherCraftPlayer.getHandle().playerConnection.sendPacket(new PacketPlayOutSpawnEntityLiving(disguisedCraftPlayer.getHandle()));

                }

            }
        }.runTaskLater(plugin, 20);

    }

    @Override
    public void clearDisguise (Player player) {

        clearDisguisedName(player);
        getSignatureValue(player, plugin);
        player.setDisplayName(disguisedPlayers.get(player.getUniqueId()).getActualName());

        BukkitTask task = new BukkitRunnable() {
            @Override
            public void run() {

                for (Player otherPlayer : Bukkit.getOnlinePlayers()) {

                    if (otherPlayer.getUniqueId() == player.getUniqueId()) {
                        continue;
                    }

                    CraftPlayer otherCraftPlayer = (CraftPlayer) otherPlayer;
                    CraftPlayer disguisedCraftPlayer = (CraftPlayer) player;

                    otherCraftPlayer.getHandle().playerConnection.sendPacket(new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.REMOVE_PLAYER, disguisedCraftPlayer.getHandle()));
                    otherCraftPlayer.getHandle().playerConnection.sendPacket(new PacketPlayOutEntityDestroy(disguisedCraftPlayer.getEntityId()));
                    otherCraftPlayer.getHandle().playerConnection.sendPacket(new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER, disguisedCraftPlayer.getHandle()));
                    otherCraftPlayer.getHandle().playerConnection.sendPacket(new PacketPlayOutNamedEntitySpawn(disguisedCraftPlayer.getHandle()));
                    otherCraftPlayer.getHandle().playerConnection.sendPacket(new PacketPlayOutSpawnEntityLiving(disguisedCraftPlayer.getHandle()));

                }

            }
        }.runTaskLater(plugin, 20);

    }

    @Override
    public void setDisguiseName (Player player) { super.setDisguiseName(player); }

    @Override
    public void clearDisguisedName (Player player) { super.clearDisguisedName(player); }

    @Override
    public void getSignatureValue (Player player, DisguiseMe plugin) { super.getSignatureValue(player, plugin); }

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
