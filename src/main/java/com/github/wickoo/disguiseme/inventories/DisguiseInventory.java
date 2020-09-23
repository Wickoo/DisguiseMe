package com.github.wickoo.disguiseme.inventories;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class DisguiseInventory extends GUI {

    private int pageNumber = 0;

    public void executeAction (ItemStack itemStack, Player player) {

        if (itemStack == null) return;

        if (itemStack.getType() == Material.ARROW) {

            ItemMeta itemMeta = itemStack.getItemMeta();
            //TBF

        }

    }

}
