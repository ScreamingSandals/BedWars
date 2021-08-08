package org.screamingsandals.bedwars.commands.admin;

import cloud.commandframework.Command;
import cloud.commandframework.CommandManager;
import cloud.commandframework.arguments.standard.*;
import org.bukkit.entity.Player;
import org.screamingsandals.bedwars.BedWarsPlugin;
import org.screamingsandals.bedwars.commands.AdminCommand;
import org.screamingsandals.bedwars.game.ItemSpawner;
import org.screamingsandals.bedwars.game.Team;
import org.screamingsandals.bedwars.lang.LangKeys;
import org.screamingsandals.bedwars.utils.ArenaUtils;
import org.screamingsandals.lib.hologram.Hologram;
import org.screamingsandals.lib.lang.Message;
import org.screamingsandals.lib.sender.CommandSenderWrapper;
import org.screamingsandals.lib.utils.annotations.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SpawnerCommand extends BaseAdminSubCommand {
    public SpawnerCommand() {
        super("spawner");
    }

    @Override
    public void construct(CommandManager<CommandSenderWrapper> manager, Command.Builder<CommandSenderWrapper> commandSenderWrapperBuilder) {
        manager.command(
                commandSenderWrapperBuilder
                        .literal("add")
                        .argument(StringArgument
                                .<CommandSenderWrapper>newBuilder("type")
                                .withSuggestionsProvider((c,s) -> BedWarsPlugin.getAllSpawnerTypes())
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
                                sender.sendMessage(Message.of(LangKeys.ADMIN_ARENA_EDIT_ERRORS_SET_BOUNDS_FIRST).defaultPrefix());
                                return;
                            }
                            if (game.getWorld() != loc.getWorld()) {
                                sender.sendMessage(Message.of(LangKeys.ADMIN_ARENA_EDIT_ERRORS_MUST_BE_IN_SAME_WORLD).defaultPrefix());
                                return;
                            }
                            if (!ArenaUtils.isInArea(loc, game.getPos1(), game.getPos2())) {
                                sender.sendMessage(Message.of(LangKeys.ADMIN_ARENA_EDIT_ERRORS_MUST_BE_IN_BOUNDS).defaultPrefix());
                                return;
                            }
                            loc.setYaw(0);
                            loc.setPitch(0);
                            var spawnerType = BedWarsPlugin.getSpawnerType(type);
                            if (spawnerType != null) {
                                game.getSpawners().add(new ItemSpawner(loc, spawnerType, customName, hologramEnabled, startLevel, team, maxSpawnedResources, floatingHologram, rotationMode));
                                sender.sendMessage(
                                        Message
                                        .of(LangKeys.ADMIN_ARENA_EDIT_SUCCESS_SPAWNER_ADDED)
                                                .defaultPrefix()
                                                .placeholder("resource", spawnerType.getItemName())
                                                .placeholder("x", loc.getBlockX())
                                                .placeholder("y", loc.getBlockY())
                                                .placeholder("z", loc.getBlockZ())
                                );
                            } else {
                                sender.sendMessage(Message.of(LangKeys.ADMIN_ARENA_EDIT_ERRORS_INVALID_SPAWNER_TYPE).defaultPrefix());
                            }
                        }))
        );

        manager.command(
                commandSenderWrapperBuilder
                        .literal("remove")
                        .handler(commandContext -> editMode(commandContext, (sender, game) -> {
                            var loc = sender.as(Player.class).getLocation();

                            if (game.getPos1() == null || game.getPos2() == null) {
                                sender.sendMessage(Message.of(LangKeys.ADMIN_ARENA_EDIT_ERRORS_SET_BOUNDS_FIRST).defaultPrefix());
                                return;
                            }
                            if (game.getWorld() != loc.getWorld()) {
                                sender.sendMessage(Message.of(LangKeys.ADMIN_ARENA_EDIT_ERRORS_MUST_BE_IN_SAME_WORLD).defaultPrefix());
                                return;
                            }
                            if (!ArenaUtils.isInArea(loc, game.getPos1(), game.getPos2())) {
                                sender.sendMessage(Message.of(LangKeys.ADMIN_ARENA_EDIT_ERRORS_MUST_BE_IN_BOUNDS).defaultPrefix());
                                return;
                            }
                            var count = List.copyOf(game.getSpawners())
                                    .stream()
                                    .filter(spawner -> spawner.getLocation().getBlock().equals(loc.getBlock()))
                                    .peek(game.getSpawners()::remove)
                                    .count();
                            sender.sendMessage(
                                    Message
                                            .of(LangKeys.ADMIN_ARENA_EDIT_SUCCESS_REMOVED_SPAWNERS)
                                            .defaultPrefix()
                                            .placeholder("count", count)
                                            .placeholder("x", loc.getBlockX())
                                            .placeholder("y", loc.getBlockY())
                                            .placeholder("z", loc.getBlockZ())
                            );
                        }))
        );

        manager.command(
                commandSenderWrapperBuilder
                        .literal("reset")
                        .handler(commandContext -> editMode(commandContext, (sender, game) -> {
                            game.getSpawners().clear();
                            sender.sendMessage(
                                    Message
                                    .of(LangKeys.ADMIN_ARENA_EDIT_SUCCESS_SPAWNERS_REMOVED)
                                    .defaultPrefix()
                                    .placeholder("arena", game.getName())
                            );
                        }))
        );
    }
}
