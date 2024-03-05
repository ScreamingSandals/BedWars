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
import cloud.commandframework.arguments.CommandArgument;
import cloud.commandframework.context.CommandContext;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.screamingsandals.bedwars.api.game.GameStatus;
import org.screamingsandals.bedwars.commands.BedWarsPermission;
import org.screamingsandals.bedwars.config.MainConfig;
import org.screamingsandals.bedwars.game.GameImpl;
import org.screamingsandals.bedwars.game.GameManagerImpl;
import org.screamingsandals.bedwars.lang.LangKeys;
import org.screamingsandals.bedwars.player.BedWarsPlayer;
import org.screamingsandals.bedwars.player.PlayerManagerImpl;
import org.screamingsandals.bedwars.special.PopUpTowerImpl;
import org.screamingsandals.bedwars.utils.MiscUtils;
import org.screamingsandals.lib.block.Block;
import org.screamingsandals.lib.lang.Message;
import org.screamingsandals.lib.player.Player;
import org.screamingsandals.lib.player.Players;
import org.screamingsandals.lib.sender.CommandSender;
import org.screamingsandals.lib.utils.BlockFace;
import org.screamingsandals.lib.utils.annotations.Service;

import java.util.Optional;

@Service
public class CheatAdminCommand extends CheatCommand {
    public CheatAdminCommand(PlayerManagerImpl playerManager, MainConfig mainConfig) {
        super("cheat", BedWarsPermission.ADMIN_PERMISSION, false, playerManager, mainConfig);
    }

    @Override
    protected void construct(Command.Builder<CommandSender> commandSenderWrapperBuilder, CommandManager<CommandSender> manager) {
        if (!mainConfig.node("enable-cheat-command-for-admins").getBoolean()) {
            return;
        }

        super.construct(commandSenderWrapperBuilder, manager);

        manager.command(commandSenderWrapperBuilder
                .literal("buildPopUpTower")
                .argument(manager
                        .argumentBuilder(String.class, "game")
                        .withSuggestionsProvider((c, s) -> GameManagerImpl.getInstance().getLocalGameNames())
                        .asOptional())
                .handler(commandContext -> {
                    var player = commandContext.getSender().as(Player.class);
                    Optional<String> game = commandContext.getOptional("game");

                    var playerFace = MiscUtils.yawToFace(player.getLocation().getYaw(), false);

                    if (game.isPresent()) {
                        var arenaN = game.get();
                        GameManagerImpl.getInstance().getLocalGame(arenaN).ifPresentOrElse(
                                game1 -> {
                                    var popupT = new PopUpTowerImpl(game1, playerManager.getPlayerOrCreate(player), null, Block.of("minecraft:white_wool"), player.getLocation().getBlock().location().add(playerFace).add(BlockFace.DOWN), playerFace);
                                    popupT.runTask();
                                    player.sendMessage(Message.of(LangKeys.IN_GAME_CHEAT_SPECIAL_ITEM_USED).placeholder("item", "Pop-Up Tower").defaultPrefix());
                                },
                                () -> player.sendMessage(Message.of(LangKeys.IN_GAME_ERRORS_GAME_NOT_FOUND).defaultPrefix())
                        );
                    } else if (playerManager.isPlayerInGame(player)) {
                        var bwPlayer = player.as(BedWarsPlayer.class);
                        var popupT = new PopUpTowerImpl(bwPlayer.getGame(), bwPlayer, bwPlayer.getGame().getPlayerTeam(bwPlayer), Block.of("minecraft:white_wool"), player.getLocation().getBlock().location().add(playerFace).add(BlockFace.DOWN), playerFace);
                        popupT.runTask();
                        player.sendMessage(Message.of(LangKeys.IN_GAME_CHEAT_SPECIAL_ITEM_USED).placeholder("item", "Pop-Up Tower").defaultPrefix());
                    } else {
                        player.sendMessage(Message.of(LangKeys.IN_GAME_ERRORS_YOU_ARE_NOT_IN_GAME).defaultPrefix());
                    }
                })
        );

        manager.command(commandSenderWrapperBuilder
                .literal("startEmptyGame")
                .handler(commandContext -> {
                    var game = getGame(commandContext);
                    if (game == null) {
                        return;
                    }

                    if (game.getStatus() != GameStatus.WAITING) {
                        commandContext.getSender().sendMessage(Message.of(LangKeys.IN_GAME_CHEAT_GAME_NOT_WAITING).defaultPrefix());
                        return;
                    }

                    game.forceGameToStart = true;

                    commandContext.getSender().sendMessage(Message.of(LangKeys.IN_GAME_CHEAT_GAME_FORCED).defaultPrefix());
                })
        );
    }

    @Override
    protected @Nullable GameImpl getGame(@NotNull CommandContext<@NotNull CommandSender> ctx) {
        var player = ctx.getSender().as(Player.class);

        var game = playerManager.getGameOfPlayer(player);
        if (game.isEmpty()) {
            player.sendMessage(Message.of(LangKeys.IN_GAME_ERRORS_NOT_IN_ANY_GAME_YET).defaultPrefix());
            return null;
        }
        return game.get();
    }

    @Override
    protected @Nullable GameImpl getGameForSuggestionProvider(@NotNull CommandContext<@NotNull CommandSender> ctx) {
        return playerManager.getGameOfPlayer(ctx.getSender().as(Player.class)).orElse(null);
    }

    @Override
    protected CommandArgument.@NotNull Builder<@NotNull CommandSender, @NotNull String> constructPlayerArgument(@NotNull CommandManager<@NotNull CommandSender> manager) {
        return super.constructPlayerArgument(manager).asOptional();
    }

    @Override
    protected @Nullable BedWarsPlayer requireBedWarsPlayer(@NotNull CommandContext<@NotNull CommandSender> ctx) {
        var game = getGame(ctx);

        var receiver = ctx.<String>getOptional("player");
        if (receiver.isPresent()) {
            var playerWrapper = receiver.map(Players::getPlayer);
            if (playerWrapper.isEmpty() || playerManager.getGameOfPlayer(playerWrapper.get()).orElse(null) != game) {
                ctx.getSender().sendMessage(Message.of(LangKeys.IN_GAME_CHEAT_INVALID_PLAYER));
                return null;
            }
            return playerManager.getPlayer(playerWrapper.get()).orElseThrow();
        } else {
            return playerManager.getPlayer(ctx.getSender().as(Player.class)).orElseThrow();
        }
    }
}
