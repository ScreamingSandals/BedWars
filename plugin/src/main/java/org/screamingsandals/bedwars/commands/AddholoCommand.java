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

package org.screamingsandals.bedwars.commands;

import org.screamingsandals.bedwars.Main;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;

import static org.screamingsandals.bedwars.lib.lang.I18n.i18n;

public class AddholoCommand extends BaseCommand {

    public AddholoCommand() {
        super("addholo", ADMIN_PERMISSION, false, false);
    }

    @Override
    public boolean execute(CommandSender sender, List<String> args) {
        Player player = (Player) sender;
        if (!Main.isHologramsEnabled()) {
            player.sendMessage(i18n("holo_not_enabled"));
        } else {
            if (args.size() >= 1 && args.get(0).equalsIgnoreCase("leaderboard")) {
                Main.getLeaderboardHolograms().addHologramLocation(player.getEyeLocation());
                player.sendMessage(i18n("leaderboard_holo_added"));
            } else {
                Main.getHologramInteraction().addHologramLocation(player.getEyeLocation());
                Main.getHologramInteraction().updateHolograms();
                player.sendMessage(i18n("holo_added"));
            }
        }
        return true;
    }

    @Override
    public void completeTab(List<String> completion, CommandSender sender, List<String> args) {
        if (args.size() == 1) {
            completion.addAll(Arrays.asList("leaderboard", "stats"));
        }
    }

}
