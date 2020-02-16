package com.github.wickoo.disguiseme.versions;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.github.wickoo.disguiseme.Disguise;
import com.github.wickoo.disguiseme.DisguiseMe;
import org.bukkit.entity.Player;

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

                    setDisguiseSkin(event.getPlayer());
                    setDisguiseName(event.getPlayer());

                }

            }
        });

    }

    public Map<String, Disguise> getCachedProfiles () { return null; }

    public boolean isDisguised (UUID uuid) { return false; }

    public Disguise getDisguisedPlayer (UUID uuid) { return null; }

    public void addToCachedProfiles (String string, Disguise disguise) { }

    public void addDisguised (UUID uuid, Disguise disguise) { }

    public void removeDisguisedPlayer (UUID uuid) { }
}