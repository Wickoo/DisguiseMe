package com.github.wickoo.disguiseme.commands;

import com.github.wickoo.disguiseme.DisguiseMe;
import com.github.wickoo.disguiseme.util.Utils;
import com.github.wickoo.disguiseme.versions.DisguiseHandler;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

public class OtherCMD implements CommandManager {

    @Override
    public String getName() {
        return "other";
    }

    @Override
    public String getPermission() {
        return "disguiseme.other";
    }

    @Override
    public String getAdditionalArgs() {
        return "<player> (disguise)";
    }

    @Override
    public String getDescription() {
        return "Disguise other players";
    }

    @Override
    public void executeCommand(Player player, DisguiseHandler disguiseHandler, CoreCMD core, String[] args, DisguiseMe plugin) {

        if (!(args.length == 3)) {
            player.sendMessage(Utils.chat("&c&lIncorrect Usage! &7See /disguise help for proper syntax"));
            return;
        }

        Player targetPlayer = Bukkit.getPlayer(args[1]);

        if (!Bukkit.getOnlinePlayers().contains(targetPlayer)) {
            player.sendMessage(Utils.chat("&c&lERROR! &7Player &c" + args[0] + " &7not found!"));
            return;
        }

        String targetName = targetPlayer.getName();
        UUID targetUUID = targetPlayer.getUniqueId();
        String disguisedName2 = args[2];
        UUID disguisedUUID2 = Bukkit.getOfflinePlayer(disguisedName2).getUniqueId();

        if (disguiseHandler.getCachedProfiles().containsKey(disguisedName2)) {
            disguiseHandler.setCachedDisguise(disguisedName2, targetPlayer);
            return;
        }

        disguiseHandler.asyncDisguise(targetPlayer, disguisedUUID2, targetUUID, disguisedName2, targetName, plugin);
        player.sendMessage(Utils.chat("&7Disguised &b" + targetName + " &7as &b" + disguisedName2));

    }

}