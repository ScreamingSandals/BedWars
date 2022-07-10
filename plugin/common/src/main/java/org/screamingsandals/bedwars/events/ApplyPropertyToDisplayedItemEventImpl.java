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

package org.screamingsandals.bedwars.events;

import org.jetbrains.annotations.NotNull;
import org.screamingsandals.bedwars.api.events.ApplyPropertyToDisplayedItemEvent;
import org.screamingsandals.bedwars.game.GameImpl;
import org.screamingsandals.bedwars.player.BedWarsPlayer;
import org.screamingsandals.lib.item.Item;

import java.util.Map;

public class ApplyPropertyToDisplayedItemEventImpl extends ApplyPropertyToItemEventImpl implements ApplyPropertyToDisplayedItemEvent {
    public ApplyPropertyToDisplayedItemEventImpl(GameImpl game, BedWarsPlayer player, String name, Map<String, Object> properties, @NotNull Item stack) {
        super(game, player, name, properties, stack);
    }
}
