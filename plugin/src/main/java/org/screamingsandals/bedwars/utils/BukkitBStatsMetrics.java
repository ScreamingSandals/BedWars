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

package org.screamingsandals.bedwars.utils;

import lombok.experimental.UtilityClass;
import org.bstats.bukkit.Metrics;
import org.bstats.charts.SimplePie;
import org.bukkit.plugin.java.JavaPlugin;
import org.screamingsandals.bedwars.BedWarsPlugin;
import org.screamingsandals.bedwars.VersionInfo;
import org.screamingsandals.bedwars.premium.PremiumBedwars;
import org.screamingsandals.lib.utils.annotations.Service;
import org.screamingsandals.lib.utils.annotations.methods.OnPostEnable;

@Service
@UtilityClass
public class BukkitBStatsMetrics {
    public final int PLUGIN_ID = 7147;

    @OnPostEnable
    public void onPostEnable() {
        var metrics = new Metrics(BedWarsPlugin.getInstance().as(JavaPlugin.class), PLUGIN_ID);
        metrics.addCustomChart(new SimplePie("edition", () -> PremiumBedwars.isPremium() ? "Premium" : "Free"));
        metrics.addCustomChart(new SimplePie("build_number", () -> VersionInfo.BUILD_NUMBER));
    }
}
