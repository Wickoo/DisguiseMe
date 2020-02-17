package com.github.wickoo.disguiseme.commands;

import com.github.wickoo.disguiseme.util.Utils;
import com.github.wickoo.disguiseme.versions.DisguiseHandler;
import org.bukkit.entity.Player;

public class FixCMD implements CommandManager {

    @Override
    public String getName() {
        return "fix";
    }

    @Override
    public String getPermission() {
        return "disguiseme.fix";
    }

    @Override
    public String getDescription() {
        return "Fixes disguise";
    }

    @Override
    public void executeCommand(Player player, DisguiseHandler disguiseHandler, CoreCMD core, String[] args) {
        disguiseHandler.initiateDisguise(player);
        player.sendMessage(Utils.chat("&b&lSUCCESS! &7Fixed disguise"));
    }

}