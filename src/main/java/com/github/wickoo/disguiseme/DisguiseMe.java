package com.github.wickoo.disguiseme;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.github.wickoo.disguiseme.commands.CoreCMD;
import com.github.wickoo.disguiseme.events.InvClick;
import com.github.wickoo.disguiseme.events.PlayerJoin;
import com.github.wickoo.disguiseme.util.Utils;
import com.github.wickoo.disguiseme.versions.DisguiseHandler;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class DisguiseMe extends JavaPlugin {

    private DisguiseMe plugin;

    private DisguiseHandler disguiseHandler;
    private ProtocolManager protocolManager;

    @Override
    public void onEnable() {

        plugin = this;
        protocolManager = ProtocolLibrary.getProtocolManager();
        disguiseHandler = Utils.getHandlerByVer(Bukkit.getVersion(), protocolManager, this);

        if (disguiseHandler == null) {
            this.getPluginLoader().disablePlugin(this);
            return;
        }

        this.getCommand("disguise").setExecutor(new CoreCMD(plugin, disguiseHandler));
        this.getServer().getPluginManager().registerEvents(new InvClick(), this);
        this.getServer().getPluginManager().registerEvents(new PlayerJoin(disguiseHandler), this);

        disguiseHandler.addPacketListener(protocolManager, this);

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public DisguiseHandler getDisguiseHandler () {
        return disguiseHandler;
    }

}
