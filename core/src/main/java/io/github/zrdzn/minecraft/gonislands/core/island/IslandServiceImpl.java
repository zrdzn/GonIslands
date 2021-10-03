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

import io.github.zrdzn.minecraft.gonislands.api.island.Island;
import io.github.zrdzn.minecraft.gonislands.api.island.IslandService;
import io.github.zrdzn.minecraft.gonislands.api.island.IslandType;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class IslandServiceImpl implements IslandService {

    private final IslandRepository islandRepository;

    public IslandServiceImpl(IslandRepository islandRepository) {
        this.islandRepository = islandRepository;
    }

    @Override
    public CompletableFuture<Island> createIsland(IslandType islandType, String islandName, UUID ownerId) {
        return CompletableFuture.supplyAsync(() -> this.islandRepository.save(islandType, islandName, ownerId));
    }

    @Override
    public CompletableFuture<Void> removeIsland(UUID islandId) {
        return CompletableFuture.runAsync(() -> this.islandRepository.delete(islandId));
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
