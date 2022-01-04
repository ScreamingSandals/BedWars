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

package org.screamingsandals.bedwars.api.entities;

import org.jetbrains.annotations.ApiStatus;
import org.screamingsandals.bedwars.api.game.Game;

import java.util.List;
import java.util.Optional;

@ApiStatus.NonExtendable
public interface EntitiesManager<E extends GameEntity<G, ?>, G extends Game<?, ?, ?, ?, ?, ?, ?, ?, ?>> {

    List<E> getEntities(G game);

    default boolean isEntityInGame(Object entity) {
        return getGameOfEntity(entity).isPresent();
    }

    Optional<G> getGameOfEntity(Object entity);

    E addEntityToGame(Object entity, G game);

    void removeEntityFromGame(Object entity);

    void removeEntityFromGame(E entityObject);
}
