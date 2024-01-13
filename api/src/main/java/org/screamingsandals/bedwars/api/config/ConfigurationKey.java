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

package org.screamingsandals.bedwars.api.config;

import lombok.AccessLevel;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

/**
 *
 * @param <T> type of the saved resource
 * @author ScreamingSandals
 * @since 0.3.0
 */
@Data
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class ConfigurationKey<T> {
    private static final Pattern singleKeyRegex = Pattern.compile("[a-z\\d\\-_]+");

    @NotNull
    private final List<@NotNull String> key;
    @NotNull
    private final Class<T> type;

    /**
     *
     * @param type configuration type, please don't use primitives!
     * @param keys list of individual key parts that lead to the configuration, each part has to match [a-z\d\-_]+
     * @return the new configuration key
     * @param <T> configuration type
     * @since 0.3.0
     */
    @NotNull
    @Contract(value = "_, _ -> new", pure = true)
    public static <T> ConfigurationKey<T> of(@NotNull Class<T> type, @NotNull String... keys) {
        return of(type, Arrays.asList(keys));
    }

    /**
     *
     * @param type configuration type, please don't use primitives!
     * @param keys list of individual key parts that lead to the configuration, each part has to match [a-z\d\-_]+
     * @return the new configuration key
     * @param <T> configuration type
     * @since 0.3.0
     */
    @NotNull
    @Contract(value = "_, _ -> new", pure = true)
    public static <T> ConfigurationKey<T> of(@NotNull Class<T> type, @NotNull List<@NotNull String> keys) {
        if (keys.stream().anyMatch(s -> !singleKeyRegex.matcher(s).matches())) {
            throw new IllegalArgumentException("Unsupported key: " + keys + "! Each key part has to match this pattern: [a-z\\d\\-_]+");
        }
        return new ConfigurationKey<>(keys, type);
    }
}
