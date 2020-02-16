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

        for (int i = 0; i < commands.size(); i++) {

            player.sendMessage(Utils.chat("&b/disguise " + commands.get(i).getName() + "\n&7 - " + commands.get(i).getDescription()));

        }

    }

}