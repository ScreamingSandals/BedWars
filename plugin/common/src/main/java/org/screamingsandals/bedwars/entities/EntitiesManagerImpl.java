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

package org.screamingsandals.bedwars.entities;

import org.screamingsandals.bedwars.api.entities.EntitiesManager;
import org.screamingsandals.bedwars.api.entities.GameEntity;
import org.screamingsandals.bedwars.api.game.LocalGame;
import org.screamingsandals.bedwars.game.GameImpl;
import org.screamingsandals.lib.api.types.server.EntityHolder;
import org.screamingsandals.lib.entity.Entity;
import org.screamingsandals.lib.plugin.ServiceManager;
import org.screamingsandals.lib.utils.annotations.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class EntitiesManagerImpl implements EntitiesManager {
    private final List<GameEntityImpl> entities = new ArrayList<>();

    public static EntitiesManagerImpl getInstance() {
        return ServiceManager.get(EntitiesManagerImpl.class);
    }

    @Override
    public List<GameEntityImpl> getEntities(LocalGame game) {
        return entities.stream().filter(gameEntity -> gameEntity.getGame() == game).collect(Collectors.toList());
    }

    @Override
    public Optional<GameImpl> getGameOfEntity(EntityHolder entity) {
        return getGameOfEntity(entity.as(Entity.class));
    }

    public Optional<GameImpl> getGameOfEntity(Entity entityBasic) {
        return entities.stream().filter(gameEntity -> gameEntity.getEntity().equals(entityBasic)).findFirst().map(GameEntityImpl::getGame);
    }

    @Override
    public GameEntityImpl addEntityToGame(EntityHolder entity, LocalGame game) {
        return addEntityToGame(entity.as(Entity.class), game);
    }

    public GameEntityImpl addEntityToGame(Entity entityBasic, LocalGame game) {
        if (!(game instanceof GameImpl)) {
            throw new IllegalArgumentException("Provided instance of game is not created by BedWars plugin!");
        }

        var gameEntity = new GameEntityImpl((GameImpl) game, entityBasic);
        entities.add(gameEntity);
        return gameEntity;
    }

    @Override
    public void removeEntityFromGame(EntityHolder entity) {
        removeEntityFromGame(entity.as(Entity.class));
    }

    public void removeEntityFromGame(Entity entityBasic) {
        entities.stream()
                .filter(gameEntity -> gameEntity.getEntity().equals(entityBasic))
                .findFirst()
                .ifPresent(this::removeEntityFromGame);
    }

    @Override
    public void removeEntityFromGame(GameEntity entityObject) {
        if (!(entityObject instanceof GameEntityImpl)) {
            throw new IllegalArgumentException("Provided instance of game entity is not created by BedWars plugin!");
        }

        entities.remove(entityObject);
    }
}
