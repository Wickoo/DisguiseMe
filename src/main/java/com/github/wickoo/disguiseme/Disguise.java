package com.github.wickoo.disguiseme;

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
}
