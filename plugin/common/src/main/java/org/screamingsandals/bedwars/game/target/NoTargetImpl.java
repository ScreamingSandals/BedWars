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

package org.screamingsandals.bedwars.game.target;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.screamingsandals.bedwars.api.game.target.NoTarget;
import org.screamingsandals.bedwars.game.GameImpl;
import org.screamingsandals.bedwars.game.SerializableGameComponent;
import org.screamingsandals.bedwars.game.SerializableGameComponentLoader;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;

import java.util.Optional;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class NoTargetImpl implements NoTarget, SerializableGameComponent {
    public static final NoTargetImpl INSTANCE = new NoTargetImpl();

    @Override
    public void saveTo(@NotNull ConfigurationNode node) throws SerializationException {
        node.node("type").set("none");
    }

    public final static class Loader implements SerializableGameComponentLoader<NoTargetImpl> {
        public static final Loader INSTANCE = new Loader();

        @Override
        @NotNull
        public Optional<NoTargetImpl> load(@NotNull GameImpl game, @NotNull ConfigurationNode node) throws ConfigurateException {
            if (node.node("type").getString("").equals("none")) {
                return Optional.of(NoTargetImpl.INSTANCE);
            }
            return Optional.empty();
        }
    }
}
