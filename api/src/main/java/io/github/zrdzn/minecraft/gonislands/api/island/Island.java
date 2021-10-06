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

import java.util.Optional;
import java.util.UUID;

public interface Island {

    /**
     * Gets the Island UUID.
     *
     * @return the island uuid
     */
    UUID getId();

    /**
     * Gets the IslandType for this Island.
     *
     * @return the island type
     */
    IslandType getType();

    /**
     * Gets the World UUID for this Island.
     *
     * @return the island world uuid
     */
    UUID getWorldId();

    /**
     * Gets the Island optional name.
     *
     * @return the island optional name
     */
    Optional<String> getName();

    /**
     * Gets the Player UUID of the Island.
     *
     * @return the island owner uuid
     */
    UUID getOwnerId();

}
