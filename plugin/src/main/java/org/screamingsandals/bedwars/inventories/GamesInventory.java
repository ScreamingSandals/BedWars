package org.screamingsandals.bedwars.inventories;

import lombok.RequiredArgsConstructor;
import org.screamingsandals.bedwars.events.PlayerOpenGamesInventoryEventImpl;
import org.screamingsandals.lib.event.EventManager;
import org.screamingsandals.lib.player.PlayerWrapper;
import org.screamingsandals.lib.utils.annotations.Service;
import org.screamingsandals.lib.utils.annotations.methods.OnPostEnable;
import org.screamingsandals.lib.utils.annotations.methods.OnPreDisable;
import org.screamingsandals.lib.utils.annotations.parameters.DataFolder;
import org.screamingsandals.lib.utils.logger.LoggerWrapper;
import org.screamingsandals.simpleinventories.SimpleInventoriesCore;
import org.screamingsandals.simpleinventories.events.PostClickEvent;
import org.screamingsandals.simpleinventories.inventory.Include;
import org.screamingsandals.simpleinventories.inventory.InventorySet;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GamesInventory {
    @DataFolder("games_inventory")
    private final Path gamesInventoryFolder;
    private final LoggerWrapper logger;

    private final Map<String, InventorySet> inventoryMap = new HashMap<>();

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
                                    .call(categoryBuilder ->{
                                        try {
                                            categoryBuilder.include(Include.of(file));
                                        }  catch (Throwable t) {
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
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void openForPlayer(PlayerWrapper player, String type) {
        final var format = inventoryMap.get(type);
        if (format == null) {
            return;
        }

        final var event = new PlayerOpenGamesInventoryEventImpl(player, type);
        EventManager.fire(event);
        if (event.isCancelled()) {
            return;
        }
        player.openInventory(format);
    }

    @OnPreDisable
    public void onPreDisable() {
        inventoryMap.clear();
    }

    private void onClick(PostClickEvent event) {
        // TODO
    }

}
