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

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.screamingsandals.bedwars.BedWarsPlugin;
import org.screamingsandals.bedwars.config.MainConfig;
import org.screamingsandals.bedwars.game.GameManagerImpl;
import org.screamingsandals.bedwars.game.remote.protocol.PacketReceivedEvent;
import org.screamingsandals.bedwars.game.remote.protocol.ProtocolManagerImpl;
import org.screamingsandals.bedwars.game.remote.protocol.packets.GameListPacket;
import org.screamingsandals.bedwars.game.remote.protocol.packets.GameStatePacket;
import org.screamingsandals.bedwars.game.remote.protocol.packets.GameStateSubscribePacket;
import org.screamingsandals.lib.event.OnEvent;
import org.screamingsandals.lib.plugin.ServiceManager;
import org.screamingsandals.lib.tasker.Tasker;
import org.screamingsandals.lib.tasker.TaskerTime;
import org.screamingsandals.lib.utils.annotations.Service;
import org.screamingsandals.lib.utils.annotations.methods.OnPostEnable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RemoteGameStateManager {
    private final @NotNull Map<@NotNull String, List<GameListPacket.@NotNull GameEntry>> knownGames = new HashMap<>();
    private final @NotNull GameManagerImpl gameManager;
    private final @NotNull Map<@NotNull UUID, List<@NotNull String>> subscriptions = new HashMap<>();
    private final @NotNull List<@NotNull Subscription> mySubscriptions = new ArrayList<>();

    private boolean broadcastStateChangesToEveryone;
    private boolean preventStateChangeSubscribing;
    private boolean preventIncomingStateChangeProcessing;

    public static @NotNull RemoteGameStateManager getInstance() {
        return ServiceManager.get(RemoteGameStateManager.class);
    }

    @OnPostEnable
    public void onPostEnable(@NotNull MainConfig config) {
        broadcastStateChangesToEveryone = config.node("bungee", "communication", "broadcast-state-changes-to-everyone").getBoolean();
        preventStateChangeSubscribing = config.node("bungee", "communication", "prevent-state-change-subscribing").getBoolean();
        preventIncomingStateChangeProcessing = config.node("bungee", "communication", "prevent-incoming-state-change-processing").getBoolean();

        if (preventStateChangeSubscribing) {
            Tasker.runAsyncDelayed(() -> {
                GameManagerImpl.getInstance().getRemoteGames()
                        .forEach(remoteGame -> {
                            if (remoteGame.getRemoteGameIdentifier() != null) {
                                subscribe(remoteGame.getRemoteServer(), remoteGame.getRemoteGameIdentifier());
                            }
                        });
            }, 2, TaskerTime.SECONDS);
        }
    }

    // TODO: if any subscriber is registered or broadcastStateChangesToEveryone is enabled, start a task

    @OnEvent
    public void onDataReceived(@NotNull PacketReceivedEvent event) {
        var packet = event.getPacket();

        if (packet instanceof GameListPacket) {
            knownGames.put(((GameListPacket) packet).getServer(), ((GameListPacket) packet).getGames());
        } else if (!preventIncomingStateChangeProcessing && packet instanceof GameStatePacket) {
            var gameState = (GameStatePacket) packet;
            gameManager.getRemoteGames()
                    .stream()
                    .filter(remoteGame ->
                            gameState.getServer().equals(remoteGame.getRemoteServer())
                                    && remoteGame.getRemoteGameIdentifier() != null
                                    && gameState.getName().equals(remoteGame.getRemoteGameIdentifier()) || gameState.getUuid().toString().equals(remoteGame.getRemoteGameIdentifier())
                    )
                    .forEach(remoteGame -> remoteGame.setState(gameState));
        } else if (packet instanceof GameStateSubscribePacket) {
            var localGame = gameManager.getLocalGame(((GameStateSubscribePacket) packet).getGameIdentifier());
            if (localGame.isPresent()) {
                var requestingServer = ((GameStateSubscribePacket) packet).getRequestingServer();
                if (!((GameStateSubscribePacket) packet).isSubscribe()) {
                    var list = subscriptions.get(localGame.get().getUuid());
                    if (list != null) {
                        list.remove(requestingServer);

                        if (list.isEmpty()) {
                            subscriptions.remove(localGame.get().getUuid());
                        }
                    }
                } else {
                    var list = subscriptions.computeIfAbsent(localGame.get().getUuid(), uuid -> new ArrayList<>());
                    if (!list.contains(requestingServer)) {
                        list.add(requestingServer);
                    }
                }
            }
        }
    }

    public @NotNull List<GameListPacket.@NotNull GameEntry> getKnownGames(@NotNull String server) {
        return knownGames.get(server);
    }

    public boolean hasKnownGamesForServer(@NotNull String server) {
        return knownGames.containsKey(server);
    }

    public void subscribe(@NotNull String server, @NotNull String gameIdentifier) {
        if (preventStateChangeSubscribing) {
            return;
        }

        var subscription = new Subscription(server, gameIdentifier);
        if (mySubscriptions.contains(subscription)) {
            return;
        }

        mySubscriptions.add(subscription);

        try {
            var thisServer = Objects.requireNonNull(BedWarsPlugin.getInstance().getServerName(), "This server does not know its name yet!");

            ProtocolManagerImpl.getInstance().sendPacket(server, new GameStateSubscribePacket(thisServer, gameIdentifier, true));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void unsubscribe(@NotNull String server, @NotNull String gameIdentifier) {
        if (preventStateChangeSubscribing) {
            return;
        }

        var subscription = new Subscription(server, gameIdentifier);
        if (!mySubscriptions.contains(subscription)) {
            return;
        }

        mySubscriptions.remove(subscription);

        try {
            var thisServer = Objects.requireNonNull(BedWarsPlugin.getInstance().getServerName(), "This server does not know its name yet!");

            ProtocolManagerImpl.getInstance().sendPacket(server, new GameStateSubscribePacket(thisServer, gameIdentifier, false));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Data
    public static class Subscription {
        private final @NotNull String server;
        private final @NotNull String gameIdentifier;
    }
}
