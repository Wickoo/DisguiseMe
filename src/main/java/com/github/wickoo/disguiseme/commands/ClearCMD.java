package com.github.wickoo.disguiseme.commands;

import com.github.wickoo.disguiseme.DMCommands;
import com.github.wickoo.disguiseme.DMUtil;
import com.github.wickoo.disguiseme.DisguiseHandler;
import org.bukkit.entity.Player;

public class ClearCMD implements CommandManager {

    @Override
    public String getName() {
        return "clear";
    }

    @Override
    public String getPermission() {
        return "disguiseme.clear";
    }

    @Override
    public String getDescription() {
        return "Clears current disguise (if one exists)";
    }

    @Override
    public void executeCommand(Player player, DisguiseHandler disguiseHandler, DMCommands core) {
        if (!disguiseHandler.isDisguised(player.getUniqueId())) {
            player.sendMessage(DMUtil.chat("&c&lERROR! &7You are not disguised!"));
        }

        disguiseHandler.clearDisguise(player);
        disguiseHandler.removeDisguisedPlayer(player.getUniqueId());
        player.sendMessage(DMUtil.chat("&b&lSUCCESS! &7You are no longer disguised!"));
    }

}