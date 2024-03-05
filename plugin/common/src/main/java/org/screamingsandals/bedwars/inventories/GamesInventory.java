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

package org.screamingsandals.bedwars.inventories;

import lombok.RequiredArgsConstructor;
import org.screamingsandals.bedwars.BedWarsPlugin;
import org.screamingsandals.bedwars.api.game.Game;
import org.screamingsandals.bedwars.api.game.GameStatus;
import org.screamingsandals.bedwars.commands.RejoinCommand;
import org.screamingsandals.bedwars.events.PlayerOpenGamesInventoryEventImpl;
import org.screamingsandals.bedwars.game.GameImpl;
import org.screamingsandals.bedwars.game.GameManagerImpl;
import org.screamingsandals.bedwars.lang.LangKeys;
import org.screamingsandals.bedwars.player.PlayerManagerImpl;
import org.screamingsandals.lib.event.EventManager;
import org.screamingsandals.lib.lang.Message;
import org.screamingsandals.lib.player.Player;
import org.screamingsandals.lib.plugin.ServiceManager;
import org.screamingsandals.lib.utils.annotations.Service;
import org.screamingsandals.lib.utils.annotations.methods.OnPostEnable;
import org.screamingsandals.lib.utils.annotations.methods.OnPreDisable;
import org.screamingsandals.lib.utils.annotations.parameters.DataFolder;
import org.screamingsandals.lib.utils.logger.LoggerWrapper;
import org.screamingsandals.simpleinventories.SimpleInventoriesCore;
import org.screamingsandals.simpleinventories.events.PostClickEvent;
import org.screamingsandals.simpleinventories.inventory.GenericItemInfo;
import org.screamingsandals.simpleinventories.inventory.Include;
import org.screamingsandals.simpleinventories.inventory.InventorySet;
import org.screamingsandals.simpleinventories.inventory.Property;
import org.spongepowered.configurate.serialize.SerializationException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GamesInventory {
    @DataFolder("games_inventory")
    private final Path gamesInventoryFolder;
    private final LoggerWrapper logger;
    private final GameManagerImpl gameManager;
    private final PlayerManagerImpl playerManager;

    private final Map<String, InventorySet> inventoryMap = new HashMap<>();

    public static GamesInventory getInstance() {
        return ServiceManager.get(GamesInventory.class);
    }

    @OnPostEnable
    public void loadInventory() {
        if (Files.exists(gamesInventoryFolder)) {
            try (var stream = Files.walk(gamesInventoryFolder.toAbsolutePath())) {
                final var results = stream.filter(Files::isRegularFile)
                        .map(Path::toFile)
                        .collect(Collectors.toList());
                if (results.isEmpty()) {
                    logger.debug("No games inventories have been found!");
                } else {
                    results.forEach(file -> {
                        if (file.exists() && file.isFile() && !file.getName().toLowerCase().endsWith(".disabled")) {
                            final var dot = file.getName().indexOf(".");
                            final var name = dot == -1 ? file.getName() : file.getName().substring(0, dot);
                            final var siFormat = SimpleInventoriesCore.builder()
                                    .categoryOptions(localOptionsBuilder -> {
                                        localOptionsBuilder
                                                .renderHeaderStart(600)
                                                .renderFooterStart(600)
                                                .renderOffset(9)
                                                .rows(4)
                                                .renderActualRows(4)
                                                .showPageNumber(false)
                                                .prefix(name); // TODO: translatable?
                                    })
                                    .call(categoryBuilder -> {
                                        try {
                                            categoryBuilder.include(Include.of(file));
                                        } catch (Throwable t) {
                                            t.printStackTrace();
                                        }
                                    })
                                    .click(this::onClick)
                                    .process()
                                    .getInventorySet();

                            inventoryMap.put(name, siFormat);
                        }
                    });
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            try {
                Files.createDirectory(gamesInventoryFolder);
                BedWarsPlugin.getInstance().saveResource("games_inventory/example-games-inventory.yml.disabled", false);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public boolean openForPlayer(Player player, String type) {
        final var format = inventoryMap.get(type);
        if (format == null) {
            return false;
        }

        final var event = new PlayerOpenGamesInventoryEventImpl(player, type);
        EventManager.fire(event);
        if (event.isCancelled()) {
            return false;
        }
        player.openInventory(format);
        return true;
    }

    public List<String> getInventoriesNames() {
        return List.copyOf(inventoryMap.keySet());
    }

    @OnPreDisable
    public void onPreDisable() {
        inventoryMap.clear();
    }

    private void onClick(PostClickEvent event) {
        final var item = event.getItem();
        final var stack = item.getStack();
        final var player = event.getPlayer();
        final var properties = item.getProperties();

        if (stack != null) {
            if (item.hasProperties()) {
                player.closeInventory();
                properties.stream()
                        .filter(Property::hasName)
                        .forEach(property -> {
                            switch (property.getPropertyName().toLowerCase()) {
                                case "randomly_join":
                                    final var randomlyJoin = item.getFirstPropertyByName("randomly_join").orElseThrow();
                                    final var games = randomlyJoin.getPropertyData().node("games");
                                    final var gameList = new ArrayList<Game>();
                                    if (!games.isList()) {
                                        // Game inventories from SBA don't contain list of games player can randomly join. This code will find them in the same sub inventory.
                                        try {
                                            var list = event.getSubInventory().getContents().stream()
                                                    .map(genericItemInfo -> genericItemInfo.getFirstPropertyByName("join"))
                                                    .filter(Optional::isPresent)
                                                    .map(Optional::get)
                                                    .map(property1 -> property1.getPropertyData().node("gameName").getString())
                                                    .filter(Objects::nonNull)
                                                    .collect(Collectors.toList());

                                            if (list.isEmpty()) {
                                                // probably all the games are in some subinv
                                                list = event.getSubInventory().getContents().stream()
                                                        .filter(GenericItemInfo::hasChildInventory)
                                                        .map(GenericItemInfo::getChildInventory)
                                                        .filter(Objects::nonNull)
                                                        .flatMap(childInventory -> childInventory.getContents().stream())
                                                        .map(genericItemInfo -> genericItemInfo.getFirstPropertyByName("join"))
                                                        .filter(Optional::isPresent)
                                                        .map(Optional::get)
                                                        .map(property1 -> property1.getPropertyData().node("gameName").getString())
                                                        .filter(Objects::nonNull)
                                                        .collect(Collectors.toList());
                                            }

                                            games.set(list);
                                        } catch (SerializationException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                    games.childrenList().forEach(configurationNode ->
                                            gameManager.getGame(configurationNode.getString("")).ifPresent(gameList::add)
                                    );
                                    if (gameList.isEmpty()) {
                                        player.sendMessage(Message.of(LangKeys.GAMES_INVENTORY_COULD_NOT_FIND_GAME).defaultPrefix());
                                        return;
                                    }
                                    gameList.stream()
                                            .filter(game -> game.getStatus() == GameStatus.WAITING)
                                            .findAny()
                                            .ifPresentOrElse(
                                                    game -> game.joinToGame(playerManager.getPlayerOrCreate(player)),
                                                    () -> player.sendMessage(Message.of(LangKeys.GAMES_INVENTORY_COULD_NOT_FIND_GAME).defaultPrefix())
                                            );
                                    break;
                                case "rejoin":
                                    ServiceManager.get(RejoinCommand.class).rejoin(player);
                                    break;
                                case "join":
                                    final var gameName = item.getFirstPropertyByName("join").orElseThrow().getPropertyData().node("gameName").getString();
                                    if (gameName == null) {
                                        player.sendMessage(Message.of(LangKeys.GAMES_INVENTORY_COULD_NOT_FIND_GAME).defaultPrefix());
                                        return;
                                    }
                                    gameManager.getGame(gameName).ifPresentOrElse(
                                            game -> game.joinToGame(playerManager.getPlayerOrCreate(player)),
                                            () -> player.sendMessage(Message.of(LangKeys.GAMES_INVENTORY_COULD_NOT_FIND_GAME).defaultPrefix())
                                    );
                                    break;
                                default:
                                    break;
                            }
                        });
            }
        }
    }

}
