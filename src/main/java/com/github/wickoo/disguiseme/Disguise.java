package com.github.wickoo.disguiseme;

import com.github.wickoo.disguiseme.versions.DisguiseHandler;
import org.bukkit.Bukkit;

import java.util.UUID;

public class Disguise {

    private UUID disguisedUUID;
    private String disguisedName;

    private String disguisedSignature;
    private String disguisedTexture;

    private String actualSignature;
    private String actualTexture;

    private String actualName;
    private UUID actualUUID;

    public Disguise(UUID disguisedUUID, String disguisedName, String actualName, UUID actualUUID ) {
        this.disguisedUUID = disguisedUUID;
        this.disguisedName = disguisedName;
        this.actualName = actualName;
        this.actualUUID = actualUUID;

    }

    /**
     *
     * Must be called async as to not stall server.
     *
     *
     * @param playerUUID
     * @param playerName
     * @param disguiseName
     * @return
     */

    public static Disguise buildDisguise (UUID playerUUID, String playerName, String disguiseName) {

        UUID disguisedUUID = Bukkit.getOfflinePlayer(disguiseName).getUniqueId();
        String[] values = DisguiseHandler.getSkinData(disguisedUUID);

        if (values == null) return null;

        Disguise disguise = new Disguise(disguisedUUID, disguiseName, playerName, playerUUID);
        disguise.setDisguisedTexture(values[0]);
        disguise.setDisguisedSignature(values[1]);
        return disguise;

    }

    public UUID getDisguisedUUID() {
        return disguisedUUID;
    }

    public String getDisguisedName() {
        return disguisedName;
    }

    public String getActualName() {
        return actualName;
    }

    public UUID getActualUUID() {
        return actualUUID;
    }

    public String getDisguisedSignature() {
        return disguisedSignature;
    }

    public void setDisguisedSignature(String disguisedSignature) {
        this.disguisedSignature = disguisedSignature;
    }

    public String getDisguisedTexture() {
        return disguisedTexture;
    }

    public void setDisguisedTexture(String disguisedTexture) {
        this.disguisedTexture = disguisedTexture;
    }

    public String getActualSignature() {
        return actualSignature;
    }

    public void setActualSignature(String actualSignature) {
        this.actualSignature = actualSignature;
    }

    public String getActualTexture() {
        return actualTexture;
    }

    public void setActualTexture(String actualTexture) {
        this.actualTexture = actualTexture;
    }

    public void setActualName(String actualName) {
        this.actualName = actualName;
    }

    public void setActualUUID(UUID actualUUID) {
        this.actualUUID = actualUUID;
    }
}
