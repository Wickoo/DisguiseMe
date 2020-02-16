package com.github.wickoo.disguiseme.commands;

import com.github.wickoo.disguiseme.Disguise;
import com.github.wickoo.disguiseme.util.Utils;
import com.github.wickoo.disguiseme.versions.DisguiseHandler;
import org.bukkit.entity.Player;

import java.util.UUID;

public class ShowCMD implements CommandManager {

    @Override
    public String getName() {
        return "show";
    }

    @Override
    public String getPermission() {
        return "disguiseme.show";
    }

    @Override
    public String getDescription() {
        return "Returns current disguise (if present)";
    }

    @Override
    public void executeCommand(Player player, DisguiseHandler disguiseHandler, CoreCMD core, String[] args) {
        UUID uuid = player.getUniqueId();
        if (!disguiseHandler.isDisguised(uuid)) {
            player.sendMessage(Utils.chat("&c&lERROR! &7You are not disguised!"));
            return;
        }

        Disguise disguise = disguiseHandler.getDisguisedPlayer(uuid);
        player.sendMessage(Utils.chat("&7Currently disguised as &b" + disguise.getDisguisedName() + " &7with UUID of &b" + disguise.getDisguisedUUID()));
    }

}