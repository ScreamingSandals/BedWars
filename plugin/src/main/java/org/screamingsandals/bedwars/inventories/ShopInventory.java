package org.screamingsandals.bedwars.inventories;

import lombok.SneakyThrows;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.screamingsandals.bedwars.Main;
import org.screamingsandals.bedwars.api.events.*;
import org.screamingsandals.bedwars.commands.DumpCommand;
import org.screamingsandals.bedwars.game.GameStore;
import org.screamingsandals.bedwars.api.game.ItemSpawnerType;
import org.screamingsandals.bedwars.api.upgrades.Upgrade;
import org.screamingsandals.bedwars.api.upgrades.UpgradeRegistry;
import org.screamingsandals.bedwars.api.upgrades.UpgradeStorage;
import org.screamingsandals.bedwars.game.CurrentTeam;
import org.screamingsandals.bedwars.game.Game;
import org.screamingsandals.bedwars.game.GamePlayer;
import org.screamingsandals.bedwars.special.listener.PermaItemListener;
import org.screamingsandals.bedwars.utils.Debugger;
import org.screamingsandals.bedwars.utils.Sounds;
import org.screamingsandals.bedwars.lib.debug.Debug;
import org.screamingsandals.lib.material.Item;
import org.screamingsandals.lib.material.builder.ItemFactory;
import org.screamingsandals.lib.player.PlayerMapper;
import org.screamingsandals.lib.utils.AdventureHelper;
import org.screamingsandals.lib.utils.ConfigurateUtils;
import org.screamingsandals.simpleinventories.SimpleInventoriesCore;
import org.screamingsandals.simpleinventories.events.ItemRenderEvent;
import org.screamingsandals.simpleinventories.events.OnTradeEvent;
import org.screamingsandals.simpleinventories.events.PreClickEvent;
import org.screamingsandals.simpleinventories.inventory.Include;
import org.screamingsandals.simpleinventories.inventory.InventorySet;
import org.spongepowered.configurate.ConfigurationNode;

import java.io.File;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

import static org.screamingsandals.bedwars.lib.lang.I.i18nc;
import static org.screamingsandals.bedwars.lib.lang.I18n.i18nonly;

public class ShopInventory implements Listener {
    private final Map<String, InventorySet> shopMap = new HashMap<>();

    public ShopInventory() {
        Main.getInstance().registerBedwarsListener(this);

        var shopFileName = "shop.yml";
        if (Main.getConfigurator().node("turnOnExperimentalGroovyShop").getBoolean()) {
            shopFileName = "shop.groovy";
        }
        var shopFile = Main.getInstance().getPluginDescription().getDataFolder().resolve(shopFileName).toFile();
        if (!shopFile.exists()) {
            Main.getInstance().saveResource(shopFileName, false);
        }

        loadNewShop("default", null, true);
    }

    public void show(Player player, GameStore store) {
        try {
            boolean parent = true;
            String fileName = null;
            if (store != null) {
                parent = store.getUseParent();
                fileName = store.getShopFile();
            }
            if (fileName != null) {
                var file = normalizeShopFile(fileName);
                var name = (parent ? "+" : "-") + file.getAbsolutePath();
                if (!shopMap.containsKey(name)) {
                    loadNewShop(name, file, parent);
                }
                PlayerMapper.wrapPlayer(player).openInventory(shopMap.get(name));
            } else {
                PlayerMapper.wrapPlayer(player).openInventory(shopMap.get("default"));
            }
        } catch (Throwable ignored) {
            player.sendMessage(i18nonly("prefix") + " Your shop.yml/shop.groovy is invalid! Check it out or contact us on Discord.");
        }
    }

    public static File normalizeShopFile(String name) {
        var dataFolder = Main.getInstance().getPluginDescription().getDataFolder();
        if (name.split("\\.").length > 1) {
            return dataFolder.resolve(name).toFile();
        }

        var fileg = dataFolder.resolve(name + ".groovy").toFile();
        if (fileg.exists()) {
            return fileg;
        }
        return dataFolder.resolve(name + ".yml").toFile();
    }

