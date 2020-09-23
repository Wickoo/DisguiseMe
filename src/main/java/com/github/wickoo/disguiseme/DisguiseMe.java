package com.github.wickoo.disguiseme;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.github.wickoo.disguiseme.commands.CoreCMD;
import com.github.wickoo.disguiseme.commands.Ping;
import com.github.wickoo.disguiseme.events.CommandPreProcess;
import com.github.wickoo.disguiseme.events.InvClick;
import com.github.wickoo.disguiseme.events.InvClose;
import com.github.wickoo.disguiseme.events.PlayerJoin;
import com.github.wickoo.disguiseme.runnables.ActionbarRunnable;
import com.github.wickoo.disguiseme.util.Utils;
import com.github.wickoo.disguiseme.versions.DisguiseHandler;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Level;

public final class DisguiseMe extends JavaPlugin {

    private DisguiseMe plugin;

    private DisguiseHandler disguiseHandler;
    private ProtocolManager protocolManager;

    @Override
    public void onEnable() {

        plugin = this;
        protocolManager = ProtocolLibrary.getProtocolManager();
        disguiseHandler = Utils.getHandlerByVer(Bukkit.getVersion(), protocolManager, this);

        String version = Bukkit.getVersion().substring(Bukkit.getVersion().lastIndexOf(':') + 1).replace(')', ' ');

        if (disguiseHandler == null) {
            this.getPluginLoader().disablePlugin(this);
            getLogger().log(Level.INFO, "Disabling plugin... DisguiseMe! does not support Minecraft version" + version);
            return;
        }

        this.getCommand("disguise").setExecutor(new CoreCMD(plugin, disguiseHandler));
        this.getCommand("ping").setExecutor(new Ping());
        this.getServer().getPluginManager().registerEvents(new InvClick(plugin), this);
        this.getServer().getPluginManager().registerEvents(new PlayerJoin(disguiseHandler), this);
        this.getServer().getPluginManager().registerEvents(new CommandPreProcess(plugin), this);
        this.getServer().getPluginManager().registerEvents(new InvClose(plugin, disguiseHandler), this);

        disguiseHandler.addPacketListener(protocolManager, this);
        getLogger().log(Level.INFO, "DisguiseMe! successfully loaded on Minecraft version" + version);

        ActionbarRunnable actionbarRunnable = new ActionbarRunnable(disguiseHandler);
        actionbarRunnable.runTaskTimer(this, 0, 40);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public DisguiseHandler getDisguiseHandler () {
        return disguiseHandler;
    }

}
