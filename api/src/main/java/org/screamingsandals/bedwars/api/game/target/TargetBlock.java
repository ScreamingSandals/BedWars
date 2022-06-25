package org.screamingsandals.bedwars.api.game.target;

import org.jetbrains.annotations.NotNull;
import org.screamingsandals.lib.utils.Wrapper;

/**
 * @author ScreamingSandals
 * @since 0.3.0
 */
public interface TargetBlock extends Target {

    /**
     * Gets the team's target block (e.g. bed) location.
     *
     * @return the target block location
     * @since 0.3.0
     */
    @NotNull
    Wrapper getTargetBlock();

    /**
     * Checks if the block is Respawn Anchor (or another chargeable block supported by the plugin) and if it's empty (not charged).
     *
     * @return true if the block is empty; false otherwise
     * @since 0.3.0
     */
    boolean isEmpty();

    /**
     * Gets charge of the Respawn Anchor or other supported chargeable block; if the block is not considered to be chargeable, 1 is returned if the block is intact, 0 otherwise.
     *
     * @return the charge
     * @since 0.3.0
     */
    int getCharge();
}
