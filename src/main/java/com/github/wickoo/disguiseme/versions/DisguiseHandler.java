package com.github.wickoo.disguiseme.versions;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.github.wickoo.disguiseme.Disguise;
import com.github.wickoo.disguiseme.DisguiseMe;
import com.github.wickoo.disguiseme.util.Utils;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.UUID;

public abstract class DisguiseHandler {

    public void setDisguiseSkin (Player player) { }

    public void clearDisguiseSkin (Player player) { }

    public void initiateDisguise (Player player) { }

    public void clearDisguise (Player player) { }

    public void setDisguiseName (Player player) { }

    public void clearDisguisedName (Player player) { }

    public void openDisguisedInv (Player player) { }

    public void openCachedInv (Player player) { }

    public void setSkin (SkullMeta meta, String texture) {

        GameProfile profile = new GameProfile(UUID.randomUUID(), null);
        profile.getProperties().put("textures", new Property("textures", texture));
        Field profileField = null;
        try {
            profileField = meta.getClass().getDeclaredField("profile");
        } catch (NoSuchFieldException | SecurityException e) {
            e.printStackTrace();
        }
        profileField.setAccessible(true);
        try {
            profileField.set(meta, profile);
        } catch (IllegalArgumentException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public void addPacketListener (ProtocolManager manager, DisguiseMe plugin) {

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
                    Player player = event.getPlayer();
                    setDisguiseSkin(player);
                    setDisguiseName(player);

                }

            }
        });

    }

    public void asyncDisguise (Player disguiseTarget, UUID disguisedUUID, UUID actualUUID, String disguisedName, String actualName, DisguiseMe plugin) {

        if (this.isDisguised(disguiseTarget.getUniqueId())) {
            this.clearDisguise(disguiseTarget);
            this.removeDisguisedPlayer(disguiseTarget.getUniqueId());
        }

        BukkitTask task = new BukkitRunnable() {
            @Override
            public void run() {

                String[] strings = Utils.fetch(disguisedUUID, disguiseTarget);

                if (strings == null || strings.length == 0) {
                    disguiseTarget.sendMessage(Utils.chat("&c&lERROR! &r&7Player &c" + disguisedName + "&7 not found!"));
                    return;
                }

                if (strings[0] == null) {
                    return;
                }

                String texture = strings[0];
                String signature = strings[1];
                Disguise disguise = new Disguise(disguisedUUID, disguisedName, actualName, actualUUID);
                disguise.setDisguisedSignature(signature);
                disguise.setDisguisedTexture(texture);
                addDisguised(actualUUID, disguise);
                initiateDisguise(disguiseTarget);
                addToCachedProfiles(disguisedName, disguise);
                disguiseTarget.sendMessage(Utils.chat("&b&lSUCCESS! &r&7Now disguised as &b" + disguisedName + "&7!"));

            }
        }.runTaskAsynchronously(plugin);

    }

    public void setCachedDisguise (String name, Player player) {

        if (this.isDisguised(player.getUniqueId())) {
            this.clearDisguise(player);
            this.removeDisguisedPlayer(player.getUniqueId());
        }

        if (this.isDisguised(player.getUniqueId())) {
            this.clearDisguise(player);
            this.removeDisguisedPlayer(player.getUniqueId());
        }

        Disguise cachedDisguise = this.getCachedProfiles().get(name);
        cachedDisguise.setActualName(player.getName());
        cachedDisguise.setActualUUID(player.getUniqueId());
        this.addDisguised(player.getUniqueId(), cachedDisguise);
        this.initiateDisguise(player);
        player.sendMessage(Utils.chat("&b&lSUCCESS! &r&7Now disguised as &b" + player.getName() + "&7!"));

    }

    public Map<String, Disguise> getCachedProfiles () { return null; }

    public Map<UUID, Disguise> getDisguisedPlayers () { return null; }

    public boolean isDisguised (UUID uuid) { return false; }

    public Disguise getDisguisedPlayer (UUID uuid) { return null; }

    public void addToCachedProfiles (String string, Disguise disguise) { }

    public void addDisguised (UUID uuid, Disguise disguise) { }

    public void removeDisguisedPlayer (UUID uuid) { }
}