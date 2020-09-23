package com.github.wickoo.disguiseme.commands;

import com.github.wickoo.disguiseme.Disguise;
import com.github.wickoo.disguiseme.DisguiseMe;
import com.github.wickoo.disguiseme.util.Utils;
import com.github.wickoo.disguiseme.versions.DisguiseHandler;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

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

        String targetName;
        UUID targetUUID = targetPlayer.getUniqueId();
        String disguisedName = args[2];


        if (disguiseHandler.isDisguised(targetPlayer.getUniqueId())) targetName = disguiseHandler.getDisguisedPlayer(targetUUID).getActualName();
        else targetName = targetPlayer.getName();

        CompletableFuture<Disguise> completableFuture = CompletableFuture.supplyAsync(() -> Disguise.buildDisguise(targetUUID, targetName, disguisedName)).thenApply(disguise -> {

            if (disguise == null) {
                player.sendMessage(Utils.chat("&c&lERROR! &7The player &c" + disguisedName + " &7does not exist"));
                return null;
            }

            disguiseHandler.registerDisguise(targetPlayer, disguise);
            disguiseHandler.updateDisguise(targetPlayer);
            targetPlayer.sendMessage(Utils.chat("&b&lSUCCESS! &b" + player.getName() + " &7disguised you as &b" + disguisedName));
            player.sendMessage(Utils.chat("&b&lSUCCESS! &7You disguised &b" + targetName + " &7as &b" + disguisedName));
            return disguise;

        });

    }

}