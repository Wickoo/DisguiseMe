package com.github.wickoo.disguiseme.events;

import com.github.wickoo.disguiseme.DisguiseMe;
import com.github.wickoo.disguiseme.util.Utils;
import com.github.wickoo.disguiseme.versions.DisguiseHandler;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class InvClick implements Listener {

    public DisguiseMe plugin;
    public DisguiseHandler handler;

    public InvClick (DisguiseMe plugin) {
        this.plugin = plugin;
        this.handler = plugin.getDisguiseHandler();
    }

    
    @EventHandler
    public void onClick (InventoryClickEvent e) {

        String cachedInvName = "&d&lCurrent Cached Disguises";
        String disguisedListName = "&b&lCurrent Disguised Players";

        String clickedName = e.getView().getTitle();

        if (!(clickedName.equals(Utils.chat(cachedInvName)) || clickedName.equals(Utils.chat(disguisedListName)))) {
            return;
        }

        e.setCancelled(true);

        if (clickedName.equals(Utils.chat(cachedInvName))) {

            ItemStack clickedItem = e.getCurrentItem();

            if (clickedItem == null) {
                return;
            }
            if (!clickedItem.hasItemMeta()) {
                return;
            }
            if (!clickedItem.getItemMeta().hasDisplayName()) {
                return;
            }

            String disguiseName = ChatColor.stripColor(clickedItem.getItemMeta().getDisplayName());

            if (handler.getCachedProfiles().containsKey(disguiseName)) {
                handler.setCachedDisguise(disguiseName, (Player) e.getWhoClicked());
            }



        }



    }

}
