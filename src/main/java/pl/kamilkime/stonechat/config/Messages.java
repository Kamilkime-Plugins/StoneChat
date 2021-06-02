/*
 * Copyright 2021 Kamil Trysiński <kamilkime@pm.me>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package pl.kamilkime.stonechat.config;

import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.util.*;

public class Messages {

    private final Plugin plugin;
    private final File messagesFile;

    private final Map<String, Object> messages = new HashMap<>();

    public Messages(final Plugin plugin) {
        this.plugin = plugin;
        this.messagesFile = new File(plugin.getDataFolder(), "messages.yml");
    }

    public void loadMessages() {
        if (!this.messagesFile.exists()) {
            this.plugin.saveResource("messages.yml", false);
        }

        final YamlConfiguration cfg = YamlConfiguration.loadConfiguration(this.messagesFile);
        this.messages.clear();

        for (final String key : cfg.getKeys(false)) {
            if (cfg.isString(key)) {
                this.messages.put(key, color(cfg.getString(key)));
            } else if (cfg.isList(key)) {
                this.messages.put(key, color(cfg.getStringList(key)));
            } else {
                this.plugin.getLogger().info("Wiadomość " + key + " nie jest prawdiłowo utworzona");
            }
        }
    }

    public void sendMessage(final CommandSender sender, final String key, final Object... replacements) {
        Object message = this.messages.get(key);
        if (message == null) {
            sender.sendMessage("?!@#$%&*");
            return;
        }

        if (message instanceof String) {
            for (int i = 0; i < replacements.length; i += 2) {
                message = StringUtils.replace((String) message, replacements[i].toString(), replacements[i + 1].toString());
            }

            sender.sendMessage((String) message);
            return;
        }

        for (String messageLine : (List<String>) message) {
            for (int i = 0; i < replacements.length; i += 2) {
                messageLine = StringUtils.replace(messageLine, replacements[i].toString(), replacements[i + 1].toString());
            }

            sender.sendMessage(messageLine);
        }
    }

    private static String color(final String string) {
        if (string == null || string.isEmpty()) {
            return "";
        }

        return ChatColor.translateAlternateColorCodes('&', string);
    }

    private static List<String> color(final List<String> strings) {
        final List<String> colored = new ArrayList<>();
        for (final String string : strings) {
            colored.add(color(string));
        }

        return colored;
    }

}
