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

package pl.kamilkime.stonechat.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import pl.kamilkime.stonechat.StoneChat;
import pl.kamilkime.stonechat.command.sub.*;
import pl.kamilkime.stonechat.config.Configuration;
import pl.kamilkime.stonechat.config.Messages;

import java.util.HashMap;
import java.util.Map;

public class StonechatCommand implements CommandExecutor {

    private final Messages messages;
    private final Map<String, SubCommand> subCommands;

    public StonechatCommand(final StoneChat plugin, final Configuration configuration, final Messages messages) {
        this.messages = messages;
        this.subCommands = new HashMap<>();

        this.registerSubCommand(new ClearSubCommand(messages), "clear", "c", "wyczysc");
        this.registerSubCommand(new HelpSubCommand(messages), "help", "pomoc");
        this.registerSubCommand(new OffSubCommand(plugin, configuration, messages), "off", "wylacz");
        this.registerSubCommand(new OnSubCommand(plugin, configuration, messages), "on", "wlacz");
        this.registerSubCommand(new ReloadSubCommand(plugin, configuration, messages), "rl", "reload");
        this.registerSubCommand(new SlowSubCommand(plugin, configuration, messages), "slow", "slowmode");
        this.registerSubCommand(new StoneSubCommand(plugin, configuration, messages), "stone", "kamien");
        this.registerSubCommand(new ToggleSubCommand(plugin, configuration, messages), "toggle", "t", "przelacz");
    }

    @Override
    public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
        if (args.length == 0) {
            this.subCommands.get("help").execute(sender, args);
            return true;
        }

        final SubCommand subCommand = this.subCommands.get(args[0].toLowerCase());
        if (subCommand == null) {
            this.subCommands.get("help").execute(sender, args);
            return true;
        }

        if (!sender.hasPermission(subCommand.getPermission())) {
            this.messages.sendMessage(sender, "noPermission");
            return true;
        }

        subCommand.execute(sender, args);
        return true;
    }

    private void registerSubCommand(final SubCommand subCommand, final String... args) {
        for (final String arg : args) {
            this.subCommands.put(arg, subCommand);
        }
    }

}
