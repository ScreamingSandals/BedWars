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

package org.screamingsandals.bedwars.commands.admin;

import cloud.commandframework.Command;
import cloud.commandframework.CommandManager;
import cloud.commandframework.arguments.standard.BooleanArgument;
import cloud.commandframework.arguments.standard.StringArgument;
import org.screamingsandals.bedwars.game.GameStoreImpl;
import org.screamingsandals.bedwars.lang.LangKeys;
import org.screamingsandals.bedwars.utils.ArenaUtils;
import org.screamingsandals.lib.entity.type.EntityTypeHolder;
import org.screamingsandals.lib.lang.Message;
import org.screamingsandals.lib.player.PlayerWrapper;
import org.screamingsandals.lib.sender.CommandSenderWrapper;
import org.screamingsandals.lib.utils.AdventureHelper;
import org.screamingsandals.lib.utils.annotations.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class StoreCommand extends BaseAdminSubCommand {
    public StoreCommand() {
        super("store");
    }

    @Override
    public void construct(CommandManager<CommandSenderWrapper> manager, Command.Builder<CommandSenderWrapper> commandSenderWrapperBuilder) {
        manager.command(
                commandSenderWrapperBuilder
                        .literal("add")
                        .argument(StringArgument
                                .<CommandSenderWrapper>newBuilder("name")
                                .asOptional()
                                .withSuggestionsProvider((c,s) -> List.of("Villager_shop", "Dealer", "Seller", "&a&lVillager_shop", "&4Barter"))
                        )
                        .argument(StringArgument.optional("file"))
                        .argument(BooleanArgument.optional("useParent"))
                        .handler(commandContext -> editMode(commandContext, (sender, game) -> {
                            var name = commandContext.<String>getOptional("name")
                                    .map(s -> AdventureHelper.translateAlternateColorCodes('&', s));
                            var file = commandContext.<String>getOptional("file");
                            boolean useParent = commandContext.getOrDefault("useParent", true);
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
                            var store = game.getGameStoreList()
                                    .stream()
                                    .filter(gameStore -> gameStore.getStoreLocation().getBlock().equals(loc.getBlock()))
                                    .findFirst();

                            if (store.isPresent()) {
                                sender.sendMessage(Message.of(LangKeys.ADMIN_ARENA_EDIT_ERRORS_STORE_ALREADY_EXIST).defaultPrefix());
                                return;
                            }
                            game.getGameStoreList().add(new GameStoreImpl(loc, file.orElse(null), useParent, name.orElse(null), name.isPresent(), false));
                            sender.sendMessage(
                                    Message
                                    .of(LangKeys.ADMIN_ARENA_EDIT_SUCCESS_STORE_ADDED)
                                    .defaultPrefix()
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
                        .literal("remove")
                        .handler(commandContext -> editMode(commandContext, (sender, game) -> {
                            var loc = sender.as(PlayerWrapper.class).getLocation();

                            if (!game.getWorld().equals(loc.getWorld())) {
                                sender.sendMessage(Message.of(LangKeys.ADMIN_ARENA_EDIT_ERRORS_MUST_BE_IN_SAME_WORLD).defaultPrefix());
                                return;
                            }
                            var store = game.getGameStoreList()
                                    .stream()
                                    .filter(gameStore -> gameStore.getStoreLocation().getBlock().equals(loc.getBlock()))
                                    .findFirst();

                            if (store.isEmpty()) {
                                sender.sendMessage(Message.of(LangKeys.ADMIN_ARENA_EDIT_ERRORS_STORE_DOES_NOT_EXIST).defaultPrefix());
                                return;
                            }
                            game.getGameStoreList().remove(store.get());
                            sender.sendMessage(
                                    Message
                                            .of(LangKeys.ADMIN_ARENA_EDIT_SUCCESS_STORE_REMOVED)
                                            .defaultPrefix()
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
                        .literal("child")
                        .handler(commandContext -> editMode(commandContext, (sender, game) -> {
                            var loc = sender.as(PlayerWrapper.class).getLocation();

                            var store = game.getGameStoreList()
                                    .stream()
                                    .filter(gameStore -> gameStore.getStoreLocation().getBlock().equals(loc.getBlock()))
                                    .findFirst();

                            if (store.isPresent()) {
                                store.get().setBaby(true);

                                sender.sendMessage(
                                        Message
                                                .of(LangKeys.ADMIN_ARENA_EDIT_SUCCESS_CHILD_STATE)
                                                .defaultPrefix()
                                                .placeholder("value", Message.of(LangKeys.ADMIN_INFO_CONSTANT_TRUE))
                                );
                                return;
                            }

                            sender.sendMessage(Message.of(LangKeys.ADMIN_ARENA_EDIT_ERRORS_STORE_DOES_NOT_EXIST).defaultPrefix());
                        }))
        );

        manager.command(
                commandSenderWrapperBuilder
                        .literal("adult")
                        .handler(commandContext -> editMode(commandContext, (sender, game) -> {
                            var loc = sender.as(PlayerWrapper.class).getLocation();

                            var store = game.getGameStoreList()
                                    .stream()
                                    .filter(gameStore -> gameStore.getStoreLocation().getBlock().equals(loc.getBlock()))
                                    .findFirst();

                            if (store.isPresent()) {
                                store.get().setBaby(false);

                                sender.sendMessage(
                                        Message
                                                .of(LangKeys.ADMIN_ARENA_EDIT_SUCCESS_CHILD_STATE)
                                                .defaultPrefix()
                                                .placeholder("value", Message.of(LangKeys.ADMIN_INFO_CONSTANT_FALSE))
                                );
                                return;
                            }

                            sender.sendMessage(Message.of(LangKeys.ADMIN_ARENA_EDIT_ERRORS_STORE_DOES_NOT_EXIST).defaultPrefix());
                        }))
        );

        manager.command(
                commandSenderWrapperBuilder
                        .literal("type")
                        .argument(StringArgument
                                .<CommandSenderWrapper>newBuilder("type")
                                .withSuggestionsProvider((c, s) -> EntityTypeHolder.all().stream().map(EntityTypeHolder::platformName).collect(Collectors.toList()))
                        )
                        .handler(commandContext -> editMode(commandContext, (sender, game) -> {
                            String type = commandContext.get("type");
                            var loc = sender.as(PlayerWrapper.class).getLocation();

                            var store = game.getGameStoreList()
                                    .stream()
                                    .filter(gameStore -> gameStore.getStoreLocation().getBlock().equals(loc.getBlock()))
                                    .findFirst();

                            if (store.isPresent()) {
                                var t = EntityTypeHolder.ofOptional(type.split(":", 2)[0].toUpperCase()).orElse(null);
                                if (t != null && !t.isAlive()) {
                                    sender.sendMessage(Message.of(LangKeys.ADMIN_ARENA_EDIT_ERRORS_INVALID_ENTITY_TYPE).defaultPrefix());
                                    return;
                                }

                                if (t != null && t.is("player")) {
                                    String[] splitted = type.split(":", 2);
                                    if (splitted.length != 2 || splitted[1].trim().equals("")) {
                                        sender.sendMessage(Message.of(LangKeys.ADMIN_ARENA_EDIT_ERRORS_NPC_MUST_HAVE_SKIN_NAME).defaultPrefix());
                                        return;
                                    }

                                    store.get().setEntityTypeNPC(splitted[1]);
                                    sender.sendMessage(Message.of(LangKeys.ADMIN_ARENA_EDIT_SUCCESS_STORE_ENTITY_TYPE_SET).defaultPrefix().placeholder("type", splitted[1]));
                                    return;
                                }

                                if (t == null) {
                                    sender.sendMessage(Message.of(LangKeys.ADMIN_ARENA_EDIT_ERRORS_INVALID_ENTITY_TYPE).defaultPrefix());
                                    return;
                                }

                                store.get().setEntityType(t);

                                sender.sendMessage(Message.of(LangKeys.ADMIN_ARENA_EDIT_SUCCESS_STORE_ENTITY_TYPE_SET).defaultPrefix().placeholder("type", t.toString()));
                                return;
                            }

                            sender.sendMessage(Message.of(LangKeys.ADMIN_ARENA_EDIT_ERRORS_STORE_DOES_NOT_EXIST).defaultPrefix());
                        }))
        );
    }
}
