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

package pl.kamilkime.stonechat.data;

import com.google.common.io.Files;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.Plugin;
import pl.kamilkime.stonechat.data.impl.MysqlDatabase;
import pl.kamilkime.stonechat.data.impl.SqliteDatabase;

import java.io.File;
import java.io.IOException;
import java.util.Optional;
import java.util.function.BiFunction;

public enum DatabaseType {

    MYSQL ((section, plugin) -> {
        final HikariConfig hikariConfig = new HikariConfig();

        hikariConfig.setJdbcUrl("jdbc:mysql://" + section.getString("host") + ":" + section.getInt("port", 3306) + "/" +
                section.getString("database") + "?characterEncoding=UTF-8&useUnicode=true&useSSL=" + section.getBoolean("useSSL"));
        hikariConfig.setUsername(section.getString("user"));
        hikariConfig.setPassword(section.getString("password"));
        hikariConfig.addDataSourceProperty("cachePrepStmts", "true");
        hikariConfig.addDataSourceProperty("prepStmtCacheSize", "250");
        hikariConfig.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        hikariConfig.addDataSourceProperty("useServerPrepStmts", true);
        hikariConfig.addDataSourceProperty("cacheResultSetMetadata", true);
        hikariConfig.addDataSourceProperty("tcpKeepAlive", true);
        hikariConfig.setLeakDetectionThreshold(60000L);
        hikariConfig.setMaximumPoolSize(section.getInt("poolSize"));
        hikariConfig.setMinimumIdle(0);
        hikariConfig.setIdleTimeout(30000L);

        return Optional.of(new MysqlDatabase(new HikariDataSource(hikariConfig), plugin, section.getParent().getString("tableName", "stonechat")));
    }),

    SQLITE ((section, plugin) -> {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (final ClassNotFoundException exception) {
            plugin.getLogger().severe("Błąd inicjalizacji sterownika SQLite");
            return Optional.empty();
        }

        final File sqliteFile = new File(plugin.getDataFolder(), section.getString("fileName", "data.db"));
        if (!sqliteFile.exists()) {
            try {
                Files.createParentDirs(sqliteFile);

                if (!sqliteFile.createNewFile()) {
                    plugin.getLogger().severe("Błąd tworzenia pliku bazy SQLite");
                    return Optional.empty();
                }
            } catch (final IOException exception) {
                plugin.getLogger().severe("Błąd tworzenia pliku bazy SQLite");
                return Optional.empty();
            }
        }

        final HikariConfig hikariConfig = new HikariConfig();

        hikariConfig.setJdbcUrl("jdbc:sqlite:" + sqliteFile.getAbsolutePath());
        hikariConfig.setConnectionTestQuery("SELECT * FROM sqlite_master");

        return Optional.of(new SqliteDatabase(new HikariDataSource(hikariConfig), plugin, section.getParent().getString("tableName", "stonechat")));
    });

    private final BiFunction<ConfigurationSection, Plugin, Optional<Database>> databaseCreator;

    DatabaseType(final BiFunction<ConfigurationSection, Plugin, Optional<Database>> databaseCreator) {
        this.databaseCreator = databaseCreator;
    }

    public Optional<Database> createDatabase(final ConfigurationSection configurationSection, final Plugin plugin) {
        return this.databaseCreator.apply(configurationSection, plugin);
    }

}
