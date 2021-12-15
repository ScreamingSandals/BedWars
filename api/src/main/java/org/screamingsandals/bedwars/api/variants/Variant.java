package org.screamingsandals.bedwars.api.variants;

import org.screamingsandals.bedwars.api.config.ConfigurationContainer;

/**
 * Represents a game variant (Classic BedWars, "Certain popular server" BedWars, etc.)
 *
 * @author ScreamingSandals
 * @since 0.3.0
 */
public interface Variant {

    /**
     *
     * @return name of this variant
     * @since 0.3.0
     */
    String getName();

    /**
     * Returns configuration container for all games inheriting this variant
     *
     * @return game's configuration container
     * @since 0.3.0
     */
    ConfigurationContainer getConfigurationContainer();

}
