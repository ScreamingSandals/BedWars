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

package org.screamingsandals.bedwars.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServerListPingEvent;
import org.screamingsandals.bedwars.Main;
import org.screamingsandals.bedwars.game.Game;

public class BungeeMotdListener implements Listener {

    @EventHandler
    public void onServerListPing(ServerListPingEvent slpe) {
        if (Main.getGameNames().isEmpty()) {
            return;
        }

        Game game = Main.getGame(Main.getGameNames().get(0));

        if (game == null) {
            return;
        }

        String string = null;

        switch (game.getStatus()) {
            case DISABLED:
                string = Main.getConfigurator().config.getString("bungee.motd.disabled");
                break;
            case GAME_END_CELEBRATING:
            case RUNNING:
                string = Main.getConfigurator().config.getString("bungee.motd.running");
                break;
            case REBUILDING:
                string = Main.getConfigurator().config.getString("bungee.motd.rebuilding");
                break;
            case WAITING:
                if (game.countPlayers() >= game.getMaxPlayers()) {
                    string = Main.getConfigurator().config.getString("bungee.motd.waiting_full");
                } else {
                    string = Main.getConfigurator().config.getString("bungee.motd.waiting");
                }
                break;
        }

        if (string == null) {
            return; // WTF??
        }

        slpe.setMotd(string.replace("%name%", game.getName()).replace("%current%", Integer.toString(game.countPlayers())).replace("%max%", Integer.toString(game.getMaxPlayers())));
    }
}
