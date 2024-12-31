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
import org.screamingsandals.bedwars.api.game.Game;

import java.util.List;

import static org.screamingsandals.bedwars.lib.lang.I18n.i18n;

public class JoinCommand extends BaseCommand {

    public JoinCommand() {
        super("join", JOIN_PERMISSION, false, Main.getConfigurator().config.getBoolean("default-permissions.join"));
    }

    @Override
    public boolean execute(CommandSender sender, List<String> args) {
        Player player = (Player) sender;
        if (Main.isPlayerInGame(player)) {
            player.sendMessage(i18n("you_are_already_in_some_game"));
            return true;
        }

        if (args.size() >= 1) {
            String arenaN = args.get(0);
            if (Main.isGameExists(arenaN)) {
                Main.getGame(arenaN).joinToGame(player);
            } else {
                player.sendMessage(i18n("no_arena_found"));
            }
        } else {
            final Game game = Main.getInstance().getRandomWaitingGameForBungeeMode();
            if (game != null) {
                game.joinToGame(player);
                return true;
            }
            player.sendMessage(i18n("there_is_no_empty_game"));
            return true;
        }
        return true;
    }

    @Override
    public void completeTab(List<String> completion, CommandSender sender, List<String> args) {
        if (args.size() == 1) {
            completion.addAll(Main.getGameNames());
        }
    }

}
