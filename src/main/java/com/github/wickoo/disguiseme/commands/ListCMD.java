package com.github.wickoo.disguiseme.commands;

import com.github.wickoo.disguiseme.DMCommands;
import com.github.wickoo.disguiseme.DisguiseHandler;
import org.bukkit.entity.Player;

public class ListCMD implements CommandManager {

    @Override
    public String getName() {
        return "list";
    }

    @Override
    public String getPermission() {
        return "disguiseme.list";
    }

    @Override
    public String getDescription() {
        return "List of all currently disguised players";
    }

    @Override
    public void executeCommand(Player player, DisguiseHandler disguiseHandler, DMCommands core) {
        disguiseHandler.openDisguisedInv(player);
    }

}