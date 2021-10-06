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
 * Called asynchronously when a Player enters an Island.
 * <p>
 * If an Island Leave event is cancelled, the Player will be teleported to the previous location.
 */
public class IslandLeaveEvent extends Event implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    protected final UUID islandId;
    protected final UUID playerId;
    protected boolean cancel;

    public IslandLeaveEvent(UUID islandId, UUID playerId) {
        super(true);
        this.islandId = islandId;
        this.playerId = playerId;
    }

    /**
     * Gets the id of the Island that Player leaves in this event.
     *
     * @return the id of the island that player leaves
     */
    public UUID getIslandId() {
        return this.islandId;
    }

    /**
     * Gets the id of the Player that leaves Island involved in this event.
     *
     * @return the id of the player that leaves the island
     */
    public UUID getPlayerId() {
        return this.playerId;
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
