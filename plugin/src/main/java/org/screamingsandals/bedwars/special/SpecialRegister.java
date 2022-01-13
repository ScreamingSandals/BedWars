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

package org.screamingsandals.bedwars.special;

import org.bukkit.plugin.Plugin;
import org.screamingsandals.bedwars.special.listener.*;

public class SpecialRegister {

    public static void onEnable(Plugin plugin) {
        plugin.getServer().getPluginManager().registerEvents(new ArrowBlockerListener(), plugin);
        plugin.getServer().getPluginManager().registerEvents(new GolemListener(), plugin);
        plugin.getServer().getPluginManager().registerEvents(new LuckyBlockAddonListener(), plugin);
        plugin.getServer().getPluginManager().registerEvents(new MagnetShoesListener(), plugin);
        plugin.getServer().getPluginManager().registerEvents(new ProtectionWallListener(), plugin);
        plugin.getServer().getPluginManager().registerEvents(new RescuePlatformListener(), plugin);
        plugin.getServer().getPluginManager().registerEvents(new TeamChestListener(), plugin);
        plugin.getServer().getPluginManager().registerEvents(new ThrowableFireballListener(), plugin);
        plugin.getServer().getPluginManager().registerEvents(new TNTSheepListener(), plugin);
        plugin.getServer().getPluginManager().registerEvents(new TrackerListener(), plugin);
        plugin.getServer().getPluginManager().registerEvents(new TrapListener(), plugin);
        plugin.getServer().getPluginManager().registerEvents(new WarpPowderListener(), plugin);
        plugin.getServer().getPluginManager().registerEvents(new AutoIgniteableTNTListener(), plugin);
    }

}
