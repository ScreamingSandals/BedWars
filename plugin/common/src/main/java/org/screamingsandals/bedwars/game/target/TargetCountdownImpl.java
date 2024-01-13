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

import lombok.Data;
import org.jetbrains.annotations.NotNull;
import org.screamingsandals.bedwars.game.GameImpl;
import org.screamingsandals.bedwars.game.SerializableGameComponent;
import org.screamingsandals.bedwars.game.SerializableGameComponentLoader;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;

import java.util.Optional;

@Data
public final class TargetCountdownImpl implements ATargetCountdown, SerializableGameComponent {
    private final int countdown;
    private volatile int remainingTime;

    @Override
    public boolean isValid() {
        return remainingTime > 0;
    }

    @Override
    public void saveTo(@NotNull ConfigurationNode node) throws SerializationException {
        node.node("type").set("countdown");
        node.node("countdown").set(countdown);
    }

    public final static class Loader implements SerializableGameComponentLoader<TargetCountdownImpl> {
        public static final Loader INSTANCE = new Loader();

        @Override
        @NotNull
        public Optional<TargetCountdownImpl> load(@NotNull GameImpl game, @NotNull ConfigurationNode node) throws ConfigurateException {
            if (node.node("type").getString("").equals("countdown")) {
                return Optional.of(new TargetCountdownImpl(node.node("countdown").getInt()));
            }
            return Optional.empty();
        }
    }
}
