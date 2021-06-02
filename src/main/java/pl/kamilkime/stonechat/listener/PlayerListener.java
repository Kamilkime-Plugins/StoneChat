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

package pl.kamilkime.stonechat.listener;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.Plugin;
import pl.kamilkime.stonechat.config.Configuration;
import pl.kamilkime.stonechat.config.Messages;
import pl.kamilkime.stonechat.data.Database;
import pl.kamilkime.stonechat.user.User;
import pl.kamilkime.stonechat.user.UserCache;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Collections;
import java.util.Locale;

public class PlayerListener implements Listener {

    private static final DecimalFormat ROUND = new DecimalFormat("#.##", DecimalFormatSymbols.getInstance(Locale.US));

    private final Plugin plugin;
    private final Configuration configuration;
    private final Messages messages;
    private final UserCache userCache;
    private final Database database;

    public PlayerListener(final Plugin plugin, final Configuration configuration, final Messages messages, final UserCache userCache, final Database database) {
        this.plugin = plugin;
        this.configuration = configuration;
        this.messages = messages;
        this.userCache = userCache;
        this.database = database;
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onAsyncChat(final AsyncPlayerChatEvent event) {
        final Player player = event.getPlayer();

        if (!this.configuration.chatStatus && !player.hasPermission("stonechat.bypass.chatoff")) {
            this.messages.sendMessage(player, "chatOffInfo");

            event.setCancelled(true);
            return;
        }

        final User user = this.userCache.getUser(player);
        if (user == null) {
            return;
        }

        if (this.configuration.requiredStone > user.getStoneMined() && !player.hasPermission("stonechat.bypass.stone")) {
            this.messages.sendMessage(player, "chatStoneInfo", "{REQUIRED}", this.configuration.requiredStone, "{MINED}", user.getStoneMined());

            event.setCancelled(true);
            return;
        }

        if (!user.canSendMessage() && !player.hasPermission("stonechat.bypass.slowmode")) {
            this.messages.sendMessage(player, "chatSlowmodeInfo", "{SLOWMODE}", this.configuration.slowmodeCooldown,
                    "{COOLDOWN}", ROUND.format((user.getNextMessageTime() - System.currentTimeMillis()) / 1000.0D));

            event.setCancelled(true);
            return;
        }

        user.cooldown(this.configuration.slowmodeCooldown);
    }

    @EventHandler
    public void onJoin(final PlayerJoinEvent event) {
        final Player player = event.getPlayer();

        Bukkit.getScheduler().runTaskAsynchronously(this.plugin, () -> {
            this.userCache.addUser(this.database.loadUser(player).orElse(new User(player.getUniqueId(), 0, 0L)));
        });
    }

    @EventHandler
    public void onQuit(final PlayerQuitEvent event) {
        Bukkit.getScheduler().runTaskAsynchronously(this.plugin, () -> {
            final User user = this.userCache.getUser(event.getPlayer());
            if (user == null) {
                return;
            }

            this.database.saveUsers(Collections.singletonList(user));
            this.userCache.removeUser(user);
        });
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onBlockBreak(final BlockBreakEvent event) {
        final Player player = event.getPlayer();

        final User user = this.userCache.getUser(player);
        if (user == null) {
            return;
        }

        if (event.getBlock().getType() != Material.STONE) {
            return;
        }

        if (user.addStoneMined() == this.configuration.requiredStone) {
            this.messages.sendMessage(player, "chatStoneEnabled", "{REQUIRED}", this.configuration.requiredStone);
        }
    }

}
