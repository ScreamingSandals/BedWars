package org.screamingsandals.bedwars.api.entities;

import org.screamingsandals.bedwars.api.game.Game;

import java.util.List;
import java.util.Optional;

public interface EntitiesManager<E extends GameEntity<G, ?>, G extends Game<?, ?, ?, ?, ?, ?>> {

    List<E> getEntities(G game);

    default boolean isEntityInGame(Object entity) {
        return getGameOfEntity(entity).isPresent();
    }

    Optional<G> getGameOfEntity(Object entity);

    E addEntityToGame(Object entity, G game);

    void removeEntityFromGame(Object entity);

    void removeEntityFromGame(E entityObject);
}
