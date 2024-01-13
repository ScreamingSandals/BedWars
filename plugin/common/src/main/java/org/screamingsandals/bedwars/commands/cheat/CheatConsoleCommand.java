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

package org.screamingsandals.bedwars.commands.cheat;

import cloud.commandframework.Command;
import cloud.commandframework.CommandManager;
import cloud.commandframework.context.CommandContext;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.screamingsandals.bedwars.commands.BedWarsPermission;
import org.screamingsandals.bedwars.config.MainConfig;
import org.screamingsandals.bedwars.game.GameImpl;
import org.screamingsandals.bedwars.game.GameManagerImpl;
import org.screamingsandals.bedwars.lang.LangKeys;
import org.screamingsandals.bedwars.player.BedWarsPlayer;
import org.screamingsandals.bedwars.player.PlayerManagerImpl;
import org.screamingsandals.lib.lang.Message;
import org.screamingsandals.lib.player.Players;
import org.screamingsandals.lib.sender.CommandSender;
import org.screamingsandals.lib.utils.annotations.Service;

@Service
public class CheatConsoleCommand extends CheatCommand {
    private final GameManagerImpl gameManager;

    public CheatConsoleCommand(PlayerManagerImpl playerManager, GameManagerImpl gameManager, MainConfig mainConfig) {
        super("cheatIn", BedWarsPermission.ADMIN_PERMISSION, true, playerManager, mainConfig);
        this.gameManager = gameManager;
    }

    @Override
    protected void construct(Command.Builder<CommandSender> commandSenderWrapperBuilder, CommandManager<CommandSender> manager) {
        super.construct(commandSenderWrapperBuilder
                .argument(
                        manager.argumentBuilder(String.class, "game")
                            .withSuggestionsProvider((c, s) -> GameManagerImpl.getInstance().getGameNames())),
                        manager
        );
    }

    @Override
    protected @Nullable GameImpl getGame(@NotNull CommandContext<@NotNull CommandSender> ctx) {
        var game = gameManager.getGame(ctx.<String>get("game"));
        if (game.isEmpty()) {
            ctx.getSender().sendMessage(Message.of(LangKeys.IN_GAME_ERRORS_GAME_NOT_FOUND).defaultPrefix());
            return null;
        }
        return game.get();
    }

    @Override
    protected @Nullable GameImpl getGameForSuggestionProvider(@NotNull CommandContext<@NotNull CommandSender> ctx) {
        return gameManager.getGame(ctx.<String>get("game")).orElse(null);
    }

    @Override
    protected @Nullable BedWarsPlayer requireBedWarsPlayer(@NotNull CommandContext<@NotNull CommandSender> ctx) {
        var game = getGame(ctx);

        var receiver = ctx.<String>get("player");
        var playerWrapper = Players.getPlayer(receiver);
        if (playerWrapper == null || playerManager.getGameOfPlayer(playerWrapper).orElse(null) != game) {
            ctx.getSender().sendMessage(Message.of(LangKeys.IN_GAME_CHEAT_INVALID_PLAYER));
            return null;
        }
        return playerManager.getPlayer(playerWrapper).orElseThrow();
    }
}
