/*
 * Copyright (C) 2022 ScreamingSandals
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

import com.google.common.io.ByteStreams;
import lombok.experimental.UtilityClass;
import net.kyori.adventure.text.Component;
import org.screamingsandals.bedwars.config.MainConfig;
import org.screamingsandals.bedwars.lib.debug.Debug;
import org.screamingsandals.lib.CustomPayload;
import org.screamingsandals.lib.player.PlayerWrapper;
import org.screamingsandals.lib.tasker.Tasker;
import org.screamingsandals.lib.tasker.TaskerTime;

@UtilityClass
public class BungeeUtils {
    public void movePlayerToBungeeServer(PlayerWrapper player, boolean serverRestart) {
        if (serverRestart) {
            internalMove(player, true);
            return;
        }

        Tasker.build(() -> internalMove(player, false)).afterOneTick().start();
    }

    public void sendPlayerBungeeMessage(PlayerWrapper player, String string) {
        Tasker.build(() -> {
            var out = ByteStreams.newDataOutput();

            out.writeUTF("Message");
            out.writeUTF(player.getName());
            out.writeUTF(string);

            CustomPayload.send(player, "BungeeCord", out.toByteArray());
        })
        .delay(30, TaskerTime.TICKS)
        .start();
    }

    private void internalMove(PlayerWrapper player, boolean restart) {
        var server = MainConfig.getInstance().node("bungee", "server").getString("hub");
        var out = ByteStreams.newDataOutput();

        out.writeUTF("Connect");
        out.writeUTF(server);

        CustomPayload.send(player, "BungeeCord", out.toByteArray());
        Debug.info("Player " + player.getName() + " has been moved to hub server.");
        if (!restart && MainConfig.getInstance().node("bungee", "kick-when-proxy-too-slow").getBoolean(true)) {
            Tasker.build(() -> {
                if (player.isOnline()) {
                    player.kick(Component.text("BedWars can't properly transfer player through bungee network. Contact server admin."));
                }
            })
            .delay(20, TaskerTime.TICKS)
            .start();
        }
    }
}