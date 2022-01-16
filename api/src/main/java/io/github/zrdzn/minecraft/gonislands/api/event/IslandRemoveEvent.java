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
package io.github.zrdzn.minecraft.gonislands.api.event;

import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

/**
 * Called asynchronously when a Player removes an Island.
 * <p>
 * If an Island Remove event is cancelled, the Island will not be removed.
 */
public class IslandRemoveEvent extends Event implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    private final UUID islandId;
    private final UUID executorId;
    private boolean cancel;

    public IslandRemoveEvent(UUID islandId, UUID executorId) {
        super(true);
        this.islandId = islandId;
        this.executorId = executorId;
    }

    /**
     * Gets the id of the Island that Player removes in this event.
     *
     * @return the id of the island that player removes
     */
    public UUID getIslandId() {
        return this.islandId;
    }

    /**
     * Gets the id of the Player that creates the island.
     *
     * @return the id of the player that creates the island
     */
    public UUID getExecutorId() {
        return this.executorId;
    }

    @Override
    public boolean isCancelled() {
        return this.cancel;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.cancel = cancel;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

}