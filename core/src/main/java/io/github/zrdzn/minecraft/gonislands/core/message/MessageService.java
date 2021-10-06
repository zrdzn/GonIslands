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
package io.github.zrdzn.minecraft.gonislands.core.message;

import net.kyori.adventure.text.Component;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.slf4j.Logger;

import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.UUID;

public class MessageService {

    private final Server server;
    private final Logger logger;
    private final Map<Locale, ResourceBundle> bundleMap;

    public MessageService(Server server, Logger logger, Map<Locale, ResourceBundle> bundleMap) {
        this.server = server;
        this.logger = logger;
        this.bundleMap = bundleMap;
    }

    public void sendMessage(UUID playerId, String key, Object... replacements) {
        Player player = this.server.getPlayer(playerId);
        if (player == null) {
            this.logger.warn("There is not any online player with {} uuid.", playerId);
            return;
        }

        String message = this.getResourceBundle(player.locale()).getString(key);

        player.sendMessage(Component.text(String.format(message, replacements)));
    }

    private ResourceBundle getResourceBundle(Locale locale) {
        ResourceBundle bundle = this.bundleMap.get(locale);
        if (bundle == null) {
            bundle = ResourceBundle.getBundle("locale/locale", Locale.ENGLISH);
        }

        return bundle;
    }

}
