package com.github.wickoo.disguiseme.versions;

import com.comphenix.protocol.ProtocolManager;
import com.github.wickoo.disguiseme.Disguise;
import com.github.wickoo.disguiseme.DisguiseMe;
import com.github.wickoo.disguiseme.handlers.GUIHandler;
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
    private GUIHandler guiHandler;

    private Map<UUID, Disguise> disguisedPlayers;
    private Map<String, Disguise> cachedProfiles;

    private Inventory inv;
    private Inventory cached;

    public DisguiseHandler_New(DisguiseMe plugin, ProtocolManager manager) {
        this.plugin = plugin;
        this.manager = manager;
        this.guiHandler = new GUIHandler(this);
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
    public void updateDisguise(Player disguisedPlayer) { super.updateDisguise(disguisedPlayer); }

    @Override
    public void clearDisguise (Player disguisedPlayer) { super.clearDisguise(disguisedPlayer); }

    @Override
    public void setPlayerName (Player player, String name) { super.setPlayerName(player, name); }

    @Override
    public void setCachedDisguise(String name, Player player) {
        super.setCachedDisguise(name, player);
    }

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

    @Override
    public GUIHandler getGUIHandler () { return guiHandler; }
}
