package com.github.wickoo.disguiseme.commands;

import com.github.wickoo.disguiseme.DMCommands;
import com.github.wickoo.disguiseme.DisguiseHandler;
import org.bukkit.entity.Player;

public class CachedCMD implements CommandManager {

    @Override
    public String getName() {
        return "cached";
    }

    @Override
    public String getPermission() {
        return "disguiseme.cached";
    }

    @Override
    public String getDescription() {
        return "Viewing of all current cached profiles";
    }

    @Override
    public void executeCommand(Player player, DisguiseHandler disguiseHandler, DMCommands core) {
        disguiseHandler.openCachedInv(player);
    }

}