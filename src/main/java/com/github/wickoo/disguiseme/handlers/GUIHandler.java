package com.github.wickoo.disguiseme.handlers;

import com.github.wickoo.disguiseme.Disguise;
import com.github.wickoo.disguiseme.inventories.GUI;
import com.github.wickoo.disguiseme.util.Utils;
import com.github.wickoo.disguiseme.versions.DisguiseHandler;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.*;

public class GUIHandler {

    private DisguiseHandler disguiseHandler;
    private Inventory disguisedInventory;
    private Inventory cachedInventory;

    private Map<UUID, GUI> openInventory;

    public GUIHandler (DisguiseHandler disguiseHandler) {

        this.disguiseHandler = disguiseHandler;
        openInventory = new HashMap<>();

    }

    public void openDisguisedPlayersInventory (Player player) {

        disguisedInventory = Bukkit.createInventory(null, 54, "Disguised players");

        int pagesRequired = (int) Math.ceil(disguiseHandler.getDisguisedPlayers().keySet().size() / 38.0);
        int numberOfDisguised = disguiseHandler.getDisguisedPlayers().keySet().size();

        int[] darkPaneIndexes = {0,1,2,3,4,5,6,7,8,45,46,47,48,49,50,51,52,53};
        for (int i : darkPaneIndexes) disguisedInventory.setItem(i, Utils.buildStack(Material.BLACK_STAINED_GLASS_PANE, " "));
        for (int i = 9; i < 45; i++) disguisedInventory.setItem(i, Utils.buildStack(Material.LIGHT_GRAY_STAINED_GLASS_PANE, " "));
        disguisedInventory.setItem(4, Utils.buildStack(Material.OAK_SIGN, "&7There are currently &b&l" + numberOfDisguised + " player(s) &7disguised"));
        if (pagesRequired > 1) disguisedInventory.setItem(52, Utils.buildStack(Material.ARROW, "&6&lNext Page"));

        int i = 0;
        for (Map.Entry<UUID, Disguise> uuid : disguiseHandler.getDisguisedPlayers().entrySet()) {

            UUID actualUUID = uuid.getKey();
            Disguise disguise = disguiseHandler.getDisguisedPlayers().get(actualUUID);

            //ItemStack skull = new ItemStack(Material.getMaterial("SKULL_ITEM"), 1, (short) 3);
            ItemStack skull = new ItemStack(Material.PLAYER_HEAD);
            SkullMeta meta = (SkullMeta) skull.getItemMeta();
            disguiseHandler.setSkin(meta, disguise.getDisguisedTexture());
            meta.setDisplayName(Utils.chat("&b&l" + disguise.getActualName()));
            List<String> lore = new ArrayList<>();
            lore.add(0, Utils.chat("&r&fDisguised as: " + "&b&l" + disguise.getDisguisedName()));
            lore.add(1, Utils.chat("&r&fDisguise UUID: " + "&b&l" + disguise.getDisguisedUUID()));
            meta.setLore(lore);
            skull.setItemMeta(meta);
            disguisedInventory.setItem(i + 9, skull);
            i++;

        }

        player.openInventory(disguisedInventory);


    }

    public void openCachedDisguisesInventory (Player player) {

        cachedInventory = Bukkit.createInventory(null, 54, "Cached disguises");

        int cachedNumber = disguiseHandler.getCachedProfiles().keySet().size();
        int pagesRequired = (int) Math.ceil(disguiseHandler.getCachedProfiles().keySet().size() / 38.0);

        int[] darkPaneIndexes = {0,1,2,3,4,5,6,7,8,45,46,47,48,49,50,51,52,53};
        for (int i : darkPaneIndexes) cachedInventory.setItem(i, Utils.buildStack(Material.BLACK_STAINED_GLASS_PANE, " "));
        for (int i = 9; i < 45; i++) cachedInventory.setItem(i, Utils.buildStack(Material.LIGHT_GRAY_STAINED_GLASS_PANE, " "));
        cachedInventory.setItem(4, Utils.buildStack(Material.OAK_SIGN, "&7There are currently &b&l" + cachedNumber + " disguise(s) &7locally cached"));
        if (pagesRequired > 1) disguisedInventory.setItem(52, Utils.buildStack(Material.ARROW, "&6&lNext Page"));

        int i = 0;
        for (Map.Entry<String, Disguise> string : disguiseHandler.getCachedProfiles().entrySet()) {

            String disguiseName = string.getKey();
            Disguise disguise = disguiseHandler.getCachedProfiles().get(disguiseName);

            //ItemStack skull = new ItemStack(Material.getMaterial("SKULL_ITEM"), 1, (short) 3);
            ItemStack skull = new ItemStack(Material.PLAYER_HEAD);
            SkullMeta meta = (SkullMeta) skull.getItemMeta();
            disguiseHandler.setSkin(meta, disguise.getDisguisedTexture());
            meta.setDisplayName(Utils.chat("&b&l" + disguise.getDisguisedName()));
            List<String> lore = new ArrayList<>();
            lore.add(0, Utils.chat("&r&fUUID: " + "&b&l" + disguise.getDisguisedUUID()));
            meta.setLore(lore);
            skull.setItemMeta(meta);
            cachedInventory.setItem(i + 9, skull);
            i++;

        }

        player.openInventory(cachedInventory);


    }

    public Map<UUID, GUI> getOpenInventory() {
        return openInventory;
    }

    public void setOpenInventory(Map<UUID, GUI> openInventory) {
        this.openInventory = openInventory;
    }
}
