/*
 * Copyright (C) 2023 ScreamingSandals
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

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.screamingsandals.bedwars.Main;
import org.screamingsandals.bedwars.api.Team;
import org.screamingsandals.bedwars.api.events.*;
import org.screamingsandals.bedwars.game.GameStore;
import org.screamingsandals.bedwars.api.game.ItemSpawnerType;
import org.screamingsandals.bedwars.api.upgrades.Upgrade;
import org.screamingsandals.bedwars.api.upgrades.UpgradeRegistry;
import org.screamingsandals.bedwars.api.upgrades.UpgradeStorage;
import org.screamingsandals.bedwars.game.CurrentTeam;
import org.screamingsandals.bedwars.game.Game;
import org.screamingsandals.bedwars.game.GamePlayer;
import org.screamingsandals.bedwars.utils.Debugger;
import org.screamingsandals.bedwars.utils.Sounds;
import org.screamingsandals.bedwars.lib.debug.Debug;
import org.screamingsandals.simpleinventories.SimpleInventories;
import org.screamingsandals.simpleinventories.events.GenerateItemEvent;
import org.screamingsandals.simpleinventories.events.PreActionEvent;
import org.screamingsandals.simpleinventories.events.ShopTransactionEvent;
import org.screamingsandals.simpleinventories.inventory.Options;
import org.screamingsandals.simpleinventories.item.ItemProperty;
import org.screamingsandals.simpleinventories.item.PlayerItemInfo;
import org.screamingsandals.simpleinventories.utils.MapReader;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;
import java.util.stream.Collectors;

import static org.screamingsandals.bedwars.lib.lang.I.i18nc;
import static org.screamingsandals.bedwars.lib.lang.I18n.i18n;
import static org.screamingsandals.bedwars.lib.lang.I18n.i18nonly;

public class ShopInventory implements Listener {
    private final Map<String, SimpleInventories> shopMap = new HashMap<>();
    private final Options options = new Options(Main.getInstance());

    public ShopInventory() {
        Bukkit.getServer().getPluginManager().registerEvents(this, Main.getInstance());

        ItemStack backItem = Main.getConfigurator().readDefinedItem("shopback", "BARRIER");
        ItemMeta backItemMeta = backItem.getItemMeta();
        backItemMeta.setDisplayName(i18n("shop_back", false));
        backItem.setItemMeta(backItemMeta);
        options.setBackItem(backItem);

        ItemStack pageBackItem = Main.getConfigurator().readDefinedItem("pageback", "ARROW");
        ItemMeta pageBackItemMeta = backItem.getItemMeta();
        pageBackItemMeta.setDisplayName(i18n("page_back", false));
        pageBackItem.setItemMeta(pageBackItemMeta);
        options.setPageBackItem(pageBackItem);

        ItemStack pageForwardItem = Main.getConfigurator().readDefinedItem("pageforward", "ARROW");
        ItemMeta pageForwardItemMeta = backItem.getItemMeta();
        pageForwardItemMeta.setDisplayName(i18n("page_forward", false));
        pageForwardItem.setItemMeta(pageForwardItemMeta);
        options.setPageForwardItem(pageForwardItem);

        ItemStack cosmeticItem = Main.getConfigurator().readDefinedItem("shopcosmetic", "AIR");
        options.setCosmeticItem(cosmeticItem);

        options.setRows(Main.getConfigurator().config.getInt("shop.rows", 4));
        options.setRender_actual_rows(Main.getConfigurator().config.getInt("shop.render-actual-rows", 6));
        options.setRender_offset(Main.getConfigurator().config.getInt("shop.render-offset", 9));
        options.setRender_header_start(Main.getConfigurator().config.getInt("shop.render-header-start", 0));
        options.setRender_footer_start(Main.getConfigurator().config.getInt("shop.render-footer-start", 45));
        options.setItems_on_row(Main.getConfigurator().config.getInt("shop.items-on-row", 9));
        options.setShowPageNumber(Main.getConfigurator().config.getBoolean("shop.show-page-numbers", true));
        options.setInventoryType(InventoryType.valueOf(Main.getConfigurator().config.getString("shop.inventory-type", "CHEST")));

        options.setPrefix(i18nonly("item_shop_name", "[BW] Shop"));
        options.setGenericShop(true);
        options.setGenericShopPriceTypeRequired(true);
        options.setAnimationsEnabled(true);
        options.setAllowAccessToConsole(Main.getConfigurator().config.getBoolean("shop.allow-execution-of-console-commands"));

        options.registerPlaceholder("team", (key, player, arguments) -> {
            GamePlayer gPlayer = Main.getPlayerGameProfile(player);
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
        });
        options.registerPlaceholder("spawner", (key, player, arguments) -> {
            GamePlayer gPlayer = Main.getPlayerGameProfile(player);
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
        });

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
                File file = normalizeShopFile(fileName);
                String name = (parent ? "+" : "-") + file.getAbsolutePath();
                if (!shopMap.containsKey(name)) {
                    loadNewShop(name, file, parent);
                }
                SimpleInventories shop = shopMap.get(name);
                shop.openForPlayer(player);
            } else {
                shopMap.get("default").openForPlayer(player);
            }
        } catch (Throwable throwable) {
            throwable.printStackTrace();
            player.sendMessage(i18nonly("prefix") + " Your shop.yml/shop.groovy is invalid! Check it out or contact us on Discord.");
        }
    }

    public static File normalizeShopFile(String name) {
        if (name.split("\\.").length > 1) {
            return Main.getInstance().getDataFolder().toPath().resolve(name).toFile();
        }

        File fileg = Main.getInstance().getDataFolder().toPath().resolve(name + ".groovy").toFile();
        if (fileg.exists()) {
            return fileg;
        }
        return Main.getInstance().getDataFolder().toPath().resolve(name + ".yml").toFile();
    }

    @EventHandler
    public void onGeneratingItem(GenerateItemEvent event) {
        if (!shopMap.containsValue(event.getFormat())) {
            return;
        }

        PlayerItemInfo item = event.getInfo();
        Player player = event.getPlayer();
        Game game = Main.getPlayerGameProfile(player).getGame();
        MapReader reader = item.getReader();
        if (reader.containsKey("price") && reader.containsKey("price-type")) {
            int price = reader.getInt("price");
            ItemSpawnerType type = Main.getSpawnerType((reader.getString("price-type")).toLowerCase());
            if (type == null) {
                return;
            }

            boolean enabled = Main.getConfigurator().config.getBoolean("lore.generate-automatically", true);
            enabled = reader.getBoolean("generate-lore", enabled);

            List<String> loreText = reader.getStringList("generated-lore-text",
                    Main.getConfigurator().config.getStringList("lore.text"))
                    .stream()
                    .map(lore -> ChatColor.translateAlternateColorCodes('&', lore))
                    .collect(Collectors.toList());

            if (enabled) {
                ItemStack stack = event.getStack();
                ItemMeta stackMeta = stack.getItemMeta();
                List<String> lore = new ArrayList<>();
                if (stackMeta.hasLore()) {
                    if (stackMeta.getLore() != null) {
                        lore = stackMeta.getLore();
                    }
                }
                for (String s : loreText) {
                    s = s.replace("%price%", Integer.toString(price));
                    s = s.replace("%resource%", type.getItemName());
                    s = s.replace("%amount%", Integer.toString(stack.getAmount()));
                    lore.add(s);
                }
                stackMeta.setLore(lore);
                stack.setItemMeta(stackMeta);
                event.setStack(stack);
            }
            if (item.hasProperties()) {
                for (ItemProperty property : item.getProperties()) {
                    if (property.hasName()) {
                        ItemStack newItem = event.getStack();
                        BedwarsApplyPropertyToDisplayedItem applyEvent = new BedwarsApplyPropertyToDisplayedItem(game,
                                player, newItem, property.getReader(player).convertToMap());
                        Main.getInstance().getServer().getPluginManager().callEvent(applyEvent);

                        event.setStack(newItem);
                    }
                }
            }
        }

    }

    @EventHandler
    public void onPreAction(PreActionEvent event) {
        if (!shopMap.containsValue(event.getFormat()) || event.isCancelled()) {
            return;
        }

        if (!Main.isPlayerInGame(event.getPlayer())) {
            event.setCancelled(true);
        }

        if (Main.getPlayerGameProfile(event.getPlayer()).isSpectator) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onShopTransaction(ShopTransactionEvent event) {
        if (!shopMap.containsValue(event.getFormat()) || event.isCancelled()) {
            return;
        }
        Game game = Main.getPlayerGameProfile(event.getPlayer()).getGame();

        MapReader reader = event.getItem().getReader();
        if (reader.containsKey("upgrade")) {
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

            if (Main.getConfigurator().config.getBoolean("automatic-coloring-in-shop")) {
                event.setStack(Main.applyColor(team.teamInfo.color, event.getStack()));
            }
        }
    }

    @SuppressWarnings("unchecked")
    private void loadDefault(SimpleInventories format) {
        format.purgeData();
        YamlConfiguration configuration = new YamlConfiguration();
        try {
            configuration.load(new InputStreamReader(ShopInventory.class.getResourceAsStream("/shop.yml")));
        } catch (IOException | InvalidConfigurationException ioException) {
            ioException.printStackTrace();
        }
        format.load((List<Object>) configuration.getList("data"));
    }

    private void loadNewShop(String name, File file, boolean useParent) {
        SimpleInventories format = new SimpleInventories(options);
        try {
            if (useParent) {
                String shopFileName = "shop.yml";
                if (Main.getConfigurator().config.getBoolean("turnOnExperimentalGroovyShop", false)) {
                    shopFileName = "shop.groovy";
                }
                format.loadFromDataFolder(Main.getInstance().getDataFolder(), shopFileName);
            }
            if (file != null) {
                format.load(file);
            }
        } catch (Exception ex) {
            Debug.warn("Wrong shop.yml/shop.groovy configuration!", true);
            Debug.warn("Check validity of your YAML/Groovy!", true);
            ex.printStackTrace();
            loadDefault(format);
        }

        try {
            format.generateData();
        } catch (Throwable t) {
            t.printStackTrace();
            Debug.warn("Your shop.yml/shop.groovy is wrong! Loading default one instead", true);
            loadDefault(format);
            format.generateData();
        }
        shopMap.put(name, format);
    }

    private static String getNameOrCustomNameOfItem(ItemStack stack) {
        try {
            if (stack.hasItemMeta()) {
                ItemMeta meta = stack.getItemMeta();
                if (meta == null) {
                    return "";
                }

                if (meta.hasDisplayName()) {
                    return meta.getDisplayName();
                }
                if (meta.hasLocalizedName()) {
                    return meta.getLocalizedName();
                }
            }
        } catch (Throwable ignored) {
        }

        String normalItemName = stack.getType().name().replace("_", " ").toLowerCase();
        String[] sArray = normalItemName.split(" ");
        StringBuilder stringBuilder = new StringBuilder();

        for (String s : sArray) {
            stringBuilder.append(Character.toUpperCase(s.charAt(0))).append(s.substring(1)).append(" ");
        }
        return stringBuilder.toString().trim();
    }

    private void handleBuy(ShopTransactionEvent event) {
        Player player = event.getPlayer();
        Game game = Main.getPlayerGameProfile(event.getPlayer()).getGame();
        ClickType clickType = event.getClickType();
        MapReader mapReader = event.getItem().getReader();
        String priceType = event.getType().toLowerCase();
        ItemSpawnerType type = Main.getSpawnerType(priceType);
        ItemStack newItem = event.getStack();

        int amount = newItem.getAmount();
        int price = event.getPrice();
        int inInventory = 0;

        if (type == null) {
            Debug.warn("Player " + player.getName() + " attempted to buy an item that costs an unavailable resource! Make sure your shop uses only resources which are available on the map.", true);
            player.sendMessage(i18nc("buy_failed", game.getCustomPrefix()).replace("%item%", amount + "x " + getNameOrCustomNameOfItem(newItem))
                    .replace("%material%", price + " " + event.getType()));
            return;
        }

        final BedwarsItemBoughtEvent itemBoughtEvent = new BedwarsItemBoughtEvent(game, player, newItem, price);
        Bukkit.getPluginManager().callEvent(itemBoughtEvent);
        if (itemBoughtEvent.isCancelled()) {
            return;
        }

        if (mapReader.containsKey("currency-changer")) {
            String changeItemToName = mapReader.getString("currency-changer");
            if (changeItemToName == null) {
                return;
            }

            String[] split = changeItemToName.trim().split(" ", 2);
            if (split.length == 2) {
                try {
                    amount = Integer.parseInt(split[0]);
                    changeItemToName = split[1].trim();
                    if (changeItemToName.startsWith("of ")) {
                        changeItemToName = changeItemToName.substring(3).trim();
                    }
                } catch (NumberFormatException ignored) {
                }
            }
            ItemSpawnerType changeItemType = Main.getSpawnerType(changeItemToName);
            if (changeItemType == null) {
                return;
            }

            newItem = changeItemType.getStack(amount);
        }

        if (!event.isHasExecutions() && clickType.isShiftClick() && newItem.getMaxStackSize() > 1) {
            double priceOfOne = (double) price / amount;
            double maxStackSize;
            int finalStackSize;

            try {
                for (ItemStack itemStack : event.getPlayer().getInventory().getStorageContents()) {
                    if (itemStack != null && itemStack.isSimilar(type.getStack())) {
                        inInventory = inInventory + itemStack.getAmount();
                    }
                }
            } catch (Throwable ignored) {
                // 1.8.8: let's just hope no one will make chestplate as a currency :skull:
                for (ItemStack itemStack : event.getPlayer().getInventory().getContents()) {
                    if (itemStack != null && itemStack.isSimilar(type.getStack())) {
                        inInventory = inInventory + itemStack.getAmount();
                    }
                }
            }
            if (Main.getConfigurator().config.getBoolean("sell-max-64-per-click-in-shop")) {
                maxStackSize = Math.min(inInventory / priceOfOne, newItem.getMaxStackSize());
            } else {
                maxStackSize = inInventory / priceOfOne;
            }

            finalStackSize = (int) maxStackSize;
            if (finalStackSize > amount) {
                price = (int) (priceOfOne * finalStackSize);
                newItem.setAmount(finalStackSize);
                amount = finalStackSize;
            }
        }

        ItemStack materialItem = type.getStack(price);
        if (event.hasPlayerInInventory(materialItem)) {
            if (event.hasProperties()) {
                for (ItemProperty property : event.getProperties()) {
                    if (property.hasName()) {
                        BedwarsApplyPropertyToBoughtItem applyEvent = new BedwarsApplyPropertyToBoughtItem(game, player,
                                newItem, property.getReader(player).convertToMap());
                        Main.getInstance().getServer().getPluginManager().callEvent(applyEvent);

                        newItem = applyEvent.getStack();
                    }
                }
            }

            event.sellStack(materialItem);
            if (event.isHasExecutions()) {
                event.setRunExecutions(true); // SIv1 will handle that when this is set to true
            } else {
                Map<Integer, ItemStack> notFit = event.buyStack(newItem);
                if (!notFit.isEmpty()) {
                    notFit.forEach((i, stack) -> player.getLocation().getWorld().dropItem(player.getLocation(), stack));
                }
            }

            if (!Main.getConfigurator().config.getBoolean("removePurchaseMessages", false)) {
                player.sendMessage(i18nc("buy_succes", game.getCustomPrefix()).replace("%item%", amount + "x " + getNameOrCustomNameOfItem(newItem))
                        .replace("%material%", price + " " + type.getItemName()));
            }
            Sounds.playSound(player, player.getLocation(),
                    Main.getConfigurator().config.getString("sounds.item_buy.sound"), Sounds.ENTITY_ITEM_PICKUP, (float) Main.getConfigurator().config.getDouble("sounds.item_buy.volume"), (float) Main.getConfigurator().config.getDouble("sounds.item_buy.pitch"));
        } else {
            if (!Main.getConfigurator().config.getBoolean("removePurchaseFailedMessages", false)) {
                player.sendMessage(i18nc("buy_failed", game.getCustomPrefix()).replace("%item%", amount + "x " + getNameOrCustomNameOfItem(newItem))
                        .replace("%material%", price + " " + type.getItemName()));
            }
        }
    }

    private void handleUpgrade(ShopTransactionEvent event) {
        Player player = event.getPlayer();
        Game game = Main.getPlayerGameProfile(event.getPlayer()).getGame();
        MapReader mapReader = event.getItem().getReader();
        String priceType = event.getType().toLowerCase();
        ItemSpawnerType itemSpawnerType = Main.getSpawnerType(priceType);

        MapReader upgradeMapReader = mapReader.getMap("upgrade");
        List<MapReader> entities = upgradeMapReader.getMapList("entities");
        String itemName = upgradeMapReader.getString("shop-name", "UPGRADE");

        int price = event.getPrice();
        boolean sendToAll = false;
        boolean isUpgrade = true;
        double maxLevel = 0.0;
        double newLevel = 0.0;
        ItemStack materialItem = itemSpawnerType.getStack(price);

        if (event.hasPlayerInInventory(materialItem)) {
            for (MapReader mapEntity : entities) {
                String configuredType = mapEntity.getString("type");
                if (configuredType == null) {
                    return;
                }

                UpgradeStorage upgradeStorage = UpgradeRegistry.getUpgrade(configuredType);
                if (upgradeStorage != null) {

                    // TODO: Learn SimpleGuiFormat upgrades pre-parsing and automatic renaming old
                    // variables
                    Team team = game.getTeamOfPlayer(event.getPlayer());
                    double addLevels = mapEntity.getDouble("add-levels",
                            mapEntity.getDouble("levels", 0) /* Old configuration */);
                    maxLevel = mapEntity.getDouble("max-level", 0.0);

                    /* You shouldn't use it in entities */
                    if (mapEntity.containsKey("shop-name")) {
                        itemName = mapEntity.getString("shop-name");
                    }
                    sendToAll = mapEntity.getBoolean("notify-team", false);

                    List<Upgrade> upgrades = new ArrayList<>();

                    if (mapEntity.containsKey("spawner-name")) {
                        String customName = mapEntity.getString("spawner-name");
                        upgrades = upgradeStorage.findItemSpawnerUpgrades(game, customName);
                    } else if (mapEntity.containsKey("spawner-type")) {
                        String mapSpawnerType = mapEntity.getString("spawner-type");
                        ItemSpawnerType spawnerType = Main.getSpawnerType(mapSpawnerType);

                        upgrades = upgradeStorage.findItemSpawnerUpgrades(game, team, spawnerType);
                    } else if (mapEntity.containsKey("team-upgrade")) {
                        boolean upgradeAllSpawnersInTeam = mapEntity.getBoolean("team-upgrade");

                        if (upgradeAllSpawnersInTeam) {
                            upgrades = upgradeStorage.findItemSpawnerUpgrades(game, team);
                        }

                    } else if (mapEntity.containsKey("customName")) { // Old configuration
                        String customName = mapEntity.getString("customName");
                        upgrades = upgradeStorage.findItemSpawnerUpgrades(game, customName);
                    } else {
                        isUpgrade = false;
                        Debugger.warn("[BedWars]> Upgrade configuration is invalid.");
                    }

                    if (isUpgrade) {
                        for (Upgrade upgrade : upgrades) {
                            if (upgrade.getLevel() + addLevels > maxLevel && maxLevel > 0.0) {
                                player.sendMessage(i18nc("spawner_reached_maximum_level", game.getCustomPrefix())
                                        .replace("%item%", itemName)
                                        .replace("%material%", price + " " + itemSpawnerType.getItemName())
                                        .replace("%maxLevel%", Double.toString(maxLevel)));
                                return;
                            }
                        }

                        event.sellStack(materialItem);
                        BedwarsUpgradeBoughtEvent bedwarsUpgradeBoughtEvent = new BedwarsUpgradeBoughtEvent(game,
                                upgradeStorage, upgrades, player, addLevels);
                        Bukkit.getPluginManager().callEvent(bedwarsUpgradeBoughtEvent);

                        if (bedwarsUpgradeBoughtEvent.isCancelled()) {
                            continue;
                        }

                        if (upgrades.isEmpty()) {
                            continue;
                        }

                        for (Upgrade upgrade : upgrades) {
                            newLevel = upgrade.getLevel() + addLevels;
                            BedwarsUpgradeImprovedEvent improvedEvent = new BedwarsUpgradeImprovedEvent(game,
                                    upgradeStorage, upgrade, upgrade.getLevel(), newLevel);
                            Bukkit.getPluginManager().callEvent(improvedEvent);
                        }
                    }
                }

                if (sendToAll) {
                    for (Player player1 : game.getTeamOfPlayer(event.getPlayer()).getConnectedPlayers()) {
                        if (!Main.getConfigurator().config.getBoolean("removeUpgradeMessages", false)) {
                            player1.sendMessage(i18nc("upgrade_success", game.getCustomPrefix())
                                    .replace("%name%", player.getDisplayName())
                                    .replace("%spawner%", itemSpawnerType.getItemName())
                                    .replace("%level%", Double.toString(newLevel)));
                        }
                        Sounds.playSound(player1, player1.getLocation(),
                                Main.getConfigurator().config.getString("sounds.upgrade_buy.sound"),
                                Sounds.ENTITY_EXPERIENCE_ORB_PICKUP, (float) Main.getConfigurator().config.getDouble("sounds.upgrade_buy.volume"), (float) Main.getConfigurator().config.getDouble("sounds.upgrade_buy.pitch"));
                    }
                } else {
                    if (!Main.getConfigurator().config.getBoolean("removeUpgradeMessages", false)) {
                        player.sendMessage(i18nc("upgrade_success", game.getCustomPrefix())
                                .replace("%name%", player.getName())
                                .replace("%spawner%", itemSpawnerType.getItemName())
                                .replace("%level%", Double.toString(newLevel)));
                    }
                    Sounds.playSound(player, player.getLocation(),
                            Main.getConfigurator().config.getString("sounds.upgrade_buy.sound"),
                            Sounds.ENTITY_EXPERIENCE_ORB_PICKUP,  (float) Main.getConfigurator().config.getDouble("sounds.upgrade_buy.volume"), (float) Main.getConfigurator().config.getDouble("sounds.upgrade_buy.pitch"));
                }
            }
        } else {
            if (!Main.getConfigurator().config.getBoolean("removePurchaseFailedMessages", false)) {
                player.sendMessage(i18nc("buy_failed", game.getCustomPrefix()).replace("%item%", i18nonly("upgrade_translate")).replace("%material%",
                        price + " " + itemSpawnerType.getItemName()));
            }
        }
    }
}