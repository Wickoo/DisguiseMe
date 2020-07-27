package com.github.wickoo.disguiseme.commands;

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
        commands.add(new SelfCMD());
        commands.add(new OtherCMD());
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
                player.sendMessage(Utils.chat("&c&lIncorrect Usage! &7See /disguise help for proper syntax"));
                break;

            case 0:

                String version = Bukkit.getVersion().substring(Bukkit.getVersion().lastIndexOf(':') + 1).replace(')', ' ');
                player.sendMessage(Utils.chat("&b&lDisguiseMe &7by author &bWick_\n&bPlugin Version: &7" +
                        plugin.getDescription().getVersion() + "\n&bCommand: &7/disguise <command>" + "\n&bMinecraft Version:&7" + version));
                return true;

            case 1:
            case 2:
            case 3:

                String subcommand = args[0];

                for (CommandManager command : commands) {

                    if (command.getName().equalsIgnoreCase(subcommand)) {

                        if(!player.hasPermission(command.getPermission())) {

                            player.sendMessage(Utils.chat("&c&lERROR! &7Insufficient permissions"));
                            return true;

                        }

                        command.executeCommand(player, handler, this, args, plugin);
                        return true;

                    }

                }

                player.sendMessage(Utils.chat("&c&lERROR! &7Unknown command &c/disguise " + args[0] ));
                return true;

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
