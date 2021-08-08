package org.screamingsandals.bedwars.entities;

import lombok.Data;
import org.screamingsandals.bedwars.api.entities.GameEntity;
import org.screamingsandals.bedwars.game.GameImpl;
import org.screamingsandals.lib.entity.EntityBasic;

@Data
public class GameEntityImpl implements GameEntity<GameImpl, EntityBasic> {
    private final GameImpl game;
    private final EntityBasic entity;
}
