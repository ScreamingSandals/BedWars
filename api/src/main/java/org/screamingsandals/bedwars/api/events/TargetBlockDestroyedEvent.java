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

package org.screamingsandals.bedwars.api.events;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;
import org.screamingsandals.bedwars.api.BedwarsAPI;
import org.screamingsandals.bedwars.api.Team;
import org.screamingsandals.bedwars.api.game.Game;
import org.screamingsandals.bedwars.api.player.BWPlayer;

import java.util.function.Consumer;

@ApiStatus.NonExtendable
public interface TargetBlockDestroyedEvent<G extends Game, P extends BWPlayer, T extends Team> {
    G getGame();

    @Nullable
    P getBroker();

    T getTeam();

    default boolean wasDestroyedByExplosion() {
        return getBroker() == null;
    }

    @SuppressWarnings("unchecked")
    static void handle(Object plugin, Consumer<TargetBlockDestroyedEvent<Game, BWPlayer, Team>> consumer) {
        BedwarsAPI.getInstance().getEventUtils().handle(plugin, TargetBlockDestroyedEvent.class, (Consumer) consumer);
    }
}
