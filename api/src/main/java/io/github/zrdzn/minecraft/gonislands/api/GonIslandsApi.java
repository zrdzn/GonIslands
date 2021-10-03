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
package io.github.zrdzn.minecraft.gonislands.api;

import io.github.zrdzn.minecraft.gonislands.api.island.IslandService;
import io.github.zrdzn.minecraft.gonislands.api.island.IslandType;

public class GonIslandsApi {

    private IslandService islandService;
    private IslandType islandType;

    /**
     * Returns the IslandService instance.
     * <p>
     * Use this if you want for example create or delete specific islands
     * by external plugin.
     *
     * @return the island service
     */
    public IslandService getIslandService() {
        return this.islandService;
    }

    public void setIslandService(IslandService islandService) {
        this.islandService = islandService;
    }

    /**
     * Returns the IslandType enum.
     *
     * @return the global server island type
     */
    public IslandType getGlobalIslandType() {
        return this.islandType;
    }

    public void setIslandType(IslandType islandType) {
        this.islandType = islandType;
    }
}
