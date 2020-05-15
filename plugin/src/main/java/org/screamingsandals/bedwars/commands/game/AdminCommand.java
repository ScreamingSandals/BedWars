package org.screamingsandals.bedwars.commands.game;

import org.bukkit.entity.Player;
import org.screamingsandals.bedwars.Main;
import org.screamingsandals.bedwars.api.Permissions;
import org.screamingsandals.bedwars.commands.BedWarsCommand;
import org.screamingsandals.bedwars.config.MainConfig;
import org.screamingsandals.bedwars.game.GameBuilder;
import org.screamingsandals.bedwars.game.team.GameTeam;
import org.screamingsandals.lib.commands.common.RegisterCommand;
import org.screamingsandals.lib.commands.common.SubCommandBuilder;
import org.screamingsandals.lib.commands.common.interfaces.ScreamingCommand;
import org.screamingsandals.lib.gamecore.GameCore;
import org.screamingsandals.lib.gamecore.adapter.LocationAdapter;
import org.screamingsandals.lib.gamecore.core.GameFrame;
import org.screamingsandals.lib.gamecore.core.GameType;
import org.screamingsandals.lib.gamecore.store.GameStore;
import org.screamingsandals.lib.gamecore.store.StoreType;
import org.screamingsandals.lib.gamecore.team.TeamColor;

import java.io.File;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import static org.screamingsandals.lib.gamecore.language.GameLanguage.mpr;

@RegisterCommand(subCommand = true)
public class AdminCommand implements ScreamingCommand {
    private GameFrame currentGame;
    private GameBuilder currentBuilder;
    private Player sender;

    @Override
    public void register() {
        SubCommandBuilder.bukkitSubCommand()
                .createSubCommand(BedWarsCommand.commandName, "admin", Permissions.ADMIN_COMMAND, Collections.emptyList())
                .handleSubPlayerCommand(this::handleCommand)
                .handleSubPlayerTab(this::handleTab);
    }

    private void handleCommand(Player player, List<String> args) {
        final var argsSize = args.size();
        if (argsSize < 3) {
            mpr("commands.admin.errors.not_enough_arguments").send(player);
            return;
        }

        this.sender = player;
        final var gameName = args.get(1);
        final var action = args.get(2).toLowerCase();
        final List<String> subList = args.subList(3, argsSize);

        if (!action.equals("create")) {
            if (currentBuilder == null) {
                mpr("game-builder.check-integrity.errors.not-created-yet").send(player);
                return;
            }

            final var currentName = currentBuilder.getGameFrame().getGameName();
            if (!currentName.equals(gameName)) {
                mpr("game-builder.check-integrity.errors.invalid-game-name")
                        .replace("%game%", currentName)
                        .sendList(player);
                return;
            }
        }

        switch (action) {
            case "create":
                handleCreateAction(gameName, subList);
                break;
            case "add":
                handleAddAction(subList);
                break;
            default:
                mpr("commands.admin.errors.invalid_action")
                        .replace("%action%", action)
                        .send(player);
                break;
        }
    }

