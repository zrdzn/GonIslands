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
import io.github.zrdzn.minecraft.gonislands.api.island.IslandType;

import java.util.Optional;
import java.util.UUID;

public class SkyIsland implements Island {

    private final UUID id;
    private final UUID worldId;
    private final String name;
    private final UUID ownerId;

    public SkyIsland(UUID id, UUID worldId, String name, UUID ownerId) {
        this.id = id;
        this.worldId = worldId;
        this.name = name;
        this.ownerId = ownerId;
    }

    @Override
    public UUID getId() {
        return this.id;
    }

    @Override
    public IslandType getType() {
        return IslandType.SKY;
    }

    @Override
    public UUID getWorldId() {
        return this.worldId;
    }

    @Override
    public Optional<String> getName() {
        return Optional.of(this.name);
    }

    @Override
    public UUID getOwnerId() {
        return this.ownerId;
    }

}
