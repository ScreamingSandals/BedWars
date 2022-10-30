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
import cloud.commandframework.arguments.standard.StringArgument;
import org.jetbrains.annotations.Nullable;
import org.screamingsandals.bedwars.PlatformService;
import org.screamingsandals.bedwars.api.config.GameConfigurationContainer;
import org.screamingsandals.bedwars.api.events.TargetInvalidationReason;
import org.screamingsandals.bedwars.api.game.GameStatus;
import org.screamingsandals.bedwars.config.MainConfig;
import org.screamingsandals.bedwars.game.GameManagerImpl;
import org.screamingsandals.bedwars.game.TeamImpl;
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
                            if (game.get().getConfigurationContainer().getOrDefault(GameConfigurationContainer.ALLOW_FAKE_DEATH, false)) {
                                var fakeDeath = PlatformService.getInstance().getFakeDeath();
                                if (fakeDeath.isAvailable()) {
                                    fakeDeath.die(bwPlayer);
                                } else {
                                    bwPlayer.setHealth(0);
                                }
                            } else {
                                bwPlayer.setHealth(0);
                            }
                            Message.of(LangKeys.IN_GAME_CHEAT_RECEIVED_KILL)
                                    .placeholder("player", player.getName())
                                    .defaultPrefix()
                                    .send(player);
                        })
        );

        manager.command(commandSenderWrapperBuilder
                .literal("startEmptyGame")
                .handler(commandContext -> {
                    var player = commandContext.getSender().as(PlayerWrapper.class);

                    var game = playerManager.getGameOfPlayer(player);
                    if (game.isEmpty()) {
                        player.sendMessage(Message.of(LangKeys.IN_GAME_ERRORS_NOT_IN_ANY_GAME_YET).defaultPrefix());
                        return;
                    }
                    if (game.get().getStatus() != GameStatus.WAITING) {
                        player.sendMessage(Message.of(LangKeys.IN_GAME_CHEAT_GAME_NOT_WAITING).defaultPrefix());
                        return;
                    }

                    game.get().forceGameToStart = true;

                    player.sendMessage(Message.of(LangKeys.IN_GAME_CHEAT_GAME_FORCED).defaultPrefix());
                })
        );

        manager.command(commandSenderWrapperBuilder
                .literal("invalidateTarget", "destroybed")
                .argument(StringArgument.<CommandSenderWrapper>newBuilder("team")
                        .withSuggestionsProvider((c, s) -> {
                            var player = c.getSender().as(PlayerWrapper.class);

                            var game = playerManager.getGameOfPlayer(player);
                            if (game.isEmpty()) {
                                return List.of();
                            }

                            return game.get().getActiveTeams().stream().map(TeamImpl::getName).collect(Collectors.toList());
                        })
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

                    String teamName = commandContext.get("team");
                    var team = game.get().getTeamFromName(teamName);
                    if (team == null) {
                        player.sendMessage(
                                Message.of(LangKeys.IN_GAME_CHEAT_TEAM_DOES_NOT_EXIST)
                                    .placeholderRaw("team", teamName)
                                    .defaultPrefix()
                        );
                        return;
                    }

                    if (!game.get().isTeamActive(team)) {
                        player.sendMessage(
                                Message.of(LangKeys.IN_GAME_CHEAT_TEAM_IS_NOT_IN_GAME)
                                        .placeholderRaw("team", teamName)
                                        .defaultPrefix()
                        );
                        return;
                    }

                    var target = team.getTarget();
                    if (target.isValid()) {
                        game.get().internalProcessInvalidation(team, target, null,  TargetInvalidationReason.COMMAND);
                    } else {
                        player.sendMessage(
                                Message.of(LangKeys.IN_GAME_CHEAT_TARGET_IS_NOT_VALID)
                                        .placeholderRaw("team", teamName)
                                        .defaultPrefix()
                        );
                        return;
                    }

                    player.sendMessage(
                            Message.of(LangKeys.IN_GAME_CHEAT_RECEIVED_TARGET_INVALIDATED)
                                    .placeholderRaw("team", teamName)
                                    .defaultPrefix()
                    );
                })
        );

        manager.command(commandSenderWrapperBuilder
                .literal("invalidateAllTargets", "destroyallbeds")
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

                    for (var team : game.get().getActiveTeams()) {
                        var target = team.getTarget();
                        if (target.isValid()) {
                            game.get().internalProcessInvalidation(team, target, null,  TargetInvalidationReason.COMMAND);
                        }
                    }

                    player.sendMessage(
                            Message.of(LangKeys.IN_GAME_CHEAT_RECEIVED_TARGETS_INVALIDATED)
                                    .defaultPrefix()
                    );
                })
        );

        manager.command(commandSenderWrapperBuilder
                .literal("joinTeam")
                .argument(StringArgument.<CommandSenderWrapper>newBuilder("team")
                        .withSuggestionsProvider((c, s) -> {
                            var player = c.getSender().as(PlayerWrapper.class);

                            var game = playerManager.getGameOfPlayer(player);
                            if (game.isEmpty()) {
                                return List.of();
                            }

                            return game.get().getActiveTeams().stream().map(TeamImpl::getName).collect(Collectors.toList());
                        })
                        .asOptional()
                )
                .argument(StringArgument.<CommandSenderWrapper>newBuilder("player")
                        .withSuggestionsProvider((c, s) ->
                                Server.getConnectedPlayers().stream().map(PlayerWrapper::getName).collect(Collectors.toList())
                        )
                        .asOptional()
                )
                .handler(commandContext -> {
                    var player = commandContext.getSender().as(PlayerWrapper.class);

                    var gameOpt = playerManager.getGameOfPlayer(player);
                    if (gameOpt.isEmpty()) {
                        player.sendMessage(Message.of(LangKeys.IN_GAME_ERRORS_NOT_IN_ANY_GAME_YET).defaultPrefix());
                        return;
                    }
                    var game = gameOpt.get();

                    @Nullable String teamName = commandContext.getOrDefault("team", null);

                    @Nullable String chosenPlayer = commandContext.getOrDefault("player", null);
                    @Nullable PlayerWrapper chosenPlayerWrapper = null;
                    if (chosenPlayer != null) {
                        var chosenPlayerWrapperOpt = PlayerMapper.getPlayer(chosenPlayer);
                        if (chosenPlayerWrapperOpt.isEmpty() || !playerManager.isPlayerInGame(chosenPlayerWrapperOpt.get())) {
                            player.sendMessage(Message.of(LangKeys.IN_GAME_CHEAT_INVALID_PLAYER));
                            return;
                        } else {
                            chosenPlayerWrapper = chosenPlayerWrapperOpt.get();
                        }
                    }

                    @Nullable TeamImpl team;
                    if (teamName != null) {
                        team = gameOpt.get().getTeamFromName(teamName);
                        if (team == null) {
                            player.sendMessage(
                                    Message.of(LangKeys.IN_GAME_CHEAT_TEAM_DOES_NOT_EXIST)
                                            .placeholderRaw("team", teamName)
                                            .defaultPrefix()
                            );
                            return;
                        }

                        if (game.getStatus() != GameStatus.WAITING) {
                            if (!game.isTeamActive(team)) {
                                player.sendMessage(
                                        Message.of(LangKeys.IN_GAME_CHEAT_TEAM_IS_NOT_IN_GAME)
                                                .placeholderRaw("team", teamName)
                                                .defaultPrefix()
                                );
                                return;
                            }
                        }
                    } else {
                        team = game.chooseRandomTeamForPlayerToJoin(true, true);
                    }

                    if (team == null) {
                        player.sendMessage(
                                Message.of(LangKeys.IN_GAME_CHEAT_TEAM_RANDOM_FAILED)
                                        .defaultPrefix()
                        );
                        return;
                    }

                    var bedwarsPlayer = (chosenPlayerWrapper != null ? chosenPlayerWrapper : player).as(BedWarsPlayer.class);

                    var result = game.internalTeamJoin(bedwarsPlayer, team, true);

                    if (game.getStatus() != GameStatus.WAITING) {
                        if (result) {
                            if (bedwarsPlayer.isSpectator()) {
                                game.makePlayerFromSpectator(bedwarsPlayer);
                            } else {
                                bedwarsPlayer.teleport(team.getRandomSpawn());
                            }
                        }
                    }

                    if (result) {
                        player.sendMessage(
                                Message.of(LangKeys.IN_GAME_CHEAT_RECEIVED_TEAM_JOIN)
                                        .placeholderRaw("player", bedwarsPlayer.getName())
                                        .placeholderRaw("team", teamName)
                                        .defaultPrefix()
                        );
                    } else {
                        player.sendMessage(
                                Message.of(LangKeys.IN_GAME_CHEAT_CHEAT_FAILED)
                                        .defaultPrefix()
                        );
                    }
                })
        );
    }
}
