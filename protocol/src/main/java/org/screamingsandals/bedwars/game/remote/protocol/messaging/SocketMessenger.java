/*
 * Copyright (C) 2025 ScreamingSandals
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

package org.screamingsandals.bedwars.game.remote.protocol.messaging;

import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.screamingsandals.bedwars.game.remote.protocol.PacketUtils;
import org.screamingsandals.bedwars.game.remote.protocol.sockets.Action;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.Socket;
import java.util.function.Consumer;

public final class SocketMessenger implements ServerNameAwareMessenger, IgnoreCapableMessenger {
    private final @NotNull String server;
    private final int port;
    private final @NotNull Consumer<byte @NotNull []> packetHandler;
    private final @NotNull Consumer<@NotNull Runnable> threadCreator;
    private @Nullable String identifier;
    private @Nullable Socket clientSocket;
    private @Nullable DataOutputStream out;
    private @Nullable DataInputStream in;
    @Setter
    private boolean running = true;

    public SocketMessenger(@NotNull String server, int port, @Nullable String identifier, @NotNull Consumer<byte @NotNull []> packetHandler, @NotNull Consumer<@NotNull Runnable> threadCreator) {
        this.server = server;
        this.port = port;
        this.identifier = identifier;
        this.packetHandler = packetHandler;
        this.threadCreator = threadCreator;
    }

    public void startConnection() throws IOException {
        //noinspection resource
        ensureConnection();
    }

    private @NotNull DataOutputStream ensureConnection() throws IOException {
        if (clientSocket != null && clientSocket.isConnected() && !clientSocket.isClosed()) {
            if (out != null) {
                return out;
            }
            stopConnection();
        }

        if (identifier == null) {
            throw new IllegalArgumentException(
                    "Cannot start communication if the server name is unknown! Connect any player to this server, so it can fetch its identifier"
            );
        }

        clientSocket = new Socket(server, port);
        out = new DataOutputStream(clientSocket.getOutputStream());
        in = new DataInputStream(new BufferedInputStream(clientSocket.getInputStream()));

        // Kind of Handshake
        PacketUtils.writeStandardUTF(out, identifier);
        out.flush();

        running = true;
        threadCreator.accept(() -> {
            try {
                while (running && clientSocket.isConnected() && !clientSocket.isClosed()) {
                    try {
                        var size = in.readInt();
                        packetHandler.accept(in.readNBytes(size));
                    } catch (EOFException e) {
                        throw new RuntimeException(e);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            } finally {
                try {
                    stopConnection();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        return out;
    }

    private void stopConnection() throws IOException {
        running = false;
        if (out != null) {
            out.close();
            out = null;
        }
        if (in != null) {
            in.close();
            in = null;
        }
        if (clientSocket != null) {
            clientSocket.close();
            clientSocket = null;
        }
    }

    @Override
    public synchronized void sendPacket(@NotNull String server, byte @NotNull [] payload) throws IOException {
        var out = ensureConnection();
        out.writeByte(Action.SEND_PACKET.getId());
        PacketUtils.writeStandardUTF(out, server);
        out.writeInt(payload.length);
        out.write(payload);
        out.flush();
    }

    @Override
    public synchronized void broadcastPacket(byte @NotNull [] payload) throws IOException {
        var out = ensureConnection();
        out.writeByte(Action.BROADCAST_PACKET.getId());
        out.writeInt(payload.length);
        out.write(payload);
        out.flush();
    }

    @Override
    public void setServerName(@NotNull String serverName) {
        if (serverName.equals(identifier)) {
            return;
        }

        identifier = serverName;
        if (clientSocket != null) {
            try {
                stopConnection();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                ensureConnection();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void ignoreIncomingState() {
        try {
            var out = ensureConnection();
            out.writeByte(Action.IGNORE_INCOMING_GAME_STATE.getId());
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void stopIgnoreIncomingState() {
        try {
            var out = ensureConnection();
            out.writeByte(Action.STOP_IGNORING_INCOMING_GAME_STATE.getId());
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
