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

package org.screamingsandals.bedwars.entities;

import org.screamingsandals.bedwars.api.entities.EntitiesManager;
import org.screamingsandals.bedwars.game.GameImpl;
import org.screamingsandals.lib.entity.EntityBasic;
import org.screamingsandals.lib.entity.EntityMapper;
import org.screamingsandals.lib.plugin.ServiceManager;
import org.screamingsandals.lib.utils.annotations.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class EntitiesManagerImpl implements EntitiesManager<GameEntityImpl, GameImpl> {
    private final List<GameEntityImpl> entities = new ArrayList<>();

    public static EntitiesManagerImpl getInstance() {
        return ServiceManager.get(EntitiesManagerImpl.class);
    }

    @Override
    public List<GameEntityImpl> getEntities(GameImpl game) {
        return entities.stream().filter(gameEntity -> gameEntity.getGame() == game).collect(Collectors.toList());
    }

    @Override
    public Optional<GameImpl> getGameOfEntity(Object entity) {
        if (entity instanceof EntityBasic) {
            return getGameOfEntity((EntityBasic) entity);
        } else {
            return getGameOfEntity(EntityMapper.wrapEntity(entity).orElseThrow());
        }
    }

    public Optional<GameImpl> getGameOfEntity(EntityBasic entityBasic) {
        return entities.stream().filter(gameEntity -> gameEntity.getEntity().equals(entityBasic)).findFirst().map(GameEntityImpl::getGame);
    }

    @Override
    public GameEntityImpl addEntityToGame(Object entity, GameImpl game) {
        if (entity instanceof EntityBasic) {
            return addEntityToGame((EntityBasic) entity, game);
        } else {
            return addEntityToGame(EntityMapper.wrapEntity(entity).orElseThrow(), game);
        }
    }

    public GameEntityImpl addEntityToGame(EntityBasic entityBasic, GameImpl game) {
        var gameEntity = new GameEntityImpl(game, entityBasic);
        entities.add(gameEntity);
        return gameEntity;
    }

    @Override
    public void removeEntityFromGame(Object entity) {
        if (entity instanceof EntityBasic) {
            removeEntityFromGame((EntityBasic) entity);
        } else {
            removeEntityFromGame(EntityMapper.wrapEntity(entity).orElseThrow());
        }
    }

    public void removeEntityFromGame(EntityBasic entityBasic) {
        entities.stream()
                .filter(gameEntity -> gameEntity.getEntity().equals(entityBasic))
                .findFirst()
                .ifPresent(this::removeEntityFromGame);
    }

    @Override
    public void removeEntityFromGame(GameEntityImpl entityObject) {
        entities.remove(entityObject);
    }
}
