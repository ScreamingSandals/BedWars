/*
 * Copyright (C) 2024 ScreamingSandals
 *
 * This file is part of Screaming BedWars.
 *
 * Screaming BedWars is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Screaming BedWars is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Screaming BedWars. If not, see <https://www.gnu.org/licenses/>.
 */

package org.screamingsandals.bedwars.commands;

import cloud.commandframework.Command;
import cloud.commandframework.CommandManager;
import org.screamingsandals.bedwars.game.GameManagerImpl;
import org.screamingsandals.bedwars.lang.LangKeys;
import org.screamingsandals.bedwars.player.PlayerManagerImpl;
import org.screamingsandals.lib.lang.Message;
import org.screamingsandals.lib.player.Player;
import org.screamingsandals.lib.sender.CommandSender;
import org.screamingsandals.lib.utils.annotations.Service;

@Service
public class RejoinCommand extends BaseCommand {
    private final PlayerManagerImpl playerManager;

    public RejoinCommand(PlayerManagerImpl playerManager) {
        super("rejoin", BedWarsPermission.REJOIN_PERMISSION, false);
        this.playerManager = playerManager;
    }

    @Override
    protected void construct(Command.Builder<CommandSender> commandSenderWrapperBuilder, CommandManager<CommandSender> manager) {
        manager.command(
                commandSenderWrapperBuilder
                    .handler(commandContext -> {
                        var player = commandContext.getSender().as(Player.class);
                        if (playerManager.isPlayerInGame(player)) {
                            commandContext.getSender().sendMessage(Message.of(LangKeys.IN_GAME_ERRORS_ALREADY_IN_GAME).defaultPrefix());
                            return;
                        }

                        rejoin(player);
                    })
        );
    }

    public void rejoin(Player player) {
        String name = null;
        if (playerManager.isPlayerRegistered(player)) {
            name = playerManager.getPlayer(player).orElseThrow().getLatestGameName();
        }
        if (name == null) {
            player.sendMessage(Message.of(LangKeys.IN_GAME_ERRORS_YOU_ARE_NOT_IN_GAME).defaultPrefix());
        } else {
            GameManagerImpl.getInstance().getGame(name)
                    .ifPresentOrElse(
                            game -> game.joinToGame(PlayerManagerImpl.getInstance().getPlayerOrCreate(player)),
                            () -> player.sendMessage(Message.of(LangKeys.IN_GAME_ERRORS_GAME_IS_GONE).defaultPrefix())
                    );
        }
    }
}
