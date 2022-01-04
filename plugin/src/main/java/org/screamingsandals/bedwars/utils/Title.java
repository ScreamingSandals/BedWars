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

import org.screamingsandals.bedwars.Main;
import org.bukkit.entity.Player;

public class Title {
    public static void send(Player player, String title, String subtitle) {
        if (!Main.getConfigurator().config.getBoolean("title.enabled")) {
            return;
        }

        int fadeIn = Main.getConfigurator().config.getInt("title.fadeIn");
        int stay = Main.getConfigurator().config.getInt("title.stay");
        int fadeOut = Main.getConfigurator().config.getInt("title.fadeOut");

        org.screamingsandals.bedwars.lib.nms.title.Title.sendTitle(player, title, subtitle, fadeIn, stay, fadeOut);
    }
}
