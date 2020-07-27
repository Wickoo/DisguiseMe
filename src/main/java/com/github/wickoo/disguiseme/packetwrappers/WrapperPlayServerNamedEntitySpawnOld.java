package com.github.wickoo.disguiseme.packetwrappers;

/*
 *  PacketWrapper - Contains wrappers for each packet in Minecraft.
 *  Copyright (C) 2012 Kristian S. Stangeland
 *
 *  This program is free software; you can redistribute it and/or modify it under the terms of the
 *  GNU Lesser General Public License as published by the Free Software Foundation; either version 2 of
 *  the License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 *  without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *  See the GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License along with this program;
 *  if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA
 *  02111-1307 USA
 */

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.injector.PacketConstructor;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.UUID;

public class WrapperPlayServerNamedEntitySpawnOld extends AbstractPacket {
    public static final PacketType TYPE = PacketType.Play.Server.NAMED_ENTITY_SPAWN;

    // For constructing packets from entities
    private static PacketConstructor entityConstructor;

    public WrapperPlayServerNamedEntitySpawnOld() {
        super(new PacketContainer(TYPE), TYPE);
        handle.getModifier().writeDefaults();
    }

    public WrapperPlayServerNamedEntitySpawnOld(PacketContainer packet) {
        super(packet, TYPE);
    }

    public WrapperPlayServerNamedEntitySpawnOld(Player player) {
        super(fromPlayer(player), TYPE);
    }

    // Useful constructor
    private static PacketContainer fromPlayer(Player player) {
        if (entityConstructor == null)
            entityConstructor = ProtocolLibrary.getProtocolManager().createPacketConstructor(TYPE, player);
        return entityConstructor.createPacket(player);
    }

    /**
     * Retrieve player ID.
     * @return The current EID
     */
    public int getEntityID() {
        return handle.getIntegers().read(0);
    }

    /**
     * Set player ID.
     * @param value - new value.
     */
    public void setEntityID(int value) {
        handle.getIntegers().write(0, value);
    }

    /**
     * Retrieve the player's entity object.
     * @param world - the word the player has joined.
     * @return The player's entity.
     */
    public Entity getEntity(World world) {
        return handle.getEntityModifier(world).read(0);
    }

    /**
     * Retrieve the player's entity object.
     * @param event - the packet event.
     * @return The player's entity.
     */
    public Entity getEntity(PacketEvent event) {
        return getEntity(event.getPlayer().getWorld());
    }

    /**
     * Retrieve the UUID of the player.
     * @return The UUID, or NULL if not set.
     */
    public UUID getPlayerUuid() {
        return (UUID) handle.getSpecificModifier(UUID.class).read(0);

    }

    /**
     * Set the UUID of the player.
     * @param - the UUID.
     */
    public void setPlayerUuid(UUID value) {
        handle.getSpecificModifier(UUID.class).write(0, (UUID) value);
    }
    /**
     * Retrieve the position of the spawned entity as a vector.
     * @return The position as a vector.
     */
    public Vector getPosition() {
        return new Vector(getX(), getY(), getZ());
    }

    /**
     * Set the position of the spawned entity using a vector.
     * @param position - the new position.
     */
    public void setPosition(Vector position) {
        setX((int) position.getX());
        setY((int) position.getY());
        setZ((int) position.getZ());
    }

    /**
     * Retrieve the x axis of the position.
     * <p>
     * Note that the coordinate is rounded off to the nearest 1/32 of a meter.
     * @return The current X
     */
    public int getX() {
        return handle.getIntegers().read(1);
    }


    /**
     * Set the x axis of the position.
     * @param value - new value.
     */
    public void setX(int value) {
        handle.getIntegers().write(1, value);
    }

    /**
     * Retrieve the y axis of the position.
     * <p>
     * Note that the coordinate is rounded off to the nearest 1/32 of a meter.
     * @return The current y
     */
    public int getY() {
        return handle.getIntegers().read(2);
    }

    /**
     * Set the y axis of the position.
     * @param value - new value.
     */
    public void setY(int value) {
        handle.getIntegers().write(2, value);
    }

    /**
     * Retrieve the z axis of the new position.
     * <p>
     * Note that the coordinate is rounded off to the nearest 1/32 of a meter.
     * @return The current z
     */
    public int getZ() {
        return handle.getIntegers().read(3);
    }

    /**
     * Set the z axis of the new position.
     * @param value - new value.
     */
    public void setZ(int value) {
        handle.getIntegers().write(3, value);
    }

    /**
     * Retrieve the yaw of the spawned entity.
     * @return The current Yaw
     */
    public byte getYaw() {
        return handle.getBytes().read(0);
    }

    /**
     * Set the yaw of the spawned entity.
     * @param value - new yaw.
     */
    public void setYaw(byte value) {
        handle.getBytes().write(0, value);
    }

    /**
     * Retrieve the pitch of the spawned entity.
     * @return The current pitch
     */
    public byte getPitch() {
        return handle.getBytes().read(1);
    }

    /**
     * Set the pitch of the spawned entity.
     * @param value - new pitch.
     */
    public void setPitch(byte value) {
        handle.getBytes().write(1, value);
    }

    /**
     * Retrieve the item the player is currently holding.
     * <p>
     * Note that this should be 0 for "no item", unlike -1 used in other packets. A negative value crashes clients.
     * @return The current item.
     */
    public short getCurrentItem() {
        return handle.getIntegers().read(4).shortValue();
    }

    /**
     * Set the item the player is currently holding.
     * <p>
     * Note that this should be 0 for "no item", unlike -1 used in other packets. A negative value crashes clients.
     * @param value - new value.
     */
    public void setCurrentItem(short value) {
        handle.getIntegers().write(4, (int) value);
    }

    /**
     * Retrieve the associated metadata object.
     * <p>
     * Note that the 1.3 client crashes on packets with no metadata, but the server can send any metadata
     * key of 0, 1 or 8 and the client is fine.
     * @return The current metadata.
     */
    public WrappedDataWatcher getMetadata() {
        return handle.getDataWatcherModifier().read(0);
    }

    /**
     * Set the associated metadata object.
     * <p>
     * Note that the 1.3 client crashes on packets with no metadata, but the server can send any metadata
     * key of 0, 1 or 8 and the client is fine..
     * @param value - new value.
     */
    public void setMetadata(WrappedDataWatcher value) {
        handle.getDataWatcherModifier().write(0, value);
    }
}