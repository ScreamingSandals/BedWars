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

package org.screamingsandals.bedwars.game.remote;

import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.screamingsandals.bedwars.game.GameManagerImpl;
import org.screamingsandals.bedwars.game.remote.protocol.PacketReceivedEvent;
import org.screamingsandals.bedwars.game.remote.protocol.packets.GameListPacket;
import org.screamingsandals.bedwars.game.remote.protocol.packets.GameStatePacket;
import org.screamingsandals.lib.event.OnEvent;
import org.screamingsandals.lib.plugin.ServiceManager;
import org.screamingsandals.lib.utils.annotations.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class RemoteGameStateStorage {
    private final @NotNull Map<@NotNull String, List<GameListPacket.@NotNull GameEntry>> knownGames = new HashMap<>();
    private final @NotNull GameManagerImpl gameManager;

    public static @NotNull RemoteGameStateStorage getInstance() {
        return ServiceManager.get(RemoteGameStateStorage.class);
    }

    @OnEvent
    public void onDataReceived(@NotNull PacketReceivedEvent event) {
        var packet = event.getPacket();

        if (packet instanceof GameListPacket) {
            knownGames.put(((GameListPacket) packet).getServer(), ((GameListPacket) packet).getGames());
        } else if (packet instanceof GameStatePacket) {
            var gameState = (GameStatePacket) packet;
            gameManager.getRemoteGames()
                    .stream()
                    .filter(remoteGame ->
                            gameState.getServer().equals(remoteGame.getRemoteServer())
                                    && remoteGame.getRemoteGameIdentifier() != null
                                    && gameState.getName().equals(remoteGame.getRemoteGameIdentifier()) || gameState.getUuid().toString().equals(remoteGame.getRemoteGameIdentifier())
                    )
                    .forEach(remoteGame -> remoteGame.setState(gameState));
        }
    }

    public @NotNull List<GameListPacket.@NotNull GameEntry> getKnownGames(@NotNull String server) {
        return knownGames.get(server);
    }

    public boolean hasKnownGamesForServer(@NotNull String server) {
        return knownGames.containsKey(server);
    }
}
