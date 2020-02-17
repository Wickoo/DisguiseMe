package com.github.wickoo.disguiseme.commands;

import com.github.wickoo.disguiseme.Disguise;
import com.github.wickoo.disguiseme.DisguiseMe;
import com.github.wickoo.disguiseme.util.Utils;
import com.github.wickoo.disguiseme.versions.DisguiseHandler;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@SuppressWarnings("deprecation")
public class CoreCMD implements CommandExecutor, TabExecutor {

    private DisguiseMe plugin;
    private DisguiseHandler handler;

    private List<CommandManager> commands;

    public CoreCMD(DisguiseMe plugin, DisguiseHandler handler) {
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

        if (!(sender instanceof Player)) {
            sender.sendMessage(Utils.chat("Only a player can execute this command!"));
            return true;
        }

        Player player = (Player) sender;
        UUID uuid = player.getUniqueId();

        switch (args.length) {

            default:
                player.sendMessage(Utils.chat("&cIncorrect Usage! &7See /disguise help for proper implementations"));
                break;

            case 0:

                String version = Bukkit.getVersion().substring(Bukkit.getVersion().lastIndexOf(':') + 1).replace(')', ' ');
                player.sendMessage(Utils.chat("&b&lDisguiseMe &7by author &bWick_\n&bPlugin Version: &7" +
                        plugin.getDescription().getVersion() + "\n&bCommand: &7/disguise <command>" + "\n&bMinecraft Version:&7" + version));
                return true;

            case 1:

                String subcommand = args[0];

                for (CommandManager command : commands) {

                    if (command.getName().equalsIgnoreCase(subcommand)) {

                        command.executeCommand(player, handler, this, args);
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
                    player.sendMessage(Utils.chat("&b&lSUCCESS! &r&7Now disguised as &b" + disguisedName + "&7!"));
                    return true;

                }

                handler.asyncDisguise(player, disguisedUUID, uuid, disguisedName, actualName, plugin);

                return true;

            case 2: // /disguise (name) <disguise>

                Player targetPlayer = Bukkit.getPlayer(args[0]);

                if (!Bukkit.getOnlinePlayers().contains(targetPlayer)) {
                    player.sendMessage(Utils.chat("&c&lERROR! &7Player &c" + args[0] + " &7not found!"));
                    return true;
                }

                String targetName = targetPlayer.getDisplayName();
                UUID targetUUID = targetPlayer.getUniqueId();
                String disguisedName2 = args[1];
                UUID disguisedUUID2 = Bukkit.getOfflinePlayer(disguisedName2).getUniqueId();

                if (handler.isDisguised(targetUUID)) {
                    handler.clearDisguise(targetPlayer);
                    handler.removeDisguisedPlayer(targetUUID);
                }

                if (handler.getCachedProfiles().containsKey(disguisedName2)) {

                    Disguise cachedDisguise = handler.getCachedProfiles().get(disguisedName2);
                    cachedDisguise.setActualName(targetName);
                    cachedDisguise.setActualUUID(targetUUID);
                    handler.addDisguised(targetUUID, cachedDisguise);
                    handler.initiateDisguise(player);
                    player.sendMessage(Utils.chat("&b&lSUCCESS! &r&7Now disguised as &b" + disguisedName2 + "&7!"));
                    return true;

                }

                handler.asyncDisguise(targetPlayer, disguisedUUID2, targetUUID, disguisedName2, targetName, plugin);
                break;

        }

        return true;

    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {

        if (command.getName().equalsIgnoreCase("disguise")) {

            ArrayList<String> autoCompletes = new ArrayList<>();

            if (args.length == 1) {

                for (CommandManager commandManager : commands) {

                    autoCompletes.add(commandManager.getName());

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
