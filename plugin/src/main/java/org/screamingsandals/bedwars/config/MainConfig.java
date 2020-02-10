package org.screamingsandals.bedwars.config;

import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.screamingsandals.bedwars.Main;
import org.screamingsandals.lib.debug.Debug;

import java.io.File;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class MainConfig extends BaseConfig {
    public MainConfig(File configFile) {
        super(configFile);
    }

    @Override
    public void initialize() {
        super.initialize();

        checkDefaults();
    }

    private void checkDefaults() {
        AtomicBoolean modify = new AtomicBoolean(false);
        checkOrSet(modify, "allowed-commands", new ArrayList<>());
        checkOrSet(modify, "change-allowed-commands-to-blacklist", false);

        checkOrSet(modify, "bungee.enabled", false);
        checkOrSet(modify, "bungee.serverRestart", true);
        checkOrSet(modify, "bungee.serverStop", false);
        checkOrSet(modify, "bungee.server", "hub");
        checkOrSet(modify, "bungee.auto-game-connect", false);

        checkOrSet(modify, "farmBlocks.enable", false);
        checkOrSet(modify, "farmBlocks.blocks", new ArrayList<>());

        checkOrSet(modify, "scoreboard.enable", true);
        checkOrSet(modify, "scoreboard.title", "§a%game%§r - %time%");
        checkOrSet(modify, "scoreboard.bedLost", "§c\u2718");
        checkOrSet(modify, "scoreboard.bedExists", "§a\u2714");
        checkOrSet(modify, "scoreboard.teamTitle", "%bed%%color%%team%");

        checkOrSet(modify, "title.fadeIn", 0);
        checkOrSet(modify, "title.stay", 20);
        checkOrSet(modify, "title.fadeOut", 0);

        checkOrSet(modify, "items.jointeam", "COMPASS");
        checkOrSet(modify, "items.leavegame", "SLIME_BALL");
        checkOrSet(modify, "items.startgame", "DIAMOND");
        checkOrSet(modify, "items.shopback", "BARRIER");
        checkOrSet(modify, "items.shopcosmetic",
                Main.isLegacy() ? new ItemStack(Objects.requireNonNull(Material.getMaterial("STAINED_GLASS_PANE")), 1, (short) 7)
                        : "GRAY_STAINED_GLASS_PANE");
        checkOrSet(modify, "items.pageback", "ARROW");
        checkOrSet(modify, "items.pageforward", "ARROW");
        checkOrSet(modify, "items.team-select",
                Main.isLegacy() ? new ItemStack(Objects.requireNonNull(Material.getMaterial("WOOL")), 1, (short) 1)
                        : "WHITE_WOOL");

        checkOrSet(modify, "vault.enable", true);
        checkOrSet(modify, "vault.reward.kill", 5);
        checkOrSet(modify, "vault.reward.win", 20);

        checkOrSet(modify, "resources", new HashMap<String, Object>() {
            {
                put("bronze", new HashMap<String, Object>() {
                    {
                        put("material", Main.isLegacy() ? "CLAY_BRICK" : "BRICK");
                        /* Config migration 1 -> 2: if spawners.bronze exists, get this value */
                        put("interval", getInt("spawners.bronze", 1));
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
                        put("interval", getInt("spawners.iron", 10));
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
                        put("interval", getInt("spawners.gold", 20));
                        put("name", "Gold");
                        put("translate", "resource_gold");
                        put("color", "GOLD");
                        put("spread", 1.0);
                    }
                });
            }
        });
        if (contains("spawners")) {
            /* Config migration 1 -> 2: After creating resources, remove old spawners */
            modify.set(true);
            set("spawners", null);
        }

        checkOrSet(modify, "respawn.protection-enabled", true);
        checkOrSet(modify, "respawn.protection-time", 10);

        checkOrSet(modify, "specials.action-bar-messages", true);
        checkOrSet(modify, "specials.rescue-platform.is-breakable", false);
        checkOrSet(modify, "specials.rescue-platform.delay", 0);
        checkOrSet(modify, "specials.rescue-platform.break-time", 10);
        checkOrSet(modify, "specials.rescue-platform.distance", 1);
        checkOrSet(modify, "specials.rescue-platform.material", "GLASS");
        checkOrSet(modify, "specials.protection-wall.is-breakable", false);
        checkOrSet(modify, "specials.protection-wall.delay", 20);
        checkOrSet(modify, "specials.protection-wall.break-time", 0);
        checkOrSet(modify, "specials.protection-wall.width", 5);
        checkOrSet(modify, "specials.protection-wall.height", 3);
        checkOrSet(modify, "specials.protection-wall.distance", 2);
        checkOrSet(modify, "specials.protection-wall.material", Main.isLegacy() ? "SANDSTONE" : "CUT_SANDSTONE");
        checkOrSet(modify, "specials.tnt-sheep.speed", 2.0);
        checkOrSet(modify, "specials.tnt-sheep.follow-range", 10.0);
        checkOrSet(modify, "specials.tnt-sheep.max-target-distance", 32);
        checkOrSet(modify, "specials.tnt-sheep.explosion-time", 8);
        checkOrSet(modify, "specials.arrow-blocker.protection-time", 10);
        checkOrSet(modify, "specials.arrow-blocker.delay", 5);
        checkOrSet(modify, "specials.warp-powder.teleport-time", 6);
        checkOrSet(modify, "specials.warp-powder.delay", 0);
        checkOrSet(modify, "specials.magnet-shoes.probability", 75);
        checkOrSet(modify, "specials.golem.speed", 0.25);
        checkOrSet(modify, "specials.golem.follow-range", 10);
        checkOrSet(modify, "specials.golem.health", 20);
        checkOrSet(modify, "specials.golem.name-format", "%teamcolor%%team% Golem");
        checkOrSet(modify, "specials.golem.show-name", true);
        checkOrSet(modify, "specials.golem.delay", 0);
        checkOrSet(modify, "specials.golem.collidable", false);
        checkOrSet(modify, "specials.teamchest.turn-all-enderchests-to-teamchests", true);

        checkOrSet(modify, "tnt.auto-ignite", false);
        checkOrSet(modify, "tnt.explosion-time", 8);

        checkOrSet(modify, "sounds.on_bed_destroyed", "ENTITY_ENDER_DRAGON_GROWL");
        checkOrSet(modify, "sounds.on_countdown", "UI_BUTTON_CLICK");
        checkOrSet(modify, "sounds.on_game_start", "ENTITY_PLAYER_LEVELUP");
        checkOrSet(modify, "sounds.on_team_kill", "ENTITY_PLAYER_LEVELUP");
        checkOrSet(modify, "sounds.on_player_kill", "ENTITY_PLAYER_BIG_FALL");
        checkOrSet(modify, "sounds.on_item_buy", "ENTITY_ITEM_PICKUP");
        checkOrSet(modify, "sounds.on_upgrade_buy", "ENTITY_EXPERIENCE_ORB_PICKUP");
        checkOrSet(modify, "sounds.on_respawn_cooldown_wait", "UI_BUTTON_CLICK");
        checkOrSet(modify, "sounds.on_respawn_cooldown_done", "ENTITY_PLAYER_LEVELUP");

        checkOrSet(modify, "game-effects.end", new HashMap<String, Object>() {
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
        checkOrSet(modify, "game-effects.start", new HashMap<String, Object>());
        checkOrSet(modify, "game-effects.kill", new HashMap<String, Object>());
        checkOrSet(modify, "game-effects.teamkill", new HashMap<String, Object>());
        checkOrSet(modify, "game-effects.lobbyjoin", new HashMap<String, Object>());
        checkOrSet(modify, "game-effects.lobbyleave", new HashMap<String, Object>());
        checkOrSet(modify, "game-effects.respawn", new HashMap<String, Object>());
        checkOrSet(modify, "game-effects.beddestroy", new HashMap<String, Object>());
        checkOrSet(modify, "game-effects.warppowdertick", new HashMap<String, Object>());

        checkOrSet(modify, "lobby-scoreboard.enabled", true);
        checkOrSet(modify, "lobby-scoreboard.title", "§eBEDWARS");
        checkOrSet(modify, "lobby-scoreboard.content", Arrays.asList(" ", "§fMap: §2%arena%",
                "§fPlayers: §2%players%§f/§2%maxplayers%", " ", "§fWaiting ...", " "));

        checkOrSet(modify, "statistics.enabled", true);
        checkOrSet(modify, "statistics.type", "yaml");
        checkOrSet(modify, "statistics.show-on-game-end", false);
        checkOrSet(modify, "statistics.bed-destroyed-kills", false);
        checkOrSet(modify, "statistics.scores.kill", 10);
        checkOrSet(modify, "statistics.scores.die", 0);
        checkOrSet(modify, "statistics.scores.win", 50);
        checkOrSet(modify, "statistics.scores.bed-destroy", 25);
        checkOrSet(modify, "statistics.scores.lose", 0);
        checkOrSet(modify, "statistics.scores.record", 100);

        checkOrSet(modify, "database.host", "localhost");
        checkOrSet(modify, "database.port", 3306);
        checkOrSet(modify, "database.db", "databse");
        checkOrSet(modify, "database.user", "root");
        checkOrSet(modify, "database.password", "secret");
        checkOrSet(modify, "database.table-prefix", "bw_");

        checkOrSet(modify, "bossbar.use-xp-bar", false);
        checkOrSet(modify, "bossbar.lobby.enable", true);
        checkOrSet(modify, "bossbar.lobby.color", "YELLOW");
        checkOrSet(modify, "bossbar.lobby.style", "SEGMENTED_20");
        checkOrSet(modify, "bossbar.game.enable", true);
        checkOrSet(modify, "bossbar.game.color", "GREEN");
        checkOrSet(modify, "bossbar.game.style", "SEGMENTED_20");

        checkOrSet(modify, "holograms.enabled", true);
        checkOrSet(modify, "holograms.headline", "Your §eBEDWARS§f stats");

        checkOrSet(modify, "chat.override", true);
        checkOrSet(modify, "chat.format", "<%teamcolor%%name%§r> ");
        checkOrSet(modify, "chat.separate-game-chat", false);
        checkOrSet(modify, "chat.send-death-messages-just-in-game", true);
        checkOrSet(modify, "chat.send-custom-death-messages", true);
        checkOrSet(modify, "chat.default-team-chat-while-running", true);
        checkOrSet(modify, "chat.all-chat-prefix", "@a");
        checkOrSet(modify, "chat.team-chat-prefix", "@t");
        checkOrSet(modify, "chat.all-chat", "[ALL] ");
        checkOrSet(modify, "chat.team-chat", "[TEAM] ");
        checkOrSet(modify, "chat.death-chat", "[DEATH] ");

        checkOrSet(modify, "rewards.enabled", false);
        checkOrSet(modify, "rewards.player-win", new ArrayList<String>() {
            {
                add("/example {player} 200");
            }
        });
        checkOrSet(modify, "rewards.player-end-game", new ArrayList<String>() {
            {
                add("/example {player} {score}");
            }
        });
        checkOrSet(modify, "rewards.player-destroy-bed", new ArrayList<String>() {
            {
                add("/example {player} {score}");
            }
        });
        checkOrSet(modify, "rewards.player-kill", new ArrayList<String>() {
            {
                add("/example {player} 10");
            }
        });

        checkOrSet(modify, "lore.generate-automatically", true);
        checkOrSet(modify, "lore.text",
                Arrays.asList("§7Price:", "§7%price% %resource%", "§7Amount:", "§7%amount%"));
        checkOrSet(modify, "sign", Arrays.asList("§c§l[BedWars]", "%arena%", "%status%", "%players%"));

        checkOrSet(modify, "hotbar.selector", 0);
        checkOrSet(modify, "hotbar.color", 1);
        checkOrSet(modify, "hotbar.start", 2);
        checkOrSet(modify, "hotbar.leave", 8);

        checkOrSet(modify, "breakable.enabled", false);
        checkOrSet(modify, "breakable.asblacklist", false);
        checkOrSet(modify, "breakable.blocks", new ArrayList<>());

        checkOrSet(modify, "leaveshortcuts.enabled", false);
        checkOrSet(modify, "leaveshortcuts.list", new ArrayList<String>() {
            {
                add("leave");
            }
        });

        checkOrSet(modify, "mainlobby.enabled", false);
        checkOrSet(modify, "mainlobby.location", "");
        checkOrSet(modify, "mainlobby.world", "");

        checkOrSet(modify, "punish-player-if-he-leaves-the-game-area", true);

        checkOrSet(modify, "version", 3);

        if (modify.get()) {
            save();
        }
    }

    private void checkOrSet(AtomicBoolean modify, String path, Object value) {
        if (!isSet(path)) {
            if (value instanceof Map) {
                getYamlConfiguration().createSection(path, (Map<?, ?>) value);
            } else {
                set(path, value);
            }
            modify.set(true);
        }
    }

    public ItemStack readDefinedItem(String item, String def) {
        ItemStack material = new ItemStack(Material.valueOf(def));

        if (isSet("items." + item)) {
            Object configObject = get("items." + item);
            if (configObject instanceof ItemStack) {
                material = (ItemStack) configObject;
            } else {
                try {
                    material.setType(Material.valueOf((String) configObject));
                } catch (IllegalArgumentException e) {
                    Debug.warn("DEFINED ITEM " + configObject + " DOES NOT EXISTS.", true);
                    Debug.warn("Check config variable: items." + item);
                }
            }
        }

        return material;
    }
}
