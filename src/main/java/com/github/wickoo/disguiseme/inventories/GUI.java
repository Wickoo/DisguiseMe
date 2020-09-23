package com.github.wickoo.disguiseme.inventories;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public abstract class GUI {

    public int getPage() { return 0; }

    public void executeAction (ItemStack itemstack, Player player) { }


}
