package com.github.wickoo.disguiseme.events;

import com.github.wickoo.disguiseme.Disguise;
import com.github.wickoo.disguiseme.versions.DisguiseHandler;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerJoin implements Listener {

    DisguiseHandler handler;
    Disguise disguise;

    public PlayerJoin (DisguiseHandler handler) {
        this.handler = handler;
    }

    @EventHandler
    public void onJoin (PlayerJoinEvent e) {

        Player player = e.getPlayer();

        if (!handler.isDisguised(player.getUniqueId())) {
            return;
        }

        Disguise disguise = handler.getDisguisedPlayer(player.getUniqueId());
        player.setDisplayName(disguise.getDisguisedName());


    }

}
