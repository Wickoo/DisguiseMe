package com.github.wickoo.disguiseme.inventories;

import com.github.wickoo.disguiseme.versions.DisguiseHandler;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class CachedInventory extends GUI {

    private DisguiseHandler disguiseHandler;

    private int pageNumber = 0;

    public CachedInventory (DisguiseHandler disguiseHandler) {

        this.disguiseHandler = disguiseHandler;

    }

    public void executeAction (ItemStack itemStack, Player player) {

        if (itemStack == null) return;

        ItemMeta itemMeta = itemStack.getItemMeta();

        switch(itemStack.getType()) {

            case PLAYER_HEAD:

                disguiseHandler.setCachedDisguise(ChatColor.stripColor(itemMeta.getDisplayName()), player);
                //
                break;

            case ARROW:
                //lol
                //
                break;
            default:



        }

    }

}
