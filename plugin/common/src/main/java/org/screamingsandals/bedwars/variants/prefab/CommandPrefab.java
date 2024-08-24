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

package org.screamingsandals.bedwars.variants.prefab;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import org.screamingsandals.bedwars.api.game.Game;
import org.screamingsandals.lib.player.Player;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.ConfigurationNode;

import java.util.List;
import java.util.Objects;

@RequiredArgsConstructor
public class CommandPrefab implements Prefab {
    private static final @NotNull String COMMANDS_KEY = "commands";

    @Getter
    @Setter
    private @NotNull List<@NotNull String> commands;

    @Override
    public void place(@NotNull Game game, @NotNull Player player) {
        commands.forEach(s -> {
            if (s.startsWith("/")) {
                s = s.substring(1);
            }
            s = s.replace("%game%", game.getName());

            player.tryToDispatchCommand(s);
        });
    }

    public static class Loader implements Prefab.Loader<CommandPrefab> {
        public static final @NotNull Loader INSTANCE = new Loader();

        @Override
        public @NotNull Prefab load(@NotNull ConfigurationNode node) throws ConfigurateException {
            return new CommandPrefab(Objects.requireNonNull(node.node(COMMANDS_KEY).getList(String.class)));
        }
    }
}
