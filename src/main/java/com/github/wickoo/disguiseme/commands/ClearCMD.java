package com.github.wickoo.disguiseme.commands;

import com.github.wickoo.disguiseme.DisguiseMe;
import com.github.wickoo.disguiseme.util.Utils;
import com.github.wickoo.disguiseme.versions.DisguiseHandler;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class ClearCMD implements CommandManager {

    @Override
    public String getName() {
        return "clear";
    }

    @Override
    public String getPermission() {
        return "disguiseme.clear";
    }

    @Override
    public String getAdditionalArgs() {
        return "";
    }

    @Override
    public String getDescription() {
        return "Clears current disguise (if one exists)";
    }

    @Override
    public void executeCommand(Player player, DisguiseHandler disguiseHandler, CoreCMD core, String[] args, DisguiseMe plugin) {

        if (args.length == 2) {

            Player targetPlayer = Bukkit.getPlayer(args[1]);

            if (targetPlayer == null) {
                player.sendMessage(Utils.chat("&c&lERROR! &7Player &c " + args[1] + " &7not found!"));
                return;
            }

            if (!disguiseHandler.isDisguised(targetPlayer.getUniqueId())) {
                player.sendMessage(Utils.chat("&c&lERROR! &7Player &c" + args[1] + " &7is not disguised!"));
                return;
            }

            disguiseHandler.clearDisguise(targetPlayer);
            disguiseHandler.removeDisguisedPlayer(targetPlayer.getUniqueId());
            player.sendMessage(Utils.chat("&b&lSUCCESS! &7Player &b" + args[1] + " &7is no longer disguised!"));
            targetPlayer.sendMessage(Utils.chat("&b&lSUCCESS! &b" + player.getName() + " &7has removed your disguise"));
            return;

        }

        if (!disguiseHandler.isDisguised(player.getUniqueId())) {
            player.sendMessage(Utils.chat("&c&lERROR! &7You are not disguised!"));
            return;
        }

        disguiseHandler.clearDisguise(player);
        disguiseHandler.removeDisguisedPlayer(player.getUniqueId());
        player.sendMessage(Utils.chat("&b&lSUCCESS! &7You are no longer disguised"));
    }

}