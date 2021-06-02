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

import org.bukkit.command.CommandSender;
import pl.kamilkime.stonechat.StoneChat;
import pl.kamilkime.stonechat.command.SubCommand;
import pl.kamilkime.stonechat.config.Configuration;
import pl.kamilkime.stonechat.config.Messages;

public class ReloadSubCommand implements SubCommand {

    private final StoneChat plugin;
    private final Configuration configuration;
    private final Messages messages;

    public ReloadSubCommand(final StoneChat plugin, final Configuration configuration, final Messages messages) {
        this.plugin = plugin;
        this.configuration = configuration;
        this.messages = messages;
    }

    @Override
    public void execute(final CommandSender sender, final String[] args) {
        this.configuration.loadSettings();
        this.messages.loadMessages();
        this.plugin.restartAutosave();

        this.messages.sendMessage(sender, "reloadComplete");
    }

    @Override
    public String getPermission() {
        return "stonechat.reload";
    }

}
