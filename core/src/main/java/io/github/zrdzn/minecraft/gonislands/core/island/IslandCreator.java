/*
 * Copyright (c) 2021 zrdzn
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.github.zrdzn.minecraft.gonislands.core.island;

import com.grinderwolf.swm.api.SlimePlugin;
import com.grinderwolf.swm.api.exceptions.WorldAlreadyExistsException;
import com.grinderwolf.swm.api.loaders.SlimeLoader;
import com.grinderwolf.swm.api.world.SlimeWorld;
import com.grinderwolf.swm.api.world.properties.SlimePropertyMap;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormats;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardReader;
import com.sk89q.worldedit.function.operation.Operation;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.session.ClipboardHolder;
import io.github.zrdzn.minecraft.gonislands.api.island.IslandType;
import org.bukkit.Server;
import org.slf4j.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

public class IslandCreator {

    private final SlimePlugin slimePlugin;
    private final Server server;
    private final Logger logger;

    public IslandCreator(SlimePlugin slimePlugin, Server server, Logger logger) {
        this.slimePlugin = slimePlugin;
        this.server = server;
        this.logger = logger;
    }

    public Optional<UUID> prepareNewWorld(IslandType islandType, String islandName) {
        SlimeLoader slimeLoader = this.slimePlugin.getLoader("mysql");

        SlimePropertyMap slimePropertyMap = new SlimePropertyMap();

        SlimeWorld slimeWorld;
        try {
            slimeWorld = this.slimePlugin.createEmptyWorld(slimeLoader, islandName, false, slimePropertyMap);
            if (slimeWorld == null) {
                return Optional.empty();
            }
        } catch (WorldAlreadyExistsException | IOException exception) {
            exception.printStackTrace();
            return Optional.empty();
        }

        // Converting SlimeWorld to WorldEdit world
        org.bukkit.World world = this.server.getWorld(slimeWorld.getName());
        if (world == null) {
            this.logger.warn("Something went wrong while parsing SlimeWorld to Bukkit world.");
            return Optional.empty();
        }

        // Loading schematic
        File file = new File(islandType.getSchematicFile());
        if (!file.exists()) {
            this.logger.warn("Schematic for {} island not found while preparing island world.", islandType);
            return Optional.empty();
        }

        ClipboardFormat format = ClipboardFormats.findByFile(file);
        if (format == null) {
            this.logger.error("Schematic for {} island not found while preparing island world.", islandType);
            return Optional.empty();
        }

        // Creating a clipboard with schematic in it
        Clipboard clipboard;
        try (ClipboardReader reader = format.getReader(new FileInputStream(file))) {
            clipboard = reader.read();
        } catch (IOException exception) {
            exception.printStackTrace();
            return Optional.empty();
        }

        // Pasting schematic from clipboard to new world
        try (EditSession editSession = WorldEdit.getInstance().newEditSession(BukkitAdapter.adapt(world))) {
            Operation operation = new ClipboardHolder(clipboard).createPaste(editSession)
                    .to(BlockVector3.at(0, 100, 0))
                    .ignoreAirBlocks(false)
                    .build();
            Operations.complete(operation);
        } catch (WorldEditException exception) {
            exception.printStackTrace();
            return Optional.empty();
        }

        return Optional.of(world.getUID());
    }

}
