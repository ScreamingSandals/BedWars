package org.screamingsandals.bedwars.commands;

import cloud.commandframework.Command;
import cloud.commandframework.CommandManager;
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
import org.screamingsandals.bedwars.config.ConfigurationContainerImpl;
import org.screamingsandals.bedwars.lang.LangKeys;
import org.screamingsandals.lib.cloud.extras.ComponentHelper;
import org.screamingsandals.lib.lang.Message;
import org.screamingsandals.lib.sender.CommandSenderWrapper;
import org.screamingsandals.lib.spectator.Component;
import org.screamingsandals.lib.spectator.ComponentLike;
import org.screamingsandals.lib.utils.TriConsumer;

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
    private final CommandManager<CommandSenderWrapper> manager;
    @NotNull
    private final Command.Builder<CommandSenderWrapper> commandBuilder;
    @NotNull
    private final ConfigurationContainerResolver resolver;
    @Nullable
    private final Path shopFolder;

    public void construct() {
        // get
        manager.command(
                commandBuilder
                        .literal("get", "show", "g")
                        .argument(StringArgument.<CommandSenderWrapper>newBuilder("key")
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
                                                .append(Component.text(String.valueOf(val), CommandService.DEFAULT_HELP_COLORS.highlight()))
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
                                            .append(Component.text(String.valueOf(value), CommandService.DEFAULT_HELP_COLORS.highlight()))
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
                        .argument(StringArgument.<CommandSenderWrapper>newBuilder("key")
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
        manager.command(
                commandBuilder
                        .literal("set", "s")
                        .literal("boolean", "booleans", "bool", "b")
                        .argument(StringArgument.<CommandSenderWrapper>newBuilder("key")
                                .withSuggestionsProvider((c, s) -> makeSuggestion(c, false, container -> container.getJoinedRegisteredKeysForConfigCommand(Boolean.class)))
                        )
                        .argument(BooleanArgument.of("value"))
                        .handler(commandContext -> handleCommand(commandContext, false, (sender, container) -> {
                            var keyString = commandContext.<String>get("key").toLowerCase();
                            var keys = List.of(keyString.split("\\."));
                            boolean value = commandContext.get("value");

                            var keyOpt = container.getRegisteredKeys()
                                    .stream()
                                    .filter(t -> t.getKey().equals(keys) && t.getType() == Boolean.class)
                                    .findFirst();

                            if (keyOpt.isEmpty()) {
                                sender.sendMessage(Message.of(LangKeys.ADMIN_ARENA_EDIT_ERRORS_INVALID_CONSTANT_NAME).defaultPrefix());
                            } else {
                                var key = keyOpt.get();
                                container.update(key.getKey(), value);
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
                commandBuilder
                        .literal("set", "s")
                        .literal("string", "strings", "str", "s")
                        .argument(StringArgument.<CommandSenderWrapper>newBuilder("key")
                                .withSuggestionsProvider((c, s) -> makeSuggestion(c, false, container -> container.getJoinedRegisteredKeysForConfigCommand(String.class)))
                        )
                        .argument(StringArgument.<CommandSenderWrapper>newBuilder("value")
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
                        )
                        .handler(commandContext -> handleCommand(commandContext, false, (sender, container) -> {
                            String keyString = commandContext.get("key");
                            var keys = List.of(keyString.toLowerCase().split("\\."));
                            String value = commandContext.get("value");

                            var keyOpt = container.getRegisteredKeys()
                                    .stream()
                                    .filter(t -> t.getKey().equals(keys) && t.getType() == String.class)
                                    .findFirst();

                            if (keyOpt.isEmpty()) {
                                sender.sendMessage(Message.of(LangKeys.ADMIN_ARENA_EDIT_ERRORS_INVALID_CONSTANT_NAME).defaultPrefix());
                            } else {
                                var key = keyOpt.get();
                                container.update(key.getKey(), value);
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
                commandBuilder
                        .literal("set", "s")
                        .literal("byte", "bytes", "by")
                        .argument(StringArgument.<CommandSenderWrapper>newBuilder("key")
                                .withSuggestionsProvider((c, s) -> makeSuggestion(c, false, container -> container.getJoinedRegisteredKeysForConfigCommand(Byte.class)))
                        )
                        .argument(ByteArgument.of("value"))
                        .handler(commandContext -> handleCommand(commandContext, false, (sender, container) -> {
                            String keyString = commandContext.get("key");
                            var keys = List.of(keyString.toLowerCase().split("\\."));
                            byte value = commandContext.get("value");

                            var keyOpt = container.getRegisteredKeys()
                                    .stream()
                                    .filter(t -> t.getKey().equals(keys) && t.getType() == Byte.class)
                                    .findFirst();

                            if (keyOpt.isEmpty()) {
                                sender.sendMessage(Message.of(LangKeys.ADMIN_ARENA_EDIT_ERRORS_INVALID_CONSTANT_NAME).defaultPrefix());
                            } else {
                                var key = keyOpt.get();
                                container.update(key.getKey(), value);
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
                commandBuilder
                        .literal("set", "s")
                        .literal("short", "shorts", "sh")
                        .argument(StringArgument.<CommandSenderWrapper>newBuilder("key")
                                .withSuggestionsProvider((c, s) -> makeSuggestion(c, false, container -> container.getJoinedRegisteredKeysForConfigCommand(Short.class)))
                        )
                        .argument(ShortArgument.of("value"))
                        .handler(commandContext -> handleCommand(commandContext, false, (sender, container) -> {
                            String keyString = commandContext.get("key");
                            var keys = List.of(keyString.toLowerCase().split("\\."));
                            short value = commandContext.get("value");

                            var keyOpt = container.getRegisteredKeys()
                                    .stream()
                                    .filter(t -> t.getKey().equals(keys) && t.getType() == Short.class)
                                    .findFirst();

                            if (keyOpt.isEmpty()) {
                                sender.sendMessage(Message.of(LangKeys.ADMIN_ARENA_EDIT_ERRORS_INVALID_CONSTANT_NAME).defaultPrefix());
                            } else {
                                var key = keyOpt.get();
                                container.update(key.getKey(), value);
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
                commandBuilder
                        .literal("set", "s")
                        .literal("integer", "integers", "int", "i")
                        .argument(StringArgument.<CommandSenderWrapper>newBuilder("key")
                                .withSuggestionsProvider((c, s) -> makeSuggestion(c, false, container -> container.getJoinedRegisteredKeysForConfigCommand(Integer.class)))
                        )
                        .argument(IntegerArgument.of("value"))
                        .handler(commandContext -> handleCommand(commandContext, false, (sender, container) -> {
                            String keyString = commandContext.get("key");
                            var keys = List.of(keyString.toLowerCase().split("\\."));
                            int value = commandContext.get("value");

                            var keyOpt = container.getRegisteredKeys()
                                    .stream()
                                    .filter(t -> t.getKey().equals(keys) && t.getType() == Integer.class)
                                    .findFirst();

                            if (keyOpt.isEmpty()) {
                                sender.sendMessage(Message.of(LangKeys.ADMIN_ARENA_EDIT_ERRORS_INVALID_CONSTANT_NAME).defaultPrefix());
                            } else {
                                var key = keyOpt.get();
                                container.update(key.getKey(), value);
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
                commandBuilder
                        .literal("set", "s")
                        .literal("long", "longs", "l")
                        .argument(StringArgument.<CommandSenderWrapper>newBuilder("key")
                                .withSuggestionsProvider((c, s) -> makeSuggestion(c, false, container -> container.getJoinedRegisteredKeysForConfigCommand(Long.class)))
                        )
                        .argument(LongArgument.of("value"))
                        .handler(commandContext -> handleCommand(commandContext, false, (sender, container) -> {
                            String keyString = commandContext.get("key");
                            var keys = List.of(keyString.toLowerCase().split("\\."));
                            long value = commandContext.get("value");

                            var keyOpt = container.getRegisteredKeys()
                                    .stream()
                                    .filter(t -> t.getKey().equals(keys) && t.getType() == Long.class)
                                    .findFirst();

                            if (keyOpt.isEmpty()) {
                                sender.sendMessage(Message.of(LangKeys.ADMIN_ARENA_EDIT_ERRORS_INVALID_CONSTANT_NAME).defaultPrefix());
                            } else {
                                var key = keyOpt.get();
                                container.update(key.getKey(), value);
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
                commandBuilder
                        .literal("set", "s")
                        .literal("float", "floats", "f")
                        .argument(StringArgument.<CommandSenderWrapper>newBuilder("key")
                                .withSuggestionsProvider((c, s) -> makeSuggestion(c, false, container -> container.getJoinedRegisteredKeysForConfigCommand(Float.class)))
                        )
                        .argument(FloatArgument.of("value"))
                        .handler(commandContext -> handleCommand(commandContext, false, (sender, container) -> {
                            String keyString = commandContext.get("key");
                            var keys = List.of(keyString.toLowerCase().split("\\."));
                            float value = commandContext.get("value");

                            var keyOpt = container.getRegisteredKeys()
                                    .stream()
                                    .filter(t -> t.getKey().equals(keys) && t.getType() == Float.class)
                                    .findFirst();

                            if (keyOpt.isEmpty()) {
                                sender.sendMessage(Message.of(LangKeys.ADMIN_ARENA_EDIT_ERRORS_INVALID_CONSTANT_NAME).defaultPrefix());
                            } else {
                                var key = keyOpt.get();
                                container.update(key.getKey(), value);
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
                commandBuilder
                        .literal("set", "s")
                        .literal("double", "doubles", "d")
                        .argument(StringArgument.<CommandSenderWrapper>newBuilder("key")
                                .withSuggestionsProvider((c, s) -> makeSuggestion(c, false, container -> container.getJoinedRegisteredKeysForConfigCommand(Double.class)))
                        )
                        .argument(DoubleArgument.of("value"))
                        .handler(commandContext -> handleCommand(commandContext, false, (sender, container) -> {
                            String keyString = commandContext.get("key");
                            var keys = List.of(keyString.toLowerCase().split("\\."));
                            double value = commandContext.get("value");

                            var keyOpt = container.getRegisteredKeys()
                                    .stream()
                                    .filter(t -> t.getKey().equals(keys) && t.getType() == Double.class)
                                    .findFirst();

                            if (keyOpt.isEmpty()) {
                                sender.sendMessage(Message.of(LangKeys.ADMIN_ARENA_EDIT_ERRORS_INVALID_CONSTANT_NAME).defaultPrefix());
                            } else {
                                var key = keyOpt.get();
                                container.update(key.getKey(), value);
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
                commandBuilder
                        .literal("set", "s")
                        .literal("char", "chars", "c")
                        .argument(StringArgument.<CommandSenderWrapper>newBuilder("key")
                                .withSuggestionsProvider((c, s) -> makeSuggestion(c, false, container -> container.getJoinedRegisteredKeysForConfigCommand(Character.class)))
                        )
                        .argument(CharArgument.of("value"))
                        .handler(commandContext -> handleCommand(commandContext, false, (sender, container) -> {
                            String keyString = commandContext.get("key");
                            var keys = List.of(keyString.toLowerCase().split("\\."));
                            char value = commandContext.get("value");

                            var keyOpt = container.getRegisteredKeys()
                                    .stream()
                                    .filter(t -> t.getKey().equals(keys) && t.getType() == Character.class)
                                    .findFirst();

                            if (keyOpt.isEmpty()) {
                                sender.sendMessage(Message.of(LangKeys.ADMIN_ARENA_EDIT_ERRORS_INVALID_CONSTANT_NAME).defaultPrefix());
                            } else {
                                var key = keyOpt.get();
                                container.update(key.getKey(), value);
                                Message
                                        .of(LangKeys.ADMIN_ARENA_EDIT_SUCCESS_CONSTANT_SET)
                                        .placeholder("config", keyString)
                                        .placeholder("value", value)
                                        .defaultPrefix()
                                        .send(sender);
                            }
                        }))
        );

        // enums
        manager.command(
                commandBuilder
                        .literal("set", "s")
                        .literal("enum", "enums", "e")
                        .argument(StringArgument.<CommandSenderWrapper>newBuilder("key")
                                .withSuggestionsProvider((c, s) -> makeSuggestion(c, false, ConfigurationContainerImpl::getJoinedRegisteredKeysForConfigCommandEnums))
                        )
                        .argument(StringArgument.<CommandSenderWrapper>newBuilder("value")
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

        // string list - add
        manager.command(
                commandBuilder
                        .literal("list", "l")
                        .literal("string", "strings", "str", "s")
                        .argument(StringArgument.<CommandSenderWrapper>newBuilder("key")
                                .withSuggestionsProvider((c, s) -> makeSuggestion(c, false, container -> container.getJoinedRegisteredKeysForConfigCommandList(String.class)))
                        )
                        .literal("add", "a")
                        .argument(StringArgument.greedy("value"))
                        .handler(commandContext -> handleCommand(commandContext, false, (sender, container) -> {
                            String keyString = commandContext.get("key");
                            var keys = List.of(keyString.toLowerCase().split("\\."));
                            String value = commandContext.get("value");

                            @SuppressWarnings("unchecked")
                            var keyOpt = container.getRegisteredListKeys()
                                    .stream()
                                    .filter(t -> t.getKey().equals(keys) && t.getType() == String.class)
                                    .map(k -> (ConfigurationListKey<String>) k)
                                    .findFirst();

                            if (keyOpt.isEmpty()) {
                                sender.sendMessage(Message.of(LangKeys.ADMIN_ARENA_EDIT_ERRORS_INVALID_CONSTANT_NAME).defaultPrefix());
                            } else {
                                var key = keyOpt.get();
                                var saved = new ArrayList<>(container.getSaved(key));
                                saved.add(value);
                                container.update(key.getKey(), saved);

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
                commandBuilder
                        .literal("list", "l")
                        .literal("string", "strings", "str", "s")
                        .argument(StringArgument.<CommandSenderWrapper>newBuilder("key")
                                .withSuggestionsProvider((c, s) -> makeSuggestion(c, false, container -> container.getJoinedRegisteredKeysForConfigCommandList(String.class)))
                        )
                        .literal("set", "s")
                        .argument(IntegerArgument.of("position"))
                        .argument(StringArgument.greedy("value"))
                        .handler(commandContext -> handleCommand(commandContext, false, (sender, container) -> {
                            String keyString = commandContext.get("key");
                            var keys = List.of(keyString.toLowerCase().split("\\."));
                            int position = commandContext.get("position");
                            String value = commandContext.get("value");

                            var keyOpt = container.getRegisteredListKeys()
                                    .stream()
                                    .filter(t -> t.getKey().equals(keys) && t.getType() == String.class)
                                    .map(k -> (ConfigurationListKey<String>) k)
                                    .findFirst();

                            if (keyOpt.isEmpty()) {
                                sender.sendMessage(Message.of(LangKeys.ADMIN_ARENA_EDIT_ERRORS_INVALID_CONSTANT_NAME).defaultPrefix());
                            } else {
                                var key = keyOpt.get();
                                var saved = new ArrayList<>(container.getSaved(key));
                                if (saved.isEmpty()) {
                                    saved.add(value);
                                    container.update(key.getKey(), saved);

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
                                        container.update(key.getKey(), saved);

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
                commandBuilder
                        .literal("list", "l")
                        .literal("string", "strings", "str", "s")
                        .argument(StringArgument.<CommandSenderWrapper>newBuilder("key")
                                .withSuggestionsProvider((c, s) -> makeSuggestion(c, false, container -> container.getJoinedRegisteredKeysForConfigCommandList(String.class)))
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
                                    .filter(t -> t.getKey().equals(keys) && t.getType() == String.class)
                                    .map(k -> (ConfigurationListKey<String>) k)
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
                                    container.update(key.getKey(), saved);

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
                commandBuilder
                        .literal("list", "l")
                        .literal("string", "strings", "str", "s")
                        .argument(StringArgument.<CommandSenderWrapper>newBuilder("key")
                                .withSuggestionsProvider((c, s) -> makeSuggestion(c, false, container -> container.getJoinedRegisteredKeysForConfigCommandList(String.class)))
                        )
                        .literal("clear", "c")
                        .handler(commandContext -> handleCommand(commandContext, false, (sender, container) -> {
                            String keyString = commandContext.get("key");
                            var keys = List.of(keyString.toLowerCase().split("\\."));

                            var keyOpt = container.getRegisteredListKeys()
                                    .stream()
                                    .filter(t -> t.getKey().equals(keys) && t.getType() == String.class)
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

        // TODO: non-string lists
    }
    
    @NotNull
    private List<String> makeSuggestion(@NotNull CommandContext<CommandSenderWrapper> context, boolean viewOnly, @NotNull Function<@NotNull ConfigurationContainerImpl, @NotNull List<String>> processor) {
        var result = resolver.resolveContainer(context, viewOnly);
        if (result.container != null) {
            return processor.apply(result.container);
        }
        return List.of();
    }

    private void handleCommand(@NotNull CommandContext<CommandSenderWrapper> context, boolean viewOnly, @NotNull BiConsumer<@NotNull CommandSenderWrapper, @NotNull ConfigurationContainerImpl> handler) {
        handleCommand(context, viewOnly, (c, con, s) -> handler.accept(c, con));
    }
    
    private void handleCommand(@NotNull CommandContext<CommandSenderWrapper> context, boolean viewOnly, @NotNull TriConsumer<@NotNull CommandSenderWrapper, @NotNull ConfigurationContainerImpl, @Nullable String> handler) {
        var result = resolver.resolveContainer(context, viewOnly);
        if (result.container == null) {
            context.getSender().sendMessage(result.errorMessage != null ? result.errorMessage : Message.of(LangKeys.UNKNOWN_COMMAND).defaultPrefix());
            return;
        }
        handler.accept(context.getSender(), result.container, result.configuredComponentName);
    }

    public interface ConfigurationContainerResolver {
        @NotNull
        ResolverResult resolveContainer(@NotNull CommandContext<CommandSenderWrapper> context, boolean viewOnly);
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
}
