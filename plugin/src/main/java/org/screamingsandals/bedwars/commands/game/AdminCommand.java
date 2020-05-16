package org.screamingsandals.bedwars.commands.game;

import org.bukkit.entity.Player;
import org.screamingsandals.bedwars.Main;
import org.screamingsandals.bedwars.api.Permissions;
import org.screamingsandals.bedwars.commands.BedWarsCommand;
import org.screamingsandals.bedwars.commands.game.actions.*;
import org.screamingsandals.bedwars.commands.game.actions.add.AddSpawnerAction;
import org.screamingsandals.bedwars.commands.game.actions.add.AddStoreAction;
import org.screamingsandals.bedwars.commands.game.actions.add.AddTeamAction;
import org.screamingsandals.bedwars.config.MainConfig;
import org.screamingsandals.bedwars.game.GameBuilder;
import org.screamingsandals.lib.commands.common.RegisterCommand;
import org.screamingsandals.lib.commands.common.SubCommandBuilder;
import org.screamingsandals.lib.commands.common.interfaces.ScreamingCommand;
import org.screamingsandals.lib.gamecore.GameCore;
import org.screamingsandals.lib.gamecore.core.GameType;

import java.util.*;

import static org.screamingsandals.lib.gamecore.language.GameLanguage.mpr;

@RegisterCommand(subCommand = true)
public class AdminCommand implements ScreamingCommand {
    private final Map<ActionType, Action> actions = new HashMap<>();

    @Override
    public void register() {
        SubCommandBuilder.bukkitSubCommand()
                .createSubCommand(BedWarsCommand.commandName, "admin", Permissions.ADMIN_COMMAND, Collections.emptyList())
                .handleSubPlayerCommand(this::handleCommand)
                .handleSubPlayerTab(this::handleTab);

        actions.put(ActionType.GAME, new GameAction());
        actions.put(ActionType.LOBBY, new LobbyAction());
        //TODO
        actions.put(ActionType.STORE, new AddStoreAction());
        actions.put(ActionType.SPAWNER, new AddSpawnerAction());
        actions.put(ActionType.TEAM, new AddTeamAction());
    }

    private void handleCommand(Player player, List<String> args) {
        final var argsSize = args.size();
        if (argsSize < 3) {
            mpr("commands.admin.errors.not_enough_arguments").send(player);
            return;
        }

        final var gameManager = GameCore.getGameManager();
        final var gameName = args.get(1);
        final var action = args.get(2).toLowerCase();
        final List<String> subList = args.subList(3, argsSize);

        GameBuilder gameBuilder = null;
        if (!action.equals("create")) {
            if (!gameManager.isBuilderRegistered(gameName)) {
                mpr("game-builder.check-integrity.errors.not-created-yet").send(player);
                return;
            }

            gameBuilder = gameManager.getGameBuilder(gameName);

            if (gameBuilder == null) {
                mpr("game-builder.check-integrity.errors.not-created-yet").send(player);
                return;
            }
        }

        switch (action) {
            case "create":
                handleCreateAction(gameName, player, subList);
                break;
            case "add":
                handleAddAction(gameBuilder, player, subList);
                break;
            default:
                mpr("commands.admin.errors.invalid_action")
                        .replace("%action%", action)
                        .send(player);
                break;
        }
    }

    private List<String> handleTab(Player player, List<String> args) {
        final var gameManager = GameCore.getGameManager();
        final List<String> toReturn = new LinkedList<>();
        final var argsSize = args.size();

        if (argsSize < 2) {
            return toReturn;
        }

        var gameName = args.get(1);

        if (argsSize == 2) {
            final List<String> available = new LinkedList<>();

            available.addAll(gameManager.getGameBuilders().keySet());
            available.addAll(gameManager.getRegisteredGamesMap().keySet());

            for (String found : available) {
                if (found.startsWith(gameName)) {
                    toReturn.add(found);
                }
            }

            return toReturn;
        }

        if (argsSize == 3) {
            final var typed = args.get(2);
            final List<String> available = List.of("create", "add");

            for (String found : available) {
                if (found.startsWith(typed)) {
                    toReturn.add(found);
                }
            }

            return toReturn;
        }


        GameBuilder gameBuilder = null;
        if (gameManager.isBuilderRegistered(gameName)) {
            gameBuilder = gameManager.getGameBuilder(gameName);
        }

        if (gameBuilder == null) {
            return toReturn;
        }
        final var action = args.get(2);
        final var subList = args.subList(3, argsSize);

        if (action.equalsIgnoreCase("create")
                && checkPermissions(player, Permissions.ADMIN_COMMAND_ACTION_CREATE)) {
            return handleCreateTab(subList);
        }

        if (action.equalsIgnoreCase("add")
                && checkPermissions(player, Permissions.ADMIN_COMMAND_ACTION_ADD)) {
            return handleAddTab(gameBuilder, player, subList);
        }


        return toReturn;
    }

