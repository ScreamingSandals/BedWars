/*
 * Copyright (C) 2022 ScreamingSandals
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
import lombok.SneakyThrows;
import org.screamingsandals.bedwars.BedWarsPlugin;
import org.screamingsandals.bedwars.api.PurchaseType;
import org.screamingsandals.bedwars.api.config.Configuration;
import org.screamingsandals.bedwars.api.config.ConfigurationContainer;
import org.screamingsandals.bedwars.commands.DumpCommand;
import org.screamingsandals.bedwars.config.MainConfig;
import org.screamingsandals.bedwars.events.*;
import org.screamingsandals.bedwars.game.GameStoreImpl;
import org.screamingsandals.bedwars.api.game.ItemSpawnerType;
import org.screamingsandals.bedwars.api.upgrades.Upgrade;
import org.screamingsandals.bedwars.api.upgrades.UpgradeRegistry;
import org.screamingsandals.bedwars.api.upgrades.UpgradeStorage;
import org.screamingsandals.bedwars.game.GameImpl;
import org.screamingsandals.bedwars.game.ItemSpawnerTypeImpl;
import org.screamingsandals.bedwars.lang.LangKeys;
import org.screamingsandals.bedwars.player.BedWarsPlayer;
import org.screamingsandals.bedwars.player.PlayerManagerImpl;
import org.screamingsandals.bedwars.special.listener.PermaItemListener;
import org.screamingsandals.bedwars.lib.debug.Debug;
import org.screamingsandals.bedwars.utils.MiscUtils;
import org.screamingsandals.lib.SpecialSoundKey;
import org.screamingsandals.lib.entity.EntityMapper;
import org.screamingsandals.lib.event.EventManager;
import org.screamingsandals.lib.event.OnEvent;
import org.screamingsandals.lib.lang.Message;
import org.screamingsandals.lib.item.Item;
import org.screamingsandals.lib.plugin.ServiceManager;
import org.screamingsandals.lib.spectator.Component;
import org.screamingsandals.lib.spectator.sound.SoundSource;
import org.screamingsandals.lib.spectator.sound.SoundStart;
import org.screamingsandals.lib.utils.ConfigurateUtils;
import org.screamingsandals.lib.utils.annotations.Service;
import org.screamingsandals.lib.utils.annotations.methods.OnPostEnable;
import org.screamingsandals.lib.utils.annotations.parameters.DataFolder;
import org.screamingsandals.simpleinventories.SimpleInventoriesCore;
import org.screamingsandals.simpleinventories.events.ItemRenderEvent;
import org.screamingsandals.simpleinventories.events.OnTradeEvent;
import org.screamingsandals.simpleinventories.events.PreClickEvent;
import org.screamingsandals.simpleinventories.inventory.Include;
import org.screamingsandals.simpleinventories.inventory.InventorySet;
import org.spongepowered.configurate.ConfigurationNode;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

@Service(dependsOn = {
        SimpleInventoriesCore.class
})
@RequiredArgsConstructor
public class ShopInventory {
    private final Map<String, InventorySet> shopMap = new HashMap<>();
    @DataFolder("shop")
    private final Path shopFolder;
    private final MainConfig mainConfig;
    private final PlayerManagerImpl playerManager;

    public static ShopInventory getInstance() {
        return ServiceManager.get(ShopInventory.class);
    }

    @OnPostEnable
    public void onEnable(@DataFolder Path pluginFolder) {
        try {
            // Only main shops are currently migrated to the new location!!!
            if (!Files.exists(shopFolder)) {
                Files.createDirectory(shopFolder);
            }
            if (mainConfig.node("turnOnExperimentalGroovyShop").getBoolean()) {
                var expectedShopGroovy = shopFolder.resolve("shop.groovy");
                if (!Files.exists(expectedShopGroovy)) {
                    var legacyShopGroovy = pluginFolder.resolve("shop.groovy");
                    if (Files.exists(legacyShopGroovy)) {
                        Files.move(legacyShopGroovy, shopFolder.resolve("shop.groovy"));
                    } else {
                        BedWarsPlugin.getInstance().saveResource("shop/shop.groovy", false);
                    }
                }
            } else {
                var expectedShopYml = shopFolder.resolve("shop.yml");
                if (!Files.exists(expectedShopYml)) {
                    var legacyShopYml = pluginFolder.resolve("shop.yml");
                    if (Files.exists(legacyShopYml)) {
                        Files.move(legacyShopYml, shopFolder.resolve("shop.yml"));
                    } else {
                        BedWarsPlugin.getInstance().saveResource("shop/shop.yml", false);
                    }
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        loadNewShop("default", null, true);
    }

    public void show(BedWarsPlayer player, GameStoreImpl store) {
        try {
            var parent = true;
            String fileName = null;
            if (store != null) {
                parent = store.isUseParent();
                fileName = store.getShopFile();
            }
            if (fileName == null && player.isInGame()) { // who invokes this method for player who is not in game goes directly to hell
                var defaultShopFile = player.getGame().getConfigurationContainer().getOrDefault(ConfigurationContainer.DEFAULT_SHOP_FILE, String.class, null);
                if (defaultShopFile != null) {
                    fileName = defaultShopFile;
                    parent = false;
                }
            }
            if (fileName != null) {
                var file = normalizeShopFile(fileName);
                var name = (parent ? "+" : "-") + file.getAbsolutePath();
                if (!shopMap.containsKey(name)) {
                    loadNewShop(name, file, parent);
                }
                player.openInventory(shopMap.get(name));
            } else {
                player.openInventory(shopMap.get("default"));
            }
        } catch (Throwable throwable) {
            player.sendMessage("[BW] Your shop.yml/shop.groovy is invalid! Check it out or contact us on Discord.");
            throwable.printStackTrace();
        }
    }

    public File normalizeShopFile(String name) {
        if (name.split("\\.").length > 1) {
            return shopFolder.resolve(name).toFile();
        }

        var fileg = shopFolder.resolve(name + ".groovy").toFile();
        if (fileg.exists()) {
            return fileg;
        }
        return shopFolder.resolve(name + ".yml").toFile();
    }

    public void onGeneratingItem(ItemRenderEvent event) {
        var itemInfo = event.getItem();
        var item = itemInfo.getStack();
        var game = playerManager.getGameOfPlayer(event.getPlayer());
        var prices = itemInfo.getOriginal().getPrices();
        if (!prices.isEmpty()) {
            // TODO: multi-price feature
            var priceObject = prices.get(0);
            var price = priceObject.getAmount();
            var type = BedWarsPlugin.getSpawnerType(priceObject.getCurrency().toLowerCase(), game.orElseThrow());
            if (type == null) {
                return;
            }

            var enabled = itemInfo.getFirstPropertyByName("generateLore")
                    .map(property -> property.getPropertyData().getBoolean())
                    .orElseGet(() -> mainConfig.node("lore", "generate-automatically").getBoolean(true));

            if (enabled) {
                var finalItem = item;
                var loreText = itemInfo.getFirstPropertyByName("generatedLoreText")
                        .map(property -> property.getPropertyData().childrenList().stream().map(ConfigurationNode::getString))
                        .orElseGet(() -> mainConfig.node("lore", "text").childrenList().stream().map(ConfigurationNode::getString))
                        .filter(Objects::nonNull)
                        .map(s -> s.replaceAll("%price%", Integer.toString(price))
                                .replaceAll("%resource%", type.getItemName().asComponent().toLegacy())
                                .replaceAll("%amount%", Integer.toString(finalItem.getAmount())))
                        .map(Component::fromLegacy)
                        .collect(Collectors.toList());

                var nL = new ArrayList<Component>();
                nL.addAll(item.getLore());
                nL.addAll(loreText);

                item = item.withItemLore(nL);

                event.setStack(item);
            }
        }
        final var preScanEvent = new PrePropertyScanEventImpl(event);
        EventManager.fire(preScanEvent);
        if (preScanEvent.isCancelled()) return;

        var finalItem1 = item;
        itemInfo.getProperties().forEach(property -> {
            if (property.hasName()) {
                var converted = ConfigurateUtils.raw(property.getPropertyData());
                if (!(converted instanceof Map)) {
                    converted = DumpCommand.nullValuesAllowingMap("value", converted);
                }

                //noinspection unchecked
                var applyEvent = new ApplyPropertyToDisplayedItemEventImpl(game.orElse(null), playerManager.getPlayer(event.getPlayer().getUuid()).orElseThrow(), property.getPropertyName(), (Map<String, Object>) converted, finalItem1);
                EventManager.fire(applyEvent);

                if (applyEvent.getStack() != null) {
                    event.setStack(applyEvent.getStack());
                }
            }
        });

        EventManager.fire(new PostPropertyScanEventImpl(event));
    }

    public void onPreAction(PreClickEvent event) {
        if (event.isCancelled()) {
            return;
        }

        var player = event.getPlayer();
        if (!playerManager.isPlayerInGame(player)) {
            event.setCancelled(true);
        }

        if (playerManager.getPlayer(player).orElseThrow().isSpectator()) {
            event.setCancelled(true);
        }
    }

    public void onShopTransaction(OnTradeEvent event) {
        if (event.isCancelled()) {
            return;
        }

        if (event.getItem().getFirstPropertyByName("upgrade").isPresent()) {
            handleUpgrade(event);
        } else {
            handleBuy(event);
        }
    }

    @OnEvent
    public void onApplyPropertyToBoughtItem(ApplyPropertyToItemEventImpl event) {
        if (event.getPropertyName().equalsIgnoreCase("applycolorbyteam")
                || event.getPropertyName().equalsIgnoreCase("transform::applycolorbyteam")) {
            var player = event.getPlayer();
            var team = event.getGame().getTeamOfPlayer(player);

            if (mainConfig.node("automatic-coloring-in-shop").getBoolean()) {
                event.setStack(BedWarsPlugin.getInstance().getColorChanger().applyColor(team.getColor(), event.getStack()));
            }
        }
    }

    @SneakyThrows
    private void loadDefault(InventorySet inventorySet) {
        inventorySet.getMainSubInventory().dropContents();
        inventorySet.getMainSubInventory().getWaitingQueue().add(Include.of(Path.of(ShopInventory.class.getResource("/shop/shop.yml").toURI())));
        inventorySet.getMainSubInventory().process();
    }

    private void loadNewShop(String name, File file, boolean useParent) {
        var inventorySet = SimpleInventoriesCore.builder()
                .genericShop(true)
                .genericShopPriceTypeRequired(true)
                .animationsEnabled(true)
                .categoryOptions(localOptionsBuilder ->
                    localOptionsBuilder
                            .backItem(mainConfig.readDefinedItem("shopback", "BARRIER"), itemBuilder ->
                                itemBuilder.name(Message.of(LangKeys.IN_GAME_SHOP_SHOP_BACK).asComponent())
                            )
                            .pageBackItem(mainConfig.readDefinedItem("pageback", "ARROW"), itemBuilder ->
                                itemBuilder.name(Message.of(LangKeys.IN_GAME_SHOP_PAGE_BACK).asComponent())
                            )
                            .pageForwardItem(mainConfig.readDefinedItem("pageforward", "BARRIER"), itemBuilder ->
                                itemBuilder.name(Message.of(LangKeys.IN_GAME_SHOP_PAGE_FORWARD).asComponent())
                            )
                            .cosmeticItem(mainConfig.readDefinedItem("shopcosmetic", "AIR"))
                            .rows(mainConfig.node("shop", "rows").getInt(4))
                            .renderActualRows(mainConfig.node("shop", "render-actual-rows").getInt(6))
                            .renderOffset(mainConfig.node("shop", "render-offset").getInt(9))
                            .renderHeaderStart(mainConfig.node("shop", "render-header-start").getInt(0))
                            .renderFooterStart(mainConfig.node("shop", "render-footer-start").getInt(45))
                            .itemsOnRow(mainConfig.node("shop", "items-on-row").getInt(9))
                            .showPageNumber(mainConfig.node("shop", "show-page-numbers").getBoolean(true))
                            .inventoryType(mainConfig.node("shop", "inventory-type").getString("CHEST"))
                            .prefix(Message.of(LangKeys.IN_GAME_SHOP_NAME).asComponent())
                )

                // old shop format compatibility
                .variableToProperty("upgrade", "upgrade")
                .variableToProperty("generate-lore", "generateLore")
                .variableToProperty("generated-lore-text", "generatedLoreText")
                .variableToProperty("currency-changer", "currencyChanger")

                .render(this::onGeneratingItem)
                .preClick(this::onPreAction)
                .buy(this::onShopTransaction)
                .define("team", (key, player, playerItemInfo, arguments) -> {
                    var gPlayer = playerManager.getPlayer(player);
                    var team = gPlayer.orElseThrow().getGame().getPlayerTeam(gPlayer.orElseThrow());
                    if (arguments.length > 0) {
                        String fa = arguments[0];
                        switch (fa) {
                            case "color":
                                return team.getColor().name();
                            case "chatcolor":
                                return MiscUtils.toLegacyColorCode(team.getColor().getTextColor());
                            case "maxplayers":
                                return Integer.toString(team.getMaxPlayers());
                            case "players":
                                return Integer.toString(team.countConnectedPlayers());
                            case "hasBed":
                                return Boolean.toString(team.isTargetBlockIntact());
                        }
                    }
                    return team.getName();
                })
                .define("spawner", (key, player, playerItemInfo, arguments) -> {
                    var gPlayer = playerManager.getPlayer(player);
                    GameImpl game = gPlayer.orElseThrow().getGame();
                    if (arguments.length > 2) {
                        String upgradeBy = arguments[0];
                        String upgrade = arguments[1];
                        UpgradeStorage upgradeStorage = UpgradeRegistry.getUpgrade("spawner");
                        if (upgradeStorage == null) {
                            return null;
                        }
                        List<Upgrade> upgrades = null;
                        switch (upgradeBy) {
                            case "name":
                                upgrades = upgradeStorage.findItemSpawnerUpgrades(game, upgrade);
                                break;
                            case "team":
                                upgrades = upgradeStorage.findItemSpawnerUpgrades(game, game.getPlayerTeam(gPlayer.get()));
                                break;
                        }

                        if (upgrades != null && !upgrades.isEmpty()) {
                            String what = "level";
                            if (arguments.length > 3) {
                                what = arguments[2];
                            }
                            double heighest = Double.MIN_VALUE;
                            switch (what) {
                                case "level":
                                    for (Upgrade upgrad : upgrades) {
                                        if (upgrad.getLevel() > heighest) {
                                            heighest = upgrad.getLevel();
                                        }
                                    }
                                    return String.valueOf(heighest);
                                case "initial":
                                    for (Upgrade upgrad : upgrades) {
                                        if (upgrad.getInitialLevel() > heighest) {
                                            heighest = upgrad.getInitialLevel();
                                        }
                                    }
                                    return String.valueOf(heighest);
                            }
                        }
                    }
                    return "";
                })
                .call(categoryBuilder -> {
                    final var includeEvent = new StoreIncludeEventImpl(name, file == null ? null : file.toPath().toAbsolutePath(), useParent, categoryBuilder);
                    EventManager.fire(includeEvent);
                    if (includeEvent.isCancelled()) {
                        return;
                    }
                    if (useParent) {
                        var shopFileName = "shop.yml";
                        if (mainConfig.node("turnOnExperimentalGroovyShop").getBoolean(false)) {
                            shopFileName = "shop.groovy";
                        }
                        categoryBuilder.include(Include.of(shopFolder.resolve(shopFileName)));
                    }

                    if (file != null) {
                        categoryBuilder.include(Include.of(file));
                    }

                })
                .getInventorySet();

        try {
            inventorySet.getMainSubInventory().process();
        } catch (Exception ex) {
            Debug.warn("Wrong shop.yml/shop.groovy configuration!", true);
            Debug.warn("Check validity of your YAML/Groovy!", true);
            ex.printStackTrace();
            loadDefault(inventorySet);
        }

        shopMap.put(name, inventorySet);
    }

    private static Component getNameOrCustomNameOfItem(Item item) {
        try {
            if (item.getDisplayName() != null) {
                return item.getDisplayName();
            }
        } catch (Throwable ignored) {
        }

        var normalItemName = item.getMaterial().platformName().replace("_", " ").toLowerCase();
        var sArray = normalItemName.split(" ");
        var stringBuilder = new StringBuilder();

        for (var s : sArray) {
            stringBuilder.append(Character.toUpperCase(s.charAt(0))).append(s.substring(1)).append(" ");
        }
        return Component.fromLegacy(stringBuilder.toString().trim());
    }

    private void handleBuy(OnTradeEvent event) {
        var player = event.getPlayer();
        var game = playerManager.getGameOfPlayer(event.getPlayer()).orElseThrow();
        var clickType = event.getClickType();
        var itemInfo = event.getItem();

        // TODO: multi-price feature
        var price = event.getPrices().get(0);
        ItemSpawnerTypeImpl type = BedWarsPlugin.getSpawnerType(price.getCurrency().toLowerCase(), game);

        var newItem = event.getStack();

        var amount = newItem.getAmount();
        var priceAmount = price.getAmount();
        int inInventory = 0;

        var currencyChanger = itemInfo.getFirstPropertyByName("currencyChanger");
        if (currencyChanger.isPresent()) {
            var changeItemToName = currencyChanger.get().getPropertyData().getString();
            if (changeItemToName == null) {
                return;
            }

            var changeItemType = BedWarsPlugin.getSpawnerType(changeItemToName.toLowerCase(), game);
            if (changeItemType == null) {
                return;
            }

            newItem = changeItemType.getItem();
        }

        var originalMaxStackSize = newItem.getMaterial().maxStackSize();
        if (clickType.isShiftClick() && originalMaxStackSize > 1) {
            double priceOfOne = (double) priceAmount / amount;
            double maxStackSize;
            int finalStackSize;

            var it = type.getItem();
            for (var itemStack : player.getPlayerInventory().getStorageContents()) {
                if (itemStack != null && itemStack.isSimilar(it)) {
                    inInventory = inInventory + itemStack.getAmount();
                }
            }
            if (mainConfig.node("sell-max-64-per-click-in-shop").getBoolean()) {
                maxStackSize = Math.min(inInventory / priceOfOne, originalMaxStackSize);
            } else {
                maxStackSize = inInventory / priceOfOne;
            }

            finalStackSize = (int) maxStackSize;
            if (finalStackSize > amount) {
                priceAmount = (int) (priceOfOne * finalStackSize);
                newItem = newItem.withAmount(finalStackSize);
                amount = finalStackSize;
            }
        }

        var materialItem = type.getItem(priceAmount);
        if (event.hasPlayerInInventory(materialItem)) {
            final var prePurchaseEvent = new StorePrePurchaseEventImpl(game, playerManager.getPlayer(event.getPlayer().getUuid()).orElseThrow(), materialItem, newItem, type, PurchaseType.NORMAL_ITEM, event);
            EventManager.fire(prePurchaseEvent);
            if (prePurchaseEvent.isCancelled()) {
                return;
            }
            Map<String, Object> permaItemPropertyData = new HashMap<>();
            for (var property : itemInfo.getProperties()) {
                var converted = ConfigurateUtils.raw(property.getPropertyData());
                if (!(converted instanceof Map)) {
                    converted = DumpCommand.nullValuesAllowingMap("value", converted);
                }
                //noinspection unchecked
                var propertyData = (Map<String, Object>) converted;
                if (property.hasName()) {
                    var applyEvent = new ApplyPropertyToBoughtItemEventImpl(game, playerManager.getPlayer(event.getPlayer().getUuid()).orElseThrow(),
                            property.getPropertyName(), propertyData, newItem);
                    EventManager.fire(applyEvent);

                    newItem = applyEvent.getStack();
                }
                // Checks if the player is buying a permanent item. Setting name to empty string to prevent other listeners from erroring out.
                else if (propertyData.get(PermaItemListener.getPermItemPropKey()) != null) {
                    permaItemPropertyData = propertyData;
                }
            }

            if (!permaItemPropertyData.isEmpty()) {
                var applyEvent = new ApplyPropertyToBoughtItemEventImpl(game, playerManager.getPlayer(event.getPlayer().getUuid()).orElseThrow(),
                        "", permaItemPropertyData, newItem);
                EventManager.fire(applyEvent);
            }

            event.sellStack(materialItem);
            var notFit = event.buyStack(newItem);
            if (!notFit.isEmpty()) {
                notFit.forEach(stack -> EntityMapper.dropItem(stack, player.getLocation()));
            }

            if (!mainConfig.node("removePurchaseMessages").getBoolean()) {
                Message.of(LangKeys.IN_GAME_SHOP_BUY_SUCCESS)
                        .prefixOrDefault(game.getCustomPrefixComponent())
                        .placeholder("item", Component.text(amount + "x ").withAppendix(getNameOrCustomNameOfItem(newItem)))
                        .placeholder("material", Component.text(priceAmount + " ").withAppendix(type.getItemName()))
                        .send(event.getPlayer());
            }
            player.playSound(SoundStart.sound(
                    SpecialSoundKey.key(mainConfig.node("sounds", "item_buy", "sound").getString("entity.item.pickup")),
                    SoundSource.PLAYER,
                    (float) MainConfig.getInstance().node("sounds", "item_buy", "volume").getDouble(),
                    (float) MainConfig.getInstance().node("sounds", "item_buy", "pitch").getDouble()
            ));

            EventManager.fire(new StorePostPurchaseEventImpl(game, playerManager.getPlayer(event.getPlayer().getUuid()).orElseThrow(), PurchaseType.NORMAL_ITEM, event));
        } else {
            final var purchaseFailedEvent = new PurchaseFailedEventImpl(game, playerManager.getPlayer(event.getPlayer().getUuid()).orElseThrow(), PurchaseType.NORMAL_ITEM, event);
            EventManager.fire(purchaseFailedEvent);
            if (purchaseFailedEvent.isCancelled()) return;

            if (!mainConfig.node("removePurchaseFailedMessages").getBoolean()) {
                Message.of(LangKeys.IN_GAME_SHOP_BUY_FAILED)
                        .prefixOrDefault(game.getCustomPrefixComponent())
                        .placeholder("item", Component.text(amount + "x ").withAppendix(getNameOrCustomNameOfItem(newItem)))
                        .placeholder("material", Component.text(priceAmount + " ").withAppendix(type.getItemName()))
                        .send(event.getPlayer());
            }
        }
    }

    private void handleUpgrade(OnTradeEvent event) {
        var player = event.getPlayer().as(BedWarsPlayer.class);
        var game = player.getGame();
        var itemInfo = event.getItem();

        // TODO: multi-price feature
        var price = event.getPrices().get(0);
        ItemSpawnerTypeImpl type = BedWarsPlugin.getSpawnerType(price.getCurrency().toLowerCase(), game);

        var priceAmount = price.getAmount();

        var upgrade = itemInfo.getFirstPropertyByName("upgrade").orElseThrow();
        var itemName = upgrade.getPropertyData().node("shop-name").getString(Message.of(LangKeys.IN_GAME_SHOP_UPGRADE_TRANSLATE).asComponent(event.getPlayer()).toLegacy());
        var entities = upgrade.getPropertyData().node("entities").childrenList();

        boolean sendToAll = false;
        boolean isUpgrade = true;
        double maxLevel = 0.0;
        double newLevel = 0.0;
        var materialItem = type.getItem(priceAmount);

        if (event.hasPlayerInInventory(materialItem)) {
            final var upgradePurchasedEvent  = new StorePrePurchaseEventImpl(game, playerManager.getPlayer(event.getPlayer().getUuid()).orElseThrow(), materialItem, null, type, PurchaseType.UPGRADES, event);
            EventManager.fire(upgradePurchasedEvent);
            if (upgradePurchasedEvent.isCancelled()) return;

            for (var entity : entities) {
                var configuredType = entity.node("type").getString();
                if (configuredType == null) {
                    return;
                }

                var upgradeStorage = UpgradeRegistry.getUpgrade(configuredType);
                if (upgradeStorage != null) {

                    // TODO: Learn SimpleGuiFormat upgrades pre-parsing and automatic renaming old
                    // variables
                    var team = game.getTeamOfPlayer(player);
                    double addLevels = entity.node("add-levels").getDouble(entity.node("levels").getDouble(0));
                    /* You shouldn't use it in entities */
                    itemName = entity.node("shop-name").getString(itemName);
                    sendToAll = entity.node("notify-team").getBoolean();
                    maxLevel = entity.node("max-level").getDouble();

                    List<Upgrade> upgrades = new ArrayList<>();

                    var spawnerNameNode = entity.node("spawner-name");
                    var spawnerTypeNode = entity.node("spawner-type");
                    var teamUpgradeNode = entity.node("team-upgrade");
                    var customNameNode = entity.node("customName");

                    if (!spawnerNameNode.empty()) {
                        String customName = spawnerNameNode.getString();
                        upgrades = upgradeStorage.findItemSpawnerUpgrades(game, customName);
                    } else if (!spawnerTypeNode.empty()) {
                        String mapSpawnerType = spawnerTypeNode.getString();
                        ItemSpawnerType spawnerType = BedWarsPlugin.getSpawnerType(mapSpawnerType, game);

                        upgrades = upgradeStorage.findItemSpawnerUpgrades(game, team, spawnerType);
                    } else if (!teamUpgradeNode.empty()) {
                        boolean upgradeAllSpawnersInTeam = teamUpgradeNode.getBoolean();

                        if (upgradeAllSpawnersInTeam) {
                            upgrades = upgradeStorage.findItemSpawnerUpgrades(game, team);
                        }

                    } else if (!customNameNode.empty()) { // Old configuration
                        String customName = customNameNode.getString();
                        upgrades = upgradeStorage.findItemSpawnerUpgrades(game, customName);
                    } else {
                        isUpgrade = false;
                        Debug.warn("[BedWars]> Upgrade configuration is invalid.");
                    }

                    if (isUpgrade) {
                        for (var up : upgrades) {
                            if (up.getLevel() + addLevels > maxLevel && maxLevel > 0) {
                                Message.of(LangKeys.IN_GAME_SPAWNER_REACHED_MAXIMUM_LEVEL)
                                        .prefixOrDefault(game.getCustomPrefixComponent())
                                        .placeholder("item", itemName)
                                        .placeholder("material", price + " " + type.getItemName())
                                        .placeholder("max_level", maxLevel)
                                        .send(player);
                                return;
                            }
                        }

                        event.sellStack(materialItem);
                        var bedwarsUpgradeBoughtEvent = new UpgradeBoughtEventImpl(game, playerManager.getPlayer(player.getUuid()).orElseThrow(), upgrades, addLevels, upgradeStorage);
                        EventManager.fire(bedwarsUpgradeBoughtEvent);

                        if (bedwarsUpgradeBoughtEvent.isCancelled()) {
                            continue;
                        }

                        if (upgrades.isEmpty()) {
                            continue;
                        }

                        for (var anUpgrade : upgrades) {
                            newLevel = anUpgrade.getLevel() + addLevels;
                            var improvedEvent = new UpgradeImprovedEventImpl(game, anUpgrade, upgradeStorage, anUpgrade.getLevel(), newLevel);
                            improvedEvent.setNewLevel(anUpgrade.getLevel() + addLevels);
                            EventManager.fire(improvedEvent);
                        }
                    }
                }

                if (sendToAll) {
                    for (var player1 : game.getPlayerTeam(player).getPlayers()) {
                        if (!mainConfig.node("removeUpgradeMessages").getBoolean()) {
                            Message.of(LangKeys.IN_GAME_SHOP_UPGRADE_SUCCESS)
                                    .prefixOrDefault(game.getCustomPrefixComponent())
                                    .placeholder("name", player.getDisplayName())
                                    .placeholder("spawner", itemName)
                                    .placeholder("level", newLevel)
                                    .send(player1);
                        }
                        player.playSound(SoundStart.sound(
                                SpecialSoundKey.key(mainConfig.node("sounds", "upgrade_buy", "sound").getString("entity.experience_orb.pickup")),
                                SoundSource.PLAYER,
                                (float) MainConfig.getInstance().node("sounds", "upgrade_buy", "volume").getDouble(),
                                (float) MainConfig.getInstance().node("sounds", "upgrade_buy", "pitch").getDouble()
                        ));
                    }
                } else {
                    if (!mainConfig.node("removeUpgradeMessages").getBoolean()) {
                        Message.of(LangKeys.IN_GAME_SHOP_UPGRADE_SUCCESS)
                                .prefixOrDefault(game.getCustomPrefixComponent())
                                .placeholder("name", player.getDisplayName())
                                .placeholder("spawner", itemName)
                                .placeholder("level", newLevel)
                                .send(event.getPlayer());
                    }
                    player.playSound(SoundStart.sound(
                            SpecialSoundKey.key(mainConfig.node("sounds", "upgrade_buy", "sound").getString("entity.experience_orb.pickup")),
                            SoundSource.PLAYER,
                            (float) MainConfig.getInstance().node("sounds", "upgrade_buy", "volume").getDouble(),
                            (float) MainConfig.getInstance().node("sounds", "upgrade_buy", "pitch").getDouble()
                    ));
                }
            }
            EventManager.fire(new StorePostPurchaseEventImpl(game, playerManager.getPlayer(event.getPlayer().getUuid()).orElseThrow(), PurchaseType.UPGRADES, event));
        } else {
            final var purchaseFailedEvent = new PurchaseFailedEventImpl(game, playerManager.getPlayer(event.getPlayer().getUuid()).orElseThrow(), PurchaseType.UPGRADES, event);
            EventManager.fire(purchaseFailedEvent);
            if (purchaseFailedEvent.isCancelled()) return;

            if (!mainConfig.node("removePurchaseFailedMessages").getBoolean()) {
                Message.of(LangKeys.IN_GAME_SHOP_BUY_FAILED)
                        .prefixOrDefault(game.getCustomPrefixComponent())
                        .placeholder("item", itemName)
                        .placeholder("material", Component.text(priceAmount + " ").withAppendix(type.getItemName()))
                        .send(event.getPlayer());
            }
        }
    }
}
