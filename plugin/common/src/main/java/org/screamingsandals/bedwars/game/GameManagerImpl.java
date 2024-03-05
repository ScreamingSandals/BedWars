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

package org.screamingsandals.bedwars.game;

import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.screamingsandals.bedwars.api.game.Game;
import org.screamingsandals.bedwars.api.game.GameManager;
import org.screamingsandals.bedwars.game.remote.RemoteGameImpl;
import org.screamingsandals.bedwars.utils.MiscUtils;
import org.screamingsandals.bedwars.variants.VariantManagerImpl;
import org.screamingsandals.lib.plugin.ServiceManager;
import org.screamingsandals.lib.utils.annotations.Service;
import org.screamingsandals.lib.utils.annotations.ServiceDependencies;
import org.screamingsandals.lib.utils.annotations.methods.OnPostEnable;
import org.screamingsandals.lib.utils.annotations.methods.OnPreDisable;
import org.screamingsandals.lib.utils.annotations.parameters.ConfigFile;
import org.screamingsandals.lib.utils.annotations.parameters.DataFolder;
import org.screamingsandals.lib.utils.logger.LoggerWrapper;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.gson.GsonConfigurationLoader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

@Service
@ServiceDependencies(dependsOn = {
        VariantManagerImpl.class // it's important to have variant manager loaded before games manager
})
@RequiredArgsConstructor
public class GameManagerImpl implements GameManager {
    @DataFolder("arenas")
    private final Path arenasFolder;
    @ConfigFile("database/remote_games.json")
    private final GsonConfigurationLoader remoteGamesLoader;
    private final LoggerWrapper logger;
    private final List<Game> games = new LinkedList<>();

    public static GameManagerImpl getInstance() {
        return ServiceManager.get(GameManagerImpl.class);
    }

    @Override
    public Optional<Game> getGame(String name) {
        try {
            var uuid = UUID.fromString(name);
            return getGame(uuid);
        } catch (Throwable ignored) {
            return games.stream().filter(game -> game.getName().equals(name)).findFirst();
        }
    }

    public Optional<GameImpl> getLocalGame(String name) {
        return getGame(name)
                .filter(game -> game instanceof GameImpl)
                .map(game -> (GameImpl) game);
    }

    public Optional<RemoteGameImpl> getRemoteGame(String name) {
        return getGame(name)
                .filter(game -> game instanceof RemoteGameImpl)
                .map(game -> (RemoteGameImpl) game);
    }

    @Override
    public Optional<Game> getGame(UUID uuid) {
        return games.stream().filter(game -> game.getUuid().equals(uuid)).findFirst();
    }

    public Optional<GameImpl> getLocalGame(UUID uuid) {
        return getGame(uuid)
                .filter(game -> game instanceof GameImpl)
                .map(game -> (GameImpl) game);
    }

    public Optional<RemoteGameImpl> getRemoteGame(UUID uuid) {
        return getGame(uuid)
                .filter(game -> game instanceof RemoteGameImpl)
                .map(game -> (RemoteGameImpl) game);
    }

    @Override
    public List<Game> getGames() {
        return List.copyOf(games);
    }

    @Override
    public List<GameImpl> getLocalGames() {
        return games.stream()
                .filter(game -> game instanceof GameImpl)
                .map(game -> (GameImpl) game)
                .collect(Collectors.toList());
    }

    @Override
    public List<RemoteGameImpl> getRemoteGames() {
        return games.stream()
                .filter(game -> game instanceof RemoteGameImpl)
                .map(game -> (RemoteGameImpl) game)
                .collect(Collectors.toList());
    }

    @Override
    public List<String> getGameNames() {
        return games.stream().map(Game::getName).collect(Collectors.toList());
    }

    public List<String> getLocalGameNames() {
        return games.stream().filter(game -> game instanceof GameImpl).map(Game::getName).collect(Collectors.toList());
    }

    public List<String> getRemoteGameNames() {
        return games.stream().filter(game -> game instanceof RemoteGameImpl).map(Game::getName).collect(Collectors.toList());
    }

    @Override
    public boolean hasGame(String name) {
        return getGame(name).isPresent();
    }

    public boolean hasLocalGame(String name) {
        return getLocalGame(name).isPresent();
    }

    @Override
    public boolean hasGame(UUID uuid) {
        return getGame(uuid).isPresent();
    }

    @Override
    public Optional<Game> getGameWithHighestPlayers(boolean fee) {
        return MiscUtils.getGameWithHighestPlayers(games, fee);
    }

    @Override
    public Optional<Game> getGameWithLowestPlayers(boolean fee) {
        return MiscUtils.getGameWithLowestPlayers(games, fee);
    }

    @Override
    public Optional<Game> getFirstWaitingGame(boolean fee) {
        return MiscUtils.getFirstWaitingGame(games, fee);
    }

    @Override
    public Optional<Game> getFirstRunningGame(boolean fee) {
        return MiscUtils.getFirstRunningGame(games, fee);
    }

    public void addGame(@NotNull Game game) {
        if (!games.contains(game)) {
            games.add(game);
            if (game instanceof RemoteGameImpl) {
                triggerRemoteSaving();
            }
        }
    }

    public void removeGame(@NotNull Game game) {
        games.remove(game);
        if (game instanceof RemoteGameImpl) {
            triggerRemoteSaving();
        }
    }

    @OnPostEnable
    public void onPostEnable() {
        try {
            var remoteGames = remoteGamesLoader.load();

            remoteGames.childrenList().forEach(node -> {
                var game = RemoteGameImpl.load(node);
                if (game != null) {
                    games.add(game);
                }
            });
        } catch (ConfigurateException e) {
            e.printStackTrace();
        }

        if (Files.exists(arenasFolder)) {
            try (var stream = Files.walk(arenasFolder.toAbsolutePath())) {
                final var results = stream.filter(Files::isRegularFile)
                        .map(Path::toFile)
                        .collect(Collectors.toList());
                if (results.isEmpty()) {
                    logger.debug("No arenas have been found!");
                } else {
                    results.forEach(file -> {
                        if (file.exists() && file.isFile() && !file.getName().toLowerCase().endsWith(".disabled")) {
                            var game = GameImpl.loadGame(file);
                            if (game != null) {
                                games.add(game);
                            }
                        }
                    });
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @OnPreDisable
    public void onPreDisable() {
        games.forEach(g -> {
            if (g instanceof GameImpl) {
                ((GameImpl) g).stop();
            }
        });
        games.clear();
    }

    public void triggerRemoteSaving() {
        var node = remoteGamesLoader.createNode();

        try {
            for (var game : games) {
                if (game instanceof RemoteGameImpl) {
                    ((RemoteGameImpl) game).serialize(node.appendListNode());
                }
            }

            remoteGamesLoader.save(node);
        } catch (ConfigurateException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Optional<Game> getGameWithHighestPlayers() {
        return getGameWithHighestPlayers(false);
    }

    @Override
    public Optional<Game> getGameWithLowestPlayers() {
        return getGameWithLowestPlayers(false);
    }

    @Override
    public Optional<Game> getFirstWaitingGame() {
        return getFirstWaitingGame(false);
    }

    @Override
    public Optional<Game> getFirstRunningGame() {
        return getFirstRunningGame(false);
    }
}
