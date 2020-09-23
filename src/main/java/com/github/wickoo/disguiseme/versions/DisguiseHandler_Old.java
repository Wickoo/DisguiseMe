package com.github.wickoo.disguiseme.versions;

import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.comphenix.protocol.wrappers.PlayerInfoData;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import com.comphenix.protocol.wrappers.WrappedGameProfile;
import com.github.wickoo.disguiseme.Disguise;
import com.github.wickoo.disguiseme.DisguiseMe;
import com.github.wickoo.disguiseme.handlers.GUIHandler;
import com.github.wickoo.disguiseme.packetwrappers.WrapperPlayServerEntityDestroy;
import com.github.wickoo.disguiseme.packetwrappers.WrapperPlayServerNamedEntitySpawnOld;
import com.github.wickoo.disguiseme.packetwrappers.WrapperPlayServerPlayerInfo;
import com.github.wickoo.disguiseme.util.Utils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class DisguiseHandler_Old extends DisguiseHandler {

    private DisguiseMe plugin;
    private ProtocolManager manager;
    private GUIHandler guiHandler;

    private Map<UUID, Disguise> disguisedPlayers;
    private Map<String, Disguise> cachedProfiles;

    private Inventory inv;
    private Inventory cached;

    public DisguiseHandler_Old(DisguiseMe plugin, ProtocolManager manager) {
        this.plugin = plugin;
        this.manager = manager;
        this.guiHandler = new GUIHandler(this);
        this.disguisedPlayers = new HashMap<>();
        cachedProfiles = new HashMap<>();
        inv = Bukkit.createInventory(null, 36, Utils.chat("&b&lCurrent Disguised Players"));
        cached = Bukkit.createInventory(null, 36, Utils.chat("&d&lCurrent Cached Disguises"));
    }

    @Override
    public void setDisguiseSkin (Player player) { super.setDisguiseSkin(player); }

    @Override
    public void clearDisguiseSkin (Player player) { super.clearDisguiseSkin(player); }

    @Override
    public void updateDisguise(Player disguisedPlayer) {

        setDisguiseSkin(disguisedPlayer);
        Disguise disguise = getDisguisedPlayer(disguisedPlayer.getUniqueId());
        setPlayerName(disguisedPlayer, disguise.getDisguisedName());
        disguisedPlayer.setDisplayName(getDisguisedPlayers().get(disguisedPlayer.getUniqueId()).getDisguisedName());

        WrapperPlayServerPlayerInfo serverInfoRemove = new WrapperPlayServerPlayerInfo();
        serverInfoRemove.setAction(EnumWrappers.PlayerInfoAction.REMOVE_PLAYER);
        List<PlayerInfoData> playerInfoDataList = serverInfoRemove.getData();

        //loop and build list of player info
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {

            PlayerInfoData playerInfoData;

            if (onlinePlayer.getUniqueId().equals(disguisedPlayer.getUniqueId())) {

                playerInfoData = new PlayerInfoData(getDisguisedProfile(disguisedPlayer), Utils.getPing(disguisedPlayer), EnumWrappers.NativeGameMode.fromBukkit(disguisedPlayer.getGameMode()), null);
                playerInfoDataList.add(playerInfoData);
                continue;

            }

            playerInfoData = new PlayerInfoData(WrappedGameProfile.fromPlayer(onlinePlayer), Utils.getPing(onlinePlayer), EnumWrappers.NativeGameMode.fromBukkit(onlinePlayer.getGameMode()), null);
            playerInfoDataList.add(playerInfoData);

        }
        serverInfoRemove.setData(playerInfoDataList);


        WrapperPlayServerEntityDestroy packetDestroyEntity = new WrapperPlayServerEntityDestroy();
        packetDestroyEntity.setEntityIds(new int[]{disguisedPlayer.getEntityId()});


        WrapperPlayServerPlayerInfo serverInfoAdd = new WrapperPlayServerPlayerInfo();
        serverInfoAdd.setData(playerInfoDataList);
        serverInfoAdd.setAction(EnumWrappers.PlayerInfoAction.ADD_PLAYER);


        WrapperPlayServerNamedEntitySpawnOld namedEntitySpawn = new WrapperPlayServerNamedEntitySpawnOld();
        namedEntitySpawn.setEntityID(disguisedPlayer.getEntityId());
        namedEntitySpawn.setY((int) disguisedPlayer.getLocation().getY());
        namedEntitySpawn.setX((int) disguisedPlayer.getLocation().getX());
        namedEntitySpawn.setZ((int) disguisedPlayer.getLocation().getZ());
        namedEntitySpawn.setYaw((byte) disguisedPlayer.getLocation().getYaw());
        namedEntitySpawn.setPitch((byte) disguisedPlayer.getLocation().getPitch());
        namedEntitySpawn.setEntityID(disguisedPlayer.getEntityId());
        namedEntitySpawn.setPlayerUuid(disguisedPlayer.getUniqueId());
        namedEntitySpawn.setCurrentItem((short) 0);
        namedEntitySpawn.setMetadata(WrappedDataWatcher.getEntityWatcher(disguisedPlayer));
        namedEntitySpawn.setCurrentItem((short) 0);


        for (Player otherPlayer : Bukkit.getOnlinePlayers()) {

            if (otherPlayer.getUniqueId().equals(disguisedPlayer.getUniqueId())) {
                continue;
            }


            try {
                getManager().sendServerPacket(otherPlayer, serverInfoRemove.getHandle());
                getManager().sendServerPacket(otherPlayer, packetDestroyEntity.getHandle());
                getManager().sendServerPacket(otherPlayer, serverInfoAdd.getHandle());
                getManager().sendServerPacket(otherPlayer, namedEntitySpawn.getHandle());
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }

        }

    }

    @Override
    public void clearDisguise (Player disguisedPlayer) {

        final WrappedGameProfile oldDisguisedProfile = getDisguisedProfile(disguisedPlayer);
        Disguise disguise = getDisguisedPlayer(disguisedPlayer.getUniqueId());
        setPlayerName(disguisedPlayer, disguise.getActualName());
        clearDisguiseSkin(disguisedPlayer);
        disguisedPlayer.setDisplayName(getDisguisedPlayers().get(disguisedPlayer.getUniqueId()).getActualName());

        BukkitTask task = new BukkitRunnable() {
            @Override
            public void run() {

                WrapperPlayServerPlayerInfo serverInfoRemove = new WrapperPlayServerPlayerInfo();
                serverInfoRemove.setAction(EnumWrappers.PlayerInfoAction.REMOVE_PLAYER);
                List<PlayerInfoData> playerInfoDataListOld = serverInfoRemove.getData();

                for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {

                    PlayerInfoData playerInfoData;

                    if (onlinePlayer.getUniqueId().equals(disguisedPlayer.getUniqueId())) {

                        playerInfoData = new PlayerInfoData(oldDisguisedProfile, Utils.getPing(disguisedPlayer), EnumWrappers.NativeGameMode.fromBukkit(disguisedPlayer.getGameMode()), null);
                        playerInfoDataListOld.add(playerInfoData);
                        continue;

                    }

                    playerInfoData = new PlayerInfoData(WrappedGameProfile.fromPlayer(onlinePlayer), Utils.getPing(onlinePlayer), EnumWrappers.NativeGameMode.fromBukkit(onlinePlayer.getGameMode()),null);
                    playerInfoDataListOld.add(playerInfoData);

                }
                serverInfoRemove.setData(playerInfoDataListOld);


                WrapperPlayServerEntityDestroy packetDestroyEntity = new WrapperPlayServerEntityDestroy();
                packetDestroyEntity.setEntityIds(new int[]{disguisedPlayer.getEntityId()});


                WrapperPlayServerPlayerInfo serverInfoAdd = new WrapperPlayServerPlayerInfo();
                serverInfoAdd.setAction(EnumWrappers.PlayerInfoAction.ADD_PLAYER);
                List<PlayerInfoData> playerInfoDataListNew = serverInfoAdd.getData();

                for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {

                    PlayerInfoData playerInfoData = new PlayerInfoData(WrappedGameProfile.fromPlayer(onlinePlayer), Utils.getPing(onlinePlayer), EnumWrappers.NativeGameMode.fromBukkit(onlinePlayer.getGameMode()),null);
                    playerInfoDataListNew.add(playerInfoData);

                }
                serverInfoAdd.setData(playerInfoDataListNew);



                WrapperPlayServerNamedEntitySpawnOld namedEntitySpawn = new WrapperPlayServerNamedEntitySpawnOld();
                namedEntitySpawn.setY((int) disguisedPlayer.getLocation().getY());
                namedEntitySpawn.setX((int) disguisedPlayer.getLocation().getX());
                namedEntitySpawn.setZ((int) disguisedPlayer.getLocation().getZ());
                namedEntitySpawn.setYaw((byte) disguisedPlayer.getLocation().getYaw());
                namedEntitySpawn.setPitch((byte) disguisedPlayer.getLocation().getPitch());
                namedEntitySpawn.setEntityID(disguisedPlayer.getEntityId());
                namedEntitySpawn.setPlayerUuid(disguisedPlayer.getUniqueId());
                namedEntitySpawn.setCurrentItem((short) 0);



                for (Player otherPlayer : Bukkit.getOnlinePlayers()) {

                    if (otherPlayer.getUniqueId().equals(disguisedPlayer.getUniqueId())) {
                        continue;
                    }


                    try {
                        getManager().sendServerPacket(otherPlayer, serverInfoRemove.getHandle());
                        getManager().sendServerPacket(otherPlayer, packetDestroyEntity.getHandle());
                        getManager().sendServerPacket(otherPlayer, serverInfoAdd.getHandle());
                        getManager().sendServerPacket(otherPlayer, namedEntitySpawn.getHandle());
                    } catch (InvocationTargetException e) {
                        e.printStackTrace();
                    }

                }

            }
        }.runTaskLater(getPlugin(), 0);

        removeDisguisedPlayer(disguisedPlayer.getUniqueId());
        disguisedPlayer.sendMessage(Utils.chat("&b&lSUCCESS! &r&7You are no longer disguised."));

    }

    @Override
    public void setPlayerName (Player player, String name) { super.setPlayerName(player, name); }

    @Override
    public void setCachedDisguise(String name, Player player) {
        super.setCachedDisguise(name, player);
    }

    @Override
    public void setSkin (SkullMeta meta, String texture) {
        super.setSkin(meta, texture);
    }

    @Override
    public Map<String, Disguise> getCachedProfiles () {
        return cachedProfiles;
    }

    @Override
    public Map<UUID, Disguise> getDisguisedPlayers () {
        return disguisedPlayers;
    }

    @Override
    public boolean isDisguised (UUID uuid) {
        return disguisedPlayers.containsKey(uuid);
    }

    @Override
    public Disguise getDisguisedPlayer (UUID uuid) {
        return disguisedPlayers.get(uuid);
    }

    @Override
    public void addToCachedProfiles (String string, Disguise disguise) {
        cachedProfiles.put(string, disguise);
    }

    @Override
    public void addDisguised (UUID uuid, Disguise disguise) {
        disguisedPlayers.put(uuid, disguise);
    }

    @Override
    public void removeDisguisedPlayer (UUID uuid) {
        disguisedPlayers.remove(uuid);
    }

    @Override
    public DisguiseMe getPlugin () { return plugin; }

    @Override
    public ProtocolManager getManager () { return manager; }

    @Override
    public Inventory getInv () { return inv; }

    @Override
    public Inventory getCachedInv () { return cached; }

    @Override
    public GUIHandler getGUIHandler () { return guiHandler; }
}
