package org.screamingsandals.bedwars.api;

import org.jetbrains.annotations.ApiStatus;

/**
 * <p>Abstract team color API.</p>
 *
 * @author ScreamingSandals
 */
@ApiStatus.NonExtendable
public interface TeamColor {
    /**
     * <p>Gets the color name.</p>
     *
     * @return the color name
     */
    String name();
}
