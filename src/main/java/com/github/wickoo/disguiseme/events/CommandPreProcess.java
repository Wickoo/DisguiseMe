package com.github.wickoo.disguiseme.events;

import com.github.wickoo.disguiseme.Disguise;
import com.github.wickoo.disguiseme.DisguiseMe;
import com.github.wickoo.disguiseme.versions.DisguiseHandler;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import java.util.Map;
import java.util.UUID;

public class CommandPreProcess implements Listener {

    private DisguiseMe plugin;
    private DisguiseHandler handler;


    public CommandPreProcess (DisguiseMe plugin) {
        this.plugin = plugin;
        handler = plugin.getDisguiseHandler();

    }

    @EventHandler
    public void onCommand (PlayerCommandPreprocessEvent e) {

        String command = e.getMessage();
        String[] args = command.split(" ");

        if (args[0].equalsIgnoreCase("disguise") || args[0].equalsIgnoreCase("dis") || args[0].equalsIgnoreCase("d") || args[0].equalsIgnoreCase("disguiseme")) {
            return;
        }

        for (Map.Entry<UUID, Disguise> uuidDisguiseEntry : handler.getDisguisedPlayers().entrySet()) {

            UUID uuid = uuidDisguiseEntry.getKey();
            Disguise disguise = handler.getDisguisedPlayer(uuid);
            String disguisedName = disguise.getDisguisedName();
            String actualName = disguise.getActualName();

            if (command.contains(disguisedName)) {

                String updatedCommand = command.replace(disguisedName, actualName);
                e.setMessage(updatedCommand);
                return;

            }

        }

    }

}
