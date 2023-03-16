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

package org.screamingsandals.bedwars.commands;

import org.screamingsandals.bedwars.Main;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

import static org.screamingsandals.bedwars.lib.lang.I18n.i18n;

public class RejoinCommand extends BaseCommand {

    public RejoinCommand() {
        super("rejoin", REJOIN_PERMISSION, false, Main.getConfigurator().config.getBoolean("default-permissions.rejoin"));
    }

    @Override
    public boolean execute(CommandSender sender, List<String> args) {
        Player player = (Player) sender;
        if (Main.isPlayerInGame(player)) {
            player.sendMessage(i18n("you_are_already_in_some_game"));
            return true;
        }

        String name = null;
        if (Main.isPlayerGameProfileRegistered(player)) {
            name = Main.getPlayerGameProfile(player).getLatestGameName();
        }
        if (name == null) {
            player.sendMessage(i18n("you_are_not_in_game_yet"));
        } else {
            if (Main.isGameExists(name)) {
                Main.getGame(name).joinToGame(player);
            } else {
                player.sendMessage(i18n("game_is_gone"));
            }
        }
        return true;
    }

    @Override
    public void completeTab(List<String> completion, CommandSender sender, List<String> args) {
        // Nothing to add.
    }

}
