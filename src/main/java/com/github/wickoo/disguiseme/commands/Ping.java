package com.github.wickoo.disguiseme.commands;

import com.github.wickoo.disguiseme.util.Utils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Ping implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {

        if (!(commandSender instanceof Player)) {
            return true;
        }

        Player player = (Player) commandSender;
        player.sendMessage(Utils.chat("&fYour current ping is &a" + Utils.getPing(player) + "ms"));

        return true;

    }

}
