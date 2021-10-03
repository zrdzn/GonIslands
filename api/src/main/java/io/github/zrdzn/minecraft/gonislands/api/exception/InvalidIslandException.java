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
package io.github.zrdzn.minecraft.gonislands.api.exception;

import io.github.zrdzn.minecraft.gonislands.api.island.Island;

import java.util.UUID;

/**
 * Exception thrown when attempting to load an invalid {@link Island}
 */
public class InvalidIslandException extends Exception {

    /**
     * Creates a new instance of InvalidIslandException with the specified message and Island UUID.
     *
     * @param id id of the island
     * @param message details of the exception
     */
    public InvalidIslandException(UUID id, String message) {
        super(String.format("[%s] Invalid island: %s", id.toString(), message));
    }

}
