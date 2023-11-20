/*
 * Copyright (C) 2023 ScreamingSandals
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

import java.util.List;
import java.util.Optional;

/**
 * Manages all BedWars variants
 *
 * @author Screaming Sandals
 * @since 0.3.0
 */
public interface VariantManager {
    Optional<? extends Variant> getVariant(String name);

    List<String> getVariantNames();

    List<? extends Variant> getVariants();

    boolean hasVariant(String name);

    Variant getDefaultVariant();
}
