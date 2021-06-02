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
import pl.kamilkime.stonechat.command.SubCommand;
import pl.kamilkime.stonechat.config.Messages;

public class ClearSubCommand implements SubCommand {

    private final Messages messages;

    public ClearSubCommand(final Messages messages) {
        this.messages = messages;
    }

    @Override
    public void execute(final CommandSender sender, final String[] args) {
        for (final Player player : Bukkit.getOnlinePlayers()) {
            for (int i = 0; i < 100; i++) {
                player.sendMessage("");
            }

            this.messages.sendMessage(player, "chatCleared", "{PLAYER}", sender.getName());
        }
    }

    @Override
    public String getPermission() {
        return "stonechat.clear";
    }

}
