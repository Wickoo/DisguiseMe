package com.github.wickoo.disguiseme;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.UUID;

public class DMCommands implements CommandExecutor {

    DisguiseMe plugin;
    DisguiseHandler handler;

    public DMCommands (DisguiseMe plugin, DisguiseHandler handler) {
        this.plugin = plugin;
        this.handler = handler;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        // /disguise <name>
        // /disguise <target> <name>
        // /disguise

        if (!(sender instanceof Player)) {
            sender.sendMessage(DMUtil.chat("Only a player can execute this command!"));
            return true;
        }

        Player player = (Player) sender;
        UUID uuid = player.getUniqueId();

        switch (args.length) {

            case 0: // /disguise

                player.sendMessage(DMUtil.chat("&b&lDisguiseMe &7by author &bWick_\n&bVersion: &7" + plugin.getDescription().getVersion() + "\n&bCommand: &7/disguise <command>"));
                return true;

            case 1: // /disguise <show> - <help> - <disguise> - <list>

                String subcommand = args[0];

                if (subcommand.equalsIgnoreCase("show")) {

                    if (!handler.isDisguised(uuid)) {
                        player.sendMessage(DMUtil.chat("&c&lERROR! &7You are not disguised!"));
                        return true;
                    }

                    Disguise disguise = handler.getDisguisedPlayer(uuid);
                    player.sendMessage(DMUtil.chat("&7Currently disguised as &b" + disguise.getDisguisedName() + " &7with UUID of &b" + disguise.getDisguisedUUID()));

                    return true;

                } if (subcommand.equalsIgnoreCase("clear")) {

                    if (!handler.isDisguised(uuid)) {
                        player.sendMessage(DMUtil.chat("&c&lERROR! &7You are not disguised!"));
                        return true;
                    }
                    
                    handler.clearDisguise(player);
                    handler.removeDisguisedPlayer(uuid);
                    player.sendMessage(DMUtil.chat("b&lSUCCESS! &7You are no longer disguised!"));
                    return true;

                } if (subcommand.equalsIgnoreCase("list")) {

                    handler.openDisguisedInv(player);
                    return true;

                } else if (subcommand.equalsIgnoreCase("help")) {

                    player.sendMessage(DMUtil.chat("&b/disguise show \n &7- Shows current disguise if it exists"));
                    player.sendMessage(DMUtil.chat("&b/disguise help \n &7- Shows commands related to this plugin"));
                    player.sendMessage(DMUtil.chat("&b/disguise <player> \n &7- Disguises yourself as specified player"));
                    player.sendMessage(DMUtil.chat("&b/disguise (otherplayer) <player> \n &7- Disguises another player"));
                    return true;

                } else {

                    String actualName = player.getDisplayName();
                    String disguisedName = args[0];
                    UUID disguisedUUID = Bukkit.getOfflinePlayer(disguisedName).getUniqueId();

                    BukkitTask task = new BukkitRunnable() {
                        @Override
                        public void run() {

                            String[] strings = DMUtil.fetch(disguisedUUID, player);

                            if (strings == null || strings.length == 0) {
                                player.sendMessage(DMUtil.chat("&c&lERROR! &r&7Player &c" + disguisedName + "&7 not found!"));
                                return;
                            }

                            String texture = strings[0];
                            String signature = strings[1];
                            Disguise disguise = new Disguise(disguisedUUID, disguisedName, actualName, uuid);
                            disguise.setDisguisedSignature(signature);
                            disguise.setDisguisedTexture(texture);
                            handler.addDisguised(uuid, disguise);
                            handler.initiateDisguise(player);

                        }
                    }.runTaskAsynchronously(plugin);

                if (!handler.isDisguised(uuid)) {
                    return true;
                }

                player.sendMessage(DMUtil.chat("&b&lSUCCESS! &r&7Now disguised as &b" + disguisedName + "&7!"));
                return true;

                }

            case 2: // /disguise (name) <disguise>

                Player targetPlayer = Bukkit.getPlayer(args[0]);

                if (!Bukkit.getOnlinePlayers().contains(targetPlayer)) {
                    player.sendMessage(DMUtil.chat("&c&lERROR! &7Player &c" + targetPlayer.getDisplayName() + " &7not found!"));
                    return true;
                }

                String targetName = targetPlayer.getDisplayName();
                UUID targetUUID = targetPlayer.getUniqueId();
                String disguiseName = args[1];
                UUID disguiseUUID = Bukkit.getOfflinePlayer(disguiseName).getUniqueId();

                BukkitTask task = new BukkitRunnable() {
                    @Override
                    public void run() {

                        String[] strings = DMUtil.fetch(targetUUID, player);

                        if (strings == null || strings.length == 0) {
                            player.sendMessage(DMUtil.chat("&c&lERROR! &r&7Player '" + disguiseName + "&7' not found!"));
                            return;
                        }

                        String texture = strings[0];
                        String signature = strings[1];
                        Disguise disguise = new Disguise(disguiseUUID, disguiseName, targetName, targetUUID);
                        disguise.setDisguisedSignature(signature);
                        disguise.setDisguisedTexture(texture);
                        handler.addDisguised(targetUUID, disguise);
                        handler.initiateDisguise(targetPlayer);

                    }
                }.runTaskAsynchronously(plugin);

                player.sendMessage(DMUtil.chat("&b&lSUCCESS! &r&7Disguised &b" + targetName + "&7 as &b" + disguiseName + "&7!"));
                targetPlayer.sendMessage(DMUtil.chat("&7You have been disguised as &b" + disguiseName + " &7by &b" + player.getDisplayName()));
                break;

            default:
                player.sendMessage(DMUtil.chat("&eCorrect usages: \n&b/disguise <disguise-name> \n&b/disguise (player) <disguise-name>"));
                break;

        }

        return true;

    }

}
