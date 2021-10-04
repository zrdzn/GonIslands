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

import com.zaxxer.hikari.HikariDataSource;
import io.github.zrdzn.minecraft.gonislands.api.event.AsyncIslandCreateEvent;
import io.github.zrdzn.minecraft.gonislands.api.event.AsyncIslandRemoveEvent;
import io.github.zrdzn.minecraft.gonislands.api.island.Island;
import io.github.zrdzn.minecraft.gonislands.api.island.IslandType;
import io.github.zrdzn.minecraft.gonislands.core.message.MessageService;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class IslandRepository {

    private final HikariDataSource dataSource;
    private final Server server;
    private final PluginManager pluginManager;
    private final IslandCreator islandCreator;
    private final MessageService messageService;

    public IslandRepository(HikariDataSource dataSource, Server server, IslandCreator islandCreator, MessageService messageService) {
        this.dataSource = dataSource;
        this.server = server;
        this.pluginManager = server.getPluginManager();
        this.islandCreator = islandCreator;
        this.messageService = messageService;
    }

    public Island save(IslandType islandType, String islandName, UUID newOwnerId) {
        UUID newIslandId = UUID.randomUUID();

        Player player = this.server.getPlayer(newOwnerId);
        if (player == null) {
            throw new IllegalArgumentException(String.format("Name of the player cannot be null (%s).", newOwnerId));
        }

        String playerName = player.getName();

        try (Connection connection = this.dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement("INSERT INTO islands (island_uuid, island_name, owner_uuid, owner_name) VALUES (?, ?, ?, ?);")) {
            statement.setString(1, newIslandId.toString());
            statement.setString(2, islandName);
            statement.setString(3, newOwnerId.toString());
            statement.setString(4, playerName);

            statement.executeUpdate();
        } catch (SQLException exception) {
            exception.printStackTrace();
            return null;
        }

        World islandWorld = this.islandCreator.prepareNewWorld(islandType, playerName).orElseThrow(() ->
                new IllegalStateException("Something went wrong while creating new world."));

        Island newIsland;
        switch (islandType) {
            case SKY -> newIsland = new SkyIsland(newIslandId, islandWorld, islandName, newOwnerId);
            case WATER -> newIsland = new WaterIsland(newIslandId, islandWorld, islandName, newOwnerId);
            default -> {
                this.messageService.sendMessage(player, "command.something_went_wrong");
                return null;
            }
        }

        this.pluginManager.callEvent(new AsyncIslandCreateEvent(newIsland.getId()));

        return newIsland;
    }

    public void delete(UUID islandId) {
        try (Connection connection = this.dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement("DELETE FROM islands WHERE island_uuid = ?")) {
            statement.setString(1, islandId.toString());

            statement.executeUpdate();
        } catch (SQLException exception) {
            exception.printStackTrace();
            return;
        }

        this.pluginManager.callEvent(new AsyncIslandRemoveEvent(islandId));
    }

    public Optional<Island> findIslandById(UUID islandId) {
        try (Connection connection = this.dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement("SELECT * FROM islands WHERE island_uuid = ?;")) {
            statement.setString(1, islandId.toString());

            ResultSet resultSet = statement.executeQuery();
            if (resultSet == null || !resultSet.next()) {
                return Optional.empty();
            }

            return this.parseFromDatabase(resultSet).isPresent() ? this.parseFromDatabase(resultSet) : Optional.empty();
        } catch (SQLException exception) {
            exception.printStackTrace();
            return Optional.empty();
        }
    }

    public List<Island> findIslandsByPlayerId(UUID playerId) {
        String queryParameter = "*";
        if (playerId != null) {
            queryParameter = playerId.toString();
        }

        List<Island> islandList = new ArrayList<>();

        try (Connection connection = this.dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement("SELECT * FROM islands WHERE owner_uuid = ?")) {
            statement.setString(1, queryParameter);

            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                this.parseFromDatabase(resultSet).ifPresent(islandList::add);
            }
        } catch (SQLException exception) {
            exception.printStackTrace();
        }

        return islandList;
    }

    private Optional<Island> parseFromDatabase(ResultSet row) {
        UUID islandId;
        UUID worldId;
        UUID playerId;
        String islandName;

        try {
            islandId = UUID.fromString(row.getString("island_uuid"));
            worldId = UUID.fromString(row.getString("world_uuid"));
            playerId = UUID.fromString(row.getString("owner_uuid"));
            islandName = row.getString("island_name");
        } catch (SQLException exception) {
            exception.printStackTrace();
            return Optional.empty();
        }

        return Optional.of(new SkyIsland(islandId, this.server.getWorld(worldId), islandName, playerId));
    }

}