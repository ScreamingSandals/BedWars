package org.screamingsandals.bedwars.commands.admin;

import cloud.commandframework.Command;
import cloud.commandframework.CommandManager;
import cloud.commandframework.arguments.standard.*;
import cloud.commandframework.context.CommandContext;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import org.screamingsandals.bedwars.BedWarsPlugin;
import org.screamingsandals.bedwars.api.game.ItemSpawner;
import org.screamingsandals.bedwars.commands.AdminCommand;
import org.screamingsandals.bedwars.game.GameImpl;
import org.screamingsandals.bedwars.game.ItemSpawnerImpl;
import org.screamingsandals.bedwars.game.TeamImpl;
import org.screamingsandals.bedwars.lang.LangKeys;
import org.screamingsandals.bedwars.utils.ArenaUtils;
import org.screamingsandals.lib.hologram.Hologram;
import org.screamingsandals.lib.lang.Message;
import org.screamingsandals.lib.player.PlayerWrapper;
import org.screamingsandals.lib.sender.CommandSenderWrapper;
import org.screamingsandals.lib.utils.TriConsumer;
import org.screamingsandals.lib.utils.annotations.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
public class SpawnerCommand extends BaseAdminSubCommand {
    public SpawnerCommand() {
        super();
    }

    @Override
    public void construct(CommandManager<CommandSenderWrapper> manager, Command.Builder<CommandSenderWrapper> commandSenderWrapperBuilder) {
        manager.command(
                commandSenderWrapperBuilder
                        .literal("add")
                        .argument(StringArgument
                                .<CommandSenderWrapper>newBuilder("type")
                                .withSuggestionsProvider((c, s) -> BedWarsPlugin.getAllSpawnerTypes())
                        )
                        .argument(BooleanArgument.optional("hologramEnabled", true))
                        .argument(DoubleArgument.optional("startLevel", 1))
                        .argument(StringArgument.optional("customName"))
                        .argument(StringArgument
                                .<CommandSenderWrapper>newBuilder("team")
                                .asOptional()
                                .withSuggestionsProvider((c, s) -> {
                                    if (AdminCommand.gc.containsKey(c.<String>get("game"))) {
                                        return AdminCommand.gc.get(c.<String>get("game"))
                                                .getTeams()
                                                .stream()
                                                .map(TeamImpl::getName)
                                                .collect(Collectors.toList());
                                    }
                                    return List.of();
                                })
                        )
                        .argument(IntegerArgument.optional("maxSpawnedResources"))
                        .argument(BooleanArgument.optional("floatingHologram"))
                        .argument(EnumArgument.optional(Hologram.RotationMode.class, "rotationMode"))
                        .argument(EnumArgument.optional(ItemSpawner.HologramType.class, "hologramType"))
                        .handler(commandContext -> editMode(commandContext, (sender, game) -> {
                            String type = commandContext.get("type");
                            String customName = commandContext.getOrDefault("customName", null);
                            boolean hologramEnabled = commandContext.get("hologramEnabled");
                            double startLevel = commandContext.get("startLevel");
                            var team = commandContext.<String>getOptional("team").map(game::getTeamFromName).orElse(null); // saved as nullable
                            int maxSpawnedResources = commandContext.getOrDefault("maxSpawnedResources", -1);
                            boolean floatingHologram = commandContext.getOrDefault("floatingHologram", false);
                            Hologram.RotationMode rotationMode = commandContext.getOrDefault("rotationMode", Hologram.RotationMode.Y);
                            ItemSpawner.HologramType hologramType = commandContext.getOrDefault("hologramType", ItemSpawner.HologramType.DEFAULT);

                            var loc = sender.as(PlayerWrapper.class).getLocation();

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
                            loc.setYaw(0);
                            loc.setPitch(0);
                            var spawnerType = BedWarsPlugin.getSpawnerType(type);
                            if (spawnerType != null) {
                                game.getSpawners().add(new ItemSpawnerImpl(loc, spawnerType, customName, hologramEnabled, startLevel, team, maxSpawnedResources, floatingHologram, rotationMode, hologramType));
                                sender.sendMessage(
                                        Message
                                                .of(LangKeys.ADMIN_ARENA_EDIT_SUCCESS_SPAWNER_ADDED)
                                                .defaultPrefix()
                                                .placeholder("resource", spawnerType.getItemName().asComponent())
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
                            var loc = sender.as(PlayerWrapper.class).getLocation();

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
                        .literal("maxSpawnedResources")
                        .argument(IntegerArgument.of("amount"))
                        .argument(IntegerArgument.optional("number"))
                        .handler(commandContext -> changeSettingCommand(commandContext, (sender, game, itemSpawner) -> {
                            int amount = commandContext.get("amount");

                            if (amount <= 0) {
                                amount = -1;
                            }

                            itemSpawner.setMaxSpawnedResources(amount);
                            sender.sendMessage(Message.of(LangKeys.ADMIN_ARENA_EDIT_SUCCESS_SPAWNER_MAX_SPAWNED_RESOURCES_SET)
                                    .placeholder("type", itemSpawner.getItemSpawnerType().getItemName())
                                    .placeholder("amount", amount)
                                    .defaultPrefix()
                            );
                        }))
        );

        manager.command(
                commandSenderWrapperBuilder
                        .literal("rotationMode")
                        .argument(EnumArgument.of(Hologram.RotationMode.class, "rotationMode"))
                        .argument(IntegerArgument.optional("number"))
                        .handler(commandContext -> changeSettingCommand(commandContext, (sender, game, itemSpawner) -> {
                            Hologram.RotationMode rotationMode = commandContext.get("rotationMode");

                            itemSpawner.setRotationMode(rotationMode);
                            sender.sendMessage(Message.of(LangKeys.ADMIN_ARENA_EDIT_SUCCESS_SPAWNER_ROTATION_MODE_SET)
                                    .placeholder("type", itemSpawner.getItemSpawnerType().getItemName())
                                    .placeholder("rotation", rotationMode.name())
                                    .defaultPrefix()
                            );
                        }))
        );

        manager.command(
                commandSenderWrapperBuilder
                        .literal("changeType")
                        .argument(StringArgument
                                .<CommandSenderWrapper>newBuilder("type")
                                .withSuggestionsProvider((c, s) -> BedWarsPlugin.getAllSpawnerTypes())
                        )
                        .argument(IntegerArgument.optional("number"))
                        .handler(commandContext -> changeSettingCommand(commandContext, (sender, game, itemSpawner) -> {
                            String type = commandContext.get("type");

                            var spawnerType = BedWarsPlugin.getSpawnerType(type);
                            if (spawnerType != null) {
                                var old = itemSpawner.getItemSpawnerType();
                                itemSpawner.setItemSpawnerType(spawnerType);
                                sender.sendMessage(Message.of(LangKeys.ADMIN_ARENA_EDIT_SUCCESS_SPAWNER_TYPE_CHANGED)
                                        .placeholder("type", old.getItemName())
                                        .placeholder("new_type", spawnerType.getItemName())
                                        .defaultPrefix()
                                );
                            } else {
                                sender.sendMessage(Message.of(LangKeys.ADMIN_ARENA_EDIT_ERRORS_INVALID_SPAWNER_TYPE).defaultPrefix());
                            }
                        }))
        );

        manager.command(
                commandSenderWrapperBuilder
                        .literal("customName")
                        .argument(StringArgument.of("name"))
                        .argument(IntegerArgument.optional("number"))
                        .handler(commandContext -> changeSettingCommand(commandContext, (sender, game, itemSpawner) -> {
                            String customName = commandContext.get("name");
                            if ("null".equalsIgnoreCase(customName)) {
                                itemSpawner.setCustomName(null);
                            } else {
                                itemSpawner.setCustomName(customName);
                            }
                            sender.sendMessage(Message.of(LangKeys.ADMIN_ARENA_EDIT_SUCCESS_SPAWNER_CUSTOM_NAME_SET)
                                    .placeholder("type", itemSpawner.getItemSpawnerType().getItemName())
                                    .placeholder("name", customName)
                                    .defaultPrefix()
                            );
                        }))
        );

        manager.command(
                commandSenderWrapperBuilder
                        .literal("hologramType")
                        .argument(EnumArgument.of(ItemSpawner.HologramType.class, "type"))
                        .argument(IntegerArgument.optional("number"))
                        .handler(commandContext -> changeSettingCommand(commandContext, (sender, game, itemSpawner) -> {
                            ItemSpawner.HologramType type = commandContext.get("type");

                            itemSpawner.setHologramType(type);
                            sender.sendMessage(Message.of(LangKeys.ADMIN_ARENA_EDIT_SUCCESS_SPAWNER_HOLOGRAM_TYPE_SET)
                                    .placeholder("type", itemSpawner.getItemSpawnerType().getItemName())
                                    .placeholder("type", type.name())
                                    .defaultPrefix()
                            );
                        }))
        );

        manager.command(
                commandSenderWrapperBuilder
                        .literal("linkedTeam")
                        .argument(StringArgument
                                .<CommandSenderWrapper>newBuilder("team")
                                .withSuggestionsProvider((c, s) -> {
                                    if (AdminCommand.gc.containsKey(c.<String>get("game"))) {
                                        return AdminCommand.gc.get(c.<String>get("game"))
                                                .getTeams()
                                                .stream()
                                                .map(TeamImpl::getName)
                                                .collect(Collectors.toList());
                                    }
                                    return List.of();
                                })
                        )
                        .argument(IntegerArgument.optional("number"))
                        .handler(commandContext -> changeSettingCommand(commandContext, (sender, game, itemSpawner) -> {
                            var team = commandContext.<String>getOptional("team").map(game::getTeamFromName).orElse(null);

                            itemSpawner.setTeam(team);
                            if (team == null) {
                                sender.sendMessage(Message.of(LangKeys.ADMIN_ARENA_EDIT_SUCCESS_SPAWNER_TEAM_UNLINKED)
                                        .placeholder("type", itemSpawner.getItemSpawnerType().getItemName())
                                        .defaultPrefix()
                                );
                            } else {
                                sender.sendMessage(Message.of(LangKeys.ADMIN_ARENA_EDIT_SUCCESS_SPAWNER_TEAM_LINKED)
                                        .placeholder("type", itemSpawner.getItemSpawnerType().getItemName())
                                        .placeholder("team", team.getName())
                                        .defaultPrefix()
                                );
                            }
                        }))
        );

        manager.command(
                commandSenderWrapperBuilder
                        .literal("baseAmount")
                        .argument(DoubleArgument.of("amount"))
                        .argument(IntegerArgument.optional("number"))
                        .handler(commandContext -> changeSettingCommand(commandContext, (sender, game, itemSpawner) -> {
                            double amount = commandContext.get("amount");

                            itemSpawner.setBaseAmountPerSpawn(amount);
                            sender.sendMessage(Message.of(LangKeys.ADMIN_ARENA_EDIT_SUCCESS_SPAWNER_BASE_AMOUNT_SET)
                                    .placeholder("type", itemSpawner.getItemSpawnerType().getItemName())
                                    .placeholder("amount", amount)
                                    .defaultPrefix()
                            );
                        }))
        );

        manager.command(
                commandSenderWrapperBuilder
                        .literal("floating")
                        .argument(BooleanArgument.of("enabled"))
                        .argument(IntegerArgument.optional("number"))
                        .handler(commandContext -> changeSettingCommand(commandContext, (sender, game, itemSpawner) -> {
                            boolean enabled = commandContext.get("enabled");

                            itemSpawner.setFloatingBlockEnabled(enabled);
                            if (enabled) {
                                sender.sendMessage(Message.of(LangKeys.ADMIN_ARENA_EDIT_SUCCESS_SPAWNER_FLOATING_ENABLED)
                                        .placeholder("type", itemSpawner.getItemSpawnerType().getItemName()).defaultPrefix());
                            } else {
                                sender.sendMessage(Message.of(LangKeys.ADMIN_ARENA_EDIT_SUCCESS_SPAWNER_FLOATING_DISABLED)
                                        .placeholder("type", itemSpawner.getItemSpawnerType().getItemName()).defaultPrefix());
                            }
                        }))
        );


        manager.command(
                commandSenderWrapperBuilder
                        .literal("hologram")
                        .argument(BooleanArgument.of("enabled"))
                        .argument(IntegerArgument.optional("number"))
                        .handler(commandContext -> changeSettingCommand(commandContext, (sender, game, itemSpawner) -> {
                            boolean enabled = commandContext.get("enabled");

                            itemSpawner.setHologramEnabled(enabled);
                            if (enabled) {
                                sender.sendMessage(Message.of(LangKeys.ADMIN_ARENA_EDIT_SUCCESS_SPAWNER_HOLOGRAM_ENABLED)
                                        .placeholder("type", itemSpawner.getItemSpawnerType().getItemName()).defaultPrefix());
                            } else {
                                sender.sendMessage(Message.of(LangKeys.ADMIN_ARENA_EDIT_SUCCESS_SPAWNER_HOLOGRAM_DISABLED)
                                        .placeholder("type", itemSpawner.getItemSpawnerType().getItemName()).defaultPrefix());
                            }
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

    private void changeSettingCommand(CommandContext<CommandSenderWrapper> commandContext, TriConsumer<CommandSenderWrapper, GameImpl, ItemSpawnerImpl> itemSpawnerConsumer) {
        editMode(commandContext, (sender, game) -> {
            var loc = sender.as(PlayerWrapper.class).getLocation();
            var optionalInteger = commandContext.<Integer>getOptional("number");

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

            var spawners = game.getSpawners()
                    .stream()
                    .filter(spawner -> spawner.getLocation().getBlock().equals(loc.getBlock()))
                    .collect(Collectors.toList());

            if (spawners.size() == 0) {
                sender.sendMessage(Message.of(LangKeys.ADMIN_ARENA_EDIT_ERRORS_NO_SPAWNER).defaultPrefix());
            } else if (spawners.size() == 1) {
                var itemSpawner = spawners.get(0);
                itemSpawnerConsumer.accept(sender, game, itemSpawner);
            } else if (optionalInteger.isPresent()) {
                if (optionalInteger.get() > 0 && optionalInteger.get() <= spawners.size()) {
                    var number = optionalInteger.get() - 1;
                    var itemSpawner = spawners.get(number);
                    itemSpawnerConsumer.accept(sender, game, itemSpawner);
                } else {
                    sender.sendMessage(Message.of(LangKeys.ADMIN_ARENA_EDIT_ERRORS_MULTIPLE_SPAWNERS_FIRST_LINE).defaultPrefix());
                    AtomicInteger number = new AtomicInteger(1);
                    spawners.forEach(itemSpawner -> {
                        var rawInput = new ArrayList<>(commandContext.getRawInput());
                        rawInput.remove(rawInput.size() - 1);
                        var command = String.join(" ", rawInput) + " " + number.getAndIncrement();
                        Message.of(LangKeys.ADMIN_ARENA_EDIT_ERRORS_MULTIPLE_SPAWNERS_LINE)
                                .placeholder("type", itemSpawner.getItemSpawnerType().getItemName())
                                .placeholder("command", Component.text(command).clickEvent(ClickEvent.runCommand(command)))
                                .send(sender);
                    });
                }
            } else {
                sender.sendMessage(Message.of(LangKeys.ADMIN_ARENA_EDIT_ERRORS_MULTIPLE_SPAWNERS_FIRST_LINE).defaultPrefix());
                AtomicInteger number = new AtomicInteger(1);
                spawners.forEach(itemSpawner -> {
                    var command = commandContext.getRawInputJoined() + " " + number.getAndIncrement();
                    Message.of(LangKeys.ADMIN_ARENA_EDIT_ERRORS_MULTIPLE_SPAWNERS_LINE)
                            .placeholder("type", itemSpawner.getItemSpawnerType().getItemName())
                            .placeholder("command", Component.text(command).clickEvent(ClickEvent.runCommand(command)))
                            .send(sender);
                });
            }
        });
    }
}