    private void handleCreateAction(String gameName, Player player, List<String> args) {
        Optional<GameType> gameType;

        if (Main.getGameManager().isGameRegistered(gameName) || GameCore.getGameManager().isBuilderRegistered(gameName)) {
            mpr("core.errors.game-already-created")
                    .replace("%game%", gameName)
                    .send(player);
            return;
        }

        if (args.isEmpty()) {
            if (Main.getMainConfig().getBoolean(MainConfig.ConfigPaths.BUNGEE_ENABLED)) {
                mpr("commands.admin.actions.create.invalid_game_type").send(player);
                return;
            }
            gameType = Optional.of(GameType.MULTI_GAME);
        } else {
            gameType = GameType.get(args.get(0));
        }

        if (gameType.isEmpty()) {
            mpr("commands.admin.actions.create.invalid_game_type").send(player);
            return;
        }

        final var gameBuilder = new GameBuilder();
        gameBuilder.create(gameName, gameType.get(), player);

        GameCore.getGameManager().registerBuilder(gameName, gameBuilder);
    }

    private List<String> handleCreateTab(List<String> args) {
        final List<String> emptyList = Collections.emptyList();

        if (args.isEmpty()) {
            return emptyList;
        }

        if (Main.getMainConfig().getBoolean(MainConfig.ConfigPaths.BUNGEE_ENABLED)) {
            return List.of("SINGLE_GAME_BUNGEE", "MULTI_GAME_BUNGEE");
        }

        return emptyList;
    }

    private void handleAddAction(GameBuilder gameBuilder, Player player, List<String> args) {
        final var currentGame = gameBuilder.getGameFrame();
        final var argsSize = args.size();

        if (argsSize == 0) {
            mpr("admin.actions.add.not-enough-args").send(player);
            return;
        }

        final var whatToAdd = args.get(0);
        final var subList = args.subList(1, argsSize);

        switch (whatToAdd) {
            case "team": {
                actions.get(ActionType.TEAM).handleCommand(gameBuilder, player, subList);
                break;
            }
            case "spawner": {
                break;
            }
            case "store":
            case "shop": {
                actions.get(ActionType.STORE).handleCommand(gameBuilder, player, subList);
                break;
            }
            default:
                mpr("admin.actions.add.unknown-parameter")
                        .game(currentGame)
                        .send(player);
                break;
        }
    }

    private List<String> handleAddTab(GameBuilder gameBuilder, Player player, List<String> args) {
        final var argsSize = args.size();
        if (argsSize == 0) {
            return Collections.emptyList();
        }

        final var action = args.get(0);
        final List<String> toReturn = new LinkedList<>();

        if (argsSize == 1) {
            final List<String> available = List.of("team", "spawner", "store");
            for (String found : available) {
                if (found.startsWith(action)) {
                    toReturn.add(found);
                }
            }
            return toReturn;
        }

        final var subList = args.subList(1, argsSize);
        System.out.println(subList);
        switch (action) {
            case "team":
                return actions.get(ActionType.TEAM).handleTab(gameBuilder, player, subList);
            case "store":
            case "shop":
                return actions.get(ActionType.STORE).handleTab(gameBuilder, player, subList);
        }

        return toReturn;
    }

    private boolean checkPermissions(Player player, String permission) {
        return player.hasPermission(permission);
    }

}
