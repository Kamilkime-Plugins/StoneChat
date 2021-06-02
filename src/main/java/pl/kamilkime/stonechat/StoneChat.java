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

package pl.kamilkime.stonechat;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;
import pl.kamilkime.stonechat.command.StonechatCommand;
import pl.kamilkime.stonechat.config.Configuration;
import pl.kamilkime.stonechat.config.Messages;
import pl.kamilkime.stonechat.data.Database;
import pl.kamilkime.stonechat.listener.PlayerListener;
import pl.kamilkime.stonechat.user.UserCache;

public final class StoneChat extends JavaPlugin {

    private Configuration configuration;
    private UserCache userCache;
    private Database database;

    private BukkitTask autosave;

    @Override
    public void onEnable() {
        this.userCache = new UserCache();

        this.configuration = new Configuration(this);
        this.configuration.loadSettings();

        final Messages messages = new Messages(this);
        messages.loadMessages();

        this.database = this.configuration.loadDatabase().orElseGet(() -> {
            this.getLogger().severe("Nie udało się załadować bazy danych");
            return null;
        });

        if (this.database == null) {
            this.setEnabled(false);
            return;
        }

        this.database.createTable();
        this.restartAutosave();

        this.getServer().getPluginManager().registerEvents(new PlayerListener(this, this.configuration, messages, this.userCache, this.database), this);
        this.getCommand("stonechat").setExecutor(new StonechatCommand(this, this.configuration, messages));

        this.getServer().getScheduler().runTaskAsynchronously(this, () -> {
            for (final Player player : Bukkit.getOnlinePlayers()) {
                this.database.loadUser(player).ifPresent((user) -> this.userCache.addUser(user));
            }
        });
    }

    @Override
    public void onDisable() {
        if (this.autosave != null) {
            this.autosave.cancel();
        }

        this.database.saveUsers(this.userCache.getUsers());
    }

    public void restartAutosave() {
        if (this.autosave != null) {
            this.autosave.cancel();
        }

        this.autosave = this.getServer().getScheduler().runTaskTimerAsynchronously(this, () -> {
            this.database.saveUsers(this.userCache.getUsers());
        }, this.configuration.autosaveInterval, this.configuration.autosaveInterval);
    }

}
