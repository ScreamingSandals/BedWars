package org.screamingsandals.bedwars.api.game.target;

/**
 * @author ScreamingSandals
 * @since 0.3.0
 */
public interface NoTarget extends Target {
    @Override
    default boolean isValid() {
        return false;
    }
}
