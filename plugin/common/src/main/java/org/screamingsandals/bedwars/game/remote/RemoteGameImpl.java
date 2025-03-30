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

package org.screamingsandals.bedwars.game.remote;

import lombok.Data;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.screamingsandals.bedwars.BedWarsPlugin;
import org.screamingsandals.bedwars.api.game.GameStatus;
import org.screamingsandals.bedwars.api.game.RemoteGame;
import org.screamingsandals.bedwars.api.player.BWPlayer;
import org.screamingsandals.bedwars.game.remote.protocol.packets.GameStatePacket;
import org.screamingsandals.bedwars.game.remote.protocol.packets.JoinGamePacket;
import org.screamingsandals.bedwars.game.remote.protocol.ProtocolManagerImpl;
import org.screamingsandals.bedwars.lib.debug.Debug;
import org.screamingsandals.bedwars.player.BedWarsPlayer;
import org.screamingsandals.bedwars.utils.BungeeUtils;
import org.screamingsandals.lib.spectator.Component;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

@Data
public class RemoteGameImpl implements RemoteGame {
    private final @Nullable File saveFile;
    private final @NotNull UUID uuid;
    private final @NotNull String name;
    private @NotNull String remoteServer;
    private @Nullable String remoteGameIdentifier;

    private @Nullable GameStatePacket state;

    public RemoteGameImpl(@Nullable File saveFile, @NotNull UUID uuid, @NotNull String name, @NotNull String remoteServer, @Nullable String remoteGameIdentifier) {
        this.saveFile = saveFile;
        this.uuid = uuid;
        this.name = name;
        this.remoteServer = remoteServer;
        this.remoteGameIdentifier = remoteGameIdentifier;
    }

    @Override
    public @NotNull GameStatus getStatus() {
        if (state == null) {
            return GameStatus.REMOTE_UNKNOWN;
        }

        return GameStatus.valueOf(state.getState());
    }

    @Override
    public int getMinPlayers() {
        if (state == null) {
            return 0;
        }

        return state.getMinPlayers();
    }

    @Override
    public int getMaxPlayers() {
        if (state == null) {
            return 0;
        }

        return state.getMaxPlayers();
    }

    @Override
    public @NotNull Component getDisplayNameComponent() {
        if (state == null) {
            return Component.text(remoteGameIdentifier != null ? remoteGameIdentifier : name);
        }

        return Component.fromJavaJson(state.getDisplayName());
    }

    @Override
    public @Nullable String getNameOnRemoteServer() {
        if (state == null) {
            return null;
        }

        return state.getName();
    }

    @Override
    public @Nullable UUID getUuidOnRemoteServer() {
        if (state == null) {
            return null;
        }

        return state.getUuid();
    }

    @Override
    public @Nullable Integer getMaxTimeInTheCurrentState() {
        if (state == null) {
            return null;
        }

        return state.getMaxTime();
    }

    @Override
    public @Nullable Integer getElapsedTimeInCurrentState() {
        if (state == null) {
            return null;
        }

        var elapsed = state.getElapsed();
        if (state.isTimeMoving()) {
            elapsed += (int) ((System.currentTimeMillis() - state.getGenerationTime()) / 1000);
        }
        return elapsed;
    }

    @Override
    public @Nullable Integer getTimeLeftInCurrentState() {
        var maxTime = getMaxTimeInTheCurrentState();
        var elapsed = getElapsedTimeInCurrentState();
        if (maxTime != null && elapsed != null) {
            return maxTime - elapsed;
        }

        return null;
    }

    @Override
    public int countConnectedPlayers() {
        if (state == null) {
            return 0;
        }

        return state.getOnlinePlayers();
    }

    @Override
    public int countAlive() {
        if (state == null) {
            return 0;
        }

        return state.getAlivePlayers();
    }

    @Override
    public int countAvailableTeams() {
        if (state == null) {
            return 0;
        }

        return state.getTeams();
    }

    @Override
    public int countActiveTeams() {
        if (state == null) {
            return 0;
        }

        return state.getAliveTeams();
    }

    @Override
    public void joinToGame(@NotNull BWPlayer p) {
        if (!(p instanceof BedWarsPlayer)) {
            throw new IllegalArgumentException("Provided instance of player is not created by BedWars plugin!");
        }
        var player = (BedWarsPlayer) p;

        if (remoteGameIdentifier != null) {
            try {
                ProtocolManagerImpl.getInstance().sendPacket(
                        remoteServer,
                        new JoinGamePacket(player.getUniqueId(), remoteGameIdentifier, BedWarsPlugin.getInstance().getServerName())
                );
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        BungeeUtils.sendBungeeMessage(player, out -> {
            out.writeUTF("Connect");
            out.writeUTF(remoteServer);
        });
        Debug.info("Player " + player.getName() + " joins a remote game "
                + (remoteGameIdentifier != null ? remoteGameIdentifier : "(legacy bungee mode)") + " on server " + remoteServer + " (local identification is " +  uuid + "/" + name + ")");
    }
}
