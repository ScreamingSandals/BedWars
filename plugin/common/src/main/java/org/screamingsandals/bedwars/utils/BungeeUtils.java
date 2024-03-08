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

package org.screamingsandals.bedwars.utils;

import lombok.experimental.UtilityClass;
import org.jetbrains.annotations.Nullable;
import org.screamingsandals.bedwars.config.MainConfig;
import org.screamingsandals.bedwars.lib.debug.Debug;
import org.screamingsandals.lib.CustomPayload;
import org.screamingsandals.lib.player.Player;
import org.screamingsandals.lib.spectator.Component;
import org.screamingsandals.lib.tasker.DefaultThreads;
import org.screamingsandals.lib.tasker.Tasker;
import org.screamingsandals.lib.tasker.TaskerTime;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

@UtilityClass
public class BungeeUtils {
    public void movePlayerToBungeeServer(Player player, boolean serverRestart) {
        movePlayerToBungeeServer(player, serverRestart, null);
    }

    public void movePlayerToBungeeServer(Player player, boolean serverRestart, @Nullable String serverName) {
        var server = serverName != null ? serverName : MainConfig.getInstance().node("bungee", "server").getString("hub");
        if (serverRestart) {
            internalMove(player, true, server);
            return;
        }

        Tasker.run(DefaultThreads.GLOBAL_THREAD, () -> internalMove(player, false, server));
    }

    public void sendPlayerBungeeMessage(Player player, String string) {
        Tasker.runDelayed(DefaultThreads.GLOBAL_THREAD, () -> {
            var out = new ByteArrayOutputStream();
            var dout = new DataOutputStream(out);

            try {
                dout.writeUTF("Message");
                dout.writeUTF(player.getName());
                dout.writeUTF(string);

                CustomPayload.send(player, "BungeeCord", out.toByteArray());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }, 30, TaskerTime.TICKS);
    }

    public ByteArrayOutputStream constructDataOutput(DataOutputFiller consumer) throws IOException {
        var out = new ByteArrayOutputStream();
        var dout = new DataOutputStream(out);

        consumer.fill(dout);

        return out;
    }

    public void sendBungeeMessage(Player player, DataOutputFiller consumer) {
        try (var out = constructDataOutput(consumer)) {
            CustomPayload.send(player, "BungeeCord", out.toByteArray());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void internalMove(Player player, boolean restart, String server) {
        var out = new ByteArrayOutputStream();
        var dout = new DataOutputStream(out);

        try {
            dout.writeUTF("Connect");
            dout.writeUTF(server);

            CustomPayload.send(player, "BungeeCord", out.toByteArray());
        } catch (IOException e) {
            e.printStackTrace();
        }
        Debug.info("Player " + player.getName() + " has been moved to hub server.");
        if (!restart && MainConfig.getInstance().node("bungee", "kick-when-proxy-too-slow").getBoolean(true)) {
            Tasker.runDelayed(DefaultThreads.GLOBAL_THREAD, () -> {
                if (player.isOnline()) {
                    player.kick(Component.text("BedWars can't properly transfer player through bungee network. Contact server admin."));
                }
            }, 20, TaskerTime.TICKS);
        }
    }

    public interface DataOutputFiller {
        void fill(DataOutputStream dataOutputStream) throws IOException;
    }
}