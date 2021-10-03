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
import org.bukkit.entity.Player;

import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

public class MessageService {

    private final Map<Locale, ResourceBundle> bundleMap;

    public MessageService(Map<Locale, ResourceBundle> bundleMap) {
        this.bundleMap = bundleMap;
    }

    public void sendMessage(Player player, String key, Object... replacements) {
        String message = this.getResourceBundle(player.locale()).getString(key);

        player.sendMessage(Component.text(String.format(message, replacements)));
    }

    private ResourceBundle getResourceBundle(Locale locale) {
        ResourceBundle bundle = this.bundleMap.get(locale);
        if (bundle == null) {
            bundle = ResourceBundle.getBundle("keys/keys", Locale.ENGLISH);
        }
        return bundle;
    }

}
