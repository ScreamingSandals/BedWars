package org.screamingsandals.bedwars.config;

import lombok.RequiredArgsConstructor;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.screamingsandals.bedwars.BedWarsPlugin;
import org.screamingsandals.lib.material.Item;
import org.screamingsandals.lib.material.builder.ItemFactory;
import org.screamingsandals.lib.plugin.ServiceManager;
import org.screamingsandals.lib.utils.annotations.Service;
import org.screamingsandals.lib.utils.annotations.methods.OnEnable;
import org.screamingsandals.lib.utils.annotations.parameters.ConfigFile;
import org.screamingsandals.simpleinventories.inventory.LocalOptions;
import org.spongepowered.configurate.BasicConfigurationNode;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;

import java.util.*;

@Service
@RequiredArgsConstructor
public class MainConfig {
    @ConfigFile("config.yml")
    private final YamlConfigurationLoader loader;

    private ConfigurationNode configurationNode;

    public static MainConfig getInstance() {
        return ServiceManager.get(MainConfig.class);
    }

    public ConfigurationNode node(Object... keys) {
        return configurationNode.node(keys);
    }

    @OnEnable
    public void load() {

        try {
            this.configurationNode = loader.load();

            // @formatter:off

            var generator = new ConfigGenerator(loader, configurationNode);

            generator.start()
                .key("locale").defValue("en")
                .key("prefix").defValue("[BW]")
                .key("debug").defValue(false)
                .key("allow-crafting").defValue(false)
                .key("keep-inventory-on-death").defValue(false)
                .key("keep-armor-on-death").defValue(false)
                .key("in-lobby-colored-leather-by-team").defValue(true)
                .key("jointeam-entity-show-name").defValue(true)
                .key("friendlyfire").defValue(false)
                .key("player-drops").defValue(true)
                .key("join-randomly-after-lobby-timeout").defValue(false)
                .key("prevent-killing-villagers").defValue(true)
                .key("compass-enabled").defValue(true)
                .key("join-randomly-on-lobby-join").defValue(false)
                .key("add-wool-to-inventory-on-join").defValue(true)
                .key("prevent-spawning-mobs").defValue(true)
                .key("spawner-holograms").defValue(true)
                .key("spawner-disable-merge").defValue(true)
                .key("prevent-lobby-spawn-mobs-in-radius").defValue(16)
                .key("spawner-holo-height").defValue(0.25)
                .key("spawner-holograms-countdown").defValue(true)
                .key("damage-when-player-is-not-in-arena").defValue(false)
                .key("remove-unused-target-blocks").defValue(true)
                .key("allow-block-falling").defValue(true)
                .key("game-start-items").moveIfAbsolute(ConfigurationNode::isList, "game-start-items", "enabled")
                .key("invisible-lobby-on-game-start").defValue(true)
                .key("enable-below-name-health-indicator").defValue(true)
                .key("enable-cheat-command-for-admins").defValue(false)
                .section("tnt-jump")
                    .key("enabled").defValue(true)
                    .key("source-damage").defValue(0.5)
                    .key("team-damage").defValue(true)
                    .key("launch-multiplier").defValue(3)
                    .key("reduce-y").defValue(2)
                    .key("acceleration-y").defValue(1)
                    .key("fall-damage").defValue(0.75)
                    .back()
                .section("game-start-items")
                    .key("enabled").defValue(false)
                    .key("items").migrateOldAbsoluteKey("gived-game-start-items").defValue(List::of)
                    .back()
                .key("player-respawn-items").moveIfAbsolute(ConfigurationNode::isList, "player-respawn-items", "enabled")
                .section("player-respawn-items")
                    .key("enabled").defValue(false)
                    .key("items").migrateOldAbsoluteKey("gived-player-respawn-items").defValue(List::of)
                    .back()
                .key("disable-hunger").defValue(false)
                .key("automatic-coloring-in-shop").defValue(true)
                .key("sell-max-64-per-click-in-shop").defValue(true)
                // .key("destroy-placed-blocks-by-explosion-except").defValue("")
                .key("destroy-placed-blocks-by-explosion-except")
                .remap(node -> {
                    if (!node.empty() && !node.isList()) {
                        var str = node.getString();
                        try {
                            var data = str == null ? List.of() : List.of(str);
                            node.set(data);
                        } catch(SerializationException ex) {
                            ex.printStackTrace();
                        }
                    }
                })
                .defValue(List::of)
                .key("destroy-placed-blocks-by-explosion").defValue(true)
                .key("holo-above-bed").defValue(true)
                .key("allow-spectator-join").defValue(false)
                .section("disable-server-message")
                    .key("player-join")
                        .defValue(false)
                    .key("player-leave")
                        .defValue(false)
                    .back()
                .key("disable-flight")
                    .defValue(true);


            generator.start()
                .section("respawn-cooldown")
                    .key("enabled").defValue(true)
                    .key("time").defValue(5)
                    .back()
                .key("stop-team-spawners-on-die").defValue(false)
                .key("allow-fake-death").defValue(false)
                .section("commands")
                    .key("list").migrateOldAbsoluteKey("allowed-commands").defValue(List::of)
                    .key("blacklist-mode").migrateOldAbsoluteKey("change-allowed-commands-to-blacklist").defValue(false)
                    .back()
                .section("bungee")
                    .key("enabled").defValue(false)
                    .key("serverRestart").defValue(true)
                    .key("serverStop").defValue(false)
                    .key("server").defValue("hub")
                    .key("auto-game-connect").defValue(false)
                    .key("kick-when-proxy-too-slow").defValue(true)
                    .section("motd")
                        .key("enabled").defValue(false)
                        .key("waiting").defValue("%name%: Waiting for players [%current%/%max%]")
                        .key("waiting_full").defValue("%name%: Game is full [%current%/%max%]")
                        .key("running").defValue("%name%: Game is running [%current%/%max%]")
                        .key("rebuilding").defValue("%name%: Rebuilding...")
                        .key("disabled").defValue("%name%: Game is disabled")
                        .back()
                    .back()
                .section("ignored-blocks")
                    .key("enabled").migrateOldAbsoluteKey("farmBlocks", "enable").defValue(false)
                    .key("blocks").migrateOldAbsoluteKey("farmBlocks", "blocks").defValue(List::of)
                    .back()
                .section("scoreboard")
                    .key("enabled").migrateOld("enable").defValue(true)
                    .key("title").defValue("§a%game%§r - %time%")
                    .key("bedLost").defValue("§c\u2718")
                    .key("anchorEmpty").defValue("§e\u2718")
                    .key("bedExists").defValue("§a\u2714")
                    .key("teamTitle").defValue("%bed%%color%%team%")
                    .back()
                .section("title")
                    .key("enabled").defValue(true)
                    .key("fadeIn").defValue(0)
                    .key("stay").defValue(20)
                    .key("fadeOut").defValue(0)
                    .back()
                .section("shop")
                    .key("rows").defValue(LocalOptions.ROWS)
                    .key("render-actual-rows").defValue(LocalOptions.RENDER_ACTUAL_ROWS)
                    .key("render-offset").defValue(LocalOptions.RENDER_OFFSET)
                    .key("render-header-start").defValue(LocalOptions.RENDER_HEADER_START)
                    .key("render-footer-start").defValue(LocalOptions.RENDER_FOOTER_START)
                    .key("items-on-row").defValue(LocalOptions.ITEMS_ON_ROW)
                    .key("show-page-numbers").defValue(LocalOptions.SHOW_PAGE_NUMBER)
                    .key("inventory-type").defValue(LocalOptions.INVENTORY_TYPE)
                    .key("citizens-enabled") /* for what is this key?*/.defValue(false)
                    .back()
                .section("items")
                    .key("jointeam").defValue("COMPASS")
                    .key("leavegame").defValue("SLIME_BALL")
                    .key("startgame").defValue("DIAMOND")
                    .key("shopback").defValue("BARRIER")
                    .key("shopcosmetic").defValue("GRAY_STAINED_GLASS_PANE")
                    .key("pageback").defValue("ARROW")
                    .key("pageforward").defValue("ARROW")
                    .key("team-select").defValue("WHITE_WOOL")
                    .back()
                .section("vault")
                    .key("enabled").migrateOld("enable").defValue(true)
                    .section("reward")
                        .key("kill").defValue(5)
                        .key("win").defValue(20)
                        .key("final-kill")
                            .migrateOld("kill")
                            .preventOldKeyRemove()
                            .defValue(5)
                        .key("bed-destroy").defValue(0)
                        .back()
                    .back()
                .key("resources").defValue(() -> Map.of(
                            "bronze", Map.of(
                                    "material", BedWarsPlugin.isLegacy() ? "CLAY_BRICK" : "BRICK",
                                    "interval", 1,
                                    "name", "Bronze",
                                    "translate", "resource_bronze",
                                    "color", "DARK_RED",
                                    "spread", 1.0
                            ),
                            "iron", Map.of(
                                    "material", "IRON_INGOT",
                                    "interval", 10,
                                    "name", "Iron",
                                    "translate", "resource_iron",
                                    "color", "GRAY",
                                    "spread", 1.0
                            ),
                            "gold", Map.of(
                                    "material", "GOLD_INGOT",
                                    "interval", 20,
                                    "name", "Gold",
                                    "translate", "resource_gold",
                                    "color", "GOLD",
                                    "spread", 1.0
                            )));

            generator.start()
                .section("respawn")
                    .key("protection-enabled").defValue(true)
                    .key("protection-time").defValue(10)
                    .back()
                .section("specials")
                    .key("action-bar-messages").defValue(true)
                    .section("rescue-platform")
                        .key("is-breakable").defValue(false)
                        .key("delay").defValue(0)
                        .key("break-time").defValue(10)
                        .key("distance").defValue(1)
                        .key("material").defValue("GLASS")
                        .back()
                    .section("protection-wall")
                        .key("is-breakable").defValue(false)
                        .key("delay").defValue(20)
                        .key("break-time").defValue(0)
                        .key("width").defValue(5)
                        .key("height").defValue(3)
                        .key("distance").defValue(2)
                        .key("material").defValue("CUT_SANDSTONE")
                        .back()
                    .section("tnt-sheep")
                        .key("speed").defValue(0.25)
                        .key("follow-range").defValue(10.0)
                        .key("max-target-distance").defValue(32)
                        .key("explosion-time").defValue(8)
                        .back()
                    .section("arrow-blocker")
                        .key("protection-time").defValue(10)
                        .key("delay").defValue(5)
                        .back()
                    .section("warp-powder")
                        .key("teleport-time").defValue(6)
                        .key("delay").defValue(0)
                        .back()
                    .key("magnet-shoes", "probability").defValue(75)
                    .section("golem")
                        .key("speed").defValue(0.25)
                        .key("follow-range").defValue(10)
                        .key("health").defValue(20)
                        .key("name-format").defValue("%teamcolor%%team% Golem")
                        .key("show-name").defValue(true)
                        .key("delay").defValue(0)
                        .key("collidable").defValue(false)
                        .back()
                    .key("teamchest", "turn-all-enderchests-to-teamchests").defValue(true)
                    .section("throwable-fireball")
                        .key("explosion").defValue(3)
                        .key("damage").defValue(2)
                        .back()
                    .section("auto-igniteable-tnt")
                        .key("explosion-time").migrateOldAbsoluteKey("tnt", "explosion-time").defValue(8)
                        .key("damage-placer")
                            .migrateOldAbsoluteKey("tnt", "dont-damage-placer")
                            .remap(node -> {
                                try {
                                    node.set(!node.getBoolean());
                                } catch (SerializationException e) {
                                    e.printStackTrace();
                                }
                            })
                            .defValue(true)
                        .back()
                    .back()
                .drop("tnt", "auto-ignite");


            generator.start()
                .section("sounds")
                    .key("bed_destroyed", "sound").migrateOld("on_bed_destroyed").defValue("ENTITY_ENDER_DRAGON_GROWL")
                    .key("bed_destroyed", "volume").defValue(1)
                    .key("bed_destroyed", "pitch").defValue(1)
                    .key("my_bed_destroyed", "sound")
                        .migrateOld("bed_destroyed", "sound")
                        .preventOldKeyRemove()
                        .defValue("ENTITY_ENDER_DRAGON_GROWL")
                    .key("my_bed_destroyed", "volume").defValue(1)
                    .key("my_bed_destroyed", "pitch").defValue(1)
                    .key("countdown", "sound").migrateOld("on_countdown").defValue("UI_BUTTON_CLICK")
                    .key("countdown", "volume").defValue(1)
                    .key("countdown", "pitch").defValue(1)
                    .key("game_start", "sound").migrateOld("on_game_start").defValue("ENTITY_PLAYER_LEVELUP")
                    .key("game_start", "volume").defValue(1)
                    .key("game_start", "pitch").defValue(1)
                    .key("team_kill", "sound").migrateOld("on_team_kill").defValue("ENTITY_PLAYER_LEVELUP")
                    .key("team_kill", "volume").defValue(1)
                    .key("team_kill", "pitch").defValue(1)
                    .key("player_kill", "sound").migrateOld("on_player_kill").defValue("ENTITY_PLAYER_BIG_FALL")
                    .key("player_kill", "volume").defValue(1)
                    .key("player_kill", "pitch").defValue(1)
                    .key("item_buy", "sound").migrateOld("on_item_buy").defValue("ENTITY_ITEM_PICKUP")
                    .key("item_buy", "volume").defValue(1)
                    .key("item_buy", "pitch").defValue(1)
                    .key("upgrade_buy", "sound").migrateOld("on_upgrade_buy").defValue("ENTITY_EXPERIENCE_ORB_PICKUP")
                    .key("upgrade_buy", "volume").defValue(1)
                    .key("upgrade_buy", "pitch").defValue(1)
                    .key("respawn_cooldown_wait", "sound").migrateOld("on_respawn_cooldown_wait").defValue("UI_BUTTON_CLICK")
                    .key("respawn_cooldown_wait", "volume").defValue(1)
                    .key("respawn_cooldown_wait", "pitch").defValue(1)
                    .key("respawn_cooldown_done", "sound").migrateOld("on_respawn_cooldown_done").defValue("ENTITY_PLAYER_LEVELUP")
                    .key("respawn_cooldown_done", "volume").defValue(1)
                    .key("respawn_cooldown_done", "pitch").defValue(1)
                    .back()
                .section("game-effects")
                    .key("end").defValue(() -> Map.of(
                                "type", "Firework",
                                "power", 1,
                                "effects", List.of(
                                        Bukkit2Map.serialize(FireworkEffect.builder()
                                                .with(FireworkEffect.Type.BALL)
                                                .trail(false)
                                                .flicker(false)
                                                .withColor(Color.WHITE)
                                                .withFade(Color.WHITE)
                                                .build())
                                )))
                    .key("start").defValue(Map::of)
                    .key("kill").defValue(Map::of)
                    .key("teamkill").defValue(Map::of)
                    .key("lobbyjoin").defValue(Map::of)
                    .key("lobbyleave").defValue(Map::of)
                    .key("respawn").defValue(Map::of)
                    .key("beddestroy").defValue(Map::of)
                    .key("warppowdertick").defValue(Map::of)
                    .back()
                .section("lobby-scoreboard")
                    .key("enabled").defValue(true)
                    .key("title").defValue("§eBEDWARS")
                    .key("content").defValue(() -> List.of(
                                " ",
                                "§fMap: §2%arena%",
                                "§fPlayers: §2%players%§f/§2%maxplayers%",
                                " ",
                                "§fWaiting ...",
                                " "
                        ))
                    .back()
                .section("statistics")
                    .key("enabled").defValue(true)
                    .key("type").defValue("yaml")
                    .key("show-on-game-end").defValue(false)
                    .key("bed-destroyed-kills").defValue(false)
                    .section("scores")
                        .key("kill").defValue(10)
                        .key("final-kill").defValue(0)
                        .key("die").defValue(0)
                        .key("win").defValue(50)
                        .key("bed-destroy").defValue(25)
                        .key("lose").defValue(0)
                        .key("record").defValue(100)
                        .back()
                    .back()
                .section("database")
                    .key("host").defValue("localhost")
                    .key("port").defValue(3306)
                    .key("db").defValue("database")
                    .key("user").defValue("root")
                    .key("password").defValue("secret")
                    .key("table-prefix").defValue("bw_")
                    .key("useSSL").defValue(false)
                    .back()
                .section("bossbar")
                    .key("use-xp-bar").defValue(false)
                    .section("lobby")
                        .key("enabled").migrateOld("enable").defValue(true)
                        .key("color").defValue("YELLOW")
                        .key("style").defValue("SEGMENTED_20")
                        .back()
                    .section("game")
                        .key("enabled").migrateOld("enable").defValue(true)
                        .key("color").defValue("GREEN")
                        .key("style").defValue("SEGMENTED_20")
                        .back()
                    .back();

            generator.start()
                .section("holograms")
                    .key("enabled").defValue(true)
                    .key("headline").defValue("Your §eBEDWARS§f stats")
                    .section("leaderboard")
                        .key("headline").defValue("&6Bedwars Leaderboard")
                        .key("format").defValue("&l%order%. &7%name% - &a%score%")
                        .key("size").defValue(10)
                        .back()
                    .back()
                .section("chat")
                    .key("override").defValue(true)
                    .key("format").defValue("<%teamcolor%%name%§r> ")
                    .section("separate-chat")
                        .key("lobby").migrateOldAbsoluteKey("chat", "separate-game-chat").preventOldKeyRemove().defValue(false)
                        .key("game").migrateOldAbsoluteKey("chat", "separate-game-chat").defValue(false)
                        .back()
                    .key("send-death-messages-just-in-game").defValue(true)
                    .key("send-custom-death-messages").defValue(true)
                    .key("default-team-chat-while-running").defValue(true)
                    .key("all-chat-prefix").defValue("@a")
                    .key("team-chat-prefix").defValue("@t")
                    .key("all-chat").defValue("[ALL] ")
                    .key("team-chat").defValue("[TEAM] ")
                    .key("death-chat").defValue("[DEATH] ")
                    .key("disable-all-chat-for-spectators").defValue(false)
                    .back()
                .section("rewards")
                    .key("enabled").defValue(false)
                    .key("player-win").defValue(() -> List.of("/example {player} 200"))
                    .key("player-end-game").defValue(() -> List.of("/example {player} {score}"))
                    .key("player-destroy-bed").defValue(() -> List.of("/example {player} {score}"))
                    .key("player-kill").defValue(() -> List.of("/example {player} 10"))
                    .key("player-final-kill").defValue(() -> List.of("/example {player} 10"))
                    .back()
                .section("lore")
                    .key("generate-automatically").defValue(true)
                    .key("text").defValue(() -> List.of(
                                "§7Price:",
                                "§7%price% %resource%",
                                "§7Amount:",
                                "§7%amount%"
                        ))
                    .back()
                .key("sign").moveIfAbsolute(ConfigurationNode::isList, "sign", "lines")
                .section("sign")
                    .key("lines").defValue(() -> List.of(
                                "§c§l[BedWars]",
                                "%arena%",
                                "%status%",
                                "%players%"
                        ))
                    .section("block-behind")
                        .key("enabled").defValue(false)
                        .key("waiting").defValue("ORANGE_STAINED_GLASS")
                        .key("rebuilding").defValue("BROWN_STAINED_GLASS")
                        .key("in-game").defValue("GREEN_STAINED_GLASS")
                        .key("game-disabled").defValue("RED_STAINED_GLASS")
                        .back()
                    .back()
                .section("hotbar")
                    .key("selector").defValue(0)
                    .key("color").defValue(1)
                    .key("start").defValue(2)
                    .key("leave").defValue(8)
                    .back()
                .section("breakable")
                    .key("enabled").defValue(false)
                    .key("blacklist-mode").migrateOld("asblacklist").defValue(false)
                    .key("blocks").defValue(List::of)
                    .back()
                .section("leaveshortcuts")
                    .key("enabled").defValue(false)
                    .key("list").defValue(() -> List.of("leave"))
                    .back()
                .section("mainlobby")
                    .key("enabled").defValue(false)
                    .key("location").defValue("")
                    .key("world").defValue("")
                    .back();

            generator.start()
                .key("turnOnExperimentalGroovyShop").defValue(false)
                .key("preventSpectatorFlyingAway").defValue(false)
                .key("removePurchaseMessages").defValue(false)
                .key("disableCakeEating").defValue(true)
                .key("disableDragonEggTeleport").defValue(true)
                .key("preventArenaFromGriefing").defValue(true)
                .section("update-checker")
                    .key("console").migrateOld("zero", "console").defValue(true)
                    .key("admins").migrateOld("zero", "admins").defValue(true)
                    .drop("one", "console")
                    .drop("one", "admins")
                    .back()
                .section("target-block")
                    .key("allow-destroying-with-explosions").defValue(false)
                    .section("respawn-anchor")
                        .key("fill-on-start").defValue(true)
                        .key("enable-decrease").defValue(true)
                        .section("sound")
                            .key("charge").defValue("BLOCK_RESPAWN_ANCHOR_CHARGE")
                            .key("used").defValue("BLOCK_GLASS_BREAK")
                            .key("deplete").defValue("BLOCK_RESPAWN_ANCHOR_DEPLETE")
                            .back()
                        .back()
                    .key("cake", "destroy-by-eating").defValue(true)
                    .back()
                .section("event-hacks")
                    .key("damage").defValue(false)
                    .key("destroy").defValue(false)
                    .key("place").defValue(false)
                    .back()
                .section("tab")
                    .key("enabled").migrateOld("enable").defValue(false)
                    .section("header")
                        .key("enabled").defValue(true)
                        .key("contents").defValue(() -> List.of(
                                    "&aMy awesome BedWars server",
                                    "&bMap: %map%",
                                    "&cPlayers: %respawnable%/%max%"
                            ))
                        .back()
                    .section("footer")
                        .key("enabled").defValue(true)
                        .key("contents").defValue(() -> List.of(
                                    "&eexample.com",
                                    "&fWow!!",
                                    "§a%spectators% are watching this match"
                            ))
                        .back()
                    .key("hide-spectators").defValue(true)
                    .key("hide-foreign-players").defValue(false)
                    .back()
                .section("default-permissions")
                    .key("join").defValue(true)
                    .key("leave").defValue(true)
                    .key("stats").defValue(true)
                    .key("list").defValue(true)
                    .key("rejoin").defValue(true)
                    .key("autojoin").defValue(true)
                    .key("leaderboard").defValue(true)
                    .key("party").defValue(true)
                    .back()
                .section("experimental")
                    .section("new-scoreboard-system")
                        .key("enabled").defValue(false)
                        .key("content").defValue(() -> List.of(
                                    " ",
                                    "%team_status%",
                                    "",
                                    "§6www.screamingsandals.org"
                            ))
                        .key("teamTitle").defValue("%bed%%color%%team% §7(%team_size%)")
                        .back()
                    .back()
                .section("party")
                    .key("enabled").defValue(false)
                    .key("autojoin-members").defValue(true)
                    .key("notify-when-warped").defValue(true)
                    .back()
                .section("floating-generator").key("enabled").defValue(true)
                    .key("holo-height").defValue(0.5)
                    .key("generator-height").defValue(0.25)
                    .back()
                .drop("version");

                 // @formatter:on
                generator.saveIfModified();
        } catch (ConfigurateException e) {
            e.printStackTrace();
            this.configurationNode = BasicConfigurationNode.root();
        }
    }

    public void saveConfig() {
        try {
            this.loader.save(this.configurationNode);
        } catch (ConfigurateException e) {
            e.printStackTrace();
        }
    }

    public Item readDefinedItem(String item, String def) {
        var node = node("items", item);
        if (!node.empty()) {
            var obj = node.raw();
            return ItemFactory.build(obj).orElse(ItemFactory.getAir());
        }

        return ItemFactory.build(def).orElse(ItemFactory.getAir());
    }
}
