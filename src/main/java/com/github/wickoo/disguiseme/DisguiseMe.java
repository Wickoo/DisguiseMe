package com.github.wickoo.disguiseme;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.github.wickoo.disguiseme.events.InvClick;
import com.github.wickoo.disguiseme.events.PlayerJoin;
import org.bukkit.plugin.java.JavaPlugin;

public final class DisguiseMe extends JavaPlugin {

    private DisguiseMe plugin;

    private DisguiseHandler disguiseHandler;
    private ProtocolManager protocolManager;

    @Override
    public void onEnable() {

        plugin = this;
        disguiseHandler = new DisguiseHandler(plugin);

        this.getCommand("disguise").setExecutor(new DMCommands(plugin, disguiseHandler));
        this.getServer().getPluginManager().registerEvents(new InvClick(), this);
        this.getServer().getPluginManager().registerEvents(new PlayerJoin(disguiseHandler), this);

        protocolManager = ProtocolLibrary.getProtocolManager();
        disguiseHandler.addPacketListener(protocolManager);

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public DisguiseHandler getDisguiseHandler () {
        return disguiseHandler;
    }

}
