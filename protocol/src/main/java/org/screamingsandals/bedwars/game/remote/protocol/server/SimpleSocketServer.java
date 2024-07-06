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

package org.screamingsandals.bedwars.game.remote.protocol.server;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.screamingsandals.bedwars.game.remote.protocol.PacketUtils;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class SimpleSocketServer {
    private boolean running = true;
    private final Map<String, ClientConnection> clients = new ConcurrentHashMap<>();

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

        public ClientConnection(@NotNull Socket socket) throws IOException {
            this.socket = socket;
            this.out = new DataOutputStream(socket.getOutputStream());
            this.in = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
        }

        public void run() {
            @Nullable String identifier = null;
            try (in; out; socket) {
                 identifier = PacketUtils.readStandardUTF(in);
                var existingEntry = clients.get(identifier);
                if (existingEntry != null && existingEntry.socket.isConnected() && !existingEntry.socket.isClosed()) {
                    throw new RuntimeException("New client used the same identifier as another client which is still online: " + identifier);
                }
                clients.put(identifier, this);
                System.out.println("Client logged in: " + identifier);

                while (running && !socket.isClosed()) {
                    var specificClient = in.readBoolean();
                    ClientConnection client = null;
                    if (specificClient) {
                        client = clients.get(PacketUtils.readStandardUTF(in));
                        if (client == null || client.socket.isClosed()) {
                            in.readNBytes(in.readInt()); // we have to read the whole packet
                            return;
                        }
                    }

                    var size = in.readInt();
                    var payload = in.readNBytes(size);

                    if (client != null) {
                        client.sendPacket(payload);
                    } else {
                        for (var c : clients.values()) {
                            if (!c.socket.isClosed()) {
                                c.sendPacket(payload);
                            }
                        }
                    }

                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            } finally {
                clients.remove(identifier);
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
