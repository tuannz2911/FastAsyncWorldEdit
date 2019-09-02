/*
 * WorldEdit, a Minecraft world manipulation toolkit
 * Copyright (C) sk89q <http://www.sk89q.com>
 * Copyright (C) WorldEdit team and contributors
 *
 * This program is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by the
 * Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License
 * for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package com.sk89q.worldedit.bukkit;

import static com.google.common.base.Preconditions.checkNotNull;

import com.sk89q.worldedit.extension.platform.AbstractNonPlayerActor;
import com.sk89q.worldedit.extension.platform.Locatable;
import com.sk89q.worldedit.extent.Extent;
import com.sk89q.worldedit.session.SessionKey;
import com.sk89q.worldedit.util.Location;
import com.sk89q.worldedit.util.auth.AuthorizationException;
import com.sk89q.worldedit.util.formatting.text.Component;
import com.sk89q.worldedit.util.formatting.text.adapter.bukkit.TextAdapter;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.BlockCommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

import javax.annotation.Nullable;

public class BukkitBlockCommandSender extends AbstractNonPlayerActor implements Locatable {

    private final BlockCommandSender sender;
    private final WorldEditPlugin plugin;
    private final Location location;
    private final UUID uuid;

    public BukkitBlockCommandSender(WorldEditPlugin plugin, BlockCommandSender sender) {
        checkNotNull(plugin);
        checkNotNull(sender);

        this.plugin = plugin;
        this.sender = sender;
        this.location = BukkitAdapter.adapt(sender.getBlock().getLocation());
        this.uuid = new UUID(location.toVector().toBlockPoint().hashCode(), location.getExtent().hashCode());
    }

    @Override
    public String getName() {
        return sender.getName();
    }

    @Override
    public void printRaw(String msg) {
        for (String part : msg.split("\n")) {
            sender.sendMessage(part);
        }
    }

    @Override
    public void print(String msg) {
        for (String part : msg.split("\n")) {
            sender.sendMessage("\u00A7d" + part);
        }
    }

    @Override
    public void printDebug(String msg) {
        for (String part : msg.split("\n")) {
            sender.sendMessage("\u00A77" + part);
        }
    }

    @Override
    public void printError(String msg) {
        for (String part : msg.split("\n")) {
            sender.sendMessage("\u00A7c" + part);
        }
    }

    @Override
    public void print(Component component) {
        TextAdapter.sendComponent(sender, component);
    }

    @Override
    public Location getLocation() {
        return this.location;
    }

    @Override
    public boolean setLocation(Location location) {
        return false;
    }

    @Override
    public Extent getExtent() {
        return this.location.getExtent();
    }

    @Override
    public UUID getUniqueId() {
        return uuid;
    }

    @Override
    public String[] getGroups() {
        return new String[0];
    }

    @Override
    public void checkPermission(String permission) throws AuthorizationException {
        if (!hasPermission(permission)) {
            throw new AuthorizationException();
        }
    }

    @Override
    public boolean hasPermission(String permission) {
        return sender.hasPermission(permission);
    }

    @Override
    public boolean togglePermission(String permission) {
        return true;
    }

    @Override
    public void setPermission(String permission, boolean value) {

    }

    @Override
    public SessionKey getSessionKey() {
        return new SessionKey() {
            @Nullable
            @Override
            public String getName() {
                return sender.getName();
            }

            @Override
            public boolean isActive() {
                @NotNull Block block = sender.getBlock();
                @NotNull World world = block.getWorld();
                if (world.isChunkLoaded(block.getX() >> 4, block.getZ() >> 4)) {
                    @NotNull Material type = block.getType();
                    return type == Material.COMMAND_BLOCK
                            || type == Material.CHAIN_COMMAND_BLOCK
                            || type == Material.REPEATING_COMMAND_BLOCK;
                }
                return false;
            }

            @Override
            public boolean isPersistent() {
                return true;
            }

            @Override
            public UUID getUniqueId() {
                return uuid;
            }
        };
    }
}