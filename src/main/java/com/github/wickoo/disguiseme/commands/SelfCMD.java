package com.github.wickoo.disguiseme.commands;

import com.github.wickoo.disguiseme.Disguise;
import com.github.wickoo.disguiseme.DisguiseMe;
import com.github.wickoo.disguiseme.util.Utils;
import com.github.wickoo.disguiseme.versions.DisguiseHandler;
import org.bukkit.entity.Player;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

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

        String actualName;
        UUID playerUUID = player.getUniqueId();
        String disguisedName = args[1];

        if (disguiseHandler.isDisguised(player.getUniqueId())) actualName = disguiseHandler.getDisguisedPlayer(playerUUID).getActualName();
        else actualName = player.getName();

        CompletableFuture<Disguise> completableFuture = CompletableFuture.supplyAsync(() -> Disguise.buildDisguise(playerUUID, actualName, disguisedName)).thenApply(disguise -> {

            if (disguise == null) {
                player.sendMessage(Utils.chat("&c&lERROR! &7The player &c" + disguisedName + " &7does not exist"));
                return null;
            }

            disguiseHandler.registerDisguise(player, disguise);
            disguiseHandler.updateDisguise(player);
            player.sendMessage(Utils.chat("&b&lSUCCESS! &7You are now disguised as &b" + disguisedName));
            return disguise;

        });

    }

}