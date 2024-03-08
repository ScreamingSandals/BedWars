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

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.screamingsandals.bedwars.game.remote.Constants;
import org.screamingsandals.lib.CustomPayload;
import org.screamingsandals.lib.Server;
import org.screamingsandals.lib.event.EventManager;
import org.screamingsandals.lib.plugin.ServiceManager;
import org.screamingsandals.lib.utils.ProxyType;
import org.screamingsandals.lib.utils.annotations.Service;
import org.screamingsandals.lib.utils.annotations.methods.OnPostEnable;
import org.screamingsandals.lib.utils.annotations.methods.OnPreDisable;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Map;
import java.util.stream.Collectors;

@Service
// TODO: custom sockets and redis
public class ProtocolManager {
    private CustomPayload.@Nullable Registration registration;

    private final Map<Class<? extends Packet>, Integer> packet2IntMap = Map.ofEntries(
            Map.entry(JoinGamePacket.class, Constants.JOIN_GAME_PACKET_ID)
    );

    private final Map<Integer, Class<? extends Packet>> int2PacketMap = packet2IntMap
            .entrySet()
            .stream()
            .collect(Collectors.toUnmodifiableMap(Map.Entry::getValue, Map.Entry::getKey));

    public static @NotNull ProtocolManager getInstance() {
        return ServiceManager.get(ProtocolManager.class);
    }

    @OnPostEnable
    public void onPostEnable() {
        if (Server.getProxyType() != ProxyType.NONE) {
            registration = CustomPayload.registerIncomingChannel("BungeeCord", (player, bytes) -> {
                try {
                    var outerIn = new DataInputStream(new ByteArrayInputStream(bytes));
                    if (!Constants.MESSAGING_CHANNEL.equals(outerIn.readUTF())) {
                        return;
                    }

                    var in = new DataInputStream(new ByteArrayInputStream(outerIn.readNBytes(outerIn.readShort())));

                    int packetId = in.readInt();
                    var clazz = int2PacketMap.get(packetId);
                    if (clazz == null) {
                        throw new RuntimeException("Unknown BedWars packet ID " + packetId);
                    }
                    var packet = clazz.getConstructor().newInstance();
                    packet.read(in);

                    EventManager.fire(new PacketReceivedEvent(packet));
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
        }
    }

    public void sendPacket(@NotNull String server, @NotNull Packet packet) throws IOException {
        var out = new ByteArrayOutputStream();
        var dout = new DataOutputStream(out);

        Integer id = packet2IntMap.get(packet.getClass());
        if (id == null) {
            throw new IllegalArgumentException("Unknown packet passed: " + packet.getClass().getName());
        }
        dout.writeInt(id);
        packet.write(dout);

        var bout = new ByteArrayOutputStream();
        var bungeeDout = new DataOutputStream(bout);
        bungeeDout.writeUTF("Forward");
        bungeeDout.writeUTF(server);
        bungeeDout.writeUTF(Constants.MESSAGING_CHANNEL);
        var packetBytes = out.toByteArray();
        bungeeDout.writeShort(packetBytes.length);
        bungeeDout.write(packetBytes);

        CustomPayload.send("BungeeCord", bout.toByteArray());
    }
}
