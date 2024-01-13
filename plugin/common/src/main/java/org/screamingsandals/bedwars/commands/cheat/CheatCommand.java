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
import cloud.commandframework.arguments.standard.IntegerArgument;
import cloud.commandframework.arguments.standard.StringArgument;
import cloud.commandframework.context.CommandContext;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.screamingsandals.bedwars.BedWarsPlugin;
import org.screamingsandals.bedwars.api.config.GameConfigurationContainer;
import org.screamingsandals.bedwars.api.events.TargetInvalidationReason;
import org.screamingsandals.bedwars.api.game.GameStatus;
import org.screamingsandals.bedwars.commands.BaseCommand;
import org.screamingsandals.bedwars.commands.BedWarsPermission;
import org.screamingsandals.bedwars.config.MainConfig;
import org.screamingsandals.bedwars.game.GameImpl;
import org.screamingsandals.bedwars.game.TeamImpl;
import org.screamingsandals.bedwars.lang.LangKeys;
import org.screamingsandals.bedwars.player.BedWarsPlayer;
import org.screamingsandals.bedwars.player.PlayerManagerImpl;
import org.screamingsandals.lib.entity.Entities;
import org.screamingsandals.lib.lang.Message;
import org.screamingsandals.lib.player.Player;
import org.screamingsandals.lib.sender.CommandSender;

import java.util.List;
import java.util.stream.Collectors;

public abstract class CheatCommand extends BaseCommand {
    protected final PlayerManagerImpl playerManager;
    protected final MainConfig mainConfig;

    public CheatCommand(String name, BedWarsPermission possiblePermission, boolean allowConsole, PlayerManagerImpl playerManager, MainConfig mainConfig) {
        super(name, possiblePermission, allowConsole);
        this.playerManager = playerManager;
        this.mainConfig = mainConfig;
    }