    public void onGeneratingItem(ItemRenderEvent event) {
        var itemInfo = event.getItem();
        var item = itemInfo.getStack();
        var player = event.getPlayer().as(Player.class);
        var game = Main.getPlayerGameProfile(player).getGame();
        var prices = itemInfo.getOriginal().getPrices();
        if (!prices.isEmpty()) {
            // TODO: multi-price feature
            var priceObject = prices.get(0);
            var price = priceObject.getAmount();
            var type = Main.getSpawnerType(priceObject.getCurrency().toLowerCase());
            if (type == null) {
                return;
            }

            var enabled = itemInfo.getFirstPropertyByName("generateLore")
                    .map(property -> property.getPropertyData().getBoolean())
                    .orElseGet(() -> Main.getConfigurator().node("lore", "generate-automatically").getBoolean(true));

            if (enabled) {
                var loreText = itemInfo.getFirstPropertyByName("generatedLoreText")
                        .map(property -> property.getPropertyData().childrenList().stream().map(ConfigurationNode::getString))
                        .orElseGet(() -> Main.getConfigurator().node("lore", "text").childrenList().stream().map(ConfigurationNode::getString))
                        .filter(Objects::nonNull)
                        .map(s -> s.replaceAll("%price%", Integer.toString(price))
                                .replaceAll("%resource%", type.getItemName())
                                .replaceAll("%amount%", Integer.toString(item.getAmount())))
                        .map(AdventureHelper::toComponent)
                        .collect(Collectors.toList());

                item.getLore().addAll(loreText);
            }
        }

        itemInfo.getProperties().forEach(property -> {
            if (property.hasName()) {
                var converted = ConfigurateUtils.raw(property.getPropertyData());
                if (!(converted instanceof Map)) {
                    converted = DumpCommand.nullValuesAllowingMap("value", converted);
                }

                //noinspection unchecked
                var applyEvent = new BedwarsApplyPropertyToDisplayedItem(game,
                        player, item.as(ItemStack.class), property.getPropertyName(), (Map<String, Object>) converted);
                Bukkit.getServer().getPluginManager().callEvent(applyEvent);

                event.setStack(ItemFactory.build(applyEvent.getStack()).orElse(item));
            }
        });
    }

