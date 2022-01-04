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
