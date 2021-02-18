package org.screamingsandals.bedwars.commands.admin;

import cloud.commandframework.Command;
import cloud.commandframework.CommandManager;
import cloud.commandframework.arguments.standard.EnumArgument;
import cloud.commandframework.arguments.standard.IntegerArgument;
import cloud.commandframework.arguments.standard.StringArgument;
import org.bukkit.block.data.type.Bed;
import org.bukkit.entity.Player;
import org.screamingsandals.bedwars.Main;
import org.screamingsandals.bedwars.commands.AdminCommand;
import org.screamingsandals.bedwars.game.Team;
import org.screamingsandals.bedwars.game.TeamColor;
import org.screamingsandals.bedwars.region.FlatteningBedUtils;
import org.screamingsandals.bedwars.region.LegacyBedUtils;
import org.screamingsandals.bedwars.utils.ArenaUtils;
import org.screamingsandals.lib.sender.CommandSenderWrapper;

import java.util.List;
import java.util.stream.Collectors;

import static org.screamingsandals.bedwars.lib.lang.I.i18n;

public class TeamCommand extends BaseAdminSubCommand {
    public TeamCommand(CommandManager<CommandSenderWrapper> manager, Command.Builder<CommandSenderWrapper> commandSenderWrapperBuilder) {
        super(manager, commandSenderWrapperBuilder, "team");
    }

