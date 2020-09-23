package com.github.wickoo.disguiseme.commands;

import com.github.wickoo.disguiseme.DisguiseMe;
import com.github.wickoo.disguiseme.inventories.CachedInventory;
import com.github.wickoo.disguiseme.inventories.GUI;
import com.github.wickoo.disguiseme.versions.DisguiseHandler;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.UUID;

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
    public String getAdditionalArgs() {
        return "";
    }

    @Override
    public String getDescription() {
        return "Viewing of all current cached profiles";
    }

    @Override
    public void executeCommand(Player player, DisguiseHandler disguiseHandler, CoreCMD core, String[] args, DisguiseMe plugin) {

        disguiseHandler.getGUIHandler().openCachedDisguisesInventory(player);

        Map<UUID, GUI> openedInventories = disguiseHandler.getGUIHandler().getOpenInventory();
        openedInventories.put(player.getUniqueId(), new CachedInventory(disguiseHandler));
        disguiseHandler.getGUIHandler().setOpenInventory(openedInventories);
    }

}