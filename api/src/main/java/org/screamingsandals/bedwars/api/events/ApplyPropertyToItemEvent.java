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

package org.screamingsandals.bedwars.api.events;

import org.jetbrains.annotations.ApiStatus;
import org.screamingsandals.bedwars.api.BedwarsAPI;
import org.screamingsandals.bedwars.api.game.Game;
import org.screamingsandals.bedwars.api.player.BWPlayer;
import org.screamingsandals.bedwars.api.utils.EventUtils;
import org.screamingsandals.lib.api.Wrapper;

import java.util.Map;
import java.util.function.Consumer;

@ApiStatus.NonExtendable
public interface ApplyPropertyToItemEvent {
    Game getGame();

    BWPlayer getPlayer();

    Wrapper getStack();

    String getPropertyName();

    // TODO - Special wrapper for ConfigurationNodes
    Map<String, Object> getProperties();

    /**
     *
     * @param stack wrapper or platform item
     */
    void setStack(Object stack);

    static void handle(Object plugin, Consumer<ApplyPropertyToItemEvent> consumer) {
        BedwarsAPI.getInstance().getEventUtils().handle(plugin, ApplyPropertyToItemEvent.class, consumer);
    }

    default Object getProperty(String key) {
        return getProperties().get(key);
    }

    default boolean hasProperty(String key) {
        return getProperties().containsKey(key);
    }

    default String getStringProperty(String key) {
        return getProperties().get(key).toString();
    }

    default byte getByteProperty(String key) {
        return ((Number) getProperties().get(key)).byteValue();
    }

    default short getShortProperty(String key) {
        return ((Number) getProperties().get(key)).shortValue();
    }

    default int getIntProperty(String key) {
        return ((Number) getProperties().get(key)).intValue();
    }

    default long getLongProperty(String key) {
        return ((Number) getProperties().get(key)).longValue();
    }

    default float getFloatProperty(String key) {
        return ((Number) getProperties().get(key)).floatValue();
    }

    default double getDoubleProperty(String key) {
        return ((Number) getProperties().get(key)).doubleValue();
    }

    default boolean getBooleanProperty(String key) {
        return Boolean.parseBoolean(getProperties().get(key).toString());
    }

    default char getCharProperty(String key) {
        return (char) getProperties().get(key);
    }
}
