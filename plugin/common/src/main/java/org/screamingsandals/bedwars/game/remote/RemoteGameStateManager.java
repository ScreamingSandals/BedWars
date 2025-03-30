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
import org.jetbrains.annotations.Nullable;
import org.screamingsandals.bedwars.BedWarsPlugin;
import org.screamingsandals.bedwars.api.config.GameConfigurationContainer;
import org.screamingsandals.bedwars.api.game.GameStatus;
import org.screamingsandals.bedwars.api.game.RemoteGame;
import org.screamingsandals.bedwars.config.MainConfig;
import org.screamingsandals.bedwars.game.GameImpl;
import org.screamingsandals.bedwars.game.GameManagerImpl;
import org.screamingsandals.bedwars.game.remote.protocol.PacketReceivedEvent;
import org.screamingsandals.bedwars.game.remote.protocol.ProtocolManagerImpl;
import org.screamingsandals.bedwars.game.remote.protocol.messaging.IgnoreCapableMessenger;
import org.screamingsandals.bedwars.game.remote.protocol.packets.GameListPacket;
import org.screamingsandals.bedwars.game.remote.protocol.packets.GameStatePacket;
import org.screamingsandals.bedwars.game.remote.protocol.packets.GameStateSubscribePacket;
import org.screamingsandals.bedwars.game.remote.protocol.packets.HelloPacket;
import org.screamingsandals.bedwars.utils.SignUtils;
import org.screamingsandals.lib.Server;
import org.screamingsandals.lib.event.OnEvent;
import org.screamingsandals.lib.plugin.ServiceManager;
import org.screamingsandals.lib.tasker.DefaultThreads;
import org.screamingsandals.lib.tasker.Tasker;
import org.screamingsandals.lib.tasker.TaskerTime;
import org.screamingsandals.lib.tasker.task.Task;
import org.screamingsandals.lib.utils.ProxyType;
import org.screamingsandals.lib.utils.annotations.Service;
import org.screamingsandals.lib.utils.annotations.methods.OnPostEnable;
import org.screamingsandals.lib.utils.annotations.methods.OnPreDisable;
import org.screamingsandals.lib.utils.logger.Logger;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RemoteGameStateManager {
    private final @NotNull Map<@NotNull String, List<GameListPacket.@NotNull GameEntry>> knownGames = new HashMap<>();
    private final @NotNull GameManagerImpl gameManager;
    private final @NotNull Map<@NotNull UUID, List<@NotNull String>> subscriptions = new HashMap<>();
    private final @NotNull List<@NotNull Subscription> mySubscriptions = new ArrayList<>();
    private final @NotNull Logger logger;

    private boolean broadcastStateChangesToEveryone;
    private boolean preventStateChangeSubscribing;
    private boolean preventIncomingStateChangeProcessing;
    private volatile @Nullable Task task;

    public static @NotNull RemoteGameStateManager getInstance() {
        return ServiceManager.get(RemoteGameStateManager.class);
    }

    @OnPostEnable
    public void onPostEnable(@NotNull MainConfig config) {
        broadcastStateChangesToEveryone = config.node("bungee", "communication", "broadcast-state-changes-to-everyone").getBoolean();
        preventStateChangeSubscribing = config.node("bungee", "communication", "prevent-state-change-subscribing").getBoolean();
        preventIncomingStateChangeProcessing = config.node("bungee", "communication", "prevent-incoming-state-change-processing").getBoolean();
        var preventSendingHelloPacket = config.node("bungee", "communication", "prevent-sending-hello-packet").getBoolean();

        if (!preventStateChangeSubscribing) {
            Tasker.runAsyncDelayed(() -> {
                GameManagerImpl.getInstance().getRemoteGames()
                        .forEach(remoteGame -> {
                            if (remoteGame.getRemoteGameIdentifier() != null) {
                                subscribe(remoteGame.getRemoteServer(), remoteGame.getRemoteGameIdentifier());
                            }
                        });
            }, 2, TaskerTime.SECONDS);
        }

        if (broadcastStateChangesToEveryone) {
            beginTask();
        }

        if (!preventSendingHelloPacket && Server.getProxyType() != ProxyType.NONE) {
            Tasker.runAsyncDelayed(() -> {
                var name = BedWarsPlugin.getInstance().getServerName();
                if (name == null) {
                    return;
                }

                try {
                    ProtocolManagerImpl.getInstance().broadcastPacket(new HelloPacket(name));
                } catch (IOException e) {
                    logger.error("Could not send hello packet to other server", e);
                }
            }, 2, TaskerTime.SECONDS);
        }

        if (preventIncomingStateChangeProcessing) {
            // We do not process them anyway, so if we can save the traffic a little, why not
            var messenger = ProtocolManagerImpl.getInstance().getMessengerOrNull();
            if (messenger instanceof IgnoreCapableMessenger) {
                ((IgnoreCapableMessenger) messenger).ignoreIncomingState();
            }
        }
    }

    @OnPreDisable
    public void onPreDisable() {
        var task = this.task;
        if (task != null) {
            task.cancel();
            this.task = null;
        }

        if (!preventStateChangeSubscribing) {
            List.copyOf(mySubscriptions).forEach(subscription -> {
                unsubscribe(subscription.server, subscription.gameIdentifier);
            });
        }
    }

    private void beginTask() {
        if (task != null) {
            return;
        }

        var protocolManager = ProtocolManagerImpl.getInstance();

        task = Tasker.runAsyncRepeatedly(taskBase -> {
            if (!broadcastStateChangesToEveryone && subscriptions.isEmpty()) {
                taskBase.cancel();
                this.task = null;
                return;
            }

            var serverName = BedWarsPlugin.getInstance().getServerName();
            if (serverName == null) {
                return;
            }

            GameManagerImpl.getInstance().getLocalGames().forEach(game -> {
                @Nullable List<@NotNull String> subscribingServers;
                if (!broadcastStateChangesToEveryone) {
                    subscribingServers = subscriptions.get(game.getUuid());
                    if (subscribingServers == null) {
                        return;
                    }
                } else {
                    subscribingServers = null;
                }

                var gameState = buildStatePacket(game, serverName);

                try {
                    if (subscribingServers != null) {
                        for (var server : subscribingServers) {
                            protocolManager.sendPacket(server, gameState);
                        }
                    } else {
                        protocolManager.broadcastPacket(gameState);
                    }
                } catch (IOException e) {
                    logger.error("An error occurred while trying to send GameStatePacket", e);
                }
            });
        }, 1, TaskerTime.SECONDS);
    }

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
                            remoteGame instanceof RemoteGameImpl
                                    && gameState.getServer().equals(remoteGame.getRemoteServer())
                                    && remoteGame.getRemoteGameIdentifier() != null
                                    && gameState.getName().equals(remoteGame.getRemoteGameIdentifier()) || gameState.getUuid().toString().equals(remoteGame.getRemoteGameIdentifier())
                    )
                    .forEach(remoteGame -> {
                        //noinspection DataFlowIssue
                        ((RemoteGameImpl) remoteGame).setState(gameState);
                        Tasker.run(DefaultThreads.GLOBAL_THREAD, () -> SignUtils.updateSigns(remoteGame));
                    });
        } else if (!broadcastStateChangesToEveryone && packet instanceof GameStateSubscribePacket) {
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
                        if (this.task == null) {
                            beginTask();
                        }
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

    public static @NotNull GameStatePacket buildStatePacket(@NotNull GameImpl game, @NotNull String serverName) {
        int maxTime;
        switch (game.getStatus()) {
            case WAITING:
                maxTime = game.getLobbyCountdown();
                break;
            case GAME_END_CELEBRATING:
                maxTime = game.getPostGameWaiting();
                break;
            default:
                maxTime = game.getGameTime();
        }

        return GameStatePacket.builder()
                .server(serverName)
                .uuid(game.getUuid())
                .name(game.getName())
                .displayName(game.getDisplayNameComponent().toJavaJson())
                .onlinePlayers(game.countConnectedPlayers())
                .alivePlayers(game.countAlive())
                .maxPlayers(game.getMaxPlayers())
                .minPlayers(game.getMinPlayers())
                .teams(game.getTeams().size())
                .aliveTeams(game.getTeamsAlive().size())
                .state(game.getStatus().name())
                .players(
                        game.getPlayers().stream()
                                .filter(p -> !p.isSpectator())
                                .map(p -> new GameStatePacket.PlayerEntry(p.getUuid(), p.getName()))
                                .collect(Collectors.toList())
                )
                .elapsed(maxTime - game.getTimeLeft())
                .maxTime(maxTime)
                .isTimeMoving(
                        game.getStatus() != GameStatus.WAITING
                                || (game.countConnectedPlayers() >= game.getMinPlayers()
                                && (game.countActiveTeams() > 1 || game.getConfigurationContainer().getOrDefault(GameConfigurationContainer.JOIN_RANDOM_TEAM_AFTER_LOBBY, false))
                        )
                )
                .build();
    }
}
