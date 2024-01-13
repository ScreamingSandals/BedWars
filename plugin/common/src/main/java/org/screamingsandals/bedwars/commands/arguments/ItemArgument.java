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

package org.screamingsandals.bedwars.commands.arguments;

import cloud.commandframework.arguments.CommandArgument;
import cloud.commandframework.arguments.parser.ArgumentParseResult;
import cloud.commandframework.arguments.parser.ArgumentParser;
import cloud.commandframework.context.CommandContext;
import cloud.commandframework.exceptions.parsing.NoInputProvidedException;
import io.leangen.geantyref.TypeToken;

import java.util.*;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.screamingsandals.bedwars.lang.LangKeys;
import org.screamingsandals.lib.item.ItemStack;
import org.screamingsandals.lib.item.builder.ItemStackFactory;
import org.screamingsandals.lib.lang.Message;
import org.screamingsandals.lib.nbt.CompoundTag;
import org.screamingsandals.lib.nbt.NumericTag;
import org.screamingsandals.lib.nbt.SNBTSerializer;
import org.screamingsandals.lib.nbt.StringTag;
import org.screamingsandals.lib.player.Player;
import org.screamingsandals.lib.sender.CommandSender;
import org.screamingsandals.lib.spectator.Component;
import org.screamingsandals.lib.spectator.utils.ComponentMessageThrowable;

public final class ItemArgument extends CommandArgument<CommandSender, ItemStack> {

    private ItemArgument(final boolean required, final @NotNull String name, final @NotNull String defaultValue) {
        super(
                required,
                name,
                new ItemParser(),
                defaultValue,
                TypeToken.get(ItemStack.class),
                null,
                new LinkedList<>()
        );
    }

    public static @NotNull ItemArgument of(final @NotNull String name) {
        return new ItemArgument(
                true,
                name,
                ""
        );
    }


    public static @NotNull ItemArgument optional(final @NotNull String name) {
        return new ItemArgument(
                false,
                name,
                ""
        );
    }

    public static @NotNull ItemArgument optionalWithDefault(final @NotNull String name, final @NotNull String defaultValue) {
        return new ItemArgument(
                false,
                name,
                defaultValue
        );
    }


    public static final class ItemParser implements ArgumentParser<CommandSender, ItemStack> {

        @Override
        @NotNull
        public ArgumentParseResult<@NotNull ItemStack> parse(final @NotNull CommandContext<@NotNull CommandSender> commandContext, final @NotNull Queue<@NotNull String> inputQueue) {
            if (inputQueue.peek() == null) {
                return ArgumentParseResult.failure(
                        new NoInputProvidedException(
                                ItemParser.class,
                                commandContext
                        )
                );
            }

            final var sj = new StringJoiner(" ");
            final int size = inputQueue.size();

            for (int i = 0; i < size; i++) {
                final var string = inputQueue.peek();

                if (string == null) {
                    break;
                }

                sj.add(string);
                inputQueue.remove();
            }
            final var input = sj.toString();

            if ("itemInHand".equalsIgnoreCase(input)) {
                var sender = commandContext.getSender();
                if (sender instanceof Player) {
                    var hand = ((Player) sender).getItemInMainHand();
                    if (hand == null || hand.isAir()) {
                        hand = ((Player) sender).getItemInOffHand();
                    }
                    if (hand != null && !hand.isAir()) {
                        return ArgumentParseResult.success(hand);
                    } else {
                        return ArgumentParseResult.failure(
                                new ItemParseException(
                                        Message.of(LangKeys.ADMIN_ARENA_EDIT_ERRORS_NOTHING_IN_HAND)
                                                .getForJoined(commandContext.getSender())
                                )
                        );
                    }
                }
            }
            var opt = ItemStackFactory.build(input);
            if (opt != null) {
                return ArgumentParseResult.success(opt);
            } else {
                var snbt = SNBTSerializer.builder().shouldSaveLongArraysDirectly(true).build();
                try {
                    var tag = (CompoundTag) snbt.deserialize(input);
                    var id = (StringTag) tag.tag("id");
                    if (id != null) {
                        var count = tag.tag("count");
                        var metaTag = tag.tag("tag");

                        var builder = ItemStackFactory.builder()
                                .type(id.value())
                                .amount(count instanceof NumericTag ? ((NumericTag) count).intValue() : 1);
                        if (metaTag instanceof CompoundTag) {
                            builder.tag((CompoundTag) metaTag);
                        }
                        var item = builder.build();
                        if (item != null) {
                            return ArgumentParseResult.success(item);
                        }
                    }
                } catch (Throwable ignored) {
                }
            }
            return ArgumentParseResult.failure(
                    new ItemParseException(
                            Message.of(LangKeys.ADMIN_ARENA_EDIT_ERRORS_INVALID_ITEM)
                                    .placeholderRaw("item", input)
                                    .getForJoined(commandContext.getSender())
                    )
            );
        }

        @Override
        public @NotNull List<@NotNull String> suggestions(final @NotNull CommandContext<CommandSender> commandContext, final @NotNull String input) {
            final List<String> suggestions = new LinkedList<>();
            suggestions.add("itemInHand");
            suggestions.add("minecraft:stone");
            suggestions.add("minecraft:golden_pickaxe;1;Useless pickaxe");
            suggestions.add("{id:\"minecraft:stone\",count:4,tag:{Example:\"Tag\"}}");
            return suggestions;
        }

    }


    private static final class ItemParseException extends Exception implements ComponentMessageThrowable {
        private final Component component;

        private ItemParseException(final @NotNull Component component) {
            super(component.toPlainText());
            this.component = component;
        }

        @Override
        public @Nullable Component componentMessage() {
            return component;
        }
    }

}
