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

package org.screamingsandals.bedwars.commands;

import cloud.commandframework.Command;
import cloud.commandframework.CommandManager;
import cloud.commandframework.arguments.CommandArgument;
import cloud.commandframework.arguments.standard.*;
import cloud.commandframework.context.CommandContext;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.screamingsandals.bedwars.api.config.ConfigurationKey;
import org.screamingsandals.bedwars.api.config.ConfigurationListKey;
import org.screamingsandals.bedwars.api.config.GameConfigurationContainer;
import org.screamingsandals.bedwars.commands.arguments.ItemArgument;
import org.screamingsandals.bedwars.config.ConfigurationContainerImpl;
import org.screamingsandals.bedwars.lang.LangKeys;
import org.screamingsandals.lib.cloud.extras.ComponentHelper;
import org.screamingsandals.lib.item.ItemStack;
import org.screamingsandals.lib.lang.Message;
import org.screamingsandals.lib.sender.CommandSender;
import org.screamingsandals.lib.spectator.Component;
import org.screamingsandals.lib.spectator.ComponentLike;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RequiredArgsConstructor
@Builder
public class ConfigCommandFactory {
    @NotNull
    private final CommandManager<CommandSender> manager;
    @NotNull
    private final Command.Builder<CommandSender> commandBuilder;
    @NotNull
    private final ConfigurationContainerResolver resolver;
    @Nullable
    private final Path shopFolder;

