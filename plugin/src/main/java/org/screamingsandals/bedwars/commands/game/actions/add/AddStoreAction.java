package org.screamingsandals.bedwars.commands.game.actions.add;

import org.bukkit.entity.Player;
import org.screamingsandals.bedwars.Main;
import org.screamingsandals.bedwars.commands.game.actions.Action;
import org.screamingsandals.bedwars.game.GameBuilder;
import org.screamingsandals.lib.gamecore.adapter.LocationAdapter;
import org.screamingsandals.lib.gamecore.store.GameStore;
import org.screamingsandals.lib.gamecore.store.StoreType;

import java.io.File;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import static org.screamingsandals.lib.gamecore.language.GameLanguage.m;
import static org.screamingsandals.lib.gamecore.language.GameLanguage.mpr;

public class AddStoreAction implements Action {

    @Override
    public void handleCommand(GameBuilder gameBuilder, Player player, List<String> args) {
        final var currentGame = gameBuilder.getGameFrame();
        final var argsSize = args.size();
        if (argsSize < 2) {
            mpr("commands.admin.actions.add.store.invalid-entry")
                    .game(currentGame)
                    .sendList(player);
            return;
        }

        GameStore gameStore = null;
        final var location = new LocationAdapter(player.getLocation());
        final var store = StoreType.get(args.get(0));
        final var storeName = args.get(1);
        final var defaultStore = new File(Main.getInstance().getDataFolder(), "shop.yml"); //TODO - better way

        if (store.isEmpty()) {
            mpr("commands.admin.actions.add.store.invalid-store-type")
                    .game(currentGame)
                    .send(player);
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
                            .game(currentGame)
                            .replace("%team%", enteredTeamName)
                            .send(player);
                    return;
                }

                if (!customStore.exists()) {
                    mpr("commands.admin.actions.add.store.invalid-store-file")
                            .game(currentGame)
                            .replace("%file%", enteredStoreName)
                            .send(player);
                    return;
                }

                gameStore = new GameStore(location, storeName, customStore, team.get(), storeType);
            }

            if (storeType == StoreType.CUSTOM) {
                mpr("commands.admin.actions.add.store.not-custom-store")
                        .game(currentGame)
                        .sendList(player);
                return;
            }

            if (argsSize == 3) {
                final var enteredTeamName = args.get(2);
                final var team = currentGame.getRegisteredTeam(enteredTeamName);

                if (team.isEmpty()) {
                    mpr("commands.admin.actions.add.store.invalid-team")
                            .game(currentGame)
                            .replace("%team%", enteredTeamName)
                            .sendList(player);
                    return;
                }

                gameStore = new GameStore(location, storeName, defaultStore, team.get(), storeType);
            }

            if (argsSize == 2) {
                gameStore = new GameStore(location, storeName, defaultStore, storeType);
            }

        } else {
            mpr("commands.admin.actions.add.store.invalid-entry")
                    .game(currentGame)
                    .sendList(player);
        }

        if (gameStore == null) {
            mpr("commands.admin.actions.add.store.invalid-entry")
                    .game(currentGame)
                    .sendList(player);
            return;
        }

        gameBuilder.addShop(gameStore);
        gameStore.spawn(gameBuilder.getGameFrame(), "&a&lGameBuilder - " + storeName);

        mpr("commands.admin.actions.add.store.created").game(currentGame).send(player);
        System.out.println(currentGame.getStores());
    }

    @Override
    public List<String> handleTab(GameBuilder gameBuilder, Player player, List<String> args) {
        final var argsSize = args.size();
        final List<String> toReturn = new LinkedList<>();
        if (argsSize == 0) {
            return toReturn;
        }


        final var typed = args.get(0);
        if (argsSize == 1) {
            for (StoreType found : StoreType.values()) {
                final var name = found.name();

                if (name.startsWith(typed)) {
                    toReturn.add(name);
                }
            }
        }

        if (argsSize == 2) {
            return Collections.singletonList(m("commands.admin.actions.add.store.write-custom-name").get());
        }

        if (typed.equalsIgnoreCase("custom") && argsSize == 3) {
            return Collections.singletonList(m("commands.admin.actions.add.write-custom-file-name").get());
        }

        return toReturn;
    }
}
