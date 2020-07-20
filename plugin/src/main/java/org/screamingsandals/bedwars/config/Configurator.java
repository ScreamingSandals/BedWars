package org.screamingsandals.bedwars.config;

import org.screamingsandals.bedwars.Main;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Material;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.screamingsandals.lib.debug.Debug;
import org.screamingsandals.simpleinventories.utils.StackParser;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

public class Configurator {
    public File configFile, shopFile, signsFile, recordFile, langFolder;
    public FileConfiguration config, recordConfig;

    public final File dataFolder;
    public final Main main;

    public Configurator(Main main) {
        this.dataFolder = main.getDataFolder();
        this.main = main;
    }

    public void createFiles() {
        dataFolder.mkdirs();

        configFile = new File(dataFolder, "config.yml");
        signsFile = new File(dataFolder, "sign.yml");
        recordFile = new File(dataFolder, "record.yml");
        langFolder = new File(dataFolder.toString(), "languages");

        config = new YamlConfiguration();
        recordConfig = new YamlConfiguration();

        if (!configFile.exists()) {
            try {
                configFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (!signsFile.exists()) {
            try {
                signsFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (!recordFile.exists()) {
            try {
                recordFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (!langFolder.exists()) {
            langFolder.mkdirs();

            File[] listOfFiles = dataFolder.listFiles();
            if (listOfFiles != null && listOfFiles.length > 0) {
                for (File file : listOfFiles) {
                    if (file.isFile() && file.getName().startsWith("messages_") && file.getName().endsWith(".yml")) {
                    	File dest = new File(langFolder, "language_" + file.getName().substring(9));
                    	file.renameTo(dest);
                    }
                }
            }
        }

        try {
            config.load(configFile);
            recordConfig.load(recordFile);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }

        String shopFileName = "shop.yml";
        if (config.getBoolean("turnOnExperimentalGroovyShop", false)) {
            shopFileName = "shop.groovy";
        }
        shopFile = new File(dataFolder, shopFileName);
        if (!shopFile.exists()) {
            main.saveResource(shopFileName, false);
        }

        AtomicBoolean modify = new AtomicBoolean(false);
        checkOrSetConfig(modify, "locale", "en");
        checkOrSetConfig(modify, "debug", false);

        checkOrSetConfig(modify, "allow-crafting", false);
        checkOrSetConfig(modify, "keep-inventory-on-death", false);
        checkOrSetConfig(modify, "in-lobby-colored-leather-by-team", true);
        checkOrSetConfig(modify, "jointeam-entity-show-name", true);
        checkOrSetConfig(modify, "friendlyfire", false);
        checkOrSetConfig(modify, "player-drops", true);
        checkOrSetConfig(modify, "join-randomly-after-lobby-timeout", false);
        checkOrSetConfig(modify, "prevent-killing-villagers", true);
        checkOrSetConfig(modify, "compass-enabled", true);
        checkOrSetConfig(modify, "join-randomly-on-lobby-join", false);
        checkOrSetConfig(modify, "add-wool-to-inventory-on-join", true);
        checkOrSetConfig(modify, "prevent-spawning-mobs", true);
        checkOrSetConfig(modify, "spawner-holograms", true);
        checkOrSetConfig(modify, "spawner-disable-merge", true);
        checkOrSetConfig(modify, "prevent-lobby-spawn-mobs-in-radius", 16);
        checkOrSetConfig(modify, "spawner-holo-height", 0.25);
        checkOrSetConfig(modify, "spawner-holograms-countdown", true);
        checkOrSetConfig(modify, "damage-when-player-is-not-in-arena", false);
        checkOrSetConfig(modify, "remove-unused-target-blocks", true);
        checkOrSetConfig(modify, "allow-block-falling", true);
        checkOrSetConfig(modify, "game-start-items", false);
        checkOrSetConfig(modify, "player-respawn-items", false);
        checkOrSetConfig(modify, "gived-game-start-items", new ArrayList<>());
        checkOrSetConfig(modify, "gived-player-respawn-items", new ArrayList<>());
        checkOrSetConfig(modify, "disable-hunger", false);
        checkOrSetConfig(modify, "automatic-coloring-in-shop", true);
        checkOrSetConfig(modify, "sell-max-64-per-click-in-shop", true);
        checkOrSetConfig(modify, "destroy-placed-blocks-by-explosion-except", "");
        checkOrSetConfig(modify, "destroy-placed-blocks-by-explosion", true);
        checkOrSetConfig(modify, "holo-above-bed", true);
        checkOrSetConfig(modify, "allow-spectator-join", false);
        checkOrSetConfig(modify, "disable-server-message.player-join", false);
        checkOrSetConfig(modify, "disable-server-message.player-leave", false);
        checkOrSetConfig(modify, "disable-flight", true);
        checkOrSetConfig(modify, "respawn-cooldown.enabled", true);
        checkOrSetConfig(modify, "respawn-cooldown.time", 5);
        checkOrSetConfig(modify, "stop-team-spawners-on-die", false);

        checkOrSetConfig(modify, "allowed-commands", new ArrayList<>());
        checkOrSetConfig(modify, "change-allowed-commands-to-blacklist", false);

        checkOrSetConfig(modify, "bungee.enabled", false);
        checkOrSetConfig(modify, "bungee.serverRestart", true);
        checkOrSetConfig(modify, "bungee.serverStop", false);
        checkOrSetConfig(modify, "bungee.server", "hub");
        checkOrSetConfig(modify, "bungee.auto-game-connect", false);

        checkOrSetConfig(modify, "farmBlocks.enable", false);
        checkOrSetConfig(modify, "farmBlocks.blocks", new ArrayList<>());

        checkOrSetConfig(modify, "scoreboard.enable", true);
        checkOrSetConfig(modify, "scoreboard.title", "§a%game%§r - %time%");
        checkOrSetConfig(modify, "scoreboard.bedLost", "§c\u2718");
        checkOrSetConfig(modify, "scoreboard.anchorEmpty", "§e\u2718");
        checkOrSetConfig(modify, "scoreboard.bedExists", "§a\u2714");
        checkOrSetConfig(modify, "scoreboard.teamTitle", "%bed%%color%%team%");

        checkOrSetConfig(modify, "title.enabled", true);
        checkOrSetConfig(modify, "title.fadeIn", 0);
        checkOrSetConfig(modify, "title.stay", 20);
        checkOrSetConfig(modify, "title.fadeOut", 0);
        
        checkOrSetConfig(modify, "shop.rows", 4);
        checkOrSetConfig(modify, "shop.render-actual-rows", 6);
        checkOrSetConfig(modify, "shop.render-offset", 9);
        checkOrSetConfig(modify, "shop.render-header-start", 0);
        checkOrSetConfig(modify, "shop.render-footer-start", 45);
        checkOrSetConfig(modify, "shop.items-on-row", 9);
        checkOrSetConfig(modify, "shop.show-page-numbers", true);
        checkOrSetConfig(modify, "shop.inventory-type", "CHEST");
        checkOrSetConfig(modify, "shop.citizens-enabled", false);
        
        checkOrSetConfig(modify, "items.jointeam", "COMPASS");
        checkOrSetConfig(modify, "items.leavegame", "SLIME_BALL");
        checkOrSetConfig(modify, "items.startgame", "DIAMOND");
        checkOrSetConfig(modify, "items.shopback", "BARRIER");
        checkOrSetConfig(modify, "items.shopcosmetic",
                Main.isLegacy() ? new ItemStack(Material.getMaterial("STAINED_GLASS_PANE"), 1, (short) 7)
                        : "GRAY_STAINED_GLASS_PANE");
        checkOrSetConfig(modify, "items.pageback", "ARROW");
        checkOrSetConfig(modify, "items.pageforward", "ARROW");
        checkOrSetConfig(modify, "items.team-select",
                Main.isLegacy() ? new ItemStack(Material.getMaterial("WOOL"), 1, (short) 1)
                        : "WHITE_WOOL");

        checkOrSetConfig(modify, "vault.enable", true);
        checkOrSetConfig(modify, "vault.reward.kill", 5);
        checkOrSetConfig(modify, "vault.reward.win", 20);

        checkOrSetConfig(modify, "resources", new HashMap<String, Object>() {
            {
                put("bronze", new HashMap<String, Object>() {
                    {
                        put("material", Main.isLegacy() ? "CLAY_BRICK" : "BRICK");
                        /* Config migration 1 -> 2: if spawners.bronze exists, get this value */
                        put("interval", config.getInt("spawners.bronze", 1));
                        put("name", "Bronze");
                        put("translate", "resource_bronze");
                        put("color", "DARK_RED");
                        put("spread", 1.0);
                    }
                });
                put("iron", new HashMap<String, Object>() {
                    {
                        put("material", "IRON_INGOT");
                        /* Config migration 1 -> 2: if spawners.iron exists, get this value */
                        put("interval", config.getInt("spawners.iron", 10));
                        put("name", "Iron");
                        put("translate", "resource_iron");
                        put("color", "GRAY");
                        put("spread", 1.0);
                    }
                });
                put("gold", new HashMap<String, Object>() {
                    {
                        put("material", "GOLD_INGOT");
                        /* Config migration 1 -> 2: if spawners.gold exists, get this value */
                        put("interval", config.getInt("spawners.gold", 20));
                        put("name", "Gold");
                        put("translate", "resource_gold");
                        put("color", "GOLD");
                        put("spread", 1.0);
                    }
                });
            }
        });
        if (config.contains("spawners")) {
            /* Config migration 1 -> 2: After creating resources, remove old spawners */
            modify.set(true);
            config.set("spawners", null);
        }

        checkOrSetConfig(modify, "respawn.protection-enabled", true);
        checkOrSetConfig(modify, "respawn.protection-time", 10);

        checkOrSetConfig(modify, "specials.action-bar-messages", true);
        checkOrSetConfig(modify, "specials.rescue-platform.is-breakable", false);
        checkOrSetConfig(modify, "specials.rescue-platform.delay", 0);
        checkOrSetConfig(modify, "specials.rescue-platform.break-time", 10);
        checkOrSetConfig(modify, "specials.rescue-platform.distance", 1);
        checkOrSetConfig(modify, "specials.rescue-platform.material", "GLASS");
        checkOrSetConfig(modify, "specials.protection-wall.is-breakable", false);
        checkOrSetConfig(modify, "specials.protection-wall.delay", 20);
        checkOrSetConfig(modify, "specials.protection-wall.break-time", 0);
        checkOrSetConfig(modify, "specials.protection-wall.width", 5);
        checkOrSetConfig(modify, "specials.protection-wall.height", 3);
        checkOrSetConfig(modify, "specials.protection-wall.distance", 2);
        checkOrSetConfig(modify, "specials.protection-wall.material", Main.isLegacy() ? "SANDSTONE" : "CUT_SANDSTONE");
        checkOrSetConfig(modify, "specials.tnt-sheep.speed", 2.0);
        checkOrSetConfig(modify, "specials.tnt-sheep.follow-range", 10.0);
        checkOrSetConfig(modify, "specials.tnt-sheep.max-target-distance", 32);
        checkOrSetConfig(modify, "specials.tnt-sheep.explosion-time", 8);
        checkOrSetConfig(modify, "specials.arrow-blocker.protection-time", 10);
        checkOrSetConfig(modify, "specials.arrow-blocker.delay", 5);
        checkOrSetConfig(modify, "specials.warp-powder.teleport-time", 6);
        checkOrSetConfig(modify, "specials.warp-powder.delay", 0);
        checkOrSetConfig(modify, "specials.magnet-shoes.probability", 75);
        checkOrSetConfig(modify, "specials.golem.speed", 0.25);
        checkOrSetConfig(modify, "specials.golem.follow-range", 10);
        checkOrSetConfig(modify, "specials.golem.health", 20);
        checkOrSetConfig(modify, "specials.golem.name-format", "%teamcolor%%team% Golem");
        checkOrSetConfig(modify, "specials.golem.show-name", true);
        checkOrSetConfig(modify, "specials.golem.delay", 0);
        checkOrSetConfig(modify, "specials.golem.collidable", false);
        checkOrSetConfig(modify, "specials.teamchest.turn-all-enderchests-to-teamchests", true);
        checkOrSetConfig(modify, "specials.throwable-fireball.explosion", 3.0);

        checkOrSetConfig(modify, "tnt.auto-ignite", false);
        checkOrSetConfig(modify, "tnt.explosion-time", 8);
        checkOrSetConfig(modify, "tnt.dont-damage-placer", false);

        checkOrSetConfig(modify, "sounds.on_bed_destroyed", "ENTITY_ENDER_DRAGON_GROWL");
        checkOrSetConfig(modify, "sounds.on_countdown", "UI_BUTTON_CLICK");
        checkOrSetConfig(modify, "sounds.on_game_start", "ENTITY_PLAYER_LEVELUP");
        checkOrSetConfig(modify, "sounds.on_team_kill", "ENTITY_PLAYER_LEVELUP");
        checkOrSetConfig(modify, "sounds.on_player_kill", "ENTITY_PLAYER_BIG_FALL");
        checkOrSetConfig(modify, "sounds.on_item_buy", "ENTITY_ITEM_PICKUP");
        checkOrSetConfig(modify, "sounds.on_upgrade_buy", "ENTITY_EXPERIENCE_ORB_PICKUP");
        checkOrSetConfig(modify, "sounds.on_respawn_cooldown_wait", "UI_BUTTON_CLICK");
        checkOrSetConfig(modify, "sounds.on_respawn_cooldown_done", "ENTITY_PLAYER_LEVELUP");

        checkOrSetConfig(modify, "game-effects.end", new HashMap<String, Object>() {
            {
                put("type", "Firework");
                put("power", 1);
                put("effects", new ArrayList<Object>() {
                    {
                        add(FireworkEffect.builder().with(FireworkEffect.Type.BALL).trail(false).flicker(false)
                                .withColor(Color.WHITE).withFade(Color.WHITE).build());
                    }
                });
            }
        });
        checkOrSetConfig(modify, "game-effects.start", new HashMap<String, Object>());
        checkOrSetConfig(modify, "game-effects.kill", new HashMap<String, Object>());
        checkOrSetConfig(modify, "game-effects.teamkill", new HashMap<String, Object>());
        checkOrSetConfig(modify, "game-effects.lobbyjoin", new HashMap<String, Object>());
        checkOrSetConfig(modify, "game-effects.lobbyleave", new HashMap<String, Object>());
        checkOrSetConfig(modify, "game-effects.respawn", new HashMap<String, Object>());
        checkOrSetConfig(modify, "game-effects.beddestroy", new HashMap<String, Object>());
        checkOrSetConfig(modify, "game-effects.warppowdertick", new HashMap<String, Object>());

        checkOrSetConfig(modify, "lobby-scoreboard.enabled", true);
        checkOrSetConfig(modify, "lobby-scoreboard.title", "§eBEDWARS");
        checkOrSetConfig(modify, "lobby-scoreboard.content", Arrays.asList(" ", "§fMap: §2%arena%",
                "§fPlayers: §2%players%§f/§2%maxplayers%", " ", "§fWaiting ...", " "));

        checkOrSetConfig(modify, "statistics.enabled", true);
        checkOrSetConfig(modify, "statistics.type", "yaml");
        checkOrSetConfig(modify, "statistics.show-on-game-end", false);
        checkOrSetConfig(modify, "statistics.bed-destroyed-kills", false);
        checkOrSetConfig(modify, "statistics.scores.kill", 10);
        checkOrSetConfig(modify, "statistics.scores.die", 0);
        checkOrSetConfig(modify, "statistics.scores.win", 50);
        checkOrSetConfig(modify, "statistics.scores.bed-destroy", 25);
        checkOrSetConfig(modify, "statistics.scores.lose", 0);
        checkOrSetConfig(modify, "statistics.scores.record", 100);

        checkOrSetConfig(modify, "database.host", "localhost");
        checkOrSetConfig(modify, "database.port", 3306);
        checkOrSetConfig(modify, "database.db", "databse");
        checkOrSetConfig(modify, "database.user", "root");
        checkOrSetConfig(modify, "database.password", "secret");
        checkOrSetConfig(modify, "database.table-prefix", "bw_");
        checkOrSetConfig(modify, "database.useSSL", false);

        checkOrSetConfig(modify, "bossbar.use-xp-bar", false);
        checkOrSetConfig(modify, "bossbar.lobby.enable", true);
        checkOrSetConfig(modify, "bossbar.lobby.color", "YELLOW");
        checkOrSetConfig(modify, "bossbar.lobby.style", "SEGMENTED_20");
        checkOrSetConfig(modify, "bossbar.game.enable", true);
        checkOrSetConfig(modify, "bossbar.game.color", "GREEN");
        checkOrSetConfig(modify, "bossbar.game.style", "SEGMENTED_20");

        checkOrSetConfig(modify, "holograms.enabled", true);
        checkOrSetConfig(modify, "holograms.headline", "Your §eBEDWARS§f stats");

        checkOrSetConfig(modify, "chat.override", true);
        checkOrSetConfig(modify, "chat.format", "<%teamcolor%%name%§r> ");
        checkOrSetConfig(modify, "chat.separate-chat.lobby", config.get("chat.separate-game-chat", false));
        checkOrSetConfig(modify, "chat.separate-chat.game", config.get("chat.separate-game-chat", false));
        if (config.isSet("chat.separate-game-chat")) {
        	config.set("chat.separate-game-chat", null);
        }
        
        checkOrSetConfig(modify, "chat.send-death-messages-just-in-game", true);
        checkOrSetConfig(modify, "chat.send-custom-death-messages", true);
        checkOrSetConfig(modify, "chat.default-team-chat-while-running", true);
        checkOrSetConfig(modify, "chat.all-chat-prefix", "@a");
        checkOrSetConfig(modify, "chat.team-chat-prefix", "@t");
        checkOrSetConfig(modify, "chat.all-chat", "[ALL] ");
        checkOrSetConfig(modify, "chat.team-chat", "[TEAM] ");
        checkOrSetConfig(modify, "chat.death-chat", "[DEATH] ");

        checkOrSetConfig(modify, "rewards.enabled", false);
        checkOrSetConfig(modify, "rewards.player-win", new ArrayList<String>() {
            {
                add("/example {player} 200");
            }
        });
        checkOrSetConfig(modify, "rewards.player-end-game", new ArrayList<String>() {
            {
                add("/example {player} {score}");
            }
        });
        checkOrSetConfig(modify, "rewards.player-destroy-bed", new ArrayList<String>() {
            {
                add("/example {player} {score}");
            }
        });
        checkOrSetConfig(modify, "rewards.player-kill", new ArrayList<String>() {
            {
                add("/example {player} 10");
            }
        });

        checkOrSetConfig(modify, "lore.generate-automatically", true);
        checkOrSetConfig(modify, "lore.text",
                Arrays.asList("§7Price:", "§7%price% %resource%", "§7Amount:", "§7%amount%"));

        checkOrSetConfig(modify, "sign.lines", config.getList("sign", Arrays.asList("§c§l[BedWars]", "%arena%", "%status%", "%players%")));

        checkOrSetConfig(modify, "sign.block-behind.enabled", false);
        checkOrSetConfig(modify, "sign.block-behind.waiting", "ORANGE_STAINED_GLASS");
        checkOrSetConfig(modify, "sign.block-behind.rebuilding", "BROWN_STAINED_GLASS");
        checkOrSetConfig(modify, "sign.block-behind.in-game", "GREEN_STAINED_GLASS");
        checkOrSetConfig(modify, "sign.block-behind.game-disabled", "RED_STAINED_GLASS");

        checkOrSetConfig(modify, "hotbar.selector", 0);
        checkOrSetConfig(modify, "hotbar.color", 1);
        checkOrSetConfig(modify, "hotbar.start", 2);
        checkOrSetConfig(modify, "hotbar.leave", 8);

        checkOrSetConfig(modify, "breakable.enabled", false);
        checkOrSetConfig(modify, "breakable.asblacklist", false);
        checkOrSetConfig(modify, "breakable.blocks", new ArrayList<>());

        checkOrSetConfig(modify, "leaveshortcuts.enabled", false);
        checkOrSetConfig(modify, "leaveshortcuts.list", new ArrayList<String>() {
        	{
        		add("leave");
        	}
        });

        checkOrSetConfig(modify, "mainlobby.enabled", false);
        checkOrSetConfig(modify, "mainlobby.location", "");
        checkOrSetConfig(modify, "mainlobby.world", "");

        checkOrSetConfig(modify, "turnOnExperimentalGroovyShop", false);
        checkOrSetConfig(modify, "preventSpectatorFlyingAway", false);
        checkOrSetConfig(modify, "removePurchaseMessages", false);
        checkOrSetConfig(modify, "disableCakeEating", true);
        checkOrSetConfig(modify, "disableDragonEggTeleport", true);
        checkOrSetConfig(modify, "preventArenaFromGriefing", false);

        checkOrSetConfig(modify, "update-checker.zero.console", true);
        checkOrSetConfig(modify, "update-checker.zero.admins", true);
        checkOrSetConfig(modify, "update-checker.one.console", true);
        checkOrSetConfig(modify, "update-checker.one.admins", true);

        checkOrSetConfig(modify, "target-block.allow-destroying-with-explosions", false);
        checkOrSetConfig(modify, "target-block.respawn-anchor.fill-on-start", true);
        checkOrSetConfig(modify, "target-block.respawn-anchor.enable-decrease", true);
        checkOrSetConfig(modify, "target-block.respawn-anchor.sound.charge", "BLOCK_RESPAWN_ANCHOR_CHARGE");
        checkOrSetConfig(modify, "target-block.respawn-anchor.sound.used", "BLOCK_GLASS_BREAK");
        checkOrSetConfig(modify, "target-block.respawn-anchor.sound.deplete", "BLOCK_RESPAWN_ANCHOR_DEPLETE");
        checkOrSetConfig(modify, "target-block.cake.destroy-by-eating", true);

        checkOrSetConfig(modify, "event-hacks.damage", false);
        checkOrSetConfig(modify, "event-hacks.destroy", false);
        checkOrSetConfig(modify, "event-hacks.place", false);

        checkOrSetConfig(modify, "default-permissions.join", true);
        checkOrSetConfig(modify, "default-permissions.leave", true);
        checkOrSetConfig(modify, "default-permissions.stats", true);
        checkOrSetConfig(modify, "default-permissions.list", true);
        checkOrSetConfig(modify, "default-permissions.rejoin", true);
        checkOrSetConfig(modify, "default-permissions.autojoin", true);

        checkOrSetConfig(modify, "version", 2);

        if (modify.get()) {
            try {
                config.save(configFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void saveConfig() {
        try {
            config.save(configFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void checkOrSetConfig(AtomicBoolean modify, String path, Object value) {
        checkOrSet(modify, this.config, path, value);
    }

    private static void checkOrSet(AtomicBoolean modify, FileConfiguration config, String path, Object value) {
        if (!config.isSet(path)) {
            if (value instanceof Map) {
                config.createSection(path, (Map<?, ?>) value);
            } else {
                config.set(path, value);
            }
            modify.set(true);
        }
    }

    public ItemStack readDefinedItem(String item, String def) {
        if (config.isSet("items." + item) && config.get("items." + item) != null) {
            Object obj = config.get("items." + item);
            return StackParser.parse(obj);
        }

        return StackParser.parse(def);
    }
}
