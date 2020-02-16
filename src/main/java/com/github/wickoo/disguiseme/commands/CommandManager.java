package com.github.wickoo.disguiseme.commands;

import com.github.wickoo.disguiseme.versions.DisguiseHandler;
import org.bukkit.entity.Player;

public interface CommandManager {

    String getName ();
    String getPermission ();
    String getDescription();
    void executeCommand (Player player, DisguiseHandler disguiseHandler, CoreCMD core, String[] args);

}
