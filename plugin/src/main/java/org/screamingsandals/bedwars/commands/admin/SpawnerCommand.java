package org.screamingsandals.bedwars.commands.admin;

import cloud.commandframework.Command;
import cloud.commandframework.CommandManager;
import cloud.commandframework.arguments.parser.ArgumentParseResult;
import cloud.commandframework.arguments.standard.*;
import org.bukkit.entity.Player;
import org.screamingsandals.bedwars.Main;
import org.screamingsandals.bedwars.commands.AdminCommand;
import org.screamingsandals.bedwars.game.ItemSpawner;
import org.screamingsandals.bedwars.game.ItemSpawnerType;
import org.screamingsandals.bedwars.game.Team;
import org.screamingsandals.bedwars.utils.ArenaUtils;
import org.screamingsandals.lib.hologram.Hologram;
import org.screamingsandals.lib.sender.CommandSenderWrapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.screamingsandals.bedwars.lib.lang.I.i18n;

public class SpawnerCommand extends BaseAdminSubCommand {
    public SpawnerCommand(CommandManager<CommandSenderWrapper> manager, Command.Builder<CommandSenderWrapper> commandSenderWrapperBuilder) {
        super(manager, commandSenderWrapperBuilder, "spawner");
    }

    @Override
    public void construct(Command.Builder<CommandSenderWrapper> commandSenderWrapperBuilder) {
        manager.command(
                commandSenderWrapperBuilder
                        .literal("add")
                        .argument(StringArgument
                                .<CommandSenderWrapper>newBuilder("type")
                                .withSuggestionsProvider((c,s) -> Main.getAllSpawnerTypes())
                        )
                        .argument(BooleanArgument.optional("hologramEnabled", true))
                        .argument(DoubleArgument.optional("startLevel", 1))
                        .argument(StringArgument.optional("customName"))
                        .argument(StringArgument
                                        .<CommandSenderWrapper>newBuilder("team")
                                        .asOptional()
                                        .withSuggestionsProvider((c,s) -> {
                                            if (AdminCommand.gc.containsKey(c.<String>get("game"))) {
                                                return AdminCommand.gc.get(c.<String>get("game"))
                                                        .getTeams()
                                                        .stream()
                                                        .map(Team::getName)
                                                        .collect(Collectors.toList());
                                            }
                                            return List.of();
                                        })
                        )
                        .argument(IntegerArgument.optional("maxSpawnedResources"))
                        .argument(BooleanArgument.optional("floatingHologram"))
                        .argument(EnumArgument.optional(Hologram.RotationMode.class, "rotationMode"))
                        .handler(commandContext -> editMode(commandContext, (sender, game) -> {
                            String type = commandContext.get("type");
                            String customName = commandContext.getOrDefault("customName", null);
                            boolean hologramEnabled = commandContext.get("hologramEnabled");
                            double startLevel = commandContext.get("startLevel");
                            var team = commandContext.<String>getOptional("team").map(game::getTeamFromName).orElse(null); // saved as nullable
                            int maxSpawnedResources = commandContext.getOrDefault("maxSpawnedResources", -1);
                            boolean floatingHologram = commandContext.getOrDefault("floatingHologram", false);
                            Hologram.RotationMode rotationMode = commandContext.getOrDefault("rotationMode", Hologram.RotationMode.Y);

                            var loc = sender.as(Player.class).getLocation();

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
                            loc.setYaw(0);
                            loc.setPitch(0);
                            var spawnerType = Main.getSpawnerType(type);
                            if (spawnerType != null) {
                                game.getSpawners().add(new ItemSpawner(loc, spawnerType, customName, hologramEnabled, startLevel, team, maxSpawnedResources, floatingHologram, rotationMode));
                                sender.sendMessage(
                                        i18n("admin_command_spawner_added")
                                                .replace("%resource%", spawnerType.getItemName())
                                                .replace("%x%", Integer.toString(loc.getBlockX()))
                                                .replace("%y%", Integer.toString(loc.getBlockY()))
                                                .replace("%z%", Integer.toString(loc.getBlockZ()))
                                );
                            } else {
                                sender.sendMessage(i18n("admin_command_invalid_spawner_type"));
                            }
                        }))
        );

        manager.command(
                commandSenderWrapperBuilder
                        .literal("remove")
                        .handler(commandContext -> editMode(commandContext, (sender, game) -> {
                            var loc = sender.as(Player.class).getLocation();

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
                            var count = List.copyOf(game.getSpawners())
                                    .stream()
                                    .filter(spawner -> spawner.getLocation().getBlock().equals(loc.getBlock()))
                                    .peek(game.getSpawners()::remove)
                                    .count();
                            sender.sendMessage(
                                    i18n("admin_command_removed_spawners")
                                            .replace("%count%", Long.toString(count))
                                            .replace("%x%", Integer.toString(loc.getBlockX()))
                                            .replace("%y%", Integer.toString(loc.getBlockY()))
                                            .replace("%z%", Integer.toString(loc.getBlockZ()))
                            );
                        }))
        );

        manager.command(
                commandSenderWrapperBuilder
                        .literal("reset")
                        .handler(commandContext -> editMode(commandContext, (sender, game) -> {
                            game.getSpawners().clear();
                            sender.sendMessage(
                                    i18n("admin_command_spawners_reseted")
                                            .replace("%arena%", game.getName())
                            );
                        }))
        );
    }
}
