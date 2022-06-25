package org.screamingsandals.bedwars.api.game.target;

/**
 * @author ScreamingSandals
 * @since 0.3.0
 */
public interface TargetCountdown extends Target {

    /**
     * Gets the set countdown to the target invalidation.
     *
     * @return the countdown in seconds
     * @since 0.3.0
     */
    int getCountdown();

    /**
     * Gets the current remaining time until the target is invalidated.
     *
     * @return the current remaining time in seconds
     * @since 0.3.0
     */
    int getRemainingTime();
}
