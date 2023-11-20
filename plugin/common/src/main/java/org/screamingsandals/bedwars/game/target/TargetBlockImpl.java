/*
 * Copyright (C) 2023 ScreamingSandals
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
import org.screamingsandals.bedwars.api.game.target.TargetBlock;
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

@Data
public class TargetBlockImpl implements TargetBlock, SerializableGameComponent {
    @NotNull
    private final Location targetBlock;
    private boolean valid = true;

    @Override
    public boolean isEmpty() {
        if (!isValid()) {
            return false;
        }

        var blockType = targetBlock.getBlock().block();
        if (!blockType.isSameType("respawn_anchor")) {
            return false;
        }

        var charges = blockType.getInt("charges");
        return charges != null && charges == 0;
    }

    @Override
    public int getCharge() {
        if (!isValid()) {
            return 0;
        }

        var blockType = targetBlock.getBlock().block();
        if (!blockType.isSameType("respawn_anchor")) {
            return 1;
        }

        var charges = blockType.getInt("charges");
        return charges != null ? charges : 1;
    }

    @Override
    public void saveTo(@NotNull ConfigurationNode node) throws SerializationException {
        node.node("type").set("block");
        node.node("loc").set(MiscUtils.writeLocationToString(targetBlock));
    }

    public static class Loader implements SerializableGameComponentLoader<TargetBlockImpl> {
        public static final Loader INSTANCE = new Loader();

        @Override
        @NotNull
        public Optional<TargetBlockImpl> load(@NotNull GameImpl game, @NotNull ConfigurationNode node) throws ConfigurateException {
            if (!node.node("type").getString("").equals("block")) {
                return Optional.empty();
            }

            return Optional.of(new TargetBlockImpl(MiscUtils.readLocationFromString(game.getWorld(), Objects.requireNonNull(node.node("loc").getString()))));
        }
    }
}