    @Override
    public void construct(Command.Builder<CommandSenderWrapper> commandSenderWrapperBuilder) {
        manager.command(
                commandSenderWrapperBuilder
                        .literal("add")
                        .argument(StringArgument.of("teamName"))
                        .argument(EnumArgument.of(TeamColor.class, "color"))
                        .argument(IntegerArgument
                                .<CommandSenderWrapper>newBuilder("maximumPlayers")
                                .withMin(1)
                        )
                        .handler(commandContext -> editMode(commandContext, (sender, game) -> {
                            String name = commandContext.get("teamName");
                            TeamColor color = commandContext.get("color");
                            int maxPlayers = commandContext.get("maximumPlayers");

                            if (game.getTeamFromName(name) != null) {
                                sender.sendMessage(i18n("admin_command_team_is_already_exists"));
                                return;
                            }

                            if (maxPlayers < 1) {
                                sender.sendMessage(i18n("admin_command_max_players_fail"));
                                return;
                            }

                            Team team = new Team();
                            team.name = name;
                            team.color = color;
                            team.maxPlayers = maxPlayers;
                            team.game = game;
                            game.getTeams().add(team);

                            sender.sendMessage(
                                    i18n("admin_command_team_created")
                                            .replace("%team%", team.name)
                                            .replace("%teamcolor%", team.color.chatColor + team.color.name())
                                            .replace("%maxplayers%", Integer.toString(team.maxPlayers))
                            );
                        }))
        );

        var teamNameArgument = StringArgument
                .<CommandSenderWrapper>newBuilder("teamName")
                .withSuggestionsProvider((c, s) -> {
                    if (AdminCommand.gc.containsKey(c.<String>get("game"))) {
                        return AdminCommand.gc.get(c.<String>get("game"))
                                .getTeams()
                                .stream()
                                .map(Team::getName)
                                .collect(Collectors.toList());
                    }
                    return List.of();
                });


        manager.command(
                commandSenderWrapperBuilder
                        .literal("remove")
                        .argument(teamNameArgument)
                        .handler(commandContext -> editMode(commandContext, (sender, game) -> {
                            String name = commandContext.get("teamName");

                            var forRemove = game.getTeamFromName(name);
                            if (forRemove != null) {
                                game.getTeams().remove(forRemove);

                                sender.sendMessage(i18n("admin_command_team_removed").replace("%team%", forRemove.name));
                                return;
                            }
                            sender.sendMessage(i18n("admin_command_team_is_not_exists"));
                        }))
        );

        manager.command(
                commandSenderWrapperBuilder
                        .literal("color")
                        .argument(teamNameArgument)
                        .argument(EnumArgument.of(TeamColor.class, "color"))
                        .handler(commandContext -> editMode(commandContext, (sender, game) -> {
                            String name = commandContext.get("teamName");
                            TeamColor color = commandContext.get("color");

                            var team = game.getTeamFromName(name);
                            if (team == null) {
                                sender.sendMessage(i18n("admin_command_team_is_not_exists"));
                                return;
                            }

                            team.color = color;

                            sender.sendMessage(
                                    i18n("admin_command_team_color_setted")
                                            .replace("%team%", team.name)
                                            .replace("%teamcolor%", team.color.chatColor + team.color.name())
                            );
                        }))
        );

        manager.command(
                commandSenderWrapperBuilder
                        .literal("maxplayers")
                        .argument(teamNameArgument)
                        .argument(IntegerArgument
                                .<CommandSenderWrapper>newBuilder("maximumPlayers")
                                .withMin(1)
                        )
                        .handler(commandContext -> editMode(commandContext, (sender, game) -> {
                            String name = commandContext.get("teamName");
                            int maxPlayers = commandContext.get("maximumPlayers");

                            var team = game.getTeamFromName(name);
                            if (team == null) {
                                sender.sendMessage(i18n("admin_command_team_is_not_exists"));
                                return;
                            }

                            if (maxPlayers < 1) {
                                sender.sendMessage(i18n("admin_command_max_players_fail"));
                                return;
                            }

                            team.maxPlayers = maxPlayers;

                            sender.sendMessage(
                                    i18n("admin_command_team_maxplayers_setted")
                                            .replace("%team%", team.name)
                                            .replace("%maxplayers%", Integer.toString(team.maxPlayers)));
                        }))
        );

        manager.command(
                commandSenderWrapperBuilder
                        .literal("spawn")
                        .argument(teamNameArgument)
                        .handler(commandContext -> editMode(commandContext, (sender, game) -> {
                            String name = commandContext.get("teamName");

                            var loc = sender.as(Player.class).getLocation();

                            var team = game.getTeamFromName(name);
                            if (team == null) {
                                sender.sendMessage(i18n("admin_command_team_is_not_exists"));
                                return;
                            }

                            if (game.getPos1() == null || game.getPos2() == null) {
                                sender.sendMessage(i18n("admin_command_set_pos1_pos2_first"));
                                return;
                            }
                            if (game.getWorld() != loc.getWorld()) {
                                sender.sendMessage(i18n("admin_command_must_be_in_same_world"));
                                return;
                            }
                            if (!ArenaUtils.isInArea(loc, game.getPos1(), game.getPos2())) {
                                sender.sendMessage(i18n("admin_command_spawn_must_be_in_area"));
                                return;
                            }
                            team.spawn = loc;
                            sender.sendMessage(
                                    i18n("admin_command_team_spawn_setted")
                                            .replace("%team%", team.name)
                                            .replace("%x%", Double.toString(team.spawn.getX()))
                                            .replace("%y%", Double.toString(team.spawn.getY()))
                                            .replace("%z%", Double.toString(team.spawn.getZ()))
                                            .replace("%yaw%", Float.toString(team.spawn.getYaw()))
                                            .replace("%pitch%", Float.toString(team.spawn.getPitch()))
                            );
                        }))
        );

        manager.command(
                commandSenderWrapperBuilder
                        .literal("bed", "block")
                        .argument(teamNameArgument)
                        .handler(commandContext -> editMode(commandContext, (sender, game) -> {
                            String name = commandContext.get("teamName");

                            var block = sender.as(Player.class).getTargetBlock(null, 5);
                            var loc = block.getLocation();

                            var team = game.getTeamFromName(name);
                            if (team == null) {
                                sender.sendMessage(i18n("admin_command_team_is_not_exists"));
                                return;
                            }

                            if (game.getPos1() == null || game.getPos2() == null) {
                                sender.sendMessage(i18n("admin_command_set_pos1_pos2_first"));
                                return;
                            }
                            if (game.getWorld() != loc.getWorld()) {
                                sender.sendMessage(i18n("admin_command_must_be_in_same_world"));
                                return;
                            }
                            if (!ArenaUtils.isInArea(loc, game.getPos1(), game.getPos2())) {
                                sender.sendMessage(i18n("admin_command_spawn_must_be_in_area"));
                                return;
                            }

                            if (Main.isLegacy()) {
                                // Legacy
                                if (block.getState().getData() instanceof org.bukkit.material.Bed) {
                                    org.bukkit.material.Bed bed = (org.bukkit.material.Bed) block.getState().getData();
                                    if (!bed.isHeadOfBed()) {
                                        team.bed = LegacyBedUtils.getBedNeighbor(block).getLocation();
                                    } else {
                                        team.bed = loc;
                                    }
                                } else {
                                    team.bed = loc;
                                }

                            } else {
                                // 1.13+
                                if (block.getBlockData() instanceof Bed) {
                                    Bed bed = (Bed) block.getBlockData();
                                    if (bed.getPart() != Bed.Part.HEAD) {
                                        team.bed = FlatteningBedUtils.getBedNeighbor(block).getLocation();
                                    } else {
                                        team.bed = loc;
                                    }
                                } else {
                                    team.bed = loc;
                                }
                            }
                            sender.sendMessage(
                                    i18n("admin_command_bed_setted")
                                            .replace("%team%", team.name)
                                            .replace("%x%", Integer.toString(team.bed.getBlockX()))
                                            .replace("%y%", Integer.toString(team.bed.getBlockY()))
                                            .replace("%z%", Integer.toString(team.bed.getBlockZ()))
                            );
                        }))
        );
    }
}