    @Override
    protected void construct(Command.Builder<CommandSender> commandSenderWrapperBuilder, CommandManager<CommandSender> manager) {
        if (!mainConfig.node("enable-cheat-command-for-admins").getBoolean()) {
            return;
        }

        manager.command(commandSenderWrapperBuilder
                .literal("rebuildRegion")
                .handler(commandContext -> {
                    var game = getGame(commandContext);
                    if (game == null) {
                        return;
                    }

                    game.getRegion().regen();
                    commandContext.getSender().sendMessage(Message.of(LangKeys.IN_GAME_CHEAT_REGION_REGENERATED).placeholder("arena",game.getName()).defaultPrefix());
                })
        );

        manager.command(
                commandSenderWrapperBuilder
                .literal("give")
                .argument(manager
                        .argumentBuilder(String.class, "resource")
                        .withSuggestionsProvider((c, s) -> {
                            var game = getGameForSuggestionProvider(c);
                            if (game == null) {
                                return List.of();
                            }

                            return game.getGameVariant().getItemSpawnerTypeNames();
                        })
                )
                .argument(IntegerArgument.optional("amount", 1))
                .argument(constructPlayerArgument(manager))
                .handler(commandContext -> {
                    var sender = commandContext.getSender();

                    var game = getGame(commandContext);
                    if (game == null) {
                        return;
                    }
                    if (game.getStatus() != GameStatus.RUNNING) {
                        sender.sendMessage(Message.of(LangKeys.IN_GAME_CHEAT_GAME_NOT_RUNNING).defaultPrefix());
                        return;
                    }

                    var resource = commandContext.<String>get("resource");
                    int amount = commandContext.get("amount");
                    var bwPlayer = requireBedWarsPlayer(commandContext);
                    if (bwPlayer == null) {
                        return;
                    }

                    if (bwPlayer.isSpectator()) {
                        sender.sendMessage(Message.of(LangKeys.IN_GAME_CHEAT_INVALID_PLAYER));
                        return;
                    }
                    var spawnerType = game.getGameVariant().getItemSpawnerType(resource);
                    if (spawnerType == null) {
                        sender.sendMessage(Message.of(LangKeys.ADMIN_ARENA_EDIT_ERRORS_INVALID_SPAWNER_TYPE).defaultPrefix());
                        return;
                    }
                    var remaining = bwPlayer.getPlayerInventory().addItem(spawnerType.getItem(amount));
                    remaining.forEach(item ->
                        Entities.dropItem(item, bwPlayer.getLocation())
                    );
                    Message.of(LangKeys.IN_GAME_CHEAT_RECEIVED_GIVE)
                            .placeholder("player", bwPlayer.getName())
                            .placeholder("amount", amount)
                            .placeholder("resource", spawnerType.getItemName().asComponent())
                            .defaultPrefix()
                            .send(sender);
                })
        );

        manager.command(
                commandSenderWrapperBuilder
                        .literal("kill")
                        .argument(constructPlayerArgument(manager))
                        .handler(commandContext -> {
                            var sender = commandContext.getSender();

                            var game = getGame(commandContext);
                            if (game == null) {
                                return;
                            }

                            if (game.getStatus() != GameStatus.RUNNING) {
                                sender.sendMessage(Message.of(LangKeys.IN_GAME_CHEAT_GAME_NOT_RUNNING).defaultPrefix());
                                return;
                            }

                            var bwPlayer = requireBedWarsPlayer(commandContext);
                            if (bwPlayer == null) {
                                return;
                            }

                            if (bwPlayer.isSpectator()) {
                                sender.sendMessage(Message.of(LangKeys.IN_GAME_CHEAT_INVALID_PLAYER));
                                return;
                            }
                            if (game.getConfigurationContainer().getOrDefault(GameConfigurationContainer.ALLOW_FAKE_DEATH, false)) {
                                BedWarsPlugin.processFakeDeath(bwPlayer);
                            } else {
                                bwPlayer.setHealth(0);
                            }
                            Message.of(LangKeys.IN_GAME_CHEAT_RECEIVED_KILL)
                                    .placeholder("player", bwPlayer.getName())
                                    .defaultPrefix()
                                    .send(sender);
                        })
        );

        manager.command(commandSenderWrapperBuilder
                .literal("invalidateTarget", "destroybed")
                .argument(StringArgument.<CommandSender>newBuilder("team")
                        .withSuggestionsProvider((c, s) -> {
                            var game = getGameForSuggestionProvider(c);
                            if (game == null) {
                                return List.of();
                            }

                            return game.getActiveTeams().stream().map(TeamImpl::getName).collect(Collectors.toList());
                        })
                )
                .handler(commandContext -> {
                    var sender = commandContext.getSender();

                    var game = getGame(commandContext);
                    if (game == null) {
                        return;
                    }
                    if (game.getStatus() != GameStatus.RUNNING) {
                        sender.sendMessage(Message.of(LangKeys.IN_GAME_CHEAT_GAME_NOT_RUNNING).defaultPrefix());
                        return;
                    }

                    String teamName = commandContext.get("team");
                    var team = game.getTeamFromName(teamName);
                    if (team == null) {
                        sender.sendMessage(
                                Message.of(LangKeys.IN_GAME_CHEAT_TEAM_DOES_NOT_EXIST)
                                    .placeholderRaw("team", teamName)
                                    .defaultPrefix()
                        );
                        return;
                    }

                    if (!game.isTeamActive(team)) {
                        sender.sendMessage(
                                Message.of(LangKeys.IN_GAME_CHEAT_TEAM_IS_NOT_IN_GAME)
                                        .placeholderRaw("team", teamName)
                                        .defaultPrefix()
                        );
                        return;
                    }

                    var target = team.getTarget();
                    if (target.isValid()) {
                        game.internalProcessInvalidation(team, target, null,  TargetInvalidationReason.COMMAND);
                    } else {
                        sender.sendMessage(
                                Message.of(LangKeys.IN_GAME_CHEAT_TARGET_IS_NOT_VALID)
                                        .placeholderRaw("team", teamName)
                                        .defaultPrefix()
                        );
                        return;
                    }

                    sender.sendMessage(
                            Message.of(LangKeys.IN_GAME_CHEAT_RECEIVED_TARGET_INVALIDATED)
                                    .placeholderRaw("team", teamName)
                                    .defaultPrefix()
                    );
                })
        );

        manager.command(commandSenderWrapperBuilder
                .literal("invalidateAllTargets", "destroyallbeds")
                .handler(commandContext -> {
                    var sender = commandContext.getSender();

                    var game = getGame(commandContext);
                    if (game == null) {
                        return;
                    }

                    if (game.getStatus() != GameStatus.RUNNING) {
                        sender.sendMessage(Message.of(LangKeys.IN_GAME_CHEAT_GAME_NOT_RUNNING).defaultPrefix());
                        return;
                    }

                    for (var team : game.getActiveTeams()) {
                        var target = team.getTarget();
                        if (target.isValid()) {
                            game.internalProcessInvalidation(team, target, null,  TargetInvalidationReason.COMMAND);
                        }
                    }

                    sender.sendMessage(
                            Message.of(LangKeys.IN_GAME_CHEAT_RECEIVED_TARGETS_INVALIDATED)
                                    .defaultPrefix()
                    );
                })
        );

        manager.command(commandSenderWrapperBuilder
                .literal("joinTeam")
                .argument(StringArgument.<CommandSender>newBuilder("team")
                        .withSuggestionsProvider((c, s) -> {
                            var game = getGameForSuggestionProvider(c);
                            if (game == null) {
                                return List.of();
                            }

                            return game.getActiveTeams().stream().map(TeamImpl::getName).collect(Collectors.toList());
                        })
                        .asOptional()
                )
                .argument(constructPlayerArgument(manager))
                .handler(commandContext -> {
                    var sender = commandContext.getSender();

                    var game = getGame(commandContext);
                    if (game == null) {
                        return;
                    };

                    @Nullable String teamName = commandContext.getOrDefault("team", null);

                    var bedwarsPlayer = requireBedWarsPlayer(commandContext);
                    if (bedwarsPlayer == null) {
                        return;
                    }

                    @Nullable TeamImpl team;
                    if (teamName != null) {
                        team = game.getTeamFromName(teamName);
                        if (team == null) {
                            sender.sendMessage(
                                    Message.of(LangKeys.IN_GAME_CHEAT_TEAM_DOES_NOT_EXIST)
                                            .placeholderRaw("team", teamName)
                                            .defaultPrefix()
                            );
                            return;
                        }

                        if (game.getStatus() != GameStatus.WAITING) {
                            if (!game.isTeamActive(team)) {
                                sender.sendMessage(
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
                        sender.sendMessage(
                                Message.of(LangKeys.IN_GAME_CHEAT_TEAM_RANDOM_FAILED)
                                        .defaultPrefix()
                        );
                        return;
                    }

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
                        sender.sendMessage(
                                Message.of(LangKeys.IN_GAME_CHEAT_RECEIVED_TEAM_JOIN)
                                        .placeholderRaw("player", bedwarsPlayer.getName())
                                        .placeholderRaw("team", team.getName())
                                        .defaultPrefix()
                        );
                    } else {
                        sender.sendMessage(
                                Message.of(LangKeys.IN_GAME_CHEAT_CHEAT_FAILED)
                                        .defaultPrefix()
                        );
                    }
                })
        );
    }

    protected abstract @Nullable GameImpl getGame(@NotNull CommandContext<@NotNull CommandSender> ctx);

    protected abstract @Nullable GameImpl getGameForSuggestionProvider(@NotNull CommandContext<@NotNull CommandSender> ctx);

    protected CommandArgument.@NotNull Builder<@NotNull CommandSender, @NotNull String> constructPlayerArgument(@NotNull CommandManager<@NotNull CommandSender> manager) {
        return manager
                .argumentBuilder(String.class, "player")
                .withSuggestionsProvider((c, s) -> {
                    var game = getGameForSuggestionProvider(c);
                    if (game == null) {
                        return List.of();
                    }

                    return game.getConnectedPlayers().stream().map(Player::getName).collect(Collectors.toList());
                });
    }

    protected abstract @Nullable BedWarsPlayer requireBedWarsPlayer(@NotNull CommandContext<@NotNull CommandSender> ctx);
}
