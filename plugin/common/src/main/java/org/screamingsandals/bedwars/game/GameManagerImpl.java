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

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.screamingsandals.bedwars.api.game.Game;
import org.screamingsandals.bedwars.api.game.GameManager;
import org.screamingsandals.bedwars.config.MainConfig;
import org.screamingsandals.bedwars.utils.MiscUtils;
import org.screamingsandals.bedwars.variants.VariantManagerImpl;
import org.screamingsandals.lib.plugin.ServiceManager;
import org.screamingsandals.lib.tasker.DefaultThreads;
import org.screamingsandals.lib.tasker.Tasker;
import org.screamingsandals.lib.utils.annotations.Service;
import org.screamingsandals.lib.utils.annotations.ServiceDependencies;
import org.screamingsandals.lib.utils.annotations.methods.OnPostEnable;
import org.screamingsandals.lib.utils.annotations.methods.OnPreDisable;
import org.screamingsandals.lib.utils.annotations.parameters.DataFolder;
import org.screamingsandals.lib.utils.logger.LoggerWrapper;

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
    private final LoggerWrapper logger;
    private final List<GameImpl> games = new LinkedList<>();

    @Getter
    private @Nullable Game preselectedGame;
    @Getter
    private boolean doGamePreselection;

    public static GameManagerImpl getInstance() {
        return ServiceManager.get(GameManagerImpl.class);
    }

    @Override
    public Optional<GameImpl> getGame(String name) {
        try {
            var uuid = UUID.fromString(name);
            return getGame(uuid);
        } catch (Throwable ignored) {
            return games.stream().filter(game -> game.getName().equals(name)).findFirst();
        }
    }

    @Override
    public Optional<GameImpl> getGame(UUID uuid) {
        return games.stream().filter(game -> game.getUuid().equals(uuid)).findFirst();
    }

    @Override
    public List<GameImpl> getGames() {
        return List.copyOf(games);
    }

    @Override
    public List<String> getGameNames() {
        return games.stream().map(GameImpl::getName).collect(Collectors.toList());
    }

    @Override
    public boolean hasGame(String name) {
        return getGame(name).isPresent();
    }

    @Override
    public boolean hasGame(UUID uuid) {
        return getGame(uuid).isPresent();
    }

    @Override
    public Optional<GameImpl> getGameWithHighestPlayers(boolean fee) {
        return MiscUtils.getGameWithHighestPlayers(games, fee);
    }

    @Override
    public Optional<GameImpl> getGameWithLowestPlayers(boolean fee) {
        return MiscUtils.getGameWithLowestPlayers(games, fee);
    }

    @Override
    public Optional<GameImpl> getFirstWaitingGame(boolean fee) {
        return MiscUtils.getFirstWaitingGame(games, fee);
    }

    @Override
    public Optional<GameImpl> getFirstRunningGame(boolean fee) {
        return MiscUtils.getFirstRunningGame(games, fee);
    }

    public void addGame(@NotNull GameImpl game) {
        if (!games.contains(game)) {
            games.add(game);
        }
    }

    public void removeGame(@NotNull GameImpl game) {
        games.remove(game);
    }

    @OnPostEnable
    public void onPostEnable() {
        if (Files.exists(arenasFolder)) {
            try (var stream = Files.walk(arenasFolder.toAbsolutePath())) {
                final var results = stream.filter(Files::isRegularFile)
                        .map(Path::toFile)
                        .collect(Collectors.toList());
                if (results.isEmpty()) {
                    logger.debug("No arenas have been found!");
                } else {
                    results.forEach(file -> {
                        if (file.exists() && file.isFile() && !file.getName().toLowerCase(Locale.ROOT).endsWith(".disabled")) {
                            LocalGameLoaderImpl.getInstance().loadGame(file, true).thenAccept(game -> {
                                if (game != null) {
                                    games.add(game);
                                }
                            });
                        }
                    });
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (
            MainConfig.getInstance().node("bungee", "enabled").getBoolean()
            && MainConfig.getInstance().node("bungee", "random-game-selection", "enabled").getBoolean()
            && MainConfig.getInstance().node("bungee", "random-game-selection", "preselect-games").getBoolean()
        ) {
            Tasker.run(DefaultThreads.GLOBAL_THREAD, () -> {
                preselectedGame = getGameWithHighestPlayers().orElse(null);
                doGamePreselection = true;
            });
        }
    }

    @OnPreDisable
    public void onPreDisable() {
        preselectedGame = null;
        doGamePreselection = false;
        games.forEach(GameImpl::stop);
        games.clear();
    }

    public void reselectGame() {
        if (doGamePreselection) {
            preselectedGame = getGameWithHighestPlayers().orElse(null);
        }
    }

    @Override
    public Optional<GameImpl> getGameWithHighestPlayers() {
        return getGameWithHighestPlayers(false);
    }

    @Override
    public Optional<GameImpl> getGameWithLowestPlayers() {
        return getGameWithLowestPlayers(false);
    }

    @Override
    public Optional<GameImpl> getFirstWaitingGame() {
        return getFirstWaitingGame(false);
    }

    @Override
    public Optional<GameImpl> getFirstRunningGame() {
        return getFirstRunningGame(false);
    }
}