    public void onPreAction(PreClickEvent event) {
        if (event.isCancelled()) {
            return;
        }

        var player = event.getPlayer().as(Player.class);
        if (!Main.isPlayerInGame(player)) {
            event.setCancelled(true);
        }

        if (Main.getPlayerGameProfile(player).isSpectator) {
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

    @EventHandler
    public void onApplyPropertyToBoughtItem(BedwarsApplyPropertyToItem event) {
        if (event.getPropertyName().equalsIgnoreCase("applycolorbyteam")
                || event.getPropertyName().equalsIgnoreCase("transform::applycolorbyteam")) {
            Player player = event.getPlayer();
            CurrentTeam team = (CurrentTeam) event.getGame().getTeamOfPlayer(player);

            if (Main.getConfigurator().node("automatic-coloring-in-shop").getBoolean()) {
                event.setStack(Main.applyColor(team.teamInfo.color, event.getStack()));
            }
        }
    }

    @SneakyThrows
    private void loadDefault(InventorySet inventorySet) {
        inventorySet.getMainSubInventory().dropContents();
        inventorySet.getMainSubInventory().getWaitingQueue().add(Include.of(Path.of(ShopInventory.class.getResource("/shop.yml").toURI())));
        inventorySet.getMainSubInventory().process();
    }

    private void loadNewShop(String name, File file, boolean useParent) {
        var inventorySet = SimpleInventoriesCore.builder()
                .genericShop(true)
                .genericShopPriceTypeRequired(true)
                .animationsEnabled(true)
                .categoryOptions(localOptionsBuilder ->
                    localOptionsBuilder
                            .backItem(Main.getConfigurator().readDefinedItem("shopback", "BARRIER"), itemBuilder ->
                                itemBuilder.name(i18nonly("shop_back"))
                            )
                            .pageBackItem(Main.getConfigurator().readDefinedItem("pageback", "ARROW"), itemBuilder ->
                                itemBuilder.name(i18nonly("page_back"))
                            )
                            .pageForwardItem(Main.getConfigurator().readDefinedItem("pageforward", "BARRIER"), itemBuilder ->
                                itemBuilder.name(i18nonly("page_forward"))
                            )
                            .cosmeticItem(Main.getConfigurator().readDefinedItem("shopcosmetic", "AIR"))
                            .rows(Main.getConfigurator().node("shop", "rows").getInt(4))
                            .renderActualRows(Main.getConfigurator().node("shop", "render-actual-rows").getInt(6))
                            .renderOffset(Main.getConfigurator().node("shop", "render-offset").getInt(9))
                            .renderHeaderStart(Main.getConfigurator().node("shop", "render-header-start").getInt(0))
                            .renderFooterStart(Main.getConfigurator().node("shop", "render-footer-start").getInt(45))
                            .itemsOnRow(Main.getConfigurator().node("shop", "items-on-row").getInt(9))
                            .showPageNumber(Main.getConfigurator().node("shop", "show-page-numbers").getBoolean(true))
                            .inventoryType(Main.getConfigurator().node("shop", "inventory-type").getString("CHEST"))
                            .prefix(i18nonly("item_shop_name", "[BW] Shop"))
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
                    GamePlayer gPlayer = Main.getPlayerGameProfile(player.as(Player.class));
                    CurrentTeam team = gPlayer.getGame().getPlayerTeam(gPlayer);
                    if (arguments.length > 0) {
                        String fa = arguments[0];
                        switch (fa) {
                            case "color":
                                return team.teamInfo.color.name();
                            case "chatcolor":
                                return team.teamInfo.color.chatColor.toString();
                            case "maxplayers":
                                return Integer.toString(team.teamInfo.maxPlayers);
                            case "players":
                                return Integer.toString(team.players.size());
                            case "hasBed":
                                return Boolean.toString(team.isBed);
                        }
                    }
                    return team.getName();
                })
                .define("spawner", (key, player, playerItemInfo, arguments) -> {
                    GamePlayer gPlayer = Main.getPlayerGameProfile(player.as(Player.class));
                    Game game = gPlayer.getGame();
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
                                upgrades = upgradeStorage.findItemSpawnerUpgrades(game, game.getPlayerTeam(gPlayer));
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
                    if (useParent) {
                        var shopFileName = "shop.yml";
                        if (Main.getConfigurator().node("turnOnExperimentalGroovyShop").getBoolean(false)) {
                            shopFileName = "shop.groovy";
                        }
                        categoryBuilder.include(shopFileName);
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

    private static String getNameOrCustomNameOfItem(Item item) {
        try {
            if (item.getDisplayName() != null) {
                return AdventureHelper.toLegacy(item.getDisplayName());
            }
            if (item.getLocalizedName() != null) {
                return AdventureHelper.toLegacy(item.getLocalizedName());
            }
        } catch (Throwable ignored) {
        }

        var normalItemName = item.getMaterial().getPlatformName().replace("_", " ").toLowerCase();
        var sArray = normalItemName.split(" ");
        var stringBuilder = new StringBuilder();

        for (var s : sArray) {
            stringBuilder.append(Character.toUpperCase(s.charAt(0))).append(s.substring(1)).append(" ");
        }
        return stringBuilder.toString().trim();
    }

    private void handleBuy(OnTradeEvent event) {
        var player = event.getPlayer().as(Player.class);
        var game = Main.getPlayerGameProfile(player).getGame();
        var clickType = event.getClickType();
        var itemInfo = event.getItem();

        // TODO: multi-price feature
        var price = event.getPrices().get(0);
        ItemSpawnerType type = Main.getSpawnerType(price.getCurrency().toLowerCase());

        var newItem = event.getStack();

        var amount = newItem.getAmount();
        var priceAmount = price.getAmount();
        int inInventory = 0;

        var currencyChanger = itemInfo.getFirstPropertyByName("currencyChanger");
        if (currencyChanger.isPresent()) {
            var changeItemToName = currencyChanger.get().getPropertyData().getString();
            ItemSpawnerType changeItemType;
            if (changeItemToName == null) {
                return;
            }

            changeItemType = Main.getSpawnerType(changeItemToName.toLowerCase());
            if (changeItemType == null) {
                return;
            }

            newItem = ItemFactory.build(changeItemType.getStack()).orElse(newItem);
        }

        var originalMaxStackSize = newItem.getMaterial().as(Material.class).getMaxStackSize();
        if (clickType.isShiftClick() && originalMaxStackSize > 1) {
            double priceOfOne = (double) priceAmount / amount;
            double maxStackSize;
            int finalStackSize;

            for (ItemStack itemStack : player.getInventory().getStorageContents()) {
                if (itemStack != null && itemStack.isSimilar(type.getStack())) {
                    inInventory = inInventory + itemStack.getAmount();
                }
            }
            if (Main.getConfigurator().node("sell-max-64-per-click-in-shop").getBoolean()) {
                maxStackSize = Math.min(inInventory / priceOfOne, originalMaxStackSize);
            } else {
                maxStackSize = inInventory / priceOfOne;
            }

            finalStackSize = (int) maxStackSize;
            if (finalStackSize > amount) {
                priceAmount = (int) (priceOfOne * finalStackSize);
                newItem.setAmount(finalStackSize);
                amount = finalStackSize;
            }
        }

        var materialItem = ItemFactory.build(type.getStack(priceAmount)).orElseThrow();
        if (event.hasPlayerInInventory(materialItem)) {
            Map<String, Object> permaItemPropertyData = new HashMap<>();
            for (var property : itemInfo.getProperties()) {
                var converted = ConfigurateUtils.raw(property.getPropertyData());
                if (!(converted instanceof Map)) {
                    converted = DumpCommand.nullValuesAllowingMap("value", converted);
                }
                //noinspection unchecked
                var propertyData = (Map<String, Object>) converted;
                if (property.hasName()) {
                    var applyEvent = new BedwarsApplyPropertyToBoughtItem(game, player,
                            newItem.as(ItemStack.class), property.getPropertyName(), propertyData);
                    Bukkit.getServer().getPluginManager().callEvent(applyEvent);

                    newItem = ItemFactory.build(applyEvent.getStack()).orElse(newItem);
                }
                // Checks if the player is buying a permanent item. Setting name to empty string to prevent other listeners from erroring out.
                else if (propertyData.get(PermaItemListener.getPermItemPropKey()) != null) {
                    permaItemPropertyData = propertyData;
                }
            }

            if (!permaItemPropertyData.isEmpty()) {
                BedwarsApplyPropertyToBoughtItem applyEvent = new BedwarsApplyPropertyToBoughtItem(game, player,
                        newItem.as(ItemStack.class), "", permaItemPropertyData);
                Bukkit.getServer().getPluginManager().callEvent(applyEvent);
            }

            event.sellStack(materialItem);
            List<Item> notFit = event.buyStack(newItem);
            if (!notFit.isEmpty()) {
                notFit.forEach(stack -> player.getLocation().getWorld().dropItem(player.getLocation(), stack.as(ItemStack.class)));
            }

            if (!Main.getConfigurator().node("removePurchaseMessages").getBoolean()) {
                player.sendMessage(i18nc("buy_succes", game.getCustomPrefix()).replace("%item%", amount + "x " + getNameOrCustomNameOfItem(newItem))
                        .replace("%material%", priceAmount + " " + type.getItemName()));
            }
            Sounds.playSound(player, player.getLocation(),
                    Main.getConfigurator().node("sounds", "item_buy").getString(), Sounds.ENTITY_ITEM_PICKUP, 1, 1);
        } else {
            if (!Main.getConfigurator().node("removePurchaseMessages").getBoolean()) {
                player.sendMessage(i18nc("buy_failed", game.getCustomPrefix()).replace("%item%", amount + "x " + getNameOrCustomNameOfItem(newItem))
                        .replace("%material%", priceAmount + " " + type.getItemName()));
            }
        }
    }

    private void handleUpgrade(OnTradeEvent event) {
        var player = event.getPlayer().as(Player.class);
        var game = Main.getPlayerGameProfile(player).getGame();
        var itemInfo = event.getItem();

        // TODO: multi-price feature
        var price = event.getPrices().get(0);
        ItemSpawnerType type = Main.getSpawnerType(price.getCurrency().toLowerCase());

        var priceAmount = price.getAmount();

        var upgrade = itemInfo.getFirstPropertyByName("upgrade").orElseThrow();
        var itemName = upgrade.getPropertyData().node("shop-name").getString("UPGRADE");
        var entities = upgrade.getPropertyData().node("entities").childrenList();

        boolean sendToAll = false;
        boolean isUpgrade = true;
        var materialItem = ItemFactory.build(type.getStack(priceAmount)).orElseThrow();

        if (event.hasPlayerInInventory(materialItem)) {
            event.sellStack(materialItem);
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
                        ItemSpawnerType spawnerType = Main.getSpawnerType(mapSpawnerType);

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
                        Debugger.warn("[BedWars]> Upgrade configuration is invalid.");
                    }

                    if (isUpgrade) {
                        BedwarsUpgradeBoughtEvent bedwarsUpgradeBoughtEvent = new BedwarsUpgradeBoughtEvent(game,
                                upgradeStorage, upgrades, player, addLevels);
                        Bukkit.getPluginManager().callEvent(bedwarsUpgradeBoughtEvent);

                        if (bedwarsUpgradeBoughtEvent.isCancelled()) {
                            continue;
                        }

                        if (upgrades.isEmpty()) {
                            continue;
                        }

                        for (var anUpgrade : upgrades) {
                            BedwarsUpgradeImprovedEvent improvedEvent = new BedwarsUpgradeImprovedEvent(game,
                                    upgradeStorage, anUpgrade, anUpgrade.getLevel(), anUpgrade.getLevel() + addLevels);
                            Bukkit.getPluginManager().callEvent(improvedEvent);
                        }
                    }
                }

                if (sendToAll) {
                    for (Player player1 : game.getTeamOfPlayer(player).getConnectedPlayers()) {
                        if (!Main.getConfigurator().node("removePurchaseMessages").getBoolean()) {
                            player1.sendMessage(i18nc("buy_succes", game.getCustomPrefix()).replace("%item%", itemName).replace("%material%",
                                    priceAmount + " " + type.getItemName()));
                        }
                        Sounds.playSound(player1, player1.getLocation(),
                                Main.getConfigurator().node("sounds", "upgrade_buy").getString(),
                                Sounds.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 1);
                    }
                } else {
                    if (!Main.getConfigurator().node("removePurchaseMessages").getBoolean()) {
                        player.sendMessage(i18nc("buy_succes", game.getCustomPrefix()).replace("%item%", itemName).replace("%material%",
                                priceAmount + " " + type.getItemName()));
                    }
                    Sounds.playSound(player, player.getLocation(),
                            Main.getConfigurator().node("sounds", "upgrade_buy").getString(),
                            Sounds.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 1);
                }
            }
        } else {
            if (!Main.getConfigurator().node("removePurchaseMessages").getBoolean()) {
                player.sendMessage(i18nc("buy_failed", game.getCustomPrefix()).replace("%item%", "UPGRADE").replace("%material%",
                        priceAmount + " " + type.getItemName()));
            }
        }
    }
}
