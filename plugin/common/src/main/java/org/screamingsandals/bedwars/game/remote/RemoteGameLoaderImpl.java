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

import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.screamingsandals.bedwars.api.game.RemoteGame;
import org.screamingsandals.bedwars.api.game.RemoteGameLoader;
import org.screamingsandals.bedwars.game.GameManagerImpl;
import org.screamingsandals.bedwars.lib.debug.Debug;
import org.screamingsandals.bedwars.utils.ConfigurateUtils;
import org.screamingsandals.bedwars.utils.MiscUtils;
import org.screamingsandals.lib.Server;
import org.screamingsandals.lib.plugin.ServiceManager;
import org.screamingsandals.lib.spectator.Color;
import org.screamingsandals.lib.spectator.Component;
import org.screamingsandals.lib.utils.annotations.Service;
import org.screamingsandals.lib.utils.logger.Logger;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;

import java.io.File;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RemoteGameLoaderImpl implements RemoteGameLoader {
    private final @NotNull Logger logger;

    public static @NotNull RemoteGameLoaderImpl getInstance() {
        return ServiceManager.get(RemoteGameLoaderImpl.class);
    }

    @Override
    public @NotNull List<? extends @NotNull RemoteGame> loadGames(@NotNull File file) {
        final var remoteGames = ConfigurateUtils.loadFileAsNode(file);
        if (remoteGames == null) {
            return List.of();
        }
        var gameManager = GameManagerImpl.getInstance();

        return remoteGames.childrenList()
                .stream()
                .map(node -> {
                    var game = load(file, node);
                    if (game != null) {
                        gameManager.addGame(game);
                    }
                    return game;
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    private @Nullable RemoteGameImpl load(@NotNull File file, @NotNull ConfigurationNode node) {
        try {
            var uuid = node.node("uuid").get(UUID.class);
            var name = node.node("name").getString();
            var remoteServer = node.node("remoteServer").getString();
            var remoteGameIdentifier = node.node("remoteGameIdentifier").getString();

            if (uuid == null || name == null || name.isBlank() || remoteServer == null || remoteServer.isBlank()) {
                Server.getConsoleSender().sendMessage(
                        MiscUtils.BW_PREFIX.withAppendix(
                                Component.text("Remote game cannot be loaded because one of the following required things are missing! Maybe corrupted database/remote_games.json file?", Color.RED)
                        )
                );
                Server.getConsoleSender().sendMessage(
                        MiscUtils.BW_PREFIX.withAppendix(
                                Component.text("- uuid: " + (uuid == null ? "" : uuid), Color.RED)
                        )
                );
                Server.getConsoleSender().sendMessage(
                        MiscUtils.BW_PREFIX.withAppendix(
                                Component.text("- name: " + (name == null ? "" : name), Color.RED)
                        )
                );
                Server.getConsoleSender().sendMessage(
                        MiscUtils.BW_PREFIX.withAppendix(
                                Component.text("- remoteServer: " + (remoteServer == null ? "" : remoteServer), Color.RED)
                        )
                );
                return null;
            }

            if (GameManagerImpl.getInstance().getGame(uuid).isPresent()) {
                Server.getConsoleSender().sendMessage(
                        MiscUtils.BW_PREFIX.withAppendix(
                                Component.text("Remote game " + uuid + " has the same unique id as another arena that is already loaded. Skipping!", Color.RED)
                        )
                );
                return null;
            }

            if (remoteGameIdentifier != null && remoteGameIdentifier.isBlank()) {
                remoteGameIdentifier = null;
            }

            var remoteGame = new RemoteGameImpl(file.getAbsoluteFile(), uuid, name, remoteServer, remoteGameIdentifier);
            Server.getConsoleSender().sendMessage(
                    MiscUtils.BW_PREFIX.withAppendix(
                            Component.text("Remote game ", Color.GREEN),
                            Component.text(uuid + "/" + name, Color.WHITE),
                            Component.text(" (located on server ", Color.GREEN),
                            Component.text(remoteServer, Color.WHITE),
                            Component.text(" as ", Color.GREEN),
                            Component.text((remoteGameIdentifier != null ? remoteGameIdentifier : "legacy bungee mode"), Color.WHITE),
                            Component.text(") loaded!", Color.GREEN)
                    )
            );
            return remoteGame;
        } catch (SerializationException e) {
            logger.warn("Something went wrong while loading remote game from the shared file {}. Please report this to our Discord or GitHub!", file.getName(), e);
            return null;
        }
    }

    @Override
    public void saveGame(@NotNull RemoteGame game) {
        if (!(game instanceof RemoteGameImpl) || ((RemoteGameImpl) game).getSaveFile() == null) {
            throw new IllegalArgumentException("Remote game cannot be saved because it is not persistent");
        }

        var file = ((RemoteGameImpl) game).getSaveFile();

        try {
            final var loader = ConfigurateUtils.getConfigurationLoaderForFile(file);
            var configMap = loader.createNode();

            for (var g : GameManagerImpl.getInstance().getRemoteGames()) {
                if (g instanceof RemoteGameImpl && file.equals(((RemoteGameImpl) g).getSaveFile())) {
                    serialize((RemoteGameImpl) g, configMap.appendListNode());
                }
            }

            loader.save(configMap);
        } catch (ConfigurateException e) {
            logger.error("Something went wrong while saving remote games to the shared file {}. Please report this to our Discord or GitHub!", file.getName(), e);
        }
    }

    private void serialize(@NotNull RemoteGameImpl remoteGame, @NotNull ConfigurationNode node) throws SerializationException {
        node.node("uuid").set(remoteGame.getUuid());
        node.node("name").set(remoteGame.getName());
        node.node("remoteServer").set(remoteGame.getRemoteServer());
        node.node("remoteGameIdentifier").set(remoteGame.getRemoteGameIdentifier());
    }
}
