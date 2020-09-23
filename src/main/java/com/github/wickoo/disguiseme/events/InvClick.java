package com.github.wickoo.disguiseme.events;

import com.github.wickoo.disguiseme.DisguiseMe;
import com.github.wickoo.disguiseme.handlers.GUIHandler;
import com.github.wickoo.disguiseme.inventories.GUI;
import com.github.wickoo.disguiseme.versions.DisguiseHandler;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class InvClick implements Listener {

    private DisguiseMe plugin;
    private DisguiseHandler handler;
    private GUIHandler guiHandler;

    public InvClick (DisguiseMe plugin) {
        this.plugin = plugin;
        this.handler = plugin.getDisguiseHandler();
        this.guiHandler = handler.getGUIHandler();
    }

    
    @EventHandler
    public void onClick (InventoryClickEvent e) {

        Player player = (Player) e.getWhoClicked();
        ItemStack itemStack = e.getCurrentItem();
        GUI gui = guiHandler.getOpenInventory().get(player.getUniqueId());
        if (gui == null) return;
        e.setCancelled(true);
        gui.executeAction(itemStack, player);


    }

}
