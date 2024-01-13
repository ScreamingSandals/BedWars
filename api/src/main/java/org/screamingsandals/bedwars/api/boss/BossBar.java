/*
 * Copyright (C) 2024 ScreamingSandals
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

package org.screamingsandals.bedwars.api.boss;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.screamingsandals.lib.api.Wrapper;

/**
 * @author ScreamingSandals
 */
@ApiStatus.NonExtendable
public interface BossBar extends StatusBar {
    /**
     * @return current message
     */
	Wrapper getMessage();

    /**
     * @param message
     */
	void setMessage(@Nullable Object message);

    /**
     * @return color
     */
    Wrapper getColor();

    /**
     * @param color
     */
    void setColor(@NotNull Object color);

    /**
     * @return style
     */
    Wrapper getStyle();

    /**
     * @param style
     */
    void setStyle(@NotNull Object style);

}