    private List<String> handleTab(Player player, List<String> args) {
        final List<String> toReturn = new LinkedList<>();
        final var argsSize = args.size();

        if (argsSize == 2 && currentGame != null) {
            toReturn.add(currentGame.getGameName());
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

        if (argsSize >= 4) {
            final var action = args.get(2);
            final var subList = args.subList(3, argsSize);

            if (action.equalsIgnoreCase("create")
                    && checkPermissions(player, Permissions.ADMIN_COMMAND_ACTION_CREATE)) {
                return handleCreateTab(subList);
            }

            if (action.equalsIgnoreCase("add")
                    && checkPermissions(player, Permissions.ADMIN_COMMAND_ACTION_ADD)) {
                return handleAddTab(subList);
            }
        }

        return toReturn;
    }

    private void handleCreateAction(String gameName, List<String> args) {
        final var builders = GameCore.getGameManager().getGameBuilders();
        Optional<GameType> gameType;

        if (Main.getGameManager().isGameRegistered(gameName) || builders.containsKey(gameName)) {
            mpr("core.errors.game-already-created")
                    .replace("%game%", gameName)
                    .send(sender);
            return;
        }

        if (args.isEmpty()) {
            if (Main.getMainConfig().getBoolean(MainConfig.ConfigPaths.BUNGEE_ENABLED)) {
                mpr("commands.admin.actions.create.invalid_game_type").send(sender);
                return;
            }
            gameType = Optional.of(GameType.MULTI_GAME);
        } else {
            gameType = GameType.get(args.get(0));
        }

        if (gameType.isEmpty()) {
            mpr("commands.admin.actions.create.invalid_game_type").send(sender);
            return;
        }

        final var gameBuilder = new GameBuilder();
        gameBuilder.create(gameName, gameType.get(), sender);

        currentBuilder = gameBuilder;
        currentGame = gameBuilder.getGameFrame();

        builders.put(gameName, gameBuilder);
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

    private void handleAddAction(List<String> args) {
        final var argsSize = args.size();
        if (argsSize == 0) {
            mpr("admin.actions.add.not-enough-args").send(sender);
            return;
        }

        final var whatToAdd = args.get(0);
        final var subList = args.subList(1, argsSize);
        switch (whatToAdd) {
            case "team": {
                handleTeamAdding(subList);
                break;
            }
            case "spawner": {
                break;
            }
            case "shop": {
                handleShopAdding(subList);
                break;
            }
            default:
                mpr("admin.actions.add.unknown-parameter").game(currentGame).send(sender);
                break;
        }
    }

    private List<String> handleAddTab(List<String> args) {
        final var argsSize = args.size();
        if (argsSize == 0) {
            return Collections.emptyList();
        }

        final var action = args.get(0);
        final List<String> toReturn = new LinkedList<>();
        final List<String> available = List.of("team", "spawner", "shop");

        if (argsSize == 1) {
            for (String found : available) {
                if (found.startsWith(action)) {
                    toReturn.add(found);
                }
            }
        }

        return toReturn;
    }

    private void handleTeamAdding(List<String> args) {
        final var argsSize = args.size();
        if (argsSize < 3) {
            mpr("commands.admin.actions.add.team.invalid-entry").sendList(sender);
            return;
        }

        final var teamName = args.get(0);
        final var teamColor = TeamColor.get(args.get(1));
        int maxPlayer;

        if (currentBuilder.getGameFrame().isTeamRegistered(teamName)) {
            mpr("commands.admin.actions.add.team.already-registered")
                    .game(currentGame)
                    .replace("%name%", teamName)
                    .send(sender);
            return;
        }

        if (teamColor.isEmpty()) {
            mpr("commands.admin.actions.add.team.invalid-color")
                    .game(currentGame)
                    .replace("%color%", args.get(1))
                    .sendList(sender);
            return;
        }

        try {
            maxPlayer = Integer.parseInt(args.get(2));
        } catch (NumberFormatException ignored) {
            mpr("commands.admin.actions.add.team.invalid-number")
                    .game(currentGame)
                    .send(sender);
            return;
        }

        final var color = teamColor.get();
        currentBuilder.getGameFrame().getTeams().add(new GameTeam(teamName, color, maxPlayer));
        mpr("commands.admin.actions.add.team.created")
                .game(currentGame)
                .replace("%color%", color.chatColor)
                .replace("%name%", color)
                .send(sender);
        //DEBUG
        System.out.println(currentBuilder.getGameFrame().getTeams());
    }

    private void handleShopAdding(List<String> args) {
        final var argsSize = args.size();
        if (argsSize < 2) {
            mpr("commands.admin.actions.add.store.invalid-entry").sendList(sender);
            return;
        }

        GameStore gameStore = null;
        final var location = new LocationAdapter(sender.getLocation());
        final var store = StoreType.get(args.get(0));
        final var storeName = args.get(1);
        final var defaultStore = new File(Main.getInstance().getDataFolder(), "shop.yml"); //TODO - better way

        if (store.isEmpty()) {
            mpr("commands.admin.actions.add.store.invalid-store-type").send(sender);
            return;
        }

        if (argsSize <= 4) {
            final var storeType = store.get();
            if (argsSize == 4) {
                if (storeType != StoreType.CUSTOM) {
                    return;
                }

                final var enteredTeamName = args.get(2);
                final var enteredStoreName = args.get(3);
                final var team = currentGame.getRegisteredTeam(enteredTeamName);
                final var customStore = new File(Main.getInstance().getDataFolder(), enteredStoreName);

                if (team.isEmpty()) {
                    mpr("commands.admin.actions.add.store.invalid-team")
                            .replace("%team%", enteredTeamName)
                            .send(sender);
                    return;
                }

                if (!customStore.exists()) {
                    mpr("commands.admin.actions.add.store.invalid-store-file")
                            .replace("%file%", enteredStoreName)
                            .send(sender);
                    return;
                }

                gameStore = new GameStore(location, storeName, customStore, team.get(), storeType);
            }

            if (storeType == StoreType.CUSTOM) {
                mpr("commands.admin.actions.add.store.not-custom-store").sendList(sender);
                return;
            }

            if (argsSize == 3) {
                final var enteredTeamName = args.get(2);
                final var team = currentGame.getRegisteredTeam(enteredTeamName);

                if (team.isEmpty()) {
                    mpr("commands.admin.actions.add.store.invalid-team")
                            .replace("%team%", enteredTeamName)
                            .sendList(sender);
                    return;
                }

                gameStore = new GameStore(location, storeName, defaultStore, team.get(), storeType);
            }

            if (argsSize == 2) {
                gameStore = new GameStore(location, storeName, defaultStore, storeType);
            }

        } else {
            mpr("commands.admin.actions.add.store.invalid-entry")
                    .sendList(sender);
        }

        if (gameStore == null) {
            mpr("commands.admin.actions.add.store.invalid-entry")
                    .sendList(sender);
            return;
        }

        currentBuilder.addShop(gameStore);
        gameStore.spawn("&a&lGameBuilder - " + storeName);

        mpr("commands.admin.actions.add.store.created").send(sender);
        System.out.println(currentBuilder.getGameFrame().getStores());
    }

    private boolean checkPermissions(Player player, String permission) {
        return player.hasPermission(permission);
    }

}
