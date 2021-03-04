package org.screamingsandals.bedwars.game;

import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.screamingsandals.bedwars.api.game.GameStatus;
import org.screamingsandals.lib.plugin.ServiceManager;
import org.screamingsandals.lib.utils.annotations.Service;
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
@RequiredArgsConstructor
public class GameManager implements org.screamingsandals.bedwars.api.game.GameManager<Game> {
    @DataFolder("arenas")
    private final Path arenasFolder;
    private final LoggerWrapper logger;
    private final List<Game> games = new LinkedList<>();

    public static GameManager getInstance() {
        return ServiceManager.get(GameManager.class);
    }

    @Override
    public Optional<Game> getGame(String name) {
        return games.stream().filter(game -> game.getName().equals(name)).findFirst();
    }

    @Override
    public List<Game> getGames() {
        return List.copyOf(games);
    }

    @Override
    public List<String> getGameNames() {
        return games.stream().map(Game::getName).collect(Collectors.toList());
    }

    @Override
    public boolean hasGame(String name) {
        return getGame(name).isPresent();
    }

    @Override
    public Optional<Game> getGameWithHighestPlayers() {
        return games.stream()
                .filter(game -> game.getStatus() == GameStatus.WAITING)
                .filter(game -> game.countConnectedPlayers() >= game.getMaxPlayers())
                .max(Comparator.comparingInt(Game::countConnectedPlayers));
    }

    @Override
    public Optional<Game> getGameWithLowestPlayers() {
        return games.stream()
                .filter(game -> game.getStatus() == GameStatus.WAITING)
                .filter(game -> game.countConnectedPlayers() >= game.getMaxPlayers())
                .min(Comparator.comparingInt(Game::countConnectedPlayers));
    }

    @Override
    public Optional<Game> getFirstWaitingGame() {
        return games.stream()
                .filter(game -> game.getStatus() == GameStatus.WAITING)
                .max(Comparator.comparingInt(Game::countConnectedPlayers));
    }

    @Override
    public Optional<Game> getFirstRunningGame() {
        return games.stream()
                .filter(game -> game.getStatus() == GameStatus.RUNNING || game.getStatus() == GameStatus.GAME_END_CELEBRATING)
                .max(Comparator.comparingInt(Game::countConnectedPlayers));
    }

    public void addGame(@NotNull Game game) {
        if (!games.contains(game)) {
            games.add(game);
        }
    }

    public void removeGame(@NotNull Game game) {
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
                        if (file.exists() && file.isFile()) {
                            games.add(Game.loadGame(file));
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
        games.forEach(Game::stop);
        games.clear();
    }
}
