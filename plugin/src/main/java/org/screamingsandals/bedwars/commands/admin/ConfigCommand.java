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
import cloud.commandframework.arguments.standard.*;
import org.screamingsandals.bedwars.api.config.ConfigurationContainer;
import org.screamingsandals.bedwars.api.config.ConfigurationKey;
import org.screamingsandals.bedwars.api.config.ConfigurationListKey;
import org.screamingsandals.bedwars.commands.AdminCommand;
import org.screamingsandals.bedwars.lang.LangKeys;
import org.screamingsandals.lib.lang.Message;
import org.screamingsandals.lib.sender.CommandSenderWrapper;
import org.screamingsandals.lib.utils.annotations.Service;
import org.screamingsandals.lib.utils.annotations.parameters.DataFolder;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class ConfigCommand extends BaseAdminSubCommand {
    private final Path shopFolder;

    public ConfigCommand(@DataFolder("shop") Path shopFolder) {
        super("config");
        this.shopFolder = shopFolder;
    }

    @Override
    public void construct(CommandManager<CommandSenderWrapper> manager, Command.Builder<CommandSenderWrapper> commandSenderWrapperBuilder) {
        // TODO: get (show, g)

        // reset
        manager.command(
                commandSenderWrapperBuilder
                        .literal("reset", "inherit", "default", "set-to-default", "set-to-inherit", "r")
                        .argument(StringArgument.<CommandSenderWrapper>newBuilder("key")
                                .withSuggestionsProvider((c, s) -> {
                                    if (AdminCommand.gc.containsKey(c.<String>get("game"))) {
                                        return AdminCommand.gc.get(c.<String>get("game")).getConfigurationContainer().getJoinedRegisteredKeysForConfigCommand();
                                    }
                                    return List.of();
                                })
                        )
                        .handler(commandContext -> editMode(commandContext, (sender, game) -> {
                            var keyString = commandContext.<String>get("key").toLowerCase();
                            var keys = List.of(keyString.split("\\."));

                            var keyOpt = game.getConfigurationContainer().getRegisteredKeys()
                                    .stream()
                                    .map(ConfigurationKey::getKey)
                                    .filter(key -> key.equals(keys))
                                    .findFirst()
                                    .or(() -> game.getConfigurationContainer().getRegisteredListKeys()
                                            .stream()
                                            .map(ConfigurationListKey::getKey)
                                            .filter(key -> key.equals(keys))
                                            .findFirst()
                                    );

                            if (keyOpt.isEmpty()) {
                                sender.sendMessage(Message.of(LangKeys.ADMIN_ARENA_EDIT_ERRORS_INVALID_CONSTANT_NAME).defaultPrefix());
                            } else {
                                var key = keyOpt.get();
                                game.getConfigurationContainer().update(key, null);
                                Message
                                        .of(LangKeys.ADMIN_ARENA_EDIT_SUCCESS_CONSTANT_RESET)
                                        .placeholder("config", keyString)
                                        .defaultPrefix()
                                        .send(sender);
                            }
                        }))
        );

        // booleans
        manager.command(
                commandSenderWrapperBuilder
                        .literal("set", "s")
                        .literal("boolean", "booleans", "bool", "b")
                        .argument(StringArgument.<CommandSenderWrapper>newBuilder("key")
                                .withSuggestionsProvider((c, s) -> {
                                    if (AdminCommand.gc.containsKey(c.<String>get("game"))) {
                                        return AdminCommand.gc.get(c.<String>get("game")).getConfigurationContainer().getJoinedRegisteredKeysForConfigCommand(Boolean.class);
                                    }
                                    return List.of();
                                })
                        )
                        .argument(BooleanArgument.of("value"))
                        .handler(commandContext -> editMode(commandContext, (sender, game) -> {
                            var keyString = commandContext.<String>get("key").toLowerCase();
                            var keys = List.of(keyString.split("\\."));
                            boolean value = commandContext.get("value");

                            var keyOpt = game.getConfigurationContainer().getRegisteredKeys()
                                    .stream()
                                    .filter(t -> t.getKey().equals(keys) && t.getType() == Boolean.class)
                                    .findFirst();

                            if (keyOpt.isEmpty()) {
                                sender.sendMessage(Message.of(LangKeys.ADMIN_ARENA_EDIT_ERRORS_INVALID_CONSTANT_NAME).defaultPrefix());
                            } else {
                                var key = keyOpt.get();
                                game.getConfigurationContainer().update(key.getKey(), value);
                                Message
                                        .of(LangKeys.ADMIN_ARENA_EDIT_SUCCESS_CONSTANT_SET)
                                        .placeholder("config", keyString)
                                        .placeholder("value", value)
                                        .defaultPrefix()
                                        .send(sender);
                            }
                        }))
        );

        // strings
        manager.command(
                commandSenderWrapperBuilder
                        .literal("set", "s")
                        .literal("string", "strings", "str", "s")
                        .argument(StringArgument.<CommandSenderWrapper>newBuilder("key")
                                .withSuggestionsProvider((c, s) -> {
                                    if (AdminCommand.gc.containsKey(c.<String>get("game"))) {
                                        return AdminCommand.gc.get(c.<String>get("game")).getConfigurationContainer().getJoinedRegisteredKeysForConfigCommand(String.class);
                                    }
                                    return List.of();
                                })
                        )
                        .argument(StringArgument.<CommandSenderWrapper>newBuilder("value")
                                .greedy()
                                .withSuggestionsProvider(editModeSuggestion((commandContext, sender, game) -> {
                                    var keys = List.of(commandContext.<String>get("key").toLowerCase().split("\\."));

                                    var key = game.getConfigurationContainer().getRegisteredKeys()
                                            .stream()
                                            .filter(t -> t.getKey().equals(keys) && t.getType() == String.class)
                                            .findFirst()
                                            .orElse(null);

                                    if (key != null) {
                                        if (key.equals(ConfigurationContainer.DEFAULT_SHOP_FILE)) {
                                            // some hardcoded suggestion :O
                                            try (var walk = Files.walk(shopFolder)) {
                                                return Stream.concat(
                                                                walk
                                                                        .filter(Files::isRegularFile)
                                                                        .map(p -> shopFolder.relativize(p).toString()),
                                                                Stream.of("null")
                                                        )
                                                        .collect(Collectors.toList());
                                            } catch (IOException ignored) {
                                            }
                                        }
                                    }
                                    return List.of();
                                }))
                        )
                        .handler(commandContext -> editMode(commandContext, (sender, game) -> {
                            String keyString = commandContext.get("key");
                            var keys = List.of(keyString.toLowerCase().split("\\."));
                            String value = commandContext.get("value");

                            var keyOpt = game.getConfigurationContainer().getRegisteredKeys()
                                    .stream()
                                    .filter(t -> t.getKey().equals(keys) && t.getType() == String.class)
                                    .findFirst();

                            if (keyOpt.isEmpty()) {
                                sender.sendMessage(Message.of(LangKeys.ADMIN_ARENA_EDIT_ERRORS_INVALID_CONSTANT_NAME).defaultPrefix());
                            } else {
                                var key = keyOpt.get();
                                game.getConfigurationContainer().update(key.getKey(), value);
                                Message
                                        .of(LangKeys.ADMIN_ARENA_EDIT_SUCCESS_CONSTANT_SET)
                                        .placeholder("config", keyString)
                                        .placeholder("value", value)
                                        .defaultPrefix()
                                        .send(sender);
                            }
                        }))
        );

        // bytes
        manager.command(
                commandSenderWrapperBuilder
                        .literal("set", "s")
                        .literal("byte", "bytes", "by")
                        .argument(StringArgument.<CommandSenderWrapper>newBuilder("key")
                                .withSuggestionsProvider((c, s) -> {
                                    if (AdminCommand.gc.containsKey(c.<String>get("game"))) {
                                        return AdminCommand.gc.get(c.<String>get("game")).getConfigurationContainer().getJoinedRegisteredKeysForConfigCommand(Byte.class);
                                    }
                                    return List.of();
                                })
                        )
                        .argument(ByteArgument.of("value"))
                        .handler(commandContext -> editMode(commandContext, (sender, game) -> {
                            String keyString = commandContext.get("key");
                            var keys = List.of(keyString.toLowerCase().split("\\."));
                            byte value = commandContext.get("value");

                            var keyOpt = game.getConfigurationContainer().getRegisteredKeys()
                                    .stream()
                                    .filter(t -> t.getKey().equals(keys) && t.getType() == Byte.class)
                                    .findFirst();

                            if (keyOpt.isEmpty()) {
                                sender.sendMessage(Message.of(LangKeys.ADMIN_ARENA_EDIT_ERRORS_INVALID_CONSTANT_NAME).defaultPrefix());
                            } else {
                                var key = keyOpt.get();
                                game.getConfigurationContainer().update(key.getKey(), value);
                                Message
                                        .of(LangKeys.ADMIN_ARENA_EDIT_SUCCESS_CONSTANT_SET)
                                        .placeholder("config", keyString)
                                        .placeholder("value", value)
                                        .defaultPrefix()
                                        .send(sender);
                            }
                        }))
        );

        // shorts
        manager.command(
                commandSenderWrapperBuilder
                        .literal("set", "s")
                        .literal("short", "shorts", "sh")
                        .argument(StringArgument.<CommandSenderWrapper>newBuilder("key")
                                .withSuggestionsProvider((c, s) -> {
                                    if (AdminCommand.gc.containsKey(c.<String>get("game"))) {
                                        return AdminCommand.gc.get(c.<String>get("game")).getConfigurationContainer().getJoinedRegisteredKeysForConfigCommand(Short.class);
                                    }
                                    return List.of();
                                })
                        )
                        .argument(ShortArgument.of("value"))
                        .handler(commandContext -> editMode(commandContext, (sender, game) -> {
                            String keyString = commandContext.get("key");
                            var keys = List.of(keyString.toLowerCase().split("\\."));
                            short value = commandContext.get("value");

                            var keyOpt = game.getConfigurationContainer().getRegisteredKeys()
                                    .stream()
                                    .filter(t -> t.getKey().equals(keys) && t.getType() == Short.class)
                                    .findFirst();

                            if (keyOpt.isEmpty()) {
                                sender.sendMessage(Message.of(LangKeys.ADMIN_ARENA_EDIT_ERRORS_INVALID_CONSTANT_NAME).defaultPrefix());
                            } else {
                                var key = keyOpt.get();
                                game.getConfigurationContainer().update(key.getKey(), value);
                                Message
                                        .of(LangKeys.ADMIN_ARENA_EDIT_SUCCESS_CONSTANT_SET)
                                        .placeholder("config", keyString)
                                        .placeholder("value", value)
                                        .defaultPrefix()
                                        .send(sender);
                            }
                        }))
        );

        // integers
        manager.command(
                commandSenderWrapperBuilder
                        .literal("set", "s")
                        .literal("integer", "integers", "int", "i")
                        .argument(StringArgument.<CommandSenderWrapper>newBuilder("key")
                                .withSuggestionsProvider((c, s) -> {
                                    if (AdminCommand.gc.containsKey(c.<String>get("game"))) {
                                        return AdminCommand.gc.get(c.<String>get("game")).getConfigurationContainer().getJoinedRegisteredKeysForConfigCommand(Integer.class);
                                    }
                                    return List.of();
                                })
                        )
                        .argument(IntegerArgument.of("value"))
                        .handler(commandContext -> editMode(commandContext, (sender, game) -> {
                            String keyString = commandContext.get("key");
                            var keys = List.of(keyString.toLowerCase().split("\\."));
                            int value = commandContext.get("value");

                            var keyOpt = game.getConfigurationContainer().getRegisteredKeys()
                                    .stream()
                                    .filter(t -> t.getKey().equals(keys) && t.getType() == Integer.class)
                                    .findFirst();

                            if (keyOpt.isEmpty()) {
                                sender.sendMessage(Message.of(LangKeys.ADMIN_ARENA_EDIT_ERRORS_INVALID_CONSTANT_NAME).defaultPrefix());
                            } else {
                                var key = keyOpt.get();
                                game.getConfigurationContainer().update(key.getKey(), value);
                                Message
                                        .of(LangKeys.ADMIN_ARENA_EDIT_SUCCESS_CONSTANT_SET)
                                        .placeholder("config", keyString)
                                        .placeholder("value", value)
                                        .defaultPrefix()
                                        .send(sender);
                            }
                        }))
        );

        // longs
        manager.command(
                commandSenderWrapperBuilder
                        .literal("set", "s")
                        .literal("long", "longs", "l")
                        .argument(StringArgument.<CommandSenderWrapper>newBuilder("key")
                                .withSuggestionsProvider((c, s) -> {
                                    if (AdminCommand.gc.containsKey(c.<String>get("game"))) {
                                        return AdminCommand.gc.get(c.<String>get("game")).getConfigurationContainer().getJoinedRegisteredKeysForConfigCommand(Long.class);
                                    }
                                    return List.of();
                                })
                        )
                        .argument(LongArgument.of("value"))
                        .handler(commandContext -> editMode(commandContext, (sender, game) -> {
                            String keyString = commandContext.get("key");
                            var keys = List.of(keyString.toLowerCase().split("\\."));
                            long value = commandContext.get("value");

                            var keyOpt = game.getConfigurationContainer().getRegisteredKeys()
                                    .stream()
                                    .filter(t -> t.getKey().equals(keys) && t.getType() == Long.class)
                                    .findFirst();

                            if (keyOpt.isEmpty()) {
                                sender.sendMessage(Message.of(LangKeys.ADMIN_ARENA_EDIT_ERRORS_INVALID_CONSTANT_NAME).defaultPrefix());
                            } else {
                                var key = keyOpt.get();
                                game.getConfigurationContainer().update(key.getKey(), value);
                                Message
                                        .of(LangKeys.ADMIN_ARENA_EDIT_SUCCESS_CONSTANT_SET)
                                        .placeholder("config", keyString)
                                        .placeholder("value", value)
                                        .defaultPrefix()
                                        .send(sender);
                            }
                        }))
        );

        // floats
        manager.command(
                commandSenderWrapperBuilder
                        .literal("set", "s")
                        .literal("float", "floats", "f")
                        .argument(StringArgument.<CommandSenderWrapper>newBuilder("key")
                                .withSuggestionsProvider((c, s) -> {
                                    if (AdminCommand.gc.containsKey(c.<String>get("game"))) {
                                        return AdminCommand.gc.get(c.<String>get("game")).getConfigurationContainer().getJoinedRegisteredKeysForConfigCommand(Float.class);
                                    }
                                    return List.of();
                                })
                        )
                        .argument(FloatArgument.of("value"))
                        .handler(commandContext -> editMode(commandContext, (sender, game) -> {
                            String keyString = commandContext.get("key");
                            var keys = List.of(keyString.toLowerCase().split("\\."));
                            float value = commandContext.get("value");

                            var keyOpt = game.getConfigurationContainer().getRegisteredKeys()
                                    .stream()
                                    .filter(t -> t.getKey().equals(keys) && t.getType() == Float.class)
                                    .findFirst();

                            if (keyOpt.isEmpty()) {
                                sender.sendMessage(Message.of(LangKeys.ADMIN_ARENA_EDIT_ERRORS_INVALID_CONSTANT_NAME).defaultPrefix());
                            } else {
                                var key = keyOpt.get();
                                game.getConfigurationContainer().update(key.getKey(), value);
                                Message
                                        .of(LangKeys.ADMIN_ARENA_EDIT_SUCCESS_CONSTANT_SET)
                                        .placeholder("config", keyString)
                                        .placeholder("value", value)
                                        .defaultPrefix()
                                        .send(sender);
                            }
                        }))
        );

        // doubles
        manager.command(
                commandSenderWrapperBuilder
                        .literal("set", "s")
                        .literal("double", "doubles", "d")
                        .argument(StringArgument.<CommandSenderWrapper>newBuilder("key")
                                .withSuggestionsProvider((c, s) -> {
                                    if (AdminCommand.gc.containsKey(c.<String>get("game"))) {
                                        return AdminCommand.gc.get(c.<String>get("game")).getConfigurationContainer().getJoinedRegisteredKeysForConfigCommand(Double.class);
                                    }
                                    return List.of();
                                })
                        )
                        .argument(DoubleArgument.of("value"))
                        .handler(commandContext -> editMode(commandContext, (sender, game) -> {
                            String keyString = commandContext.get("key");
                            var keys = List.of(keyString.toLowerCase().split("\\."));
                            double value = commandContext.get("value");

                            var keyOpt = game.getConfigurationContainer().getRegisteredKeys()
                                    .stream()
                                    .filter(t -> t.getKey().equals(keys) && t.getType() == Double.class)
                                    .findFirst();

                            if (keyOpt.isEmpty()) {
                                sender.sendMessage(Message.of(LangKeys.ADMIN_ARENA_EDIT_ERRORS_INVALID_CONSTANT_NAME).defaultPrefix());
                            } else {
                                var key = keyOpt.get();
                                game.getConfigurationContainer().update(key.getKey(), value);
                                Message
                                        .of(LangKeys.ADMIN_ARENA_EDIT_SUCCESS_CONSTANT_SET)
                                        .placeholder("config", keyString)
                                        .placeholder("value", value)
                                        .defaultPrefix()
                                        .send(sender);
                            }
                        }))
        );

        // chars
        manager.command(
                commandSenderWrapperBuilder
                        .literal("set", "s")
                        .literal("char", "chars", "c")
                        .argument(StringArgument.<CommandSenderWrapper>newBuilder("key")
                                .withSuggestionsProvider((c, s) -> {
                                    if (AdminCommand.gc.containsKey(c.<String>get("game"))) {
                                        return AdminCommand.gc.get(c.<String>get("game")).getConfigurationContainer().getJoinedRegisteredKeysForConfigCommand(Character.class);
                                    }
                                    return List.of();
                                })
                        )
                        .argument(CharArgument.of("value"))
                        .handler(commandContext -> editMode(commandContext, (sender, game) -> {
                            String keyString = commandContext.get("key");
                            var keys = List.of(keyString.toLowerCase().split("\\."));
                            char value = commandContext.get("value");

                            var keyOpt = game.getConfigurationContainer().getRegisteredKeys()
                                    .stream()
                                    .filter(t -> t.getKey().equals(keys) && t.getType() == Character.class)
                                    .findFirst();

                            if (keyOpt.isEmpty()) {
                                sender.sendMessage(Message.of(LangKeys.ADMIN_ARENA_EDIT_ERRORS_INVALID_CONSTANT_NAME).defaultPrefix());
                            } else {
                                var key = keyOpt.get();
                                game.getConfigurationContainer().update(key.getKey(), value);
                                Message
                                        .of(LangKeys.ADMIN_ARENA_EDIT_SUCCESS_CONSTANT_SET)
                                        .placeholder("config", keyString)
                                        .placeholder("value", value)
                                        .defaultPrefix()
                                        .send(sender);
                            }
                        }))
        );

        // string list - add
        manager.command(
                commandSenderWrapperBuilder
                        .literal("list", "l")
                        .literal("string", "strings", "str", "s")
                        .argument(StringArgument.<CommandSenderWrapper>newBuilder("key")
                                .withSuggestionsProvider((c, s) -> {
                                    if (AdminCommand.gc.containsKey(c.<String>get("game"))) {
                                        return AdminCommand.gc.get(c.<String>get("game")).getConfigurationContainer().getJoinedRegisteredKeysForConfigCommandList(String.class);
                                    }
                                    return List.of();
                                })
                        )
                        .literal("add", "a")
                        .argument(StringArgument.greedy("value"))
                        .handler(commandContext -> editMode(commandContext, (sender, game) -> {
                            String keyString = commandContext.get("key");
                            var keys = List.of(keyString.toLowerCase().split("\\."));
                            String value = commandContext.get("value");

                            @SuppressWarnings("unchecked")
                            var keyOpt = game.getConfigurationContainer().getRegisteredListKeys()
                                    .stream()
                                    .filter(t -> t.getKey().equals(keys) && t.getType() == String.class)
                                    .map(k -> (ConfigurationListKey<String>) k)
                                    .findFirst();

                            if (keyOpt.isEmpty()) {
                                sender.sendMessage(Message.of(LangKeys.ADMIN_ARENA_EDIT_ERRORS_INVALID_CONSTANT_NAME).defaultPrefix());
                            } else {
                                var key = keyOpt.get();
                                var saved = new ArrayList<>(game.getConfigurationContainer().getSaved(key));
                                saved.add(value);
                                game.getConfigurationContainer().update(key.getKey(), saved);

                                Message
                                        .of(LangKeys.ADMIN_ARENA_EDIT_SUCCESS_CONSTANT_LIST_ADDED)
                                        .placeholder("config", keyString)
                                        .placeholder("value", value)
                                        .placeholder("position", saved.size())
                                        .defaultPrefix()
                                        .send(sender);
                            }
                        }))
        );

        // string list - set
        manager.command(
                commandSenderWrapperBuilder
                        .literal("list", "l")
                        .literal("string", "strings", "str", "s")
                        .argument(StringArgument.<CommandSenderWrapper>newBuilder("key")
                                .withSuggestionsProvider((c, s) -> {
                                    if (AdminCommand.gc.containsKey(c.<String>get("game"))) {
                                        return AdminCommand.gc.get(c.<String>get("game")).getConfigurationContainer().getJoinedRegisteredKeysForConfigCommandList(String.class);
                                    }
                                    return List.of();
                                })
                        )
                        .literal("set", "s")
                        .argument(IntegerArgument.of("position"))
                        .argument(StringArgument.greedy("value"))
                        .handler(commandContext -> editMode(commandContext, (sender, game) -> {
                            String keyString = commandContext.get("key");
                            var keys = List.of(keyString.toLowerCase().split("\\."));
                            int position = commandContext.get("position");
                            String value = commandContext.get("value");

                            var keyOpt = game.getConfigurationContainer().getRegisteredListKeys()
                                    .stream()
                                    .filter(t -> t.getKey().equals(keys) && t.getType() == String.class)
                                    .map(k -> (ConfigurationListKey<String>) k)
                                    .findFirst();

                            if (keyOpt.isEmpty()) {
                                sender.sendMessage(Message.of(LangKeys.ADMIN_ARENA_EDIT_ERRORS_INVALID_CONSTANT_NAME).defaultPrefix());
                            } else {
                                var key = keyOpt.get();
                                var saved = new ArrayList<>(game.getConfigurationContainer().getSaved(key));
                                if (saved.isEmpty()) {
                                    saved.add(value);
                                    game.getConfigurationContainer().update(key.getKey(), saved);

                                    Message
                                            .of(LangKeys.ADMIN_ARENA_EDIT_SUCCESS_CONSTANT_LIST_ADDED)
                                            .placeholder("config", keyString)
                                            .placeholder("value", value)
                                            .placeholder("position", saved.size())
                                            .defaultPrefix()
                                            .send(sender);
                                } else {
                                    position -= 1;
                                    if (position < 0 || position >= saved.size()) {
                                        Message.of(LangKeys.ADMIN_ARENA_EDIT_ERRORS_INVALID_CONSTANT_POSITION)
                                                .defaultPrefix()
                                                .send(sender);
                                    } else {
                                        saved.set(position, value);
                                        game.getConfigurationContainer().update(key.getKey(), saved);

                                        Message
                                                .of(LangKeys.ADMIN_ARENA_EDIT_SUCCESS_CONSTANT_LIST_ADDED)
                                                .placeholder("config", keyString)
                                                .placeholder("value", value)
                                                .placeholder("position", position + 1)
                                                .defaultPrefix()
                                                .send(sender);
                                    }
                                }
                            }
                        }))
        );

        // string list - remove
        manager.command(
                commandSenderWrapperBuilder
                        .literal("list", "l")
                        .literal("string", "strings", "str", "s")
                        .argument(StringArgument.<CommandSenderWrapper>newBuilder("key")
                                .withSuggestionsProvider((c, s) -> {
                                    if (AdminCommand.gc.containsKey(c.<String>get("game"))) {
                                        return AdminCommand.gc.get(c.<String>get("game")).getConfigurationContainer().getJoinedRegisteredKeysForConfigCommandList(String.class);
                                    }
                                    return List.of();
                                })
                        )
                        .literal("remove", "delete", "r", "d")
                        .argument(IntegerArgument.of("position"))
                        .handler(commandContext -> editMode(commandContext, (sender, game) -> {
                            String keyString = commandContext.get("key");
                            var keys = List.of(keyString.toLowerCase().split("\\."));
                            int position = commandContext.get("position");

                            @SuppressWarnings("unchecked")
                            var keyOpt = game.getConfigurationContainer().getRegisteredListKeys()
                                    .stream()
                                    .filter(t -> t.getKey().equals(keys) && t.getType() == String.class)
                                    .map(k -> (ConfigurationListKey<String>) k)
                                    .findFirst();

                            if (keyOpt.isEmpty()) {
                                sender.sendMessage(Message.of(LangKeys.ADMIN_ARENA_EDIT_ERRORS_INVALID_CONSTANT_NAME).defaultPrefix());
                            } else {
                                var key = keyOpt.get();
                                var saved = new ArrayList<>(game.getConfigurationContainer().getSaved(key));
                                position -= 1;
                                if (position < 0 || position >= saved.size()) {
                                    Message.of(LangKeys.ADMIN_ARENA_EDIT_ERRORS_INVALID_CONSTANT_POSITION)
                                            .defaultPrefix()
                                            .send(sender);
                                } else {
                                    saved.remove(position);
                                    game.getConfigurationContainer().update(key.getKey(), saved);

                                    Message
                                            .of(LangKeys.ADMIN_ARENA_EDIT_SUCCESS_CONSTANT_LIST_REMOVED)
                                            .placeholder("config", keyString)
                                            .placeholder("position", position + 1)
                                            .defaultPrefix()
                                            .send(sender);
                                }
                            }
                        }))
        );

        // string list - clear
        manager.command(
                commandSenderWrapperBuilder
                        .literal("list", "l")
                        .literal("string", "strings", "str", "s")
                        .argument(StringArgument.<CommandSenderWrapper>newBuilder("key")
                                .withSuggestionsProvider((c, s) -> {
                                    if (AdminCommand.gc.containsKey(c.<String>get("game"))) {
                                        return AdminCommand.gc.get(c.<String>get("game")).getConfigurationContainer().getJoinedRegisteredKeysForConfigCommandList(String.class);
                                    }
                                    return List.of();
                                })
                        )
                        .literal("clear", "c")
                        .handler(commandContext -> editMode(commandContext, (sender, game) -> {
                            String keyString = commandContext.get("key");
                            var keys = List.of(keyString.toLowerCase().split("\\."));

                            var keyOpt = game.getConfigurationContainer().getRegisteredListKeys()
                                    .stream()
                                    .filter(t -> t.getKey().equals(keys) && t.getType() == String.class)
                                    .findFirst();

                            if (keyOpt.isEmpty()) {
                                sender.sendMessage(Message.of(LangKeys.ADMIN_ARENA_EDIT_ERRORS_INVALID_CONSTANT_NAME).defaultPrefix());
                            } else {
                                var key = keyOpt.get();
                                game.getConfigurationContainer().remove(key.getKey());
                                game.getConfigurationContainer().update(key.getKey(), List.of());

                                Message
                                        .of(LangKeys.ADMIN_ARENA_EDIT_SUCCESS_CONSTANT_LIST_CLEARED)
                                        .placeholder("config", keyString)
                                        .defaultPrefix()
                                        .send(sender);
                            }
                        }))
        );



        // TODO: non-string lists
    }
}
