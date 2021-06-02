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

package pl.kamilkime.stonechat.data.impl;

import com.zaxxer.hikari.HikariDataSource;
import org.bukkit.plugin.Plugin;
import pl.kamilkime.stonechat.data.Database;
import pl.kamilkime.stonechat.user.User;

import java.util.Collection;

public class SqliteDatabase extends Database {

    private static final String SAVE_USER = "INSERT INTO {TABLE} VALUES (?, ?, ?) ON CONFLICT (uuid) DO UPDATE SET stone_mined = ?, " +
            "next_message_time = ?";

    public SqliteDatabase(final HikariDataSource dataSource, final Plugin plugin, final String tableName) {
        super(dataSource, plugin, tableName);
    }

    @Override
    public void saveUsers(final Collection<User> users) {
        this.saveUsers(users, SAVE_USER);
    }

}
