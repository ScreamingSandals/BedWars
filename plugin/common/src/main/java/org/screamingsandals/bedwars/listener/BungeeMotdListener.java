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

package org.screamingsandals.bedwars.listener;

import org.jetbrains.annotations.NotNull;
import org.screamingsandals.bedwars.api.game.Game;
import org.screamingsandals.bedwars.config.MainConfig;
import org.screamingsandals.bedwars.game.GameImpl;
import org.screamingsandals.bedwars.game.GameManagerImpl;
import org.screamingsandals.lib.event.OnEvent;
import org.screamingsandals.lib.event.server.ServerListPingEvent;
import org.screamingsandals.lib.spectator.Component;
import org.screamingsandals.lib.utils.annotations.Service;
import org.screamingsandals.lib.utils.annotations.methods.ShouldRunControllable;

@Service
public class BungeeMotdListener {
    @ShouldRunControllable
    public boolean isEnabled() {
        return MainConfig.getInstance().node("bungee", "enabled").getBoolean() && MainConfig.getInstance().node("bungee", "motd", "enabled").getBoolean();
    }

    @OnEvent
    public void onServerListPing(@NotNull ServerListPingEvent slpe) {
        var games = GameManagerImpl.getInstance().getLocalGames();
        if (games.isEmpty()) {
            return;
        }

        Game gameA = null;
        if (GameManagerImpl.getInstance().isDoGamePreselection()) {
            gameA = GameManagerImpl.getInstance().getPreselectedGame();
        }
        if (gameA == null) {
            if (MainConfig.getInstance().node("bungee", "random-game-selection", "enabled").getBoolean()) {
                gameA = GameManagerImpl.getInstance().getGameWithHighestPlayers().orElse(null);
            } else {
                gameA = games.get(0);
            }
        }

        if (!(gameA instanceof GameImpl)) {
            return;
        }

        var game = (GameImpl) gameA;

        String string = null;

        switch (game.getStatus()) {
            case DISABLED:
                string = MainConfig.getInstance().node("bungee", "motd", "disabled").getString();
                break;
            case GAME_END_CELEBRATING:
            case RUNNING:
                string = MainConfig.getInstance().node("bungee", "motd", "running").getString();
                break;
            case REBUILDING:
                string = MainConfig.getInstance().node("bungee", "motd", "rebuilding").getString();
                break;
            case WAITING:
                if (game.countPlayers() >= game.getMaxPlayers()) {
                    string = MainConfig.getInstance().node("bungee", "motd", "waiting_full").getString();
                } else {
                    string = MainConfig.getInstance().node("bungee", "motd", "waiting").getString();
                }
                break;
        }

        if (string == null) {
            return; // WTF??
        }

        // TODO: migrate the config to minimessage
        slpe.description(Component.fromLegacy(
                string.replace("%name%", game.getName()).replace("%displayName%", game.getDisplayName() != null ? Component.fromMiniMessage(game.getDisplayName()).toLegacy() : game.getName()).replace("%current%", Integer.toString(game.countPlayers())).replace("%max%", Integer.toString(game.getMaxPlayers()))
        ));
    }
}
