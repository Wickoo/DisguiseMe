package com.github.wickoo.disguiseme.versions;

import com.comphenix.protocol.ProtocolManager;
import com.github.wickoo.disguiseme.Disguise;
import com.github.wickoo.disguiseme.DisguiseMe;
import com.github.wickoo.disguiseme.util.Utils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class DisguiseHandler_New extends DisguiseHandler {

    private DisguiseMe plugin;
    private ProtocolManager manager;

    private Map<UUID, Disguise> disguisedPlayers;
    private Map<String, Disguise> cachedProfiles;

    private Inventory inv;
    private Inventory cached;

    public DisguiseHandler_New(DisguiseMe plugin, ProtocolManager manager) {
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
    public void clearDisguiseSkin (Player player) { super.clearDisguiseSkin(player); }

    @Override
    public void initiateDisguise(Player disguisedPlayer) { super.initiateDisguise(disguisedPlayer); }

    @Override
    public void clearDisguise (Player disguisedPlayer) { super.clearDisguise(disguisedPlayer); }

    @Override
    public void setDisguiseName (Player player) { super.setDisguiseName(player); }

    @Override
    public void clearDisguisedName (Player player) { super.clearDisguisedName(player); }

    @Override
    public void openDisguisedInv (Player player) { super.openDisguisedInv(player); }

    @Override
    public void openCachedInv (Player player) { super.openCachedInv(player); }

    @Override
    public void setCachedDisguise(String name, Player player) {
        super.setCachedDisguise(name, player);
    }

    @Override
    public void asyncDisguise(Player disguiseTarget, UUID disguisedUUID, UUID actualUUID, String disguisedName, String actualName) { super.asyncDisguise(disguiseTarget, disguisedUUID, actualUUID, disguisedName, actualName); }

    @Override
    public void setSkin (SkullMeta meta, String texture) {
        super.setSkin(meta, texture);
    }

    @Override
    public Map<String, Disguise> getCachedProfiles () {
        return cachedProfiles;
    }

    @Override
    public Map<UUID, Disguise> getDisguisedPlayers () {
        return disguisedPlayers;
    }

    @Override
    public boolean isDisguised (UUID uuid) {
        return disguisedPlayers.containsKey(uuid);
    }

    @Override
    public Disguise getDisguisedPlayer (UUID uuid) {
        return disguisedPlayers.get(uuid);
    }

    @Override
    public void addToCachedProfiles (String string, Disguise disguise) {
        cachedProfiles.put(string, disguise);
    }

    @Override
    public void addDisguised (UUID uuid, Disguise disguise) {
        disguisedPlayers.put(uuid, disguise);
    }

    @Override
    public void removeDisguisedPlayer (UUID uuid) {
        disguisedPlayers.remove(uuid);
    }

    @Override
    public DisguiseMe getPlugin () { return plugin; }

    @Override
    public ProtocolManager getManager () { return manager; }

    @Override
    public Inventory getInv () { return inv; }

    @Override
    public Inventory getCachedInv () { return cached; }
}
