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

import lombok.AllArgsConstructor;
import lombok.Data;
import org.screamingsandals.bedwars.api.events.ApplyPropertyToItemEvent;
import org.screamingsandals.bedwars.game.GameImpl;
import org.screamingsandals.bedwars.player.BedWarsPlayer;
import org.screamingsandals.lib.event.SEvent;
import org.screamingsandals.lib.item.Item;
import org.screamingsandals.lib.item.builder.ItemFactory;

import java.util.Map;

@Data
@AllArgsConstructor
public class ApplyPropertyToItemEventImpl implements ApplyPropertyToItemEvent, SEvent {
    private final GameImpl game;
    private final BedWarsPlayer player;
    private final String propertyName;
    private final Map<String, Object> properties;
    private Item stack;

    @Override
    public void setStack(Object stack) {
        if (stack instanceof Item) {
            this.stack = (Item) stack;
        } else {
            this.stack = ItemFactory.build(stack).orElseThrow();
        }
    }
}
