package org.screamingsandals.bedwars.commands.game.actions.add;

import org.bukkit.entity.Player;
import org.screamingsandals.bedwars.commands.game.actions.Action;
import org.screamingsandals.bedwars.game.GameBuilder;
import org.screamingsandals.lib.gamecore.adapter.LocationAdapter;
import org.screamingsandals.lib.gamecore.resources.ResourceSpawner;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import static org.screamingsandals.lib.gamecore.language.GameLanguage.mpr;

public class AddSpawnerAction implements Action {

    @Override
    public void handleCommand(GameBuilder gameBuilder, Player player, List<String> args) {
        final var currentGame = gameBuilder.getGameFrame();
        final var argsSize = args.size();
        if (argsSize < 1) {
            mpr("commands.admin.actions.add.spawner.invalid-entry")
                    .game(currentGame)
                    .sendList(player);
            return;
        }

        final var resourceTypes = currentGame.getResourceManager().getResourceTypes();
        final var enteredType = args.get(0);
        final var spawnerType = resourceTypes.getType(enteredType);

        if (spawnerType == null) {
            mpr("commands.admin.actions.add.spawner.invalid-spawner-type")
                    .replace("%type%", enteredType)
                    .game(currentGame)
                    .send(player);
            return;
        }

        final ResourceSpawner spawner;
        final var location = new LocationAdapter(player.getLocation());

        if (argsSize >= 2) {
            final var enteredValue = args.get(1);
            var hologramEnabled = true;

            try {
                hologramEnabled = Boolean.parseBoolean(enteredValue);
            } catch (Exception e) {
                mpr("general.errors.invalid-boolean-value")
                        .replace("%enteredValue%", enteredType)
                        .send(player);
            }

            spawner = new ResourceSpawner(location, spawnerType, hologramEnabled);

            if (argsSize == 3) {
                final var enteredTeam = args.get(2);
                final var gameTeam = currentGame.getRegisteredTeam(enteredTeam);

                if (gameTeam.isEmpty()) {
                    mpr("general.errors.invalid-team")
                            .replace("%team%", enteredTeam)
                            .send(player);
                    return;
                }
                spawner.setGameTeam(gameTeam.get());
            }

        } else {
            spawner = new ResourceSpawner(location, spawnerType);
        }

        gameBuilder.addSpawner(spawner, player);
    }

    @Override
    public List<String> handleTab(GameBuilder gameBuilder, Player player, List<String> args) {
        if (gameBuilder == null) {
            return Collections.emptyList();
        }

        final var currentGame = gameBuilder.getGameFrame();
        final var resourceTypes = currentGame.getResourceManager().getResourceTypes();
        final var argsSize = args.size();
        final List<String> toReturn = new LinkedList<>();

        if (argsSize == 0) {
            return toReturn;
        }

        final var typed = args.get(0);
        if (argsSize == 1) {
            final var resourceType = resourceTypes.getSpawnerTypes().values();
            for (ResourceSpawner.Type type : resourceType) {
                final var name = type.getName();

                if (name.startsWith(typed)) {
                    toReturn.add(name);
                }
            }
        }

        return toReturn;
    }
}
