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

import io.github.zrdzn.minecraft.gonislands.api.event.IslandCreateEvent;
import io.github.zrdzn.minecraft.gonislands.api.event.IslandRemoveEvent;
import io.github.zrdzn.minecraft.gonislands.api.island.Island;
import io.github.zrdzn.minecraft.gonislands.api.island.IslandService;
import io.github.zrdzn.minecraft.gonislands.api.island.IslandType;
import io.github.zrdzn.minecraft.gonislands.core.message.MessageService;
import org.bukkit.OfflinePlayer;
import org.bukkit.Server;
import org.bukkit.plugin.PluginManager;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class IslandServiceImpl implements IslandService {

    private final IslandRepository islandRepository;
    private final PluginManager pluginManager;
    private final Server server;
    private final MessageService messageService;

    public IslandServiceImpl(IslandRepository islandRepository, PluginManager pluginManager, Server server,
                             MessageService messageService) {
        this.islandRepository = islandRepository;
        this.pluginManager = pluginManager;
        this.server = server;
        this.messageService = messageService;
    }

    @Override
    public CompletableFuture<Void> createIsland(IslandType islandType, String islandName, UUID ownerId,
                                                UUID executorId) {
        return CompletableFuture.runAsync(() -> {
            OfflinePlayer player = this.server.getOfflinePlayer(ownerId);
            if (player.getPlayer() == null) {
                throw new IllegalArgumentException("Player with that id ({}) does not exist on the server.");
            }

            Island island = this.islandRepository.save(islandType, islandName == null ?
                player.getName() + "-" +player.getLastLogin() : islandName, ownerId);
            if (island == null) {
                this.messageService.sendMessage(executorId, "command.something_went_wrong");
                return;
            }

            this.pluginManager.callEvent(new IslandCreateEvent(island.getId(), ownerId));
        });
    }

    @Override
    public CompletableFuture<Void> removeIsland(UUID islandId, UUID executorId) {
        return CompletableFuture.runAsync(() -> {
            if (this.islandRepository.delete(islandId)) {
                this.pluginManager.callEvent(new IslandRemoveEvent(islandId, executorId));
            }
        });
    }

    @Override
    public CompletableFuture<Optional<Island>> getIsland(UUID islandId) {
        return CompletableFuture.supplyAsync(() -> this.islandRepository.findIslandById(islandId));
    }

    @Override
    public CompletableFuture<List<Island>> getIslands(UUID ownerId) {
        return CompletableFuture.supplyAsync(() -> this.islandRepository.findIslandsByPlayerId(ownerId));
    }

    @Override
    public CompletableFuture<List<Island>> getIslands() {
        return CompletableFuture.supplyAsync(() -> this.islandRepository.findIslandsByPlayerId(null));
    }

}
