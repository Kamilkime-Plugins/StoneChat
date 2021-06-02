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

import com.zaxxer.hikari.HikariDataSource;
import org.apache.commons.lang.StringUtils;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import pl.kamilkime.stonechat.user.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Optional;
import java.util.logging.Logger;

public abstract class Database {

    private static final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS {TABLE} (uuid VARCHAR(36) NOT NULL, stone_mined INT NOT NULL, " +
            "next_message_time BIGINT NOT NULL, PRIMARY KEY(uuid));";
    private static final String LOAD_USER = "SELECT * FROM {TABLE} WHERE uuid = ?;";

    private final HikariDataSource dataSource;
    private final String tableName;
    private final Logger logger;

    public Database(final HikariDataSource dataSource, final Plugin plugin, final String tableName) {
        this.dataSource = dataSource;
        this.tableName = tableName;
        this.logger = plugin.getLogger();
    }

    public void createTable() {
        try (final Connection connection = this.dataSource.getConnection();
             final PreparedStatement statement = connection.prepareStatement(this.replaceTable(CREATE_TABLE))) {
            statement.executeUpdate();
        } catch (final SQLException exception) {
            this.logger.severe("Nieudane utworzenie tabeli w bazie danych");
        }
    }

    public Optional<User> loadUser(final Player player) {
        try (final Connection connection = this.dataSource.getConnection();
             final PreparedStatement statement = connection.prepareStatement(this.replaceTable(LOAD_USER))) {
            statement.setString(1, player.getUniqueId().toString());

            try (final ResultSet resultSet = statement.executeQuery()) {
                if (!resultSet.next()) {
                    return Optional.empty();
                }

                return Optional.of(new User(player.getUniqueId(), resultSet.getInt("stone_mined"), resultSet.getLong("next_message_time")));
            }
        } catch (final SQLException exception) {
            this.logger.severe("Nieudane pobranie danych gracza z bazy danych");
        }

        return Optional.empty();
    }

    protected void saveUsers(final Collection<User> users, final String query) {
        try (final Connection connection = this.dataSource.getConnection();
             final PreparedStatement statement = connection.prepareStatement(this.replaceTable(query))) {
            connection.setAutoCommit(false);

            for (final User user : users) {
                statement.setString(1, user.getUuid().toString());
                statement.setInt(2, user.getStoneMined());
                statement.setLong(3, user.getNextMessageTime());
                statement.setInt(4, user.getStoneMined());
                statement.setLong(5, user.getNextMessageTime());
                statement.addBatch();
            }

            statement.executeBatch();
            connection.commit();

            connection.setAutoCommit(true);
        } catch (final SQLException exception) {
            this.logger.severe("Nieudany zapis gracza w bazie danych");
        }
    }

    public abstract void saveUsers(final Collection<User> users);

    private String replaceTable(final String statement) {
        return StringUtils.replace(statement, "{TABLE}", this.tableName);
    }

}
