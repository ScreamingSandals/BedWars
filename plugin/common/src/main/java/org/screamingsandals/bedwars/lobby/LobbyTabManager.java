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
import org.screamingsandals.lib.event.player.PlayerJoinEvent;
import org.screamingsandals.lib.event.player.PlayerLeaveEvent;
import org.screamingsandals.lib.event.player.PlayerWorldChangeEvent;
import org.screamingsandals.lib.lang.Message;
import org.screamingsandals.lib.player.Player;
import org.screamingsandals.lib.spectator.Component;
import org.screamingsandals.lib.tasker.DefaultThreads;
import org.screamingsandals.lib.tasker.Tasker;
import org.screamingsandals.lib.tasker.TaskerTime;
import org.screamingsandals.lib.utils.annotations.Service;
import org.screamingsandals.lib.utils.annotations.ServiceDependencies;
import org.screamingsandals.lib.utils.annotations.methods.OnPostEnable;
import org.screamingsandals.lib.utils.annotations.methods.ShouldRunControllable;
import org.spongepowered.configurate.ConfigurationNode;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@Service
@ServiceDependencies(dependsOn = {
        MainConfig.class
})
@RequiredArgsConstructor
public class LobbyTabManager {
    private final MainConfig mainConfig;
    private final List<Player> viewers = new CopyOnWriteArrayList<>();

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

        Tasker.runRepeatedly(DefaultThreads.GLOBAL_THREAD, this::update, 20, TaskerTime.TICKS);
    }

    private void update() {
        viewers.forEach(this::updateForPlayer);
    }

    private void updateForPlayer(Player player) {
        if (player.isOnline() && !PlayerManagerImpl.getInstance().isPlayerInGame(player) && (header != null || footer != null)) {
            player.sendPlayerListHeaderFooter(header != null ? header.asComponent(player) : Component.empty(), footer != null ? footer.asComponent(player) : Component.empty());
        }
    }

    public void addViewer(Player player) {
        if (!viewers.contains(player) && player.isOnline() && player.getLocation().getWorld().getName().equals(world)) {
            viewers.add(player);
            updateForPlayer(player);
        }
    }

    public void removeViewer(Player player) {
        if (viewers.contains(player)) {
            viewers.remove(player);
            if (player.isOnline()) {
                player.sendPlayerListHeaderFooter(Component.empty(), Component.empty());
            }
        }
    }

    private Message translate(List<String> origin) {
        var message = new AtomicReference<Message>();
        origin.forEach(a -> {
            if (message.get() == null) {
                message.set(LobbyUtils.setupPlaceholders(Message.ofRichText(a)));
            } else {
                message.get().joinRichText(a);
            }
        });
        return message.get();
    }

    @OnEvent
    public void onJoin(PlayerJoinEvent event) {
        var player = event.player();

        if (world.isEmpty()) {
            return; // :(
        }

        if (player.getLocation().getWorld().getName().equals(world) && !PlayerManagerImpl.getInstance().isPlayerInGame(player)) {
            addViewer(player);
        }
    }

    @OnEvent
    public void onLeave(PlayerLeaveEvent event) {
        removeViewer(event.player());
    }

    @OnEvent
    public void onWorldChange(PlayerWorldChangeEvent event) {
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

        Tasker.runDelayed(DefaultThreads.GLOBAL_THREAD, () -> {
            if (player.isOnline() && player.getLocation().getWorld().getName().equals(world)) {
                addViewer(player);
            }
        }, 20, TaskerTime.TICKS);
    }
}
