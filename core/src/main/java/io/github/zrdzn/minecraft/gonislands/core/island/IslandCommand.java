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
import io.github.zrdzn.minecraft.gonislands.core.message.MessageService;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.UUID;

public class IslandCommand implements CommandExecutor {

    private final IslandService islandService;
    private final IslandType globalIslandType;
    private final MessageService messageService;

    public IslandCommand(IslandService islandService, IslandType globalIslandType, MessageService messageService) {
        this.islandService = islandService;
        this.globalIslandType = globalIslandType;
        this.messageService = messageService;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (!(sender instanceof Player player)) {
            return true;
        }

        UUID playerId = player.getUniqueId();

        if (args.length == 0) {
            this.islandService.createIsland(this.globalIslandType, null, playerId, playerId);
            return true;
        }

        this.islandService.getIslands(playerId).thenAccept(islands -> {
            switch (args[0].toLowerCase()) {
                case "create" -> {
                    String islandName = null;
                    if (args[1] != null) {
                        islandName = args[1];
                    }

                    this.islandService.createIsland(this.globalIslandType, islandName, playerId, playerId);
                }
                case "delete" -> {
                    Island islandToBeDeleted;
                    if (islands.size() == 1) {
                        islandToBeDeleted = islands.get(0);
                    } else {
                        if (args.length == 1) {
                            this.sendIslandList(playerId, islands);
                            this.messageService.sendMessage(playerId, "command.usage.delete");

                            return;
                        }

                        islandToBeDeleted = islands.get(Integer.parseInt(args[1]));
                    }

                    this.islandService.removeIsland(islandToBeDeleted.getId(), playerId);
                }
                case "list" -> this.sendIslandList(playerId, islands);
                default -> this.messageService.sendMessage(playerId, "command.invalid_argument");
            }
        });


        return true;
    }

    private void sendIslandList(UUID playerId, List<Island> islandList) {
        if (islandList.isEmpty()) {
            this.messageService.sendMessage(playerId, "island.does_not_have");
            return;
        }

        for (int i = 0; i < islandList.size(); i++) {
            Island island = islandList.get(i);
            this.messageService.sendMessage(playerId,
                    "command.island_list_format",
                    String.valueOf(i + 1),
                    island.getId().toString(),
                    island.getName());
        }
    }

}
