package com.github.wickoo.disguiseme.commands;

import com.github.wickoo.disguiseme.DisguiseMe;
import com.github.wickoo.disguiseme.util.Utils;
import com.github.wickoo.disguiseme.versions.DisguiseHandler;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

@SuppressWarnings("deprecation")
public class SelfCMD implements CommandManager {

    @Override
    public String getName() {
        return "self";
    }

    @Override
    public String getPermission() {
        return "disguiseme.self";
    }

    @Override
    public String getAdditionalArgs() {
        return "(disguise)";
    }

    @Override
    public String getDescription() {
        return "Disguises yourself as a player";
    }

    @Override
    public void executeCommand(Player player, DisguiseHandler disguiseHandler, CoreCMD core, String[] args, DisguiseMe plugin) {

        if (!(args.length == 2)) {
            player.sendMessage(Utils.chat("&c&lIncorrect Usage! &7See /disguise help for proper syntax"));
            return;
        }

        String actualName = player.getDisplayName();
        String disguisedName = args[1];
        UUID disguisedUUID = Bukkit.getOfflinePlayer(disguisedName).getUniqueId();

        if (disguiseHandler.getCachedProfiles().containsKey(disguisedName)) {
            disguiseHandler.setCachedDisguise(disguisedName, player);
            return;

        }

        disguiseHandler.asyncDisguise(player, disguisedUUID, player.getUniqueId(), disguisedName, actualName, plugin);

    }

}