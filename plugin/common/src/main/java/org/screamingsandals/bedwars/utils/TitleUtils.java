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

package org.screamingsandals.bedwars.utils;

import lombok.experimental.UtilityClass;
import org.screamingsandals.bedwars.config.MainConfig;
import org.screamingsandals.lib.spectator.title.TimesProvider;

import java.time.Duration;

@UtilityClass
public class TitleUtils {
    public TimesProvider defaultTimes() {
        int fadeIn = MainConfig.getInstance().node("title", "fadeIn").getInt(0);
        int stay = MainConfig.getInstance().node("title", "stay").getInt(20);
        int fadeOut = MainConfig.getInstance().node("title", "fadeOut").getInt(0);

        return TimesProvider.times(
                Duration.ofMillis(fadeIn * 50L),
                Duration.ofMillis(stay * 50L),
                Duration.ofMillis(fadeOut * 50L)
        );
    }
}
