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

package org.screamingsandals.bedwars.commands.admin;

import cloud.commandframework.Command;
import cloud.commandframework.CommandManager;
import cloud.commandframework.arguments.standard.EnumArgument;
import cloud.commandframework.arguments.standard.IntegerArgument;
import cloud.commandframework.arguments.standard.StringArgument;
import cloud.commandframework.execution.CommandExecutionHandler;
import org.screamingsandals.bedwars.commands.AdminCommand;
import org.screamingsandals.bedwars.game.TeamImpl;
import org.screamingsandals.bedwars.game.TeamColorImpl;
import org.screamingsandals.bedwars.game.target.NoTargetImpl;
import org.screamingsandals.bedwars.game.target.ExpirableTargetBlockImpl;
import org.screamingsandals.bedwars.game.target.TargetBlockImpl;
import org.screamingsandals.bedwars.game.target.ExpirableTargetImpl;
import org.screamingsandals.bedwars.lang.LangKeys;
import org.screamingsandals.bedwars.utils.BedUtils;
import org.screamingsandals.bedwars.utils.ArenaUtils;
import org.screamingsandals.lib.block.BlockPlacement;
import org.screamingsandals.lib.lang.Message;
import org.screamingsandals.lib.particle.Particle;
import org.screamingsandals.lib.particle.ParticleType;
import org.screamingsandals.lib.player.Player;
import org.screamingsandals.lib.sender.CommandSender;
import org.screamingsandals.lib.spectator.Component;
import org.screamingsandals.lib.spectator.event.ClickEvent;
import org.screamingsandals.lib.spectator.event.HoverEvent;
import org.screamingsandals.lib.utils.annotations.Service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class TeamCommand extends BaseAdminSubCommand {
    public TeamCommand() {
        super("team");
    }

    @Override
    public void construct(CommandManager<CommandSender> manager, Command.Builder<CommandSender> commandSenderWrapperBuilder) {
        manager.command(
                commandSenderWrapperBuilder
                        .literal("add")
                        .argument(StringArgument.of("team-name"))
                        .argument(EnumArgument.of(TeamColorImpl.class, "color"))
                        .argument(IntegerArgument
                                .<CommandSender>newBuilder("maximum-players")
                                .withMin(1)
                        )
                        .handler(commandContext -> editMode(commandContext, (sender, game) -> {
                            String name = commandContext.get("team-name");
                            TeamColorImpl color = commandContext.get("color");
                            int maxPlayers = commandContext.get("maximum-players");

                            if (game.getTeamFromName(name) != null) {
                                sender.sendMessage(Message.of(LangKeys.ADMIN_ARENA_EDIT_ERRORS_TEAM_ALREADY_EXISTS).defaultPrefix());
                                return;
                            }

                            if (maxPlayers < 1) {
                                sender.sendMessage(Message.of(LangKeys.ADMIN_ARENA_EDIT_ERRORS_MAX_PLAYERS).defaultPrefix());
                                return;
                            }

                            var team = new TeamImpl();
                            team.setName(name);
                            team.setColor(color);
                            team.setMaxPlayers(maxPlayers);
                            team.setGame(game);
                            game.getTeams().add(team);

                            sender.sendMessage(
                                    Message
                                            .of(LangKeys.ADMIN_ARENA_EDIT_SUCCESS_TEAM_CREATED)
                                            .defaultPrefix()
                                            .placeholderRaw("team", team.getName())
                                            .placeholder("teamcolor", Component.text(team.getColor().name(), team.getColor().getTextColor()))
                                            .placeholder("maxplayers", team.getMaxPlayers())
                            );
                        }))
        );

        var teamNameArgument = StringArgument
                .<CommandSender>newBuilder("team-name")
                .withSuggestionsProvider((c, s) -> {
                    if (AdminCommand.gc.containsKey(c.<String>get("game"))) {
                        return AdminCommand.gc.get(c.<String>get("game"))
                                .getTeams()
                                .stream()
                                .map(TeamImpl::getName)
                                .collect(Collectors.toList());
                    }
                    return List.of();
                });


        manager.command(
                commandSenderWrapperBuilder
                        .literal("remove")
                        .argument(teamNameArgument)
                        .handler(commandContext -> editMode(commandContext, (sender, game) -> {
                            String name = commandContext.get("team-name");

                            var forRemove = game.getTeamFromName(name);
                            if (forRemove != null) {
                                game.getTeams().remove(forRemove);

                                sender.sendMessage(Message.of(LangKeys.ADMIN_ARENA_EDIT_SUCCESS_TEAM_REMOVED).defaultPrefix().placeholderRaw("team", forRemove.getName()));
                                return;
                            }
                            sender.sendMessage(Message.of(LangKeys.ADMIN_ARENA_EDIT_ERRORS_TEAM_DOES_NOT_EXIST).defaultPrefix());
                        }))
        );

        manager.command(
                commandSenderWrapperBuilder
                        .literal("color")
                        .argument(teamNameArgument)
                        .argument(EnumArgument.of(TeamColorImpl.class, "color"))
                        .handler(commandContext -> editMode(commandContext, (sender, game) -> {
                            String name = commandContext.get("team-name");
                            TeamColorImpl color = commandContext.get("color");

                            var team = game.getTeamFromName(name);
                            if (team == null) {
                                sender.sendMessage(Message.of(LangKeys.ADMIN_ARENA_EDIT_ERRORS_TEAM_DOES_NOT_EXIST).defaultPrefix());
                                return;
                            }

                            team.setColor(color);

                            sender.sendMessage(
                                    Message
                                            .of(LangKeys.ADMIN_ARENA_EDIT_SUCCESS_TEAM_COLOR_SET)
                                            .defaultPrefix()
                                            .placeholderRaw("team", team.getName())
                                            .placeholder("teamcolor", Component.text(team.getColor().name(), team.getColor().getTextColor()))
                            );
                        }))
        );

        manager.command(
                commandSenderWrapperBuilder
                        .literal("max-players")
                        .argument(teamNameArgument)
                        .argument(IntegerArgument
                                .<CommandSender>newBuilder("maximum-players")
                                .withMin(1)
                        )
                        .handler(commandContext -> editMode(commandContext, (sender, game) -> {
                            String name = commandContext.get("team-name");
                            int maxPlayers = commandContext.get("maximum-players");

                            var team = game.getTeamFromName(name);
                            if (team == null) {
                                sender.sendMessage(Message.of(LangKeys.ADMIN_ARENA_EDIT_ERRORS_TEAM_DOES_NOT_EXIST).defaultPrefix());
                                return;
                            }

                            if (maxPlayers < 1) {
                                sender.sendMessage(Message.of(LangKeys.ADMIN_ARENA_EDIT_ERRORS_MAX_PLAYERS).defaultPrefix());
                                return;
                            }

                            team.setMaxPlayers(maxPlayers);

                            sender.sendMessage(
                                    Message
                                            .of(LangKeys.ADMIN_ARENA_EDIT_SUCCESS_TEAM_MAXPLAYERS_SET)
                                            .defaultPrefix()
                                            .placeholderRaw("team", team.getName())
                                            .placeholder("maxplayers", team.getMaxPlayers()));
                        }))
        );

        manager.command(
                commandSenderWrapperBuilder
                        .literal("spawn")
                        .argument(teamNameArgument)
                        .handler(commandContext -> editMode(commandContext, (sender, game) -> {
                            String name = commandContext.get("team-name");

                            var loc = sender.as(Player.class).getLocation();

                            var team = game.getTeamFromName(name);
                            if (team == null) {
                                sender.sendMessage(Message.of(LangKeys.ADMIN_ARENA_EDIT_ERRORS_TEAM_DOES_NOT_EXIST).defaultPrefix());
                                return;
                            }

                            if (game.getPos1() == null || game.getPos2() == null) {
                                sender.sendMessage(Message.of(LangKeys.ADMIN_ARENA_EDIT_ERRORS_SET_BOUNDS_FIRST).defaultPrefix());
                                return;
                            }
                            if (!game.getWorld().equals(loc.getWorld())) {
                                sender.sendMessage(Message.of(LangKeys.ADMIN_ARENA_EDIT_ERRORS_MUST_BE_IN_SAME_WORLD).defaultPrefix());
                                return;
                            }
                            if (!ArenaUtils.isInArea(loc, game.getPos1(), game.getPos2())) {
                                sender.sendMessage(Message.of(LangKeys.ADMIN_ARENA_EDIT_ERRORS_MUST_BE_IN_BOUNDS).defaultPrefix());
                                return;
                            }
                            if (team.getTeamSpawns().size() > 1) {
                                sender.sendMessage(Message.of(LangKeys.ADMIN_ARENA_EDIT_ERRORS_TEAM_SPAWN_SET_BLOCKED)
                                        .placeholderRaw("team", team.getName())
                                        .defaultPrefix()
                                );
                                return;
                            }
                            team.getTeamSpawns().clear();
                            team.getTeamSpawns().add(loc);
                            sender.sendMessage(
                                    Message
                                            .of(LangKeys.ADMIN_ARENA_EDIT_SUCCESS_TEAM_SPAWN_SET)
                                            .defaultPrefix()
                                            .placeholderRaw("team", team.getName())
                                            .placeholder("x", loc.getX(), 2)
                                            .placeholder("y", loc.getY(), 2)
                                            .placeholder("z", loc.getZ(), 2)
                                            .placeholder("yaw", loc.getYaw(), 5)
                                            .placeholder("pitch", loc.getPitch(), 5)
                            );
                            var command = "/bw admin " + game.getName() + " team spawns " + team.getName() + " add";
                            sender.sendMessage(
                                    Message
                                            .of(LangKeys.ADMIN_ARENA_EDIT_SUCCESS_TEAM_MULTIPLE_SPAWNS_AD)
                                            .defaultPrefix()
                                            .placeholder("command", Component.text()
                                                    .content(command)
                                                    .hoverEvent(HoverEvent.showText(Message.of(LangKeys.ADMIN_INFO_SELECT_CLICK)
                                                            .placeholderRaw("command", command)
                                                            .asComponent(sender)))
                                                    .clickEvent(ClickEvent.runCommand(command))
                                            )
                            );
                        }))
        );

        manager.command(
                commandSenderWrapperBuilder
                        .literal("spawns")
                        .argument(teamNameArgument)
                        .literal("add")
                        .handler(commandContext -> editMode(commandContext, (sender, game) -> {
                            String name = commandContext.get("team-name");

                            var loc = sender.as(Player.class).getLocation();

                            var team = game.getTeamFromName(name);
                            if (team == null) {
                                sender.sendMessage(Message.of(LangKeys.ADMIN_ARENA_EDIT_ERRORS_TEAM_DOES_NOT_EXIST).defaultPrefix());
                                return;
                            }

                            if (game.getPos1() == null || game.getPos2() == null) {
                                sender.sendMessage(Message.of(LangKeys.ADMIN_ARENA_EDIT_ERRORS_SET_BOUNDS_FIRST).defaultPrefix());
                                return;
                            }
                            if (!game.getWorld().equals(loc.getWorld())) {
                                sender.sendMessage(Message.of(LangKeys.ADMIN_ARENA_EDIT_ERRORS_MUST_BE_IN_SAME_WORLD).defaultPrefix());
                                return;
                            }
                            if (!ArenaUtils.isInArea(loc, game.getPos1(), game.getPos2())) {
                                sender.sendMessage(Message.of(LangKeys.ADMIN_ARENA_EDIT_ERRORS_MUST_BE_IN_BOUNDS).defaultPrefix());
                                return;
                            }
                            // .getBlock().getLocation() = exact block location, not relative entity location
                            if (team.getTeamSpawns().stream()
                                    .anyMatch(l -> l.getBlock().location().equals(loc.getBlock().location()))) {
                                sender.sendMessage(
                                        Message.of(LangKeys.ADMIN_ARENA_EDIT_ERRORS_TEAM_SPAWN_ON_THE_SAME_BLOCK).defaultPrefix()
                                );
                            }
                            team.getTeamSpawns().add(loc);
                            sender.sendMessage(
                                    Message
                                            .of(LangKeys.ADMIN_ARENA_EDIT_SUCCESS_TEAM_SPAWN_ADDED)
                                            .defaultPrefix()
                                            .placeholderRaw("team", team.getName())
                                            .placeholder("x", loc.getX(), 2)
                                            .placeholder("y", loc.getY(), 2)
                                            .placeholder("z", loc.getZ(), 2)
                                            .placeholder("yaw", loc.getYaw(), 5)
                                            .placeholder("pitch", loc.getPitch(), 5)
                            );
                        }))
        );

        manager.command(
                commandSenderWrapperBuilder
                        .literal("spawns")
                        .argument(teamNameArgument)
                        .literal("remove")
                        .handler(commandContext -> editMode(commandContext, (sender, game) -> {
                            String name = commandContext.get("team-name");

                            var loc = sender.as(Player.class).getLocation();

                            var team = game.getTeamFromName(name);
                            if (team == null) {
                                sender.sendMessage(Message.of(LangKeys.ADMIN_ARENA_EDIT_ERRORS_TEAM_DOES_NOT_EXIST).defaultPrefix());
                                return;
                            }

                            if (game.getPos1() == null || game.getPos2() == null) {
                                sender.sendMessage(Message.of(LangKeys.ADMIN_ARENA_EDIT_ERRORS_SET_BOUNDS_FIRST).defaultPrefix());
                                return;
                            }
                            if (!game.getWorld().equals(loc.getWorld())) {
                                sender.sendMessage(Message.of(LangKeys.ADMIN_ARENA_EDIT_ERRORS_MUST_BE_IN_SAME_WORLD).defaultPrefix());
                                return;
                            }
                            if (!ArenaUtils.isInArea(loc, game.getPos1(), game.getPos2())) {
                                sender.sendMessage(Message.of(LangKeys.ADMIN_ARENA_EDIT_ERRORS_MUST_BE_IN_BOUNDS).defaultPrefix());
                                return;
                            }
                            var loc2Opt = team.getTeamSpawns().stream()
                                    .filter(l -> l.getBlock().location().equals(loc.getBlock().location()))
                                    .findFirst();
                            if (loc2Opt.isEmpty()) {
                                sender.sendMessage(Message.of(LangKeys.ADMIN_ARENA_EDIT_ERRORS_TEAM_NO_SPAWN_THERE).defaultPrefix());
                                return;
                            }
                            var loc2 = loc2Opt.get();
                            team.getTeamSpawns().remove(loc2);
                            sender.sendMessage(
                                    Message
                                            .of(LangKeys.ADMIN_ARENA_EDIT_SUCCESS_TEAM_SPAWN_ADDED)
                                            .defaultPrefix()
                                            .placeholderRaw("team", team.getName())
                                            .placeholder("x", loc2.getX(), 2)
                                            .placeholder("y", loc2.getY(), 2)
                                            .placeholder("z", loc2.getZ(), 2)
                                            .placeholder("yaw", loc2.getYaw(), 5)
                                            .placeholder("pitch", loc2.getPitch(), 5)
                            );
                        }))
        );

        manager.command(
                commandSenderWrapperBuilder
                        .literal("spawns")
                        .argument(teamNameArgument)
                        .literal("reset")
                        .handler(commandContext -> editMode(commandContext, (sender, game) -> {
                            String name = commandContext.get("team-name");

                            var loc = sender.as(Player.class).getLocation();

                            var team = game.getTeamFromName(name);
                            if (team == null) {
                                sender.sendMessage(Message.of(LangKeys.ADMIN_ARENA_EDIT_ERRORS_TEAM_DOES_NOT_EXIST).defaultPrefix());
                                return;
                            }

                            team.getTeamSpawns().clear();
                            sender.sendMessage(
                                    Message
                                            .of(LangKeys.ADMIN_ARENA_EDIT_SUCCESS_TEAM_SPAWN_RESET)
                                            .defaultPrefix()
                                            .placeholderRaw("team", team.getName())
                            );
                        }))
        );

        manager.command(
                commandSenderWrapperBuilder
                        .literal("spawns")
                        .argument(teamNameArgument)
                        .literal("list")
                        .handler(commandContext -> editMode(commandContext, (sender, game) -> {
                            String name = commandContext.get("team-name");

                            var loc = sender.as(Player.class).getLocation();

                            var team = game.getTeamFromName(name);
                            if (team == null) {
                                sender.sendMessage(Message.of(LangKeys.ADMIN_ARENA_EDIT_ERRORS_TEAM_DOES_NOT_EXIST).defaultPrefix());
                                return;
                            }

                            var teamSpawns = team.getTeamSpawns();

                            if (teamSpawns.isEmpty()) {
                                sender.sendMessage(Message.of(LangKeys.ADMIN_ARENA_EDIT_SUCCESS_TEAM_SPAWN_LIST_NO)
                                        .defaultPrefix()
                                        .placeholderRaw("team", team.getName())
                                );
                            } else {
                                sender.sendMessage(Message.of(LangKeys.ADMIN_ARENA_EDIT_SUCCESS_TEAM_SPAWN_LIST)
                                        .defaultPrefix()
                                        .placeholderRaw("team", team.getName())
                                        .placeholder("count", teamSpawns.size())
                                );
                                for (var teamSpawn : teamSpawns) {
                                    sender.sendMessage(Message.of(LangKeys.ADMIN_ARENA_EDIT_SUCCESS_TEAM_SPAWN_LIST_ENTRY)
                                            .placeholder("x", teamSpawn.getX(), 2)
                                            .placeholder("y", teamSpawn.getY(), 2)
                                            .placeholder("z", teamSpawn.getZ(), 2)
                                            .placeholder("yaw", teamSpawn.getYaw(), 5)
                                            .placeholder("pitch", teamSpawn.getPitch(), 5)
                                    );
                                }
                            }
                        }))
        );

        CommandExecutionHandler<CommandSender> handleTargetBlock = commandContext -> editMode(commandContext, (sender, game) -> {
            String name = commandContext.get("team-name");
            TargetBlockSetModes mode = commandContext.get("mode");

            BlockPlacement block;
            if (mode == TargetBlockSetModes.LOOKING_AT) {
                block = sender.as(Player.class).getTargetBlock(null, 5);
            } else {
                block = sender.as(Player.class).getLocation().subtract(0, 0.5, 0).getBlock();
            }
            var loc = block.location();

            var team = game.getTeamFromName(name);
            if (team == null) {
                sender.sendMessage(Message.of(LangKeys.ADMIN_ARENA_EDIT_ERRORS_TEAM_DOES_NOT_EXIST).defaultPrefix());
                return;
            }

            if (game.getPos1() == null || game.getPos2() == null) {
                sender.sendMessage(Message.of(LangKeys.ADMIN_ARENA_EDIT_ERRORS_SET_BOUNDS_FIRST).defaultPrefix());
                return;
            }
            if (!game.getWorld().equals(loc.getWorld())) {
                sender.sendMessage(Message.of(LangKeys.ADMIN_ARENA_EDIT_ERRORS_MUST_BE_IN_SAME_WORLD).defaultPrefix());
                return;
            }
            if (!ArenaUtils.isInArea(loc, game.getPos1(), game.getPos2())) {
                sender.sendMessage(Message.of(LangKeys.ADMIN_ARENA_EDIT_ERRORS_MUST_BE_IN_BOUNDS).defaultPrefix());
                return;
            }

            var chosenLoc = loc;
            if (block.block().is("#beds") && !"head".equals(block.block().get("part"))) {
                chosenLoc = Objects.requireNonNull(BedUtils.getBedNeighbor(block)).location();
            } else if (block.block().is("#doors") && !"lower".equals(block.block().get("half"))) {
                chosenLoc = loc.subtract(0, 1, 0);
            }
            team.setTarget(new TargetBlockImpl(chosenLoc));
            var particle = new Particle(
                    ParticleType.of("minecraft:happy_villager")
            );
            chosenLoc.add(0, 0, 0).sendParticle(particle);
            chosenLoc.add(0, 0, 1).sendParticle(particle);
            chosenLoc.add(1, 0, 0).sendParticle(particle);
            chosenLoc.add(1, 0, 1).sendParticle(particle);
            chosenLoc.add(0, 1, 0).sendParticle(particle);
            chosenLoc.add(0, 1, 1).sendParticle(particle);
            chosenLoc.add(1, 1, 0).sendParticle(particle);
            chosenLoc.add(1, 1, 1).sendParticle(particle);

            sender.sendMessage(
                    Message
                            .of(LangKeys.ADMIN_ARENA_EDIT_SUCCESS_TARGET_BLOCK_SET)
                            .defaultPrefix()
                            .placeholderRaw("team", team.getName())
                            .placeholder("x", chosenLoc.getBlockX())
                            .placeholder("y", chosenLoc.getBlockY())
                            .placeholder("z", chosenLoc.getBlockZ())
                            .placeholderRaw("material", chosenLoc.getBlock().block().location().asString())
            );

            if (game.getTeams().stream().anyMatch(a -> !(a.getTarget() instanceof TargetBlockImpl))) {
                sender.sendMessage(
                        Message
                                .of(LangKeys.ADMIN_ARENA_EDIT_SUCCESS_TARGET_NOT_PROPERLY_BALANCED)
                                .defaultPrefix()
                );
            }
        });

        manager.command(
                commandSenderWrapperBuilder
                .literal("target")
                .literal("block")
                .argument(teamNameArgument)
                .argument(EnumArgument.optional(TargetBlockSetModes.class, "mode", TargetBlockSetModes.LOOKING_AT))
                .handler(handleTargetBlock)
        );

        manager.command(
                commandSenderWrapperBuilder
                        .literal("bed")
                        .argument(teamNameArgument)
                        .argument(EnumArgument.optional(TargetBlockSetModes.class, "mode", TargetBlockSetModes.LOOKING_AT))
                        .handler(handleTargetBlock)
        );

        manager.command(
                commandSenderWrapperBuilder
                        .literal("target")
                        .literal("none")
                        .argument(teamNameArgument)
                        .handler(commandContext -> editMode(commandContext, (sender, game) -> {
                            String name = commandContext.get("team-name");

                            var team = game.getTeamFromName(name);
                            if (team == null) {
                                sender.sendMessage(Message.of(LangKeys.ADMIN_ARENA_EDIT_ERRORS_TEAM_DOES_NOT_EXIST).defaultPrefix());
                                return;
                            }

                            team.setTarget(NoTargetImpl.INSTANCE);

                            sender.sendMessage(
                                    Message
                                            .of(LangKeys.ADMIN_ARENA_EDIT_SUCCESS_TARGET_NONE_SET)
                                            .defaultPrefix()
                                            .placeholderRaw("team", team.getName())
                            );

                            if (game.getTeams().stream().anyMatch(a -> !(a.getTarget() instanceof NoTargetImpl))) {
                                sender.sendMessage(
                                        Message
                                                .of(LangKeys.ADMIN_ARENA_EDIT_SUCCESS_TARGET_NOT_PROPERLY_BALANCED)
                                                .defaultPrefix()
                                );
                            }
                        }))
        );

        manager.command(
                commandSenderWrapperBuilder
                        .literal("target")
                        .literal("expirable")
                        .argument(teamNameArgument)
                        .argument(IntegerArgument.<CommandSender>newBuilder("countdown").withMin(1))
                        .handler(commandContext -> editMode(commandContext, (sender, game) -> {
                            String name = commandContext.get("team-name");
                            int countdown = commandContext.get("countdown");

                            var team = game.getTeamFromName(name);
                            if (team == null) {
                                sender.sendMessage(Message.of(LangKeys.ADMIN_ARENA_EDIT_ERRORS_TEAM_DOES_NOT_EXIST).defaultPrefix());
                                return;
                            }

                            team.setTarget(new ExpirableTargetImpl(countdown));

                            sender.sendMessage(
                                    Message
                                            .of(LangKeys.ADMIN_ARENA_EDIT_SUCCESS_TARGET_COUNTDOWN_SET)
                                            .defaultPrefix()
                                            .placeholderRaw("team", team.getName())
                                            .placeholder("countdown", countdown)
                            );

                            if (game.getTeams().stream().anyMatch(a -> !(a.getTarget() instanceof ExpirableTargetImpl))) {
                                sender.sendMessage(
                                        Message
                                                .of(LangKeys.ADMIN_ARENA_EDIT_SUCCESS_TARGET_NOT_PROPERLY_BALANCED)
                                                .defaultPrefix()
                                );
                            }
                        }))
        );

        manager.command(
                commandSenderWrapperBuilder
                        .literal("target")
                        .literal("block-expirable")
                        .argument(teamNameArgument)
                        .argument(IntegerArgument.<CommandSender>newBuilder("countdown").withMin(1))
                        .argument(EnumArgument.optional(TargetBlockSetModes.class, "mode", TargetBlockSetModes.LOOKING_AT))
                        .handler(commandContext -> editMode(commandContext, (sender, game) -> {
                            String name = commandContext.get("team-name");
                            int countdown = commandContext.get("countdown");
                            TargetBlockSetModes mode = commandContext.get("mode");

                            BlockPlacement block;
                            if (mode == TargetBlockSetModes.LOOKING_AT) {
                                block = sender.as(Player.class).getTargetBlock(null, 5);
                            } else {
                                block = sender.as(Player.class).getLocation().subtract(0, 0.5, 0).getBlock();
                            }
                            var loc = block.location();

                            var team = game.getTeamFromName(name);
                            if (team == null) {
                                sender.sendMessage(Message.of(LangKeys.ADMIN_ARENA_EDIT_ERRORS_TEAM_DOES_NOT_EXIST).defaultPrefix());
                                return;
                            }

                            if (game.getPos1() == null || game.getPos2() == null) {
                                sender.sendMessage(Message.of(LangKeys.ADMIN_ARENA_EDIT_ERRORS_SET_BOUNDS_FIRST).defaultPrefix());
                                return;
                            }
                            if (!game.getWorld().equals(loc.getWorld())) {
                                sender.sendMessage(Message.of(LangKeys.ADMIN_ARENA_EDIT_ERRORS_MUST_BE_IN_SAME_WORLD).defaultPrefix());
                                return;
                            }
                            if (!ArenaUtils.isInArea(loc, game.getPos1(), game.getPos2())) {
                                sender.sendMessage(Message.of(LangKeys.ADMIN_ARENA_EDIT_ERRORS_MUST_BE_IN_BOUNDS).defaultPrefix());
                                return;
                            }

                            var chosenLoc = loc;
                            if (block.block().is("#beds") && !"head".equals(block.block().get("part"))) {
                                chosenLoc = Objects.requireNonNull(BedUtils.getBedNeighbor(block)).location();
                            } else if (block.block().is("#doors") && !"lower".equals(block.block().get("half"))) {
                                chosenLoc = loc.subtract(0, 1, 0);
                            }
                            team.setTarget(new ExpirableTargetBlockImpl(chosenLoc, countdown));
                            var particle = new Particle(
                                    ParticleType.of("minecraft:happy_villager")
                            );
                            chosenLoc.add(0, 0, 0).sendParticle(particle);
                            chosenLoc.add(0, 0, 1).sendParticle(particle);
                            chosenLoc.add(1, 0, 0).sendParticle(particle);
                            chosenLoc.add(1, 0, 1).sendParticle(particle);
                            chosenLoc.add(0, 1, 0).sendParticle(particle);
                            chosenLoc.add(0, 1, 1).sendParticle(particle);
                            chosenLoc.add(1, 1, 0).sendParticle(particle);
                            chosenLoc.add(1, 1, 1).sendParticle(particle);

                            sender.sendMessage(
                                    Message
                                            .of(LangKeys.ADMIN_ARENA_EDIT_SUCCESS_TARGET_BLOCK_COUNTDOWN_SET)
                                            .defaultPrefix()
                                            .placeholderRaw("team", team.getName())
                                            .placeholder("x", chosenLoc.getBlockX())
                                            .placeholder("y", chosenLoc.getBlockY())
                                            .placeholder("z", chosenLoc.getBlockZ())
                                            .placeholderRaw("material", chosenLoc.getBlock().block().location().asString())
                                            .placeholder("countdown", countdown)
                            );

                            if (game.getTeams().stream().anyMatch(a -> !(a.getTarget() instanceof ExpirableTargetBlockImpl))) {
                                sender.sendMessage(
                                        Message
                                                .of(LangKeys.ADMIN_ARENA_EDIT_SUCCESS_TARGET_NOT_PROPERLY_BALANCED)
                                                .defaultPrefix()
                                );
                            }
                        }))
        );

        manager.command(
                commandSenderWrapperBuilder
                        .literal("rename")
                        .argument(teamNameArgument)
                        .argument(StringArgument.of("new-name"))
                        .handler(commandContext -> editMode(commandContext, (sender, game) -> {
                            String name = commandContext.get("team-name");
                            String newName = commandContext.get("new-name");

                            var team = game.getTeamFromName(name);
                            if (team == null) {
                                sender.sendMessage(Message.of(LangKeys.ADMIN_ARENA_EDIT_ERRORS_TEAM_DOES_NOT_EXIST).defaultPrefix());
                                return;
                            }

                            if (game.getTeamFromName(newName) != null) {
                                sender.sendMessage(Message.of(LangKeys.ADMIN_ARENA_EDIT_ERRORS_CANNOT_RENAME_TEAM)
                                        .placeholderRaw("team", name)
                                        .placeholderRaw("new-team", newName)
                                        .defaultPrefix());
                                return;
                            }

                            team.setName(newName);

                            sender.sendMessage(
                                    Message
                                            .of(LangKeys.ADMIN_ARENA_EDIT_SUCCESS_TEAM_RENAMED)
                                            .defaultPrefix()
                                            .placeholderRaw("team", name)
                                            .placeholderRaw("new-team", newName)
                            );
                        }))
        );
    }

    public enum TargetBlockSetModes {
        LOOKING_AT,
        STANDING_ON
    }
}
