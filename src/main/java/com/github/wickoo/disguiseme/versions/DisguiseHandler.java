package com.github.wickoo.disguiseme.versions;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.*;
import com.github.wickoo.disguiseme.Disguise;
import com.github.wickoo.disguiseme.DisguiseMe;
import com.github.wickoo.disguiseme.packetwrappers.WrapperPlayServerNamedEntitySpawn;
import com.github.wickoo.disguiseme.packetwrappers.WrapperPlayServerPlayerInfo;
import com.github.wickoo.disguiseme.util.Utils;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public abstract class DisguiseHandler {

    public void setDisguiseSkin (Player player) {

        WrappedGameProfile gameProfile = WrappedGameProfile.fromPlayer(player);
        Multimap<String, WrappedSignedProperty> propertiesMap = gameProfile.getProperties();

        Disguise disguise = this.getDisguisedPlayer(player.getUniqueId());
        disguise.setActualSignature(propertiesMap.get("textures").iterator().next().getSignature());
        disguise.setActualTexture(propertiesMap.get("textures").iterator().next().getValue());

        propertiesMap.removeAll("textures");
        String signature = disguise.getDisguisedSignature();
        String localTexture = disguise.getDisguisedTexture();

        WrappedSignedProperty textures = new WrappedSignedProperty("textures", localTexture, signature);
        propertiesMap.put("textures", textures);

    }

    public WrappedGameProfile getNewProfile (Player player) {

        WrappedGameProfile gameProfile = WrappedGameProfile.fromPlayer(player);
        Multimap<String, WrappedSignedProperty> propertiesMap = gameProfile.getProperties();

        Disguise disguise = this.getDisguisedPlayer(player.getUniqueId());
        disguise.setActualSignature(propertiesMap.get("textures").iterator().next().getSignature());
        disguise.setActualTexture(propertiesMap.get("textures").iterator().next().getValue());

        propertiesMap.removeAll("textures");
        String signature = disguise.getDisguisedSignature();
        String localTexture = disguise.getDisguisedTexture();

        WrappedSignedProperty textures = new WrappedSignedProperty("textures", localTexture, signature);
        propertiesMap.put("textures", textures);
        return gameProfile;

    }

    public void clearDisguiseSkin (Player player) {

        WrappedGameProfile gameProfile = WrappedGameProfile.fromPlayer(player);
        Multimap<String, WrappedSignedProperty> propertiesMap = gameProfile.getProperties();
        propertiesMap.removeAll("textures");

        Disguise disguise = this.getDisguisedPlayer(player.getUniqueId());
        String signature = disguise.getActualSignature();
        String localTexture = disguise.getActualTexture();

        WrappedSignedProperty textures = new WrappedSignedProperty("textures", localTexture, signature);
        propertiesMap.put("textures", textures);

    }

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

                    Player player = event.getPlayer();
                    setDisguiseSkin(player);
                    setDisguiseName(player);
                    initiateDisguise(player);

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
                    initiateDisguise(player);

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