package org.screamingsandals.bedwars.api.game.target;

/**
 * @author ScreamingSandals
 * @since 0.3.0
 */
public interface Target {
    /**
     * Determines if the target is still valid (not broken for {@link TargetBlock}).
     *
     * @return true if the target is still valid and protects team from elimination; false otherwise
     * @since 0.3.0
     */
    boolean isValid();
}
