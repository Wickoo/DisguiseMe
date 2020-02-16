package com.github.wickoo.disguiseme;

import com.github.wickoo.disguiseme.commands.*;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class DMCommands implements CommandExecutor, TabExecutor {

    private DisguiseMe plugin;
    private DisguiseHandler handler;

    private List<CommandManager> commands;

    public DMCommands (DisguiseMe plugin, DisguiseHandler handler) {
        this.plugin = plugin;
        this.handler = handler;

        commands = new ArrayList<>();
        commands.add(new HelpCMD());
        commands.add(new ShowCMD());
        commands.add(new ListCMD());
        commands.add(new ClearCMD());
        commands.add(new CachedCMD());
    }

    @Override
    public boolean onCommand(CommandSender sender, Command basecommand, String label, String[] args) {

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

            default:
                player.sendMessage(DMUtil.chat("&cIncorrect Usage! &7See /disguise help for proper implementations"));
                break;

            case 0:

                player.sendMessage(DMUtil.chat("&b&lDisguiseMe &7by author &bWick_\n&bVersion: &7" + plugin.getDescription().getVersion() + "\n&bCommand: &7/disguise <command>"));
                return true;

            case 1:

                String subcommand = args[0];

                for (CommandManager command : commands) {

                    if (command.getName().equalsIgnoreCase(subcommand)) {

                        command.executeCommand(player, handler, this);
                        return true;

                    }

                }

                if (handler.isDisguised(uuid)) {
                    handler.clearDisguise(player);
                    handler.removeDisguisedPlayer(uuid);
                }

                String actualName = player.getDisplayName();
                String disguisedName = args[0];
                UUID disguisedUUID = Bukkit.getOfflinePlayer(disguisedName).getUniqueId();

                if (handler.getCachedProfiles().containsKey(disguisedName)) {

                    Disguise cachedDisguise = handler.getCachedProfiles().get(disguisedName);
                    cachedDisguise.setActualName(actualName);
                    cachedDisguise.setActualUUID(uuid);
                    handler.addDisguised(uuid, cachedDisguise);
                    handler.initiateDisguise(player);
                    player.sendMessage(DMUtil.chat("&b&lSUCCESS! &r&7Now disguised as &b" + disguisedName + "&7!"));
                    return true;

                }

                BukkitTask task = new BukkitRunnable() {
                    @Override
                    public void run() {

                        String[] strings = DMUtil.fetch(disguisedUUID, player);

                        if (strings == null || strings.length == 0) {
                            player.sendMessage(DMUtil.chat("&c&lERROR! &r&7Player &c" + disguisedName + "&7 not found!"));
                            return;
                        }

                        if (strings[0] == null) {
                            return;
                        }

                        String texture = strings[0];
                        String signature = strings[1];
                        Disguise disguise = new Disguise(disguisedUUID, disguisedName, actualName, uuid);
                        disguise.setDisguisedSignature(signature);
                        disguise.setDisguisedTexture(texture);
                        handler.addDisguised(uuid, disguise);
                        handler.initiateDisguise(player);
                        player.sendMessage(DMUtil.chat("&b&lSUCCESS! &r&7Now disguised as &b" + disguisedName + "&7!"));
                        handler.addToCachedProfiles(disguisedName, disguise);

                    }
                }.runTaskAsynchronously(plugin);

                return true;

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

                if (handler.isDisguised(targetUUID)) {
                    handler.clearDisguise(targetPlayer);
                    handler.removeDisguisedPlayer(targetUUID);
                }

                if (handler.getCachedProfiles().containsKey(disguiseName)) {

                    Disguise cachedDisguise = handler.getCachedProfiles().get(disguiseName);
                    cachedDisguise.setActualName(targetName);
                    cachedDisguise.setActualUUID(targetUUID);
                    handler.addDisguised(targetUUID, cachedDisguise);
                    handler.initiateDisguise(player);
                    player.sendMessage(DMUtil.chat("&b&lSUCCESS! &r&7Now disguised as &b" + disguiseName + "&7!"));
                    return true;

                }

                BukkitTask task2 = new BukkitRunnable() {
                    @Override
                    public void run() {

                        String[] strings = DMUtil.fetch(targetUUID, player);

                        if (strings == null || strings.length == 0) {
                            player.sendMessage(DMUtil.chat("&c&lERROR! &r&7Player '" + disguiseName + "&7' not found!"));
                            return;
                        }

                        if (strings[0] == null) {
                            return;
                        }

                        String texture = strings[0];
                        String signature = strings[1];
                        Disguise disguise = new Disguise(disguiseUUID, disguiseName, targetName, targetUUID);
                        disguise.setDisguisedSignature(signature);
                        disguise.setDisguisedTexture(texture);
                        handler.addDisguised(targetUUID, disguise);
                        handler.initiateDisguise(targetPlayer);
                        handler.addToCachedProfiles(disguiseName, disguise);

                        player.sendMessage(DMUtil.chat("&b&lSUCCESS! &r&7Disguised &b" + targetName + "&7 as &b" + disguiseName + "&7!"));
                        targetPlayer.sendMessage(DMUtil.chat("&7You have been disguised as &b" + disguiseName + " &7by &b" + player.getDisplayName()));

                    }
                }.runTaskAsynchronously(plugin);
                break;

        }

        return true;

    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {

        if (command.getName().equalsIgnoreCase("disguise")) {

            ArrayList<String> autoCompletes = new ArrayList<>();

            if (args.length == 1) {

                for (int i = 0; i <commands.size(); i++) {

                    autoCompletes.add(commands.get(i).getName());

                }

                return autoCompletes;

            }

        }

        return null;
    }

    public List<CommandManager> getCommands () {
        return commands;
    }

}
