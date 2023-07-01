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

package org.screamingsandals.bedwars.game.target;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.jetbrains.annotations.NotNull;
import org.screamingsandals.bedwars.api.game.target.TargetBlockCountdown;
import org.screamingsandals.bedwars.game.GameImpl;
import org.screamingsandals.bedwars.game.SerializableGameComponent;
import org.screamingsandals.bedwars.game.SerializableGameComponentLoader;
import org.screamingsandals.bedwars.utils.MiscUtils;
import org.screamingsandals.lib.world.Location;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;

import java.util.Objects;
import java.util.Optional;

@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class TargetBlockCountdownImpl extends TargetBlockImpl implements TargetBlockCountdown, ATargetCountdown, SerializableGameComponent {
    private final int countdown;
    private volatile int remainingTime;

    public TargetBlockCountdownImpl(@NotNull Location targetBlock, int countdown) {
        super(targetBlock);
        this.countdown = countdown;
    }

    @Override
    public boolean isValid() {
        return super.isValid() && remainingTime > 0;
    }

    @Override
    public void saveTo(@NotNull ConfigurationNode node) throws SerializationException {
        super.saveTo(node);
        node.node("type").set("block-countdown");
        node.node("countdown").set(countdown);
    }

    public static class Loader implements SerializableGameComponentLoader<TargetBlockCountdownImpl> {
        public static final Loader INSTANCE = new Loader();

        @Override
        @NotNull
        public Optional<TargetBlockCountdownImpl> load(@NotNull GameImpl game, @NotNull ConfigurationNode node) throws ConfigurateException {
            if (!node.node("type").getString("").equals("block-countdown")) {
                return Optional.empty();
            }

            return Optional.of(new TargetBlockCountdownImpl(
                    MiscUtils.readLocationFromString(game.getWorld(), Objects.requireNonNull(node.node("loc").getString())),
                    node.node("countdown").getInt()
            ));
        }
    }
}
