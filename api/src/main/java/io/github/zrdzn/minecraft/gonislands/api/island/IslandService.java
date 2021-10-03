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
package io.github.zrdzn.minecraft.gonislands.api.island;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface IslandService {

    /**
     * Creates new Island with specified parameters and then returns it.
     *
     * @param islandType type of the island
     * @param islandName name of the island
     * @param ownerId id of the owner
     *
     * @return the island that is created
     */
    CompletableFuture<Island> createIsland(IslandType islandType, String islandName, UUID ownerId);

    /**
     * Removes specified Island.
     *
     * @param islandId id of the island
     */
    CompletableFuture<Void> removeIsland(UUID islandId);

    /**
     * Gets the Island optional by specified island id.
     *
     * @param islandId id of the island
     *
     * @return the island optional
     */
    CompletableFuture<Optional<Island>> getIsland(UUID islandId);

    /**
     * Gets the Island list that is assigned to specified owner in parameter.
     *
     * @param ownerId id of the owner
     *
     * @return all islands that are assigned to owner
     */
    CompletableFuture<List<Island>> getIslands(UUID ownerId);

    /**
     * Gets the Island list from whole server.
     *
     * @return all islands that exists on the server
     */
    CompletableFuture<List<Island>> getIslands();

}
