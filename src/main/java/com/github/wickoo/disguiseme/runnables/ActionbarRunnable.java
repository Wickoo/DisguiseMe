package com.github.wickoo.disguiseme.runnables;

import com.comphenix.protocol.wrappers.EnumWrappers;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import com.github.wickoo.disguiseme.Disguise;
import com.github.wickoo.disguiseme.packetwrappers.WrapperPlayServerChat;
import com.github.wickoo.disguiseme.util.Utils;
import com.github.wickoo.disguiseme.versions.DisguiseHandler;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.UUID;

public class ActionbarRunnable extends BukkitRunnable {

    final private DisguiseHandler disguiseHandler;

    public ActionbarRunnable(DisguiseHandler disguiseHandler) {
        this.disguiseHandler = disguiseHandler;
    }

    @Override
    public void run() {

        Map<UUID, Disguise> disguisedPlayers = disguiseHandler.getDisguisedPlayers();

        for (UUID uuid : disguisedPlayers.keySet()) {

            Player disguisedPlayer = Bukkit.getPlayer(uuid);
            Disguise disguise = disguisedPlayers.get(uuid);

            if (disguisedPlayer == null) return;

            WrapperPlayServerChat actionbarPacket = new WrapperPlayServerChat();
            String message = Utils.chat("&7You are currently &b&lDISGUISED &7as &b&l" + disguise.getDisguisedName());
            WrappedChatComponent chatComponent = WrappedChatComponent.fromText(message);
            actionbarPacket.setChatType(EnumWrappers.ChatType.GAME_INFO);
            actionbarPacket.setMessage(chatComponent);

            try {
                disguiseHandler.getManager().sendServerPacket(disguisedPlayer, actionbarPacket.getHandle());
            } catch (InvocationTargetException e) {
                e.printStackTrace();


            }

        }

    }

}
