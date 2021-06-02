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

package pl.kamilkime.stonechat.command.sub;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import pl.kamilkime.stonechat.command.SubCommand;
import pl.kamilkime.stonechat.config.Configuration;
import pl.kamilkime.stonechat.config.Messages;

public class SlowSubCommand implements SubCommand {

    private final Plugin plugin;
    private final Configuration configuration;
    private final Messages messages;

    public SlowSubCommand(final Plugin plugin, final Configuration configuration, final Messages messages) {
        this.plugin = plugin;
        this.configuration = configuration;
        this.messages = messages;
    }

    @Override
    public void execute(final CommandSender sender, final String[] args) {
        if (args.length != 2) {
            this.messages.sendMessage(sender, "slowCorrectUsage");
            return;
        }

        final int slowmodeCooldown;
        try {
            slowmodeCooldown = Math.max(Integer.parseInt(args[1]), 0);
        } catch (final NumberFormatException exception) {
            this.messages.sendMessage(sender, "slowCorrectUsage");
            return;
        }

        this.configuration.slowmodeCooldown = slowmodeCooldown;
        Bukkit.getScheduler().runTaskAsynchronously(this.plugin, this.configuration::saveSettings);

        final String messageKey = slowmodeCooldown == 0 ? "slowmodeOff" : "slowmodeSet";
        for (final Player player : Bukkit.getOnlinePlayers()) {
            this.messages.sendMessage(player, messageKey, "{TIME}", slowmodeCooldown, "{PLAYER}", sender.getName());
        }
    }

    @Override
    public String getPermission() {
        return "stonechat.slow";
    }

}
