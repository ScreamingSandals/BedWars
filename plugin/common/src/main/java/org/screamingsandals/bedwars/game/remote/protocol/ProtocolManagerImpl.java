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

package org.screamingsandals.bedwars.game.remote.protocol;

import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.screamingsandals.bedwars.BedWarsPlugin;
import org.screamingsandals.bedwars.config.MainConfig;
import org.screamingsandals.bedwars.game.remote.protocol.messaging.BungeeCordMessenger;
import org.screamingsandals.bedwars.game.remote.protocol.messaging.DummyMessenger;
import org.screamingsandals.bedwars.game.remote.protocol.messaging.Messenger;
import org.screamingsandals.bedwars.game.remote.protocol.messaging.SocketMessenger;
import org.screamingsandals.bedwars.game.remote.protocol.packets.Packet;
import org.screamingsandals.lib.CustomPayload;
import org.screamingsandals.lib.Server;
import org.screamingsandals.lib.event.EventManager;
import org.screamingsandals.lib.plugin.ServiceManager;
import org.screamingsandals.lib.tasker.Tasker;
import org.screamingsandals.lib.utils.Preconditions;
import org.screamingsandals.lib.utils.ProxyType;
import org.screamingsandals.lib.utils.annotations.Service;
import org.screamingsandals.lib.utils.annotations.methods.OnPostEnable;
import org.screamingsandals.lib.utils.annotations.methods.OnPreDisable;
import org.screamingsandals.lib.utils.logger.LoggerWrapper;

@Service
@RequiredArgsConstructor
public class ProtocolManagerImpl extends ProtocolManager {
    private final @NotNull LoggerWrapper logger;

    private CustomPayload.@Nullable Registration registration;
    private @Nullable Messenger messenger;

    public static @NotNull ProtocolManagerImpl getInstance() {
        return ServiceManager.get(ProtocolManagerImpl.class);
    }

    @OnPostEnable
    public void onPostEnable() {
        var communicationType = MainConfig.getInstance().node("bungee", "communication", "type").getString("bungee");
        switch (communicationType) {
            case "bungee": {
                if (Server.getProxyType() != ProxyType.NONE) {
                    registration = CustomPayload.registerIncomingChannel("BungeeCord", (player, bytes) -> {
                        try {
                            var transformedBytes = getMessenger().incomingPacketTransformer(bytes);
                            if (transformedBytes == null) {
                                return;
                            }

                            processIncoming(transformedBytes);
                        } catch (Exception e) {
                            logger.error("Error while receiving message using BungeeCord plugin messaging channel", e);
                        }
                    });
                    messenger = new BungeeCordMessenger(payload -> CustomPayload.send("BungeeCord", payload));
                    if (Server.getProxyType() == ProxyType.VELOCITY) {
                        logger.warn(
                                "Velocity does not implement the BungeeCord plugin messaging channel the same way as BungeeCord does," +
                                    " which may result in important messages not being delivered. Consider switching to \"socket\" communication"
                        );
                    }
                }
                break;
            }
            case "socket": {
                var host = MainConfig.getInstance().node("bungee", "communication", "socket", "host").getString("localhost");
                var port = MainConfig.getInstance().node("bungee", "communication", "socket", "port").getInt(9000);
                messenger = new SocketMessenger(host, port, BedWarsPlugin.getInstance().getServerName(), bytes -> {
                    try {
                        processIncoming(bytes);
                    } catch (Exception e) {
                        logger.error("Error while receiving message using standard socket", e);
                    }
                }, Tasker::runAsync);
                break;
            }
            default: {
                logger.info("Unknown communication type: {}! Disabling...", communicationType);
                messenger = DummyMessenger.INSTANCE;
                break;
            }
        }

    }

    @OnPreDisable
    public void onPreDisable() {
        if (registration != null) {
            CustomPayload.unregisterIncomingChannel(registration);
            registration = null;
        }
        messenger = null;
    }

    @Override
    protected void receivePacket0(@NotNull Packet packet) {
        EventManager.fire(new PacketReceivedEvent(packet));
    }

    @Override
    protected @NotNull Messenger getMessenger() {
        return Preconditions.checkNotNull(messenger, "Messenger has not been constructed yet or communication is disabled!");
    }
}
