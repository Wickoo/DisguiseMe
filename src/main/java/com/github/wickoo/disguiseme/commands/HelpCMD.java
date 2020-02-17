package com.github.wickoo.disguiseme.commands;

import com.github.wickoo.disguiseme.util.Utils;
import com.github.wickoo.disguiseme.versions.DisguiseHandler;
import org.bukkit.entity.Player;

import java.util.List;

public class HelpCMD implements CommandManager {

    @Override
    public String getName() {
        return "help";
    }

    @Override
    public String getPermission() {
        return "disguiseme.help";
    }

    @Override
    public String getDescription() {
        return "List of all applicable disguise commands";
    }

    @Override
    public void executeCommand(Player player, DisguiseHandler disguiseHandler, CoreCMD core, String[] args) {
        List<CommandManager> commands = core.getCommands();

        for (CommandManager command : commands) {

            player.sendMessage(Utils.chat("&b/disguise " + command.getName() + "\n&7 - " + command.getDescription()));

        }

    }

}