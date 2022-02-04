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
import io.github.zrdzn.minecraft.gonislands.api.island.Island;
import io.github.zrdzn.minecraft.gonislands.api.island.IslandType;
import org.slf4j.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class IslandRepository {

    private final Logger logger;
    private final HikariDataSource dataSource;
    private final IslandCreator islandCreator;

    public IslandRepository(Logger logger, HikariDataSource dataSource, IslandCreator islandCreator) {
        this.logger = logger;
        this.dataSource = dataSource;
        this.islandCreator = islandCreator;
    }

    public Island save(IslandType islandType, String islandName, UUID ownerId) {
        UUID newIslandId = UUID.randomUUID();

        this.logger.info("Preparing new world for {}-{}...", islandType, islandName);
        UUID islandWorldId = this.islandCreator.prepareNewWorld(islandType, islandName).orElseThrow(() ->
                new IllegalStateException("Something went wrong while creating new world."));

        try (Connection connection = this.dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement("INSERT INTO islands (island_uuid, world_uuid, island_name, owner_uuid) VALUES (?, ?, ?, ?);")) {
            statement.setString(1, newIslandId.toString());
            statement.setString(2, islandWorldId.toString());
            statement.setString(3, islandName);
            statement.setString(4, ownerId.toString());

            if (statement.executeUpdate() > 0) {
                this.logger.info("{} island has been saved to database.", islandName);
            }
        } catch (SQLException exception) {
            this.logger.error("Something went wrong while saving island.", exception);
            return null;
        }

        Island newIsland;
        switch (islandType) {
            case SKY -> newIsland = new SkyIsland(newIslandId, islandWorldId, islandName, ownerId);
            case WATER -> newIsland = new WaterIsland(newIslandId, islandWorldId, islandName, ownerId);
            default -> {
                return null;
            }
        }

        return newIsland;
    }

    public boolean delete(UUID islandId) {
        try (Connection connection = this.dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement("DELETE FROM islands WHERE island_uuid = ?")) {
            statement.setString(1, islandId.toString());

            statement.executeUpdate();

            return true;
        } catch (SQLException exception) {
            this.logger.error("Something went wrong while deleting island.", exception);
            return false;
        }
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
            this.logger.error("Something went wrong while selecting island.", exception);
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
            this.logger.error("Something went wrong while selecting island.", exception);
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
            this.logger.error("Something went wrong while parsing island.", exception);
            return Optional.empty();
        }

        return Optional.of(new SkyIsland(islandId, worldId, islandName, playerId));
    }

}