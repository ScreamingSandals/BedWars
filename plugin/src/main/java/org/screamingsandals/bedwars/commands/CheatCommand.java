/*
 * Copyright (C) 2022 ScreamingSandals
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
import cloud.commandframework.arguments.standard.IntegerArgument;
import org.screamingsandals.bedwars.api.game.GameStatus;
import org.screamingsandals.bedwars.config.MainConfig;
import org.screamingsandals.bedwars.game.GameManagerImpl;
import org.screamingsandals.bedwars.lang.LangKeys;
import org.screamingsandals.bedwars.player.BedWarsPlayer;
import org.screamingsandals.bedwars.player.PlayerManagerImpl;
import org.screamingsandals.bedwars.special.PopUpTowerImpl;
import org.screamingsandals.bedwars.utils.MiscUtils;
import org.screamingsandals.lib.Server;
import org.screamingsandals.lib.block.BlockTypeHolder;
import org.screamingsandals.lib.entity.EntityMapper;
import org.screamingsandals.lib.lang.Message;
import org.screamingsandals.lib.player.PlayerMapper;
import org.screamingsandals.lib.player.PlayerWrapper;
import org.screamingsandals.lib.sender.CommandSenderWrapper;
import org.screamingsandals.lib.utils.BlockFace;
import org.screamingsandals.lib.utils.annotations.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CheatCommand extends BaseCommand {
    private final PlayerManagerImpl playerManager;
    private final MainConfig mainConfig;

    public CheatCommand(PlayerManagerImpl playerManager, MainConfig mainConfig) {
        super("cheat", BedWarsPermission.ADMIN_PERMISSION, false);
        this.playerManager = playerManager;
        this.mainConfig = mainConfig;
    }

    @Override
    protected void construct(Command.Builder<CommandSenderWrapper> commandSenderWrapperBuilder, CommandManager<CommandSenderWrapper> manager) {
        if (!mainConfig.node("enable-cheat-command-for-admins").getBoolean()) {
            return;
        }

        manager.command(commandSenderWrapperBuilder
                .literal("buildPopUpTower")
                .argument(manager
                        .argumentBuilder(String.class, "game")
                        .withSuggestionsProvider((c, s) -> GameManagerImpl.getInstance().getGameNames())
                        .asOptional())
                .handler(commandContext -> {
                    var player = commandContext.getSender().as(PlayerWrapper.class);
                    Optional<String> game = commandContext.getOptional("game");

                    var playerFace = MiscUtils.yawToFace(player.getLocation().getYaw(), false);

                    if (game.isPresent()) {
                        var arenaN = game.get();
                        GameManagerImpl.getInstance().getGame(arenaN).ifPresentOrElse(
                                game1 -> {
                                    var popupT = new PopUpTowerImpl(game1, playerManager.getPlayerOrCreate(player), null, BlockTypeHolder.of("minecraft:white_wool"), player.getLocation().getBlock().getLocation().add(playerFace).add(BlockFace.DOWN), playerFace);
                                    popupT.runTask();
                                    player.sendMessage(Message.of(LangKeys.IN_GAME_CHEAT_SPECIAL_ITEM_USED).placeholder("item", "Pop-Up Tower").defaultPrefix());
                                },
                                () -> player.sendMessage(Message.of(LangKeys.IN_GAME_ERRORS_GAME_NOT_FOUND).defaultPrefix())
                        );
                    } else if (playerManager.isPlayerInGame(player)) {
                        var bwPlayer = player.as(BedWarsPlayer.class);
                        var popupT = new PopUpTowerImpl(bwPlayer.getGame(), bwPlayer, bwPlayer.getGame().getPlayerTeam(bwPlayer), BlockTypeHolder.of("minecraft:white_wool"), player.getLocation().getBlock().getLocation().add(playerFace).add(BlockFace.DOWN), playerFace);
                        popupT.runTask();
                        player.sendMessage(Message.of(LangKeys.IN_GAME_CHEAT_SPECIAL_ITEM_USED).placeholder("item", "Pop-Up Tower").defaultPrefix());
                    } else {
                        player.sendMessage(Message.of(LangKeys.IN_GAME_ERRORS_YOU_ARE_NOT_IN_GAME).defaultPrefix());
                    }
                })
        );

        manager.command(commandSenderWrapperBuilder
                .literal("rebuildRegion")
                .argument(manager
                        .argumentBuilder(String.class, "game")
                        .withSuggestionsProvider((c, s) -> GameManagerImpl.getInstance().getGameNames())
                        .asOptional())
                .handler(commandContext -> {
                    var player = commandContext.getSender().as(PlayerWrapper.class);
                    Optional<String> game = commandContext.getOptional("game");

                    if (game.isPresent()) {
                        var arenaN = game.get();
                        GameManagerImpl.getInstance().getGame(arenaN).ifPresentOrElse(
                                game1 -> {
                                    game1.getRegion().regen();
                                },
                                () -> player.sendMessage(Message.of(LangKeys.IN_GAME_ERRORS_GAME_NOT_FOUND).defaultPrefix())
                        );
                    } else if (playerManager.isPlayerInGame(player)) {
                        var bwPlayer = player.as(BedWarsPlayer.class);
                        bwPlayer.getGame().getRegion().regen();
                        player.sendMessage(Message.of(LangKeys.IN_GAME_CHEAT_REGION_REGENERATED).placeholder("arena", bwPlayer.getGame().getName()).defaultPrefix());
                    } else {
                        player.sendMessage(Message.of(LangKeys.IN_GAME_ERRORS_YOU_ARE_NOT_IN_GAME).defaultPrefix());
                    }
                })
        );

        manager.command(
                commandSenderWrapperBuilder
                .literal("give")
                .argument(manager
                        .argumentBuilder(String.class, "resource")
                        .withSuggestionsProvider((c, s) -> {
                            var player = c.getSender().as(PlayerWrapper.class);

                            var game = playerManager.getGameOfPlayer(player);
                            if (game.isEmpty()) {
                                return List.of();
                            }

                            return game.get().getGameVariant().getItemSpawnerTypeNames();
                        })
                )
                .argument(IntegerArgument.optional("amount", 1))
                .argument(manager
                        .argumentBuilder(String.class, "player")
                        .withSuggestionsProvider((c, s) ->
                                Server.getConnectedPlayers().stream().map(PlayerWrapper::getName).collect(Collectors.toList())
                        )
                        .asOptional()
                )
                .handler(commandContext -> {
                    var player = commandContext.getSender().as(PlayerWrapper.class);

                    var game = playerManager.getGameOfPlayer(player);
                    if (game.isEmpty()) {
                        player.sendMessage(Message.of(LangKeys.IN_GAME_ERRORS_NOT_IN_ANY_GAME_YET).defaultPrefix());
                        return;
                    }
                    if (game.get().getStatus() != GameStatus.RUNNING) {
                        player.sendMessage(Message.of(LangKeys.IN_GAME_CHEAT_GAME_NOT_RUNNING).defaultPrefix());
                        return;
                    }

                    var resource = commandContext.<String>get("resource");
                    int amount = commandContext.get("amount");
                    var receiver = commandContext.<String>getOptional("player");
                    BedWarsPlayer bwPlayer;
                    if (receiver.isPresent()) {
                        var playerWrapper = receiver.flatMap(PlayerMapper::getPlayer);
                        if (playerWrapper.isEmpty() || !playerManager.isPlayerInGame(playerWrapper.get())) {
                            player.sendMessage(Message.of(LangKeys.IN_GAME_CHEAT_INVALID_PLAYER));
                            return;
                        }
                        bwPlayer = playerManager.getPlayer(playerWrapper.get()).orElseThrow();
                    } else {
                        bwPlayer = playerManager.getPlayer(player).orElseThrow();
                    }

                    if (bwPlayer.isSpectator()) {
                        player.sendMessage(Message.of(LangKeys.IN_GAME_CHEAT_INVALID_PLAYER));
                        return;
                    }
                    var spawnerType = game.get().getGameVariant().getItemSpawnerType(resource);
                    if (spawnerType == null) {
                        player.sendMessage(Message.of(LangKeys.ADMIN_ARENA_EDIT_ERRORS_INVALID_SPAWNER_TYPE).defaultPrefix());
                        return;
                    }
                    var remaining = bwPlayer.getPlayerInventory().addItem(spawnerType.getItem(amount));
                    remaining.forEach(item ->
                        EntityMapper.dropItem(item, player.getLocation())
                    );
                    Message.of(LangKeys.IN_GAME_CHEAT_RECEIVED_GIVE)
                            .placeholder("player", player.getName())
                            .placeholder("amount", amount)
                            .placeholder("resource", spawnerType.getItemName().asComponent())
                            .defaultPrefix()
                            .send(player);
                })
        );

        manager.command(
                commandSenderWrapperBuilder
                        .literal("kill")
                        .argument(manager
                                .argumentBuilder(String.class, "player")
                                .withSuggestionsProvider((c, s) ->
                                        Server.getConnectedPlayers().stream().map(PlayerWrapper::getName).collect(Collectors.toList())
                                )
                                .asOptional())
                        .handler(commandContext -> {
                            var player = commandContext.getSender().as(PlayerWrapper.class);

                            var game = playerManager.getGameOfPlayer(player);
                            if (game.isEmpty()) {
                                player.sendMessage(Message.of(LangKeys.IN_GAME_ERRORS_NOT_IN_ANY_GAME_YET).defaultPrefix());
                                return;
                            }
                            if (game.get().getStatus() != GameStatus.RUNNING) {
                                player.sendMessage(Message.of(LangKeys.IN_GAME_CHEAT_GAME_NOT_RUNNING).defaultPrefix());
                                return;
                            }

                            var receiver = commandContext.<String>getOptional("player");
                            BedWarsPlayer bwPlayer;
                            if (receiver.isPresent()) {
                                var playerWrapper = receiver.flatMap(PlayerMapper::getPlayer);
                                if (playerWrapper.isEmpty() || !playerManager.isPlayerInGame(playerWrapper.get())) {
                                    player.sendMessage(Message.of(LangKeys.IN_GAME_CHEAT_INVALID_PLAYER));
                                    return;
                                }
                                bwPlayer = playerManager.getPlayer(playerWrapper.get()).orElseThrow();
                            } else {
                                bwPlayer = playerManager.getPlayer(player).orElseThrow();
                            }

                            if (bwPlayer.isSpectator()) {
                                player.sendMessage(Message.of(LangKeys.IN_GAME_CHEAT_INVALID_PLAYER));
                                return;
                            }
                            bwPlayer.setHealth(0);
                            Message.of(LangKeys.IN_GAME_CHEAT_RECEIVED_KILL)
                                    .placeholder("player", player.getName())
                                    .defaultPrefix()
                                    .send(player);
                        })
        );
    }
}
