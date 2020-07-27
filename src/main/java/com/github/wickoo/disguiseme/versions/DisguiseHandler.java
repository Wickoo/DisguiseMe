package com.github.wickoo.disguiseme.versions;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.comphenix.protocol.wrappers.PlayerInfoData;
import com.comphenix.protocol.wrappers.WrappedGameProfile;
import com.comphenix.protocol.wrappers.WrappedSignedProperty;
import com.github.wickoo.disguiseme.Disguise;
import com.github.wickoo.disguiseme.DisguiseMe;
import com.github.wickoo.disguiseme.packetwrappers.WrapperPlayServerEntityDestroy;
import com.github.wickoo.disguiseme.packetwrappers.WrapperPlayServerNamedEntitySpawn;
import com.github.wickoo.disguiseme.packetwrappers.WrapperPlayServerPlayerInfo;
import com.github.wickoo.disguiseme.util.Utils;
import com.google.common.collect.Multimap;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public abstract class DisguiseHandler {

    public void setDisguiseSkin (Player player) {

        WrappedGameProfile gameProfile = WrappedGameProfile.fromPlayer(player);
        Multimap<String, WrappedSignedProperty> propertiesMap = gameProfile.getProperties();

        Disguise disguise = this.getDisguisedPlayer(player.getUniqueId());
        disguise.setActualSignature(propertiesMap.get("textures").iterator().next().getSignature());
        disguise.setActualTexture(propertiesMap.get("textures").iterator().next().getValue());

        propertiesMap.removeAll("textures");
        String signature = disguise.getDisguisedSignature();
        String localTexture = disguise.getDisguisedTexture();

        WrappedSignedProperty textures = new WrappedSignedProperty("textures", localTexture, signature);
        propertiesMap.put("textures", textures);

    }

    public WrappedGameProfile getDisguisedProfile(Player player) {

        WrappedGameProfile gameProfile = WrappedGameProfile.fromPlayer(player);
        Multimap<String, WrappedSignedProperty> propertiesMap = gameProfile.getProperties();

        Disguise disguise = this.getDisguisedPlayer(player.getUniqueId());

        propertiesMap.removeAll("textures");
        String signature = disguise.getDisguisedSignature();
        String localTexture = disguise.getDisguisedTexture();

        WrappedSignedProperty textures = new WrappedSignedProperty("textures", localTexture, signature);
        propertiesMap.put("textures", textures);

        WrappedGameProfile newProfile = WrappedGameProfile.fromPlayer(player).withName(disguise.getDisguisedName());
        newProfile.getProperties().putAll(gameProfile.getProperties());
        return newProfile;

    }

    public void clearDisguiseSkin (Player player) {

        WrappedGameProfile gameProfile = WrappedGameProfile.fromPlayer(player);
        Multimap<String, WrappedSignedProperty> propertiesMap = gameProfile.getProperties();
        propertiesMap.removeAll("textures");

        Disguise disguise = this.getDisguisedPlayer(player.getUniqueId());
        String localTexture = disguise.getActualTexture();
        String localSignature = disguise.getActualSignature();

        WrappedSignedProperty textures = new WrappedSignedProperty("textures", localTexture, localSignature);
        propertiesMap.put("textures", textures);

    }

    public void initiateDisguise(Player disguisedPlayer) {

        setDisguiseSkin(disguisedPlayer);
        setDisguiseName(disguisedPlayer);
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


        WrapperPlayServerNamedEntitySpawn namedEntitySpawn = new WrapperPlayServerNamedEntitySpawn();
        namedEntitySpawn.setY(disguisedPlayer.getLocation().getY());
        namedEntitySpawn.setX(disguisedPlayer.getLocation().getX());
        namedEntitySpawn.setZ(disguisedPlayer.getLocation().getZ());
        namedEntitySpawn.setYaw(disguisedPlayer.getLocation().getYaw());
        namedEntitySpawn.setPitch(disguisedPlayer.getLocation().getPitch());
        namedEntitySpawn.setEntityID(disguisedPlayer.getEntityId());
        namedEntitySpawn.setPlayerUUID(disguisedPlayer.getUniqueId());


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

    public void clearDisguise (Player disguisedPlayer) {

        final WrappedGameProfile oldDisguisedProfile = getDisguisedProfile(disguisedPlayer);
        clearDisguisedName(disguisedPlayer);
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



                WrapperPlayServerNamedEntitySpawn namedEntitySpawn = new WrapperPlayServerNamedEntitySpawn();
                namedEntitySpawn.setY(disguisedPlayer.getLocation().getY());
                namedEntitySpawn.setX(disguisedPlayer.getLocation().getX());
                namedEntitySpawn.setZ(disguisedPlayer.getLocation().getZ());
                namedEntitySpawn.setYaw(disguisedPlayer.getLocation().getYaw());
                namedEntitySpawn.setPitch(disguisedPlayer.getLocation().getPitch());
                namedEntitySpawn.setEntityID(disguisedPlayer.getEntityId());
                namedEntitySpawn.setPlayerUUID(disguisedPlayer.getUniqueId());



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

    public void setDisguiseName (Player player) {

        Disguise disguise = this.getDisguisedPlayer(player.getUniqueId());
        Class<?> craftPlayerClass = player.getClass();

        try {
            Method getProfileMethod = craftPlayerClass.getDeclaredMethod("getProfile");
            getProfileMethod.setAccessible(true);
            Object gameProfile = getProfileMethod.invoke(player);
            Field field = gameProfile.getClass().getDeclaredField("name");
            field.setAccessible(true);
            field.set(gameProfile, ChatColor.stripColor(disguise.getDisguisedName()));
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException | NoSuchFieldException e) {
            e.printStackTrace();
        }

    }

    public void clearDisguisedName (Player player) {

        Disguise disguise = this.getDisguisedPlayer(player.getUniqueId());
        Class<?> craftPlayerClass = player.getClass();

        try {
            Method getProfileMethod = craftPlayerClass.getDeclaredMethod("getProfile");
            getProfileMethod.setAccessible(true);
            Object gameProfile = getProfileMethod.invoke(player);
            Field field = gameProfile.getClass().getDeclaredField("name");
            field.setAccessible(true);
            field.set(gameProfile, ChatColor.stripColor(disguise.getActualName()));
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException | NoSuchFieldException e) {
            e.printStackTrace();
        }

    }

    public void openDisguisedInv (Player player) {

        getInv().clear();

        for (Map.Entry<UUID, Disguise> uuid : getDisguisedPlayers().entrySet()) {

            UUID actualUUID = uuid.getKey();
            Disguise disguise = getDisguisedPlayers().get(actualUUID);

            ItemStack skull = new ItemStack(Material.getMaterial("SKULL_ITEM"), 1, (short) 3);
            SkullMeta meta = (SkullMeta) skull.getItemMeta();
            setSkin(meta, disguise.getDisguisedTexture());
            meta.setDisplayName(Utils.chat("&b&l" + disguise.getActualName()));
            List<String> lore = new ArrayList<>();
            lore.add(0, Utils.chat("&r&fDisguised as: " + "&b&l" + disguise.getDisguisedName()));
            lore.add(1, Utils.chat("&r&fDisguise UUID: " + "&b&l" + disguise.getDisguisedUUID()));
            meta.setLore(lore);
            skull.setItemMeta(meta);
            getInv().addItem(skull);

        }

        player.openInventory(getInv());

    }

    public void openCachedInv (Player player) {

        getCachedInv().clear();

        for (Map.Entry<String, Disguise> string : getCachedProfiles().entrySet()) {

            String disguiseName = string.getKey();
            Disguise disguise = getCachedProfiles().get(disguiseName);

            ItemStack skull = new ItemStack(Material.getMaterial("SKULL_ITEM"), 1, (short) 3);
            SkullMeta meta = (SkullMeta) skull.getItemMeta();
            setSkin(meta, disguise.getDisguisedTexture());
            meta.setDisplayName(Utils.chat("&b&l" + disguise.getDisguisedName()));
            List<String> lore = new ArrayList<>();
            lore.add(0, Utils.chat("&r&fUUID: " + "&b&l" + disguise.getDisguisedUUID()));
            meta.setLore(lore);
            skull.setItemMeta(meta);
            getCachedInv().addItem(skull);

        }

        player.openInventory(getCachedInv());

    }

    public void setSkin (SkullMeta meta, String texture) {

        GameProfile profile = new GameProfile(UUID.randomUUID(), null);
        profile.getProperties().put("textures", new Property("textures", texture));
        Field profileField = null;
        try {
            profileField = meta.getClass().getDeclaredField("profile");
        } catch (NoSuchFieldException | SecurityException e) {
            e.printStackTrace();
        }
        profileField.setAccessible(true);
        try {
            profileField.set(meta, profile);
        } catch (IllegalArgumentException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public void addPacketListener (ProtocolManager manager, DisguiseMe plugin) {

        manager.addPacketListener (new PacketAdapter(plugin, PacketType.Play.Server.PLAYER_INFO) {
            @Override
            public void onPacketSending(PacketEvent event) {
                // Item packets (id: 0x29)
                if (event.getPacketType() == PacketType.Play.Server.PLAYER_INFO) {

                    WrapperPlayServerPlayerInfo wrapperPacket = new WrapperPlayServerPlayerInfo(event.getPacket());

                    if (isDisguised(event.getPlayer().getUniqueId()) && !(wrapperPacket.getAction() == EnumWrappers.PlayerInfoAction.ADD_PLAYER)) {
                        return;
                    }

                    List<PlayerInfoData> oldPlayerInfoDataList = wrapperPacket.getData();
                    List<PlayerInfoData> newPlayerInfoDataList = wrapperPacket.getData();

                    for (PlayerInfoData playerData : oldPlayerInfoDataList) {

                        Player player = Bukkit.getPlayer(playerData.getProfile().getName());

                        if (player == null || !isDisguised(player.getUniqueId())) {
                            continue;
                        }

                        Disguise disguise = getDisguisedPlayer(player.getUniqueId());
                        WrappedGameProfile newGameProfile = getDisguisedProfile(player);
                        PlayerInfoData newPlayerData = new PlayerInfoData(newGameProfile, playerData.getLatency(), playerData.getGameMode(), playerData.getDisplayName());
                        newPlayerInfoDataList.add(newPlayerData);

                    }

                    wrapperPacket.setData(newPlayerInfoDataList);
                    event.setPacket(wrapperPacket.getHandle());



                }

            }
        });

    }

    public void asyncDisguise (Player disguiseTarget, UUID disguisedUUID, UUID actualUUID, String disguisedName, String actualName) {

        if (this.isDisguised(disguiseTarget.getUniqueId())) {
            this.clearDisguise(disguiseTarget);
            this.removeDisguisedPlayer(disguiseTarget.getUniqueId());
        }

        BukkitTask task = new BukkitRunnable() {
            @Override
            public void run() {

                String[] strings = Utils.fetch(disguisedUUID, disguiseTarget);

                if (strings == null || strings.length == 0) {
                    disguiseTarget.sendMessage(Utils.chat("&c&lERROR! &r&7Player &c" + disguisedName + "&7 not found!"));
                    return;
                }

                if (strings[0] == null) {
                    return;
                }

                String texture = strings[0];
                String signature = strings[1];
                Disguise disguise = new Disguise(disguisedUUID, disguisedName, actualName, actualUUID);
                disguise.setDisguisedSignature(signature);
                disguise.setDisguisedTexture(texture);
                addDisguised(actualUUID, disguise);

                BukkitTask task = new BukkitRunnable() {
                    @Override
                    public void run() {

                        initiateDisguise(disguiseTarget);

                    }
                }.runTaskLater(getPlugin(), 0);

                addToCachedProfiles(disguisedName, disguise);
                disguiseTarget.sendMessage(Utils.chat("&b&lSUCCESS! &r&7Now disguised as &b" + disguisedName + "&7!"));

            }
        }.runTaskAsynchronously(getPlugin());

    }

    public void setCachedDisguise (String name, Player player) {

        if (this.isDisguised(player.getUniqueId())) {
            this.clearDisguise(player);
            this.removeDisguisedPlayer(player.getUniqueId());
        }

        if (this.isDisguised(player.getUniqueId())) {
            this.clearDisguise(player);
            this.removeDisguisedPlayer(player.getUniqueId());
        }

        Disguise cachedDisguise = this.getCachedProfiles().get(name);
        cachedDisguise.setActualName(player.getName());
        cachedDisguise.setActualUUID(player.getUniqueId());
        this.addDisguised(player.getUniqueId(), cachedDisguise);
        this.initiateDisguise(player);
        player.sendMessage(Utils.chat("&b&lSUCCESS! &r&7Now disguised as &b" + player.getName() + "&7!"));

    }

    public Map<String, Disguise> getCachedProfiles () { return null; }

    public Map<UUID, Disguise> getDisguisedPlayers () { return null; }

    public boolean isDisguised (UUID uuid) { return false; }

    public Disguise getDisguisedPlayer (UUID uuid) { return null; }

    public void addToCachedProfiles (String string, Disguise disguise) { }

    public void addDisguised (UUID uuid, Disguise disguise) { }

    public void removeDisguisedPlayer (UUID uuid) { }

    public DisguiseMe getPlugin () { return null; }

    public ProtocolManager getManager () {return null;}

    public Inventory getInv () { return null; }

    public Inventory getCachedInv () { return null; }
}