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

import lombok.AccessLevel;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.screamingsandals.bedwars.game.remote.protocol.messaging.BungeeCordMessenger;
import org.screamingsandals.bedwars.game.remote.protocol.messaging.Messenger;
import org.screamingsandals.bedwars.game.remote.protocol.packets.Packet;
import org.screamingsandals.lib.CustomPayload;
import org.screamingsandals.lib.Server;
import org.screamingsandals.lib.event.EventManager;
import org.screamingsandals.lib.plugin.ServiceManager;
import org.screamingsandals.lib.utils.ProxyType;
import org.screamingsandals.lib.utils.annotations.Service;
import org.screamingsandals.lib.utils.annotations.methods.OnPostEnable;
import org.screamingsandals.lib.utils.annotations.methods.OnPreDisable;

@Service
public class ProtocolManagerImpl extends ProtocolManager {
    private CustomPayload.@Nullable Registration registration;
    @Getter(AccessLevel.PROTECTED)
    private @NotNull Messenger messenger = new BungeeCordMessenger(payload -> CustomPayload.send("BungeeCord", payload));

    public static @NotNull ProtocolManagerImpl getInstance() {
        return ServiceManager.get(ProtocolManagerImpl.class);
    }

    @OnPostEnable
    public void onPostEnable() {
        if (Server.getProxyType() != ProxyType.NONE) {
            registration = CustomPayload.registerIncomingChannel("BungeeCord", (player, bytes) -> {
                try {
                    var transformedBytes = getMessenger().incomingPacketTransformer(bytes);
                    if (transformedBytes == null) {
                        return;
                    }

                    processIncoming(transformedBytes);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }
    }

    @OnPreDisable
    public void onPreDisable() {
        if (registration != null) {
            CustomPayload.unregisterIncomingChannel(registration);
            registration = null;
        }
    }

    @Override
    protected void receivePacket0(@NotNull Packet packet) {
        EventManager.fire(new PacketReceivedEvent(packet));
    }
}
