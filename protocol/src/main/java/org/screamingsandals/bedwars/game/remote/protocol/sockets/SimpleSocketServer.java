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

package org.screamingsandals.bedwars.game.remote.protocol.sockets;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.screamingsandals.bedwars.game.remote.protocol.PacketId;
import org.screamingsandals.bedwars.game.remote.protocol.PacketUtils;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class SimpleSocketServer {
    private boolean running = true;
    private final @NotNull Map<@NotNull String, ClientConnection> clients = new ConcurrentHashMap<>();
    private final @NotNull List<@NotNull String> ignoresIncomingState = Collections.synchronizedList(new ArrayList<>());

    public SimpleSocketServer(int port) throws IOException {
        try (var serverSocket = new ServerSocket(port)) {
            System.out.println("Server started on port " + port);
            while (running) {
                try {
                    var socket = serverSocket.accept();
                    System.out.println("New connection from " + socket.getRemoteSocketAddress());
                    var client = new ClientConnection(socket);
                    new Thread(client).start();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void shutdown() {
        running = false;
    }

    public class ClientConnection implements Runnable {
        private final @NotNull Socket socket;
        private final @NotNull DataOutputStream out;
        private final @NotNull DataInputStream in;
        private @Nullable String identifier;

        public ClientConnection(@NotNull Socket socket) throws IOException {
            this.socket = socket;
            this.out = new DataOutputStream(socket.getOutputStream());
            this.in = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
        }

        public void run() {
            try (in; out; socket) {
                identifier = PacketUtils.readStandardUTF(in);
                var existingEntry = clients.get(identifier);
                if (existingEntry != null && existingEntry.socket.isConnected() && !existingEntry.socket.isClosed()) {
                    throw new RuntimeException("New client used the same identifier as another client which is still online: " + identifier);
                }
                clients.put(identifier, this);
                System.out.println("Client logged in: " + identifier);

                while (running && !socket.isClosed()) {
                    var action = Action.byId(in.readByte());
                    if (action == null) {
                        System.out.println("Received invalid action from " + identifier + ". Kicking...");
                        break;
                    }

                    if (action == Action.IGNORE_INCOMING_GAME_STATE) {
                        if (!ignoresIncomingState.contains(identifier)) {
                            ignoresIncomingState.add(identifier);
                        }
                        continue;
                    } else if (action == Action.STOP_IGNORING_INCOMING_GAME_STATE) {
                        ignoresIncomingState.remove(identifier);
                        continue;
                    }

                    ClientConnection client = null;
                    if (action == Action.SEND_PACKET) {
                        client = clients.get(PacketUtils.readStandardUTF(in));
                        if (client == null || client.socket.isClosed()) {
                            in.readNBytes(in.readInt()); // we have to read the whole packet
                            return;
                        }
                    }

                    var size = in.readInt();
                    if (size == 0) {
                        continue;
                    }

                    var payload = in.readNBytes(size);
                    var isIncomingState = Byte.toUnsignedInt(payload[0]) == PacketId.GAME_STATE.getId();

                    if (client != null) {
                        if (isIncomingState && ignoresIncomingState.contains(client.identifier)) {
                            continue;
                        }

                        client.sendPacket(payload);
                    } else {
                        for (var c : clients.values()) {
                            if (c.socket.isClosed() || (isIncomingState && ignoresIncomingState.contains(c.identifier))) {
                                continue;
                            }

                            c.sendPacket(payload);
                        }
                    }

                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            } finally {
                clients.remove(identifier);
                ignoresIncomingState.remove(identifier);
            }
        }

        public synchronized void sendPacket(byte @NotNull [] payload) throws IOException {
            out.writeInt(payload.length);
            out.write(payload);
            out.flush();
        }
    }

    public static void main(@NotNull String @NotNull [] args) throws IOException {
        if (args.length < 1) {
            System.out.println("Usage: <port>");
            return;
        }

        int port;
        try {
            port = Integer.parseInt(args[0]);
        } catch (NumberFormatException e) {
            System.out.println("Port is supposed to be a number between 0 and 65535!");
            return;
        }

        if (port < 0 || port > 65535) {
            System.out.println("Port is supposed to be a number between 0 and 65535!");
            return;
        }

        new SimpleSocketServer(port);
    }
}
