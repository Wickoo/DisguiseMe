package com.github.wickoo.disguiseme.commands;

import com.github.wickoo.disguiseme.DisguiseMe;
import com.github.wickoo.disguiseme.versions.DisguiseHandler;
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
    public String getAdditionalArgs() {
        return "";
    }

    @Override
    public String getDescription() {
        return "List of all currently disguised players";
    }

    @Override
    public void executeCommand(Player player, DisguiseHandler disguiseHandler, CoreCMD core, String[] args, DisguiseMe plugin) {
        disguiseHandler.openDisguisedInv(player);
    }

}