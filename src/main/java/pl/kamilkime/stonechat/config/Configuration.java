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

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import pl.kamilkime.stonechat.data.Database;
import pl.kamilkime.stonechat.data.DatabaseType;

import java.io.File;
import java.io.IOException;
import java.util.Optional;

public class Configuration {

    private final Plugin plugin;
    private final File configFile;

    public int requiredStone = 100;
    public int slowmodeCooldown;
    public boolean chatStatus = true;
    public long autosaveInterval;

    public Configuration(final Plugin plugin) {
        this.plugin = plugin;
        this.configFile = new File(plugin.getDataFolder(), "data/config.dat");
    }

    public Optional<Database> loadDatabase() {
        this.plugin.saveDefaultConfig();
        this.plugin.reloadConfig();

        final ConfigurationSection cfg = this.plugin.getConfig();

        try {
            final DatabaseType databaseType = DatabaseType.valueOf(cfg.getString("database.mode", "SQLITE"));

            final ConfigurationSection databaseSection = cfg.getConfigurationSection("database." + databaseType.toString().toLowerCase());
            if (databaseSection == null) {
                this.plugin.getLogger().severe("Brak sekcji danych bazy danych");
                return Optional.empty();
            }

            return databaseType.createDatabase(databaseSection, this.plugin);
        } catch (final IllegalArgumentException exception) {
            this.plugin.getLogger().severe("Nieprawidłowy typ bazy danych: " + this.plugin.getConfig().getString("database.mode"));
            return Optional.empty();
        }
    }

    public void loadSettings() {
        this.autosaveInterval = this.plugin.getConfig().getLong("autosaveInterval") * 20L;

        if (!this.configFile.exists()) {
            return;
        }

        final YamlConfiguration cfg = YamlConfiguration.loadConfiguration(this.configFile);

        this.requiredStone = cfg.getInt("requiredStone", 100);
        this.slowmodeCooldown = cfg.getInt("slowmodeCooldown", 0);
        this.chatStatus = cfg.getBoolean("chatStatus", true);
    }

    public void saveSettings() {
        final YamlConfiguration cfg = YamlConfiguration.loadConfiguration(this.configFile);

        cfg.set("requiredStone", this.requiredStone);
        cfg.set("slowmodeCooldown", this.slowmodeCooldown);
        cfg.set("chatStatus", this.chatStatus);

        try {
            cfg.save(this.configFile);
        } catch (final IOException exception) {
            this.plugin.getLogger().severe("Nie udało się zapisać konfiguracji");
        }
    }

}
