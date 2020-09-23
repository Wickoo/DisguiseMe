package com.github.wickoo.disguiseme.events;

import com.github.wickoo.disguiseme.DisguiseMe;
import com.github.wickoo.disguiseme.inventories.GUI;
import com.github.wickoo.disguiseme.versions.DisguiseHandler;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;

import java.util.Map;
import java.util.UUID;

public class InvClose implements Listener {

    private DisguiseMe plugin;
    private DisguiseHandler disguiseHandler;

    public InvClose (DisguiseMe plugin, DisguiseHandler disguiseHandler) {

        this.plugin = plugin;
        this.disguiseHandler = disguiseHandler;

    }

    @EventHandler
    public void onInvClose (InventoryCloseEvent event) {

        Player player = (Player) event.getPlayer();
        Map<UUID, GUI> openInventories = disguiseHandler.getGUIHandler().getOpenInventory();
        openInventories.remove(player.getUniqueId());
        disguiseHandler.getGUIHandler().setOpenInventory(openInventories);

    }

}
