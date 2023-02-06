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

package org.screamingsandals.bedwars.lobby;

import lombok.RequiredArgsConstructor;
import org.screamingsandals.bedwars.config.MainConfig;
import org.screamingsandals.bedwars.events.PlayerJoinedEventImpl;
import org.screamingsandals.bedwars.events.PlayerLeaveEventImpl;
import org.screamingsandals.bedwars.player.PlayerManagerImpl;
import org.screamingsandals.lib.Server;
import org.screamingsandals.lib.event.OnEvent;
import org.screamingsandals.lib.event.player.SPlayerJoinEvent;
import org.screamingsandals.lib.event.player.SPlayerLeaveEvent;
import org.screamingsandals.lib.event.player.SPlayerWorldChangeEvent;
import org.screamingsandals.lib.lang.Message;
import org.screamingsandals.lib.player.PlayerWrapper;
import org.screamingsandals.lib.spectator.Component;
import org.screamingsandals.lib.tasker.Tasker;
import org.screamingsandals.lib.tasker.TaskerTime;
import org.screamingsandals.lib.utils.annotations.Service;
import org.screamingsandals.lib.utils.annotations.methods.OnPostEnable;
import org.screamingsandals.lib.utils.annotations.methods.ShouldRunControllable;
import org.spongepowered.configurate.ConfigurationNode;

import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service(dependsOn = {
        MainConfig.class
})
@RequiredArgsConstructor
public class LobbyTabManager {
    private final MainConfig mainConfig;
    private final Set<PlayerWrapper> viewers
            = ConcurrentHashMap.newKeySet();

    private Message header;
    private Message footer;
    private String world;

    @ShouldRunControllable
    public static boolean isEnabled() {
        return MainConfig.getInstance().node("main-lobby", "enabled").getBoolean() && MainConfig.getInstance().node("main-lobby", "tab", "enabled").getBoolean();
    }

    @OnPostEnable
    public void onEnable() {
        header = translate(mainConfig.node("main-lobby", "tab", "header").childrenList().stream().map(ConfigurationNode::getString).collect(Collectors.toList()));
        footer = translate(mainConfig.node("main-lobby", "tab", "footer").childrenList().stream().map(ConfigurationNode::getString).collect(Collectors.toList()));
        world = mainConfig.node("main-lobby", "world").getString("");

        if (world.isEmpty()) {
            return; // :(
        }

        Server.getConnectedPlayers().forEach(player -> {
            if (player.getLocation().getWorld().getName().equals(world) && !PlayerManagerImpl.getInstance().isPlayerInGame(player)) {
                addViewer(player);
            }
        });

        Tasker.build(this::update).repeat(20, TaskerTime.TICKS).start();
    }

    private void update() {
        viewers.forEach(this::updateForPlayer);
    }

    private void updateForPlayer(PlayerWrapper player) {
        if (player.isOnline() && !PlayerManagerImpl.getInstance().isPlayerInGame(player) && (header != null || footer != null)) {
            player.sendPlayerListHeaderFooter(header != null ? header.asComponent(player) : Component.empty(), footer != null ? footer.asComponent(player) : Component.empty());
        }
    }

    public void addViewer(PlayerWrapper player) {
        if (!viewers.contains(player) && player.isOnline() && player.getLocation().getWorld().getName().equals(world)) {
            viewers.add(player);
            updateForPlayer(player);
        }
    }

    public void removeViewer(PlayerWrapper player) {
        if (viewers.remove(player)) {
            if (player.isOnline()) {
                player.sendPlayerListHeaderFooter(Component.empty(), Component.empty());
            }
        }
    }

    private Message translate(List<String> origin) {
        Message message = null;
        for (String a : origin) {
            if (message == null) {
                message = LobbyUtils.setupPlaceholders(Message.ofRichText(a));
            } else {
                message.joinRichText(a);
            }
        }
        return message;
    }

    @OnEvent
    public void onJoin(SPlayerJoinEvent event) {
        var player = event.player();

        if (world.isEmpty()) {
            return; // :(
        }

        if (player.getLocation().getWorld().getName().equals(world) && !PlayerManagerImpl.getInstance().isPlayerInGame(player)) {
            addViewer(player);
        }
    }

    @OnEvent
    public void onLeave(SPlayerLeaveEvent event) {
        removeViewer(event.player());
    }

    @OnEvent
    public void onWorldChange(SPlayerWorldChangeEvent event) {
        var player = event.player();

        if (world.isEmpty()) {
            return; // :(
        }

        if (player.getLocation().getWorld().getName().equals(world)) {
            addViewer(player);
        } else {
            removeViewer(player);
        }
    }

    @OnEvent
    public void onBedWarsJoin(PlayerJoinedEventImpl event) {
        removeViewer(event.getPlayer());
    }

    @OnEvent
    public void onBedWarsLeave(PlayerLeaveEventImpl event) {
        var player = event.getPlayer();

        if (world.isEmpty()) {
            return; // :(
        }

        Tasker.build(() -> {
            if (player.isOnline() && player.getLocation().getWorld().getName().equals(world)) {
                addViewer(player);
            }
        }).delay(20, TaskerTime.TICKS).start();
    }
}
