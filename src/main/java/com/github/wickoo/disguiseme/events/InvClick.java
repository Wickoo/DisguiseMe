package com.github.wickoo.disguiseme.events;

import com.github.wickoo.disguiseme.DMUtil;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

public class InvClick implements Listener {

    @EventHandler
    public void onClick (InventoryClickEvent e) {

        if (!(e.getView().getTitle().equals(DMUtil.chat("&b&lCurrent Disguised Players")) || e.getView().getTitle().equals(DMUtil.chat("&d&lCurrent Cached Disguises")))) {
            return;
        }
        e.setCancelled(true);

    }

}