    public void construct() {
        // get
        manager.command(
                commandBuilder
                        .literal("get", "show", "g")
                        .argument(StringArgument.<CommandSender>newBuilder("key")
                                .withSuggestionsProvider((c, s) -> makeSuggestion(c, true, ConfigurationContainerImpl::getJoinedRegisteredKeysForConfigCommand))
                        )
                        .handler(commandContext -> handleCommand(commandContext, true, (sender, container, configuredComponentName) -> { // Allow this without edit mode
                            var keyString = commandContext.<String>get("key").toLowerCase();
                            var keys = List.of(keyString.split("\\."));

                            var keyOpt = container.getRegisteredKeys()
                                    .stream()
                                    .map(ConfigurationKey::getKey)
                                    .filter(key -> key.equals(keys))
                                    .findFirst()
                                    .or(() -> container.getRegisteredListKeys()
                                            .stream()
                                            .map(ConfigurationListKey::getKey)
                                            .filter(key -> key.equals(keys))
                                            .findFirst()
                                    );

                            if (keyOpt.isEmpty()) {
                                sender.sendMessage(Message.of(LangKeys.ADMIN_ARENA_EDIT_ERRORS_INVALID_CONSTANT_NAME).defaultPrefix());
                            } else {
                                sender.sendMessage(ComponentHelper.header(Component.space().linear(Message.of(LangKeys.ADMIN_INFO_CONFIG)
                                                .placeholderRaw("configured-component", configuredComponentName != null ? configuredComponentName : "unknown")
                                                .asComponent(sender), Component.space()),
                                        CommandService.HEADER_FOOTER_LENGTH,
                                        CommandService.DEFAULT_HELP_COLORS.primary()
                                ));

                                sender.sendMessage(Component.text()
                                        .append(ComponentHelper.lastBranch(CommandService.DEFAULT_HELP_COLORS.accent()))
                                        .append(Component.space())
                                        .append(Message.of(LangKeys.ADMIN_INFO_KEY).asComponent(sender).withColor(CommandService.DEFAULT_HELP_COLORS.primary()))
                                        .append(Component.text(": ", CommandService.DEFAULT_HELP_COLORS.primary()))
                                        .append(Component.text(keyString, CommandService.DEFAULT_HELP_COLORS.highlight()))
                                );

                                sender.sendMessage(Component.text()
                                        .append("   ")
                                        .append(ComponentHelper.branch(CommandService.DEFAULT_HELP_COLORS.accent()))
                                        .append(Component.space())
                                        .append(Message.of(LangKeys.ADMIN_INFO_DATA_TYPE).asComponent(sender).withColor(CommandService.DEFAULT_HELP_COLORS.primary()))
                                        .append(Component.text(": ", CommandService.DEFAULT_HELP_COLORS.primary()))
                                        .append(Component.text()
                                                .content(container.getStringDataTypeForCommand(keyOpt.get()))
                                                .color(CommandService.DEFAULT_HELP_COLORS.text())
                                                .build()
                                        )
                                );

                                var description = container.getDescriptionKeys().get(keyOpt.get());

                                sender.sendMessage(Component.text()
                                        .append("   ")
                                        .append(ComponentHelper.branch(CommandService.DEFAULT_HELP_COLORS.accent()))
                                        .append(Component.space())
                                        .append(Message.of(LangKeys.HELP_MESSAGES_DESCRIPTION).asComponent(sender).withColor(CommandService.DEFAULT_HELP_COLORS.primary()))
                                        .append(Component.text(": ", CommandService.DEFAULT_HELP_COLORS.primary()))
                                        .append(
                                                Message.of(description != null ? description : LangKeys.HELP_MESSAGES_NO_DESCRIPTION)
                                                        .asComponent(sender)
                                                        .withColor(CommandService.DEFAULT_HELP_COLORS.text())
                                        )
                                );

                                var value = container.getSavedNode(keys).raw();
                                if (value instanceof List) {
                                    sender.sendMessage(Component.text()
                                            .append("   ")
                                            .append(ComponentHelper.lastBranch(CommandService.DEFAULT_HELP_COLORS.accent()))
                                            .append(Component.space())
                                            .append(Message.of(LangKeys.ADMIN_INFO_VALUE).asComponent(sender).withColor(CommandService.DEFAULT_HELP_COLORS.primary()))
                                            .append(Component.text(": ", CommandService.DEFAULT_HELP_COLORS.primary()))
                                    );
                                    //noinspection unchecked
                                    var list = (List<Object>) value;
                                    for (var i = 0; i < list.size(); i++) {
                                        var val = list.get(i);
                                        sender.sendMessage(Component.text()
                                                .append("      ")
                                                .append(i + 1 < list.size() ? ComponentHelper.branch(CommandService.DEFAULT_HELP_COLORS.accent()) : ComponentHelper.lastBranch(CommandService.DEFAULT_HELP_COLORS.accent()))
                                                .append(Component.space())
                                                .append(stringValueOf(val).withColor(CommandService.DEFAULT_HELP_COLORS.highlight()))
                                        );
                                    }
                                } else if (value instanceof Boolean) {
                                    sender.sendMessage(Component.text()
                                            .append("   ")
                                            .append(ComponentHelper.lastBranch(CommandService.DEFAULT_HELP_COLORS.accent()))
                                            .append(Component.space())
                                            .append(Message.of(LangKeys.ADMIN_INFO_VALUE).asComponent(sender).withColor(CommandService.DEFAULT_HELP_COLORS.primary()))
                                            .append(Component.text(": ", CommandService.DEFAULT_HELP_COLORS.primary()))
                                            .append(Message.of((Boolean) value ? LangKeys.ADMIN_INFO_CONSTANT_TRUE : LangKeys.ADMIN_INFO_CONSTANT_FALSE).asComponent(sender))
                                    );
                                } else if (value != null) {
                                    sender.sendMessage(Component.text()
                                            .append("   ")
                                            .append(ComponentHelper.lastBranch(CommandService.DEFAULT_HELP_COLORS.accent()))
                                            .append(Component.space())
                                            .append(Message.of(LangKeys.ADMIN_INFO_VALUE).asComponent(sender).withColor(CommandService.DEFAULT_HELP_COLORS.primary()))
                                            .append(Component.text(": ", CommandService.DEFAULT_HELP_COLORS.primary()))
                                            .append(stringValueOf(value).withColor(CommandService.DEFAULT_HELP_COLORS.highlight()))
                                    );
                                } else {
                                    sender.sendMessage(Component.text()
                                            .append("   ")
                                            .append(ComponentHelper.lastBranch(CommandService.DEFAULT_HELP_COLORS.accent()))
                                            .append(Component.space())
                                            .append(Message.of(LangKeys.ADMIN_INFO_VALUE).asComponent(sender).withColor(CommandService.DEFAULT_HELP_COLORS.primary()))
                                            .append(Component.text(": ", CommandService.DEFAULT_HELP_COLORS.primary()))
                                            .append(Message.of(LangKeys.ADMIN_INFO_DEFAULT_VALUE).asComponent(sender).withColor(CommandService.DEFAULT_HELP_COLORS.highlight()))
                                    );
                                }

                                sender.sendMessage(ComponentHelper.line(
                                        CommandService.HEADER_FOOTER_LENGTH,
                                        CommandService.DEFAULT_HELP_COLORS.primary()
                                ));
                            }
                        }))
        );

        // reset
        manager.command(
                commandBuilder
                        .literal("reset", "inherit", "default", "set-to-default", "set-to-inherit", "r")
                        .argument(StringArgument.<CommandSender>newBuilder("key")
                                .withSuggestionsProvider((c, s) -> makeSuggestion(c, false, ConfigurationContainerImpl::getJoinedRegisteredKeysForConfigCommand))
                        )
                        .handler(commandContext -> handleCommand(commandContext, false, (sender, container) -> {
                            var keyString = commandContext.<String>get("key").toLowerCase();
                            var keys = List.of(keyString.split("\\."));

                            var keyOpt = container.getRegisteredKeys()
                                    .stream()
                                    .map(ConfigurationKey::getKey)
                                    .filter(key -> key.equals(keys))
                                    .findFirst()
                                    .or(() -> container.getRegisteredListKeys()
                                            .stream()
                                            .map(ConfigurationListKey::getKey)
                                            .filter(key -> key.equals(keys))
                                            .findFirst()
                                    );

                            if (keyOpt.isEmpty()) {
                                sender.sendMessage(Message.of(LangKeys.ADMIN_ARENA_EDIT_ERRORS_INVALID_CONSTANT_NAME).defaultPrefix());
                            } else {
                                var key = keyOpt.get();
                                container.update(key, null);
                                Message
                                        .of(LangKeys.ADMIN_ARENA_EDIT_SUCCESS_CONSTANT_RESET)
                                        .placeholder("config", keyString)
                                        .defaultPrefix()
                                        .send(sender);
                            }
                        }))
        );

        // booleans
        generateCommands(
                "boolean",
                Boolean.class,
                BooleanArgument::of,
                "booleans", "bool", "b"
        );

        // strings
        generateCommands(
                "string",
                String.class,
                argumentName -> StringArgument.<CommandSender>newBuilder(argumentName)
                        .greedy()
                        .withSuggestionsProvider((commandContext, sender) -> makeSuggestion(commandContext, false, container -> {
                            var keys = List.of(commandContext.<String>get("key").toLowerCase().split("\\."));

                            var key = container.getRegisteredKeys()
                                    .stream()
                                    .filter(t -> t.getKey().equals(keys) && t.getType() == String.class)
                                    .findFirst()
                                    .orElse(null);

                            if (key != null) {
                                if (key.equals(GameConfigurationContainer.DEFAULT_SHOP_FILE)) {
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
                        .build(),
                StringArgument::greedy,
                "strings", "str", "s"
        );

        // bytes
        generateCommands(
                "byte",
                Byte.class,
                ByteArgument::of,
                "bytes", "by"
        );

        // shorts
        generateCommands(
                "short",
                Short.class,
                ShortArgument::of,
                "shorts", "sh"
        );

        // integers
        generateCommands(
                "integer",
                Integer.class,
                IntegerArgument::of,
                "integers", "int", "i"
        );

        // longs
        generateCommands(
                "long",
                Long.class,
                LongArgument::of,
                "longs", "l"
        );

        // floats
        generateCommands(
                "float",
                Float.class,
                FloatArgument::of,
                "floats", "f"
        );

        // doubles
        generateCommands(
                "double",
                Double.class,
                DoubleArgument::of,
                "doubles", "d"
        );

        // chars
        generateCommands(
                "char",
                Character.class,
                CharArgument::of,
                "chars", "c"
        );

        // Item
        generateCommands(
                "item",
                ItemStack.class,
                ItemArgument::of,
                "items", "it"
        );

        // enums
        manager.command(
                commandBuilder
                        .literal("set", "s")
                        .literal("enum", "enums", "e")
                        .argument(StringArgument.<CommandSender>newBuilder("key")
                                .withSuggestionsProvider((c, s) -> makeSuggestion(c, false, ConfigurationContainerImpl::getJoinedRegisteredKeysForConfigCommandEnums))
                        )
                        .argument(StringArgument.<CommandSender>newBuilder("value")
                                .withSuggestionsProvider((commandContext, sender) -> makeSuggestion(commandContext, false, container -> {
                                    var keys = List.of(commandContext.<String>get("key").toLowerCase().split("\\."));

                                    var key = container.getRegisteredKeys()
                                            .stream()
                                            .filter(t -> t.getKey().equals(keys) && t.getType().isEnum())
                                            .findFirst()
                                            .orElse(null);

                                    if (key != null) {
                                        var constants = key.getType().getEnumConstants();
                                        if (constants != null) {
                                            return Arrays.stream(constants).map(Object::toString).collect(Collectors.toList());
                                        }
                                    }
                                    return List.of();
                                }))
                        )
                        .handler(commandContext -> handleCommand(commandContext, false, (sender, container) -> {
                            String keyString = commandContext.get("key");
                            var keys = List.of(keyString.toLowerCase().split("\\."));
                            String value = commandContext.get("value");

                            var keyOpt = container.getRegisteredKeys()
                                    .stream()
                                    .filter(t -> t.getKey().equals(keys) && t.getType().isEnum())
                                    .findFirst();

                            if (keyOpt.isEmpty()) {
                                sender.sendMessage(Message.of(LangKeys.ADMIN_ARENA_EDIT_ERRORS_INVALID_CONSTANT_NAME).defaultPrefix());
                            } else {
                                var key = keyOpt.get();
                                Object enumValue;
                                try {
                                    //noinspection unchecked
                                    enumValue = Enum.valueOf((Class) key.getType(), value);
                                } catch (Throwable ignored) {
                                    try {
                                        //noinspection unchecked
                                        enumValue = Enum.valueOf((Class) key.getType(), value.toUpperCase());
                                    } catch (Throwable ignored2) {
                                        Message.of(LangKeys.ADMIN_ARENA_EDIT_ERRORS_INVALID_CONSTANT_ENUM_VALUE)
                                                .placeholder("value", value)
                                                .placeholder("values", Arrays.toString(key.getType().getEnumConstants()))
                                                .defaultPrefix()
                                                .send(sender);
                                        return;
                                    }
                                }
                                container.update(key.getKey(), enumValue);
                                Message
                                        .of(LangKeys.ADMIN_ARENA_EDIT_SUCCESS_CONSTANT_SET)
                                        .placeholder("config", keyString)
                                        .placeholder("value", value)
                                        .defaultPrefix()
                                        .send(sender);
                            }
                        }))
        );
    }

    private <T> void generateCommands(
            @NotNull String typeName,
            @NotNull Class<T> type,
            @NotNull Function<@NotNull String, @NotNull CommandArgument<CommandSender, T>> argumentBuilder,
            @NotNull String @NotNull... aliases
    ) {
        generateCommands(typeName, type, argumentBuilder, argumentBuilder, aliases);
    }

    private <T> void generateCommands(
            @NotNull String typeName,
            @NotNull Class<T> type,
            @NotNull Function<@NotNull String, @NotNull CommandArgument<CommandSender, T>> argumentBuilder,
            @NotNull Function<@NotNull String, @NotNull CommandArgument<CommandSender, T>> argumentListBuilder,
            @NotNull String @NotNull... aliases
    ) {
        // single value
        manager.command(
                commandBuilder
                        .literal("set", "s")
                        .literal(typeName, aliases)
                        .argument(StringArgument.<CommandSender>newBuilder("key")
                                .withSuggestionsProvider((c, s) -> makeSuggestion(c, false, container -> container.getJoinedRegisteredKeysForConfigCommand(type)))
                        )
                        .argument(argumentBuilder.apply("value"))
                        .handler(commandContext -> handleCommand(commandContext, false, (sender, container) -> {
                            var keyString = commandContext.<String>get("key").toLowerCase();
                            var keys = List.of(keyString.split("\\."));
                            var value = commandContext.get("value");

                            var keyOpt = container.getRegisteredKeys()
                                    .stream()
                                    .filter(t -> t.getKey().equals(keys) && t.getType() == type)
                                    .findFirst();

                            if (keyOpt.isEmpty()) {
                                sender.sendMessage(Message.of(LangKeys.ADMIN_ARENA_EDIT_ERRORS_INVALID_CONSTANT_NAME).defaultPrefix());
                            } else {
                                var key = keyOpt.get();
                                container.update(key.getKey(), value);
                                Message
                                        .of(LangKeys.ADMIN_ARENA_EDIT_SUCCESS_CONSTANT_SET)
                                        .placeholder("config", keyString)
                                        .placeholder("value", stringValueOf(value))
                                        .defaultPrefix()
                                        .send(sender);
                            }
                        }))
        );


        // list - add
        manager.command(
                commandBuilder
                        .literal("list", "l")
                        .literal(typeName, aliases)
                        .argument(StringArgument.<CommandSender>newBuilder("key")
                                .withSuggestionsProvider((c, s) -> makeSuggestion(c, false, container -> container.getJoinedRegisteredKeysForConfigCommandList(type)))
                        )
                        .literal("add", "a")
                        .argument(argumentListBuilder.apply("value"))
                        .handler(commandContext -> handleCommand(commandContext, false, (sender, container) -> {
                            String keyString = commandContext.get("key");
                            var keys = List.of(keyString.toLowerCase().split("\\."));
                            var value = commandContext.get("value");

                            var keyOpt = container.getRegisteredListKeys()
                                    .stream()
                                    .filter(t -> t.getKey().equals(keys) && t.getType() == type)
                                    .map(k -> (ConfigurationListKey) k)
                                    .findFirst();

                            if (keyOpt.isEmpty()) {
                                sender.sendMessage(Message.of(LangKeys.ADMIN_ARENA_EDIT_ERRORS_INVALID_CONSTANT_NAME).defaultPrefix());
                            } else {
                                var key = keyOpt.get();
                                var saved = new ArrayList<>(container.getSaved(key));
                                saved.add(value);
                                container.updateList(key.getKey(), type, saved);

                                Message
                                        .of(LangKeys.ADMIN_ARENA_EDIT_SUCCESS_CONSTANT_LIST_ADDED)
                                        .placeholder("config", keyString)
                                        .placeholder("value", stringValueOf(value))
                                        .placeholder("position", saved.size())
                                        .defaultPrefix()
                                        .send(sender);
                            }
                        }))
        );

        // list - set
        manager.command(
                commandBuilder
                        .literal("list", "l")
                        .literal(typeName, aliases)
                        .argument(StringArgument.<CommandSender>newBuilder("key")
                                .withSuggestionsProvider((c, s) -> makeSuggestion(c, false, container -> container.getJoinedRegisteredKeysForConfigCommandList(type)))
                        )
                        .literal("set", "s")
                        .argument(IntegerArgument.of("position"))
                        .argument(argumentListBuilder.apply("value"))
                        .handler(commandContext -> handleCommand(commandContext, false, (sender, container) -> {
                            String keyString = commandContext.get("key");
                            var keys = List.of(keyString.toLowerCase().split("\\."));
                            int position = commandContext.get("position");
                            var value = commandContext.get("value");

                            var keyOpt = container.getRegisteredListKeys()
                                    .stream()
                                    .filter(t -> t.getKey().equals(keys) && t.getType() == type)
                                    .map(k -> (ConfigurationListKey) k)
                                    .findFirst();

                            if (keyOpt.isEmpty()) {
                                sender.sendMessage(Message.of(LangKeys.ADMIN_ARENA_EDIT_ERRORS_INVALID_CONSTANT_NAME).defaultPrefix());
                            } else {
                                var key = keyOpt.get();
                                var saved = new ArrayList<>(container.getSaved(key));
                                if (saved.isEmpty()) {
                                    saved.add(value);
                                    container.updateList(key.getKey(), type, saved);

                                    Message
                                            .of(LangKeys.ADMIN_ARENA_EDIT_SUCCESS_CONSTANT_LIST_ADDED)
                                            .placeholder("config", keyString)
                                            .placeholder("value", stringValueOf(value))
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
                                        container.updateList(key.getKey(), type, saved);

                                        Message
                                                .of(LangKeys.ADMIN_ARENA_EDIT_SUCCESS_CONSTANT_LIST_ADDED)
                                                .placeholder("config", keyString)
                                                .placeholder("value", stringValueOf(value))
                                                .placeholder("position", position + 1)
                                                .defaultPrefix()
                                                .send(sender);
                                    }
                                }
                            }
                        }))
        );

        // list - remove
        manager.command(
                commandBuilder
                        .literal("list", "l")
                        .literal(typeName, aliases)
                        .argument(StringArgument.<CommandSender>newBuilder("key")
                                .withSuggestionsProvider((c, s) -> makeSuggestion(c, false, container -> container.getJoinedRegisteredKeysForConfigCommandList(type)))
                        )
                        .literal("remove", "delete", "r", "d")
                        .argument(IntegerArgument.of("position"))
                        .handler(commandContext -> handleCommand(commandContext, false, (sender, container) -> {
                            String keyString = commandContext.get("key");
                            var keys = List.of(keyString.toLowerCase().split("\\."));
                            int position = commandContext.get("position");

                            @SuppressWarnings("unchecked")
                            var keyOpt = container.getRegisteredListKeys()
                                    .stream()
                                    .filter(t -> t.getKey().equals(keys) && t.getType() == type)
                                    .map(k -> (ConfigurationListKey) k)
                                    .findFirst();

                            if (keyOpt.isEmpty()) {
                                sender.sendMessage(Message.of(LangKeys.ADMIN_ARENA_EDIT_ERRORS_INVALID_CONSTANT_NAME).defaultPrefix());
                            } else {
                                var key = keyOpt.get();
                                var saved = new ArrayList<>(container.getSaved(key));
                                position -= 1;
                                if (position < 0 || position >= saved.size()) {
                                    Message.of(LangKeys.ADMIN_ARENA_EDIT_ERRORS_INVALID_CONSTANT_POSITION)
                                            .defaultPrefix()
                                            .send(sender);
                                } else {
                                    saved.remove(position);
                                    container.updateList(key.getKey(), type, saved);

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

        // list - clear
        manager.command(
                commandBuilder
                        .literal("list", "l")
                        .literal(typeName, aliases)
                        .argument(StringArgument.<CommandSender>newBuilder("key")
                                .withSuggestionsProvider((c, s) -> makeSuggestion(c, false, container -> container.getJoinedRegisteredKeysForConfigCommandList(type)))
                        )
                        .literal("clear", "c")
                        .handler(commandContext -> handleCommand(commandContext, false, (sender, container) -> {
                            String keyString = commandContext.get("key");
                            var keys = List.of(keyString.toLowerCase().split("\\."));

                            var keyOpt = container.getRegisteredListKeys()
                                    .stream()
                                    .filter(t -> t.getKey().equals(keys) && t.getType() == type)
                                    .findFirst();

                            if (keyOpt.isEmpty()) {
                                sender.sendMessage(Message.of(LangKeys.ADMIN_ARENA_EDIT_ERRORS_INVALID_CONSTANT_NAME).defaultPrefix());
                            } else {
                                var key = keyOpt.get();
                                container.remove(key.getKey());
                                container.update(key.getKey(), List.of());

                                Message
                                        .of(LangKeys.ADMIN_ARENA_EDIT_SUCCESS_CONSTANT_LIST_CLEARED)
                                        .placeholder("config", keyString)
                                        .defaultPrefix()
                                        .send(sender);
                            }
                        }))
        );
    }

    private @NotNull Component stringValueOf(@NotNull Object object) {
        if (object instanceof ItemStack) {
            var item = (ItemStack) object;
            return Component.text()
                    .content(item.getAmount() + "x " + item.getType().location().asString())
                    .hoverEvent(item.asItemContent())
                    .build();
        } else {
            return Component.text(object.toString());
        }
    }
    
    @NotNull
    private List<String> makeSuggestion(@NotNull CommandContext<CommandSender> context, boolean viewOnly, @NotNull Function<@NotNull ConfigurationContainerImpl, @NotNull List<String>> processor) {
        var result = resolver.resolveContainer(context, viewOnly);
        if (result.container != null) {
            return processor.apply(result.container);
        }
        return List.of();
    }

    private void handleCommand(@NotNull CommandContext<CommandSender> context, boolean viewOnly, @NotNull BiConsumer<@NotNull CommandSender, @NotNull ConfigurationContainerImpl> handler) {
        handleCommand(context, viewOnly, (c, con, s) -> handler.accept(c, con));
    }
    
    private void handleCommand(@NotNull CommandContext<CommandSender> context, boolean viewOnly, @NotNull CommandHandler handler) {
        var result = resolver.resolveContainer(context, viewOnly);
        if (result.container == null) {
            context.getSender().sendMessage(result.errorMessage != null ? result.errorMessage : Message.of(LangKeys.UNKNOWN_COMMAND).defaultPrefix());
            return;
        }
        handler.accept(context.getSender(), result.container, result.configuredComponentName);
    }

    public interface ConfigurationContainerResolver {
        @NotNull
        ResolverResult resolveContainer(@NotNull CommandContext<CommandSender> context, boolean viewOnly);
    }

    @Data
    @Builder
    public static final class ResolverResult {
        @Nullable
        public final String configuredComponentName;
        @Nullable
        public final ConfigurationContainerImpl container;
        @Nullable
        public final ComponentLike errorMessage;
    }

    public interface CommandHandler {
        void accept(@NotNull CommandSender commandSender, @NotNull ConfigurationContainerImpl configurationContainer, @Nullable String key);
    }
}
