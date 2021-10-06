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
package io.github.zrdzn.minecraft.gonislands.core;

import com.grinderwolf.swm.api.SlimePlugin;
import com.zaxxer.hikari.HikariDataSource;
import io.github.zrdzn.minecraft.gonislands.api.GonIslandsApi;
import io.github.zrdzn.minecraft.gonislands.api.island.IslandService;
import io.github.zrdzn.minecraft.gonislands.api.island.IslandType;
import io.github.zrdzn.minecraft.gonislands.core.datasource.DataSourceParser;
import io.github.zrdzn.minecraft.gonislands.core.island.IslandCommand;
import io.github.zrdzn.minecraft.gonislands.core.island.IslandCreator;
import io.github.zrdzn.minecraft.gonislands.core.island.IslandRepository;
import io.github.zrdzn.minecraft.gonislands.core.island.IslandServiceImpl;
import io.github.zrdzn.minecraft.gonislands.core.message.MessageService;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Server;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.slf4j.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

public class GonIslandsPlugin extends JavaPlugin {

    private final GonIslandsApi gonIslandsApi = new GonIslandsApi();
    private final Map<Locale, ResourceBundle> bundleMap = new HashMap<>();

    private HikariDataSource dataSource;

    @Override
    public void onEnable() {
        this.saveDefaultConfig();

        Server server = this.getServer();
        PluginManager pluginManager = server.getPluginManager();

        SlimePlugin slimePlugin = (SlimePlugin) pluginManager.getPlugin("SlimeWorldManager");

        Logger logger = this.getSLF4JLogger();

        IslandCreator islandCreator = new IslandCreator(slimePlugin, server, logger);

        this.loadBundles();

        MessageService messageService = new MessageService(server, logger, this.bundleMap);

        IslandRepository islandRepository = new IslandRepository(this.dataSource, islandCreator);

        IslandType globalIslandType;
        try {
            ConfigurationSection configuration = this.getConfig();

            ConfigurationSection databaseSection = configuration.getConfigurationSection("database");
            if (databaseSection == null) {
                throw new InvalidConfigurationException("Section database does not exist.");
            }

            this.dataSource = new DataSourceParser().parse(databaseSection);

            globalIslandType = IslandType.valueOf(StringUtils.upperCase(configuration.getString("global-island-type")));
            this.gonIslandsApi.setIslandType(globalIslandType);
        } catch (InvalidConfigurationException exception) {
            exception.printStackTrace();
            pluginManager.disablePlugin(this);

            return;
        }

        if (this.dataSource == null) {
            logger.error("Something went wrong while connecting to database. Check your database configuration and restart your server after correcting it.");
            pluginManager.disablePlugin(this);

            return;
        }

        String query = "CREATE TABLE IF NOT EXISTS islands (" +
                "id INT NOT NULL PRIMARY KEY AUTO_INCREMENT," +
                "island_uuid VARCHAR(36) NOT NULL UNIQUE KEY," +
                "world_uuid VARCHAR(32) NOT NULL UNIQUE KEY," +
                "island_name VARCHAR(32)," +
                "owner_uuid VARCHAR(36) NOT NULL UNIQUE KEY);";
        try (Connection connection = this.dataSource.getConnection(); PreparedStatement statement = connection.prepareStatement(query)) {
            statement.executeUpdate();
        } catch (SQLException exception) {
            exception.printStackTrace();
            pluginManager.disablePlugin(this);

            return;
        }

        IslandService islandService = new IslandServiceImpl(islandRepository, pluginManager, messageService);
        this.gonIslandsApi.setIslandService(islandService);

        this.getCommand("island").setExecutor(new IslandCommand(islandService, globalIslandType, messageService));
    }

    @Override
    public void onDisable() {
        if (this.dataSource != null) {
            this.dataSource.close();
        }
    }

    public GonIslandsApi getGonIslandsApi() {
        return this.gonIslandsApi;
    }

    private void loadBundles() {
        String baseName = "locale/locale";
        this.bundleMap.put(Locale.forLanguageTag("en-US"), ResourceBundle.getBundle(baseName, Locale.forLanguageTag("en-US")));
        this.bundleMap.put(Locale.forLanguageTag("pl-PL"), ResourceBundle.getBundle(baseName, Locale.forLanguageTag("pl-PL")));
    }

}
