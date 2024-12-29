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

package org.screamingsandals.bedwars.config;

import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.screamingsandals.bedwars.utils.MiscUtils;
import org.screamingsandals.lib.Server;
import org.screamingsandals.lib.item.ItemStack;
import org.screamingsandals.lib.item.builder.ItemStackFactory;
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

    public static @NotNull MainConfig getInstance() {
        return ServiceManager.get(MainConfig.class);
    }

    public ConfigurationNode node(Object... keys) {
        return configurationNode.node(keys);
    }

    @ApiStatus.Internal
    public ConfigurationNode getConfigurationNode() {
        return configurationNode;
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
                .key("join-random-team-after-lobby").migrateOld("join-randomly-after-lobby-timeout").defValue(false)
                .key("prevent-killing-villagers").defValue(true)
                .key("team-join-item-enabled").migrateOld("compass-enabled").defValue(true)
                .key("join-random-team-on-join").migrateOld("join-randomly-on-lobby-join").defValue(false)
                .key("add-wool-to-inventory-on-join").defValue(true)
                .key("prevent-spawning-mobs").defValue(true)
                .key("spawner-holograms").defValue(true)
                .key("use-certain-popular-server-like-holograms-for-spawners").defValue(false)
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
                .key("shopkeepers-are-silent").defValue(true)
                .key("use-team-letter-prefixes-before-player-names").defValue(false)
                .key("use-certain-popular-server-titles").defValue(false)
                .key("show-game-info-on-start").defValue(false)
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
                .key("destroy-placed-blocks-by-explosion").moveIfAbsolute(n -> !n.virtual() && !n.isMap(), "destroy-placed-blocks-by-explosion", "enabled")
                .section("destroy-placed-blocks-by-explosion")
                    .key("enabled").defValue(true)
                    .key("blacklist")
                        .migrateOldAbsoluteKey("destroy-placed-blocks-by-explosion-except")
                        .remapWhen(node -> !node.empty() && !node.isList())
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
                    .back()
                .key("holograms-above-bed").migrateOld("holo-above-bed").defValue(true)
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
                .key("prefer-1-19-4-display-entities").defValue(true)
                .key("remember-what-scoreboards-players-had-before").defValue(false)
                .key("use-chunk-tickets-if-available").defValue(true)
                .key("reset-full-spawner-countdown-after-picking").defValue(true)
                .key("players-can-win-game-only-after-seconds").defValue(0)
                .section("kick-players-upon-final-death")
                    .key("enabled").defValue(false)
                    .key("delay").defValue(5)
                    .back()
                .section("commands")
                    .key("list").migrateOldAbsoluteKey("allowed-commands").defValue(List::of)
                    .key("blacklist-mode").migrateOldAbsoluteKey("change-allowed-commands-to-blacklist").defValue(false)
                    .back()
                .section("bungee")
                    .key("enabled").defValue(false)
                    .key("legacy-mode").defValue(true)
                    .key("serverRestart").defValue(true)
                    .key("serverStop").defValue(false)
                    .key("server").defValue("hub")
                    .key("auto-game-connect").defValue(false)
                    .key("kick-when-proxy-too-slow").defValue(true)
                    .section("random-game-selection")
                        .key("enabled").migrateOldAbsoluteKey("bungee", "select-random-game").defValue(true)
                        .key("preselect-games").defValue(true)
                    .back()
                    .section("motd")
                        .key("enabled").defValue(false)
                        .key("waiting").defValue("%name%: Waiting for players [%current%/%max%]")
                        .key("waiting_full").defValue("%name%: Game is full [%current%/%max%]")
                        .key("running").defValue("%name%: Game is running [%current%/%max%]")
                        .key("rebuilding").defValue("%name%: Rebuilding...")
                        .key("disabled").defValue("%name%: Game is disabled")
                        .back()
                    .section("communication")
                        .key("type").defValue("bungee")
                        .section("socket")
                            .key("host").defValue("localhost")
                            .key("port").defValue(9000)
                            .back()
                        .key("broadcast-state-changes-to-everyone").defValue(false)
                        .key("prevent-state-change-subscribing").defValue(false)
                        .key("prevent-incoming-state-change-processing").defValue(false)
                        .key("prevent-sending-hello-packet").defValue(false)
                        .back()
                    .back()
                .section("ignored-blocks")
                    .key("enabled").migrateOldAbsoluteKey("farmBlocks", "enable").defValue(false)
                    .key("blocks").migrateOldAbsoluteKey("farmBlocks", "blocks").defValue(List::of)
                    .back()
                .section("sidebar")
                    .key("date-format").defValue("MM/dd/yy")
                    .section("game")
                        .key("enabled").migrateOldAbsoluteKey("scoreboard","enable").migrateOldAbsoluteKey("scoreboard","enabled").defValue(true)
                        .key("legacy-sidebar")
                            .migrateOldAbsoluteKey("experimental", "new-scoreboard-system", "enabled")
                            .migrateOldAbsoluteKey("scoreboard", "new-scoreboard", "enabled")
                            .remap(node -> {
                                try {
                                    node.set(!node.getBoolean(true));
                                } catch (SerializationException ex) {
                                    ex.printStackTrace();
                                }
                            }).defValue(false)
                        .key("title").migrateOldAbsoluteKey("scoreboard", "title").remap(node -> {
                            try {
                                var str = node.getString();
                                if (str != null) {
                                    node.set(MiscUtils.toMiniMessage(str)
                                            // migrate placeholders to MiniMessage
                                            .replace("%game%", "<game>")
                                            .replace("%time%", "<time>")
                                    );
                                }
                            } catch (SerializationException ex) {
                                ex.printStackTrace();
                            }
                        }).defValue("<green><game><reset> - <time>")
                        .section("team-prefixes")
                            .key("target-block-lost").migrateOldAbsoluteKey("scoreboard", "bedLost").remap(this::toMiniMessage).defValue("<red>\u2718")
                            .key("anchor-empty").migrateOldAbsoluteKey("scoreboard", "anchorEmpty").remap(this::toMiniMessage).defValue("<yellow>\u2718")
                            .key("target-block-exists").migrateOldAbsoluteKey("scoreboard", "bedExists").remap(this::toMiniMessage).defValue("<green>\u2714")
                            .key("team-count").defValue("<green><count>")
                            .back()
                        .key("team-line").migrateOldAbsoluteKey("scoreboard", "teamTitle").remap(node -> {
                            try {
                                var str = node.getString();
                                if (str != null) {
                                    node.set(MiscUtils.toMiniMessage(str)
                                            // migrate old placeholders to MiniMessage
                                            .replace("%bed%", "<target-block-prefix>")
                                            .replace("%color%", "<team-color>")
                                            .replace("%team%", "<team>")
                                            .replace("%team_size%", "<team-size>")
                                    );
                                }
                            } catch (SerializationException ex) {
                                ex.printStackTrace();
                            }
                        }).defValue("<target-block-prefix><team-color><team> <gray>(<team-size>)")
                        .key("content")
                            .migrateOldAbsoluteKey("experimental", "new-scoreboard-system", "content")
                            .migrateOldAbsoluteKey("scoreboard", "new-scoreboard", "content")
                            .remap(node -> {
                                try {
                                    var list = node.getList(String.class);
                                    if (list != null) {
                                        node.set(null);
                                        for (var str : list) {
                                            node.appendListNode().set(MiscUtils.toMiniMessage(str)
                                                    // migrate old placeholders to MiniMessage
                                                    .replace("%arena%", "<game>") // should be the same as placeholder in the title
                                                    .replace("%players%", "<players>")
                                                    .replace("%maxplayers%", "<max-players>")
                                                    .replace("%time%", "<time>")
                                                    .replace("%version%", "<version>")
                                                    .replace("%date%", "<date>")
                                                    .replace("%mode%", "<mode>")
                                                    .replace("%team_status%", "<team-status>")
                                            );
                                        }
                                    }
                                } catch (SerializationException ex) {
                                    ex.printStackTrace();
                                }
                            })
                            .defValue(() -> List.of(
                                " ",
                                "<team-status>",
                                "",
                                "<gold>screamingsandals.org"
                        ))
                        .section("additional-content")
                            .key("show-if-team-count").defValue("<=4")
                            .key("content").defValue(List.of())
                            .back()
                        .back()
                    .section("lobby")
                        .key("enabled").migrateOldAbsoluteKey("lobby-scoreboard", "enabled").defValue(true)
                        .key("title")
                            .migrateOldAbsoluteKey("lobby-scoreboard", "title")
                            .remap(this::toMiniMessage)
                            .defValue("<yellow>BEDWARS")
                        .key("content")
                            .migrateOldAbsoluteKey("lobby-scoreboard", "content")
                            .remap(node -> {
                                try {
                                    var list = node.getList(String.class);
                                    if (list != null) {
                                        node.set(null);
                                        for (var str : list) {
                                            node.appendListNode().set(MiscUtils.toMiniMessage(str)
                                                    // migrate old placeholders to MiniMessage
                                                    .replace("%arena%", "<game>") // should be the same as placeholder in the title
                                                    .replace("%players%", "<players>")
                                                    .replace("%maxplayers%", "<max-players>")
                                                    .replace("%time%", "<time>")
                                                    .replace("%version%", "<version>")
                                                    .replace("%date%", "<date>")
                                                    .replace("%mode%", "<mode>")
                                                    .replace("%state%", "<state>")
                                                    .replace("%countdown%", "<time>")
                                                    .replace("%countdownwaiting%", "<state>")
                                            );
                                        }
                                    }
                                } catch (SerializationException ex) {
                                    ex.printStackTrace();
                                }
                            })
                            .defValue(() -> List.of(
                                " ",
                                "<white>Map: <dark_green><game>",
                                "<white>Players: <dark_green><players><white>/<dark_green><max-players>",
                                " ",
                                "<white>Waiting ...",
                                " "
                        ))
                        .back()
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
                    .key("allow-execution-of-console-commands").defValue(true)
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
                .section("economy").migrateOld("vault")
                    .key("enabled").migrateOld("enable").defValue(true)
                    .key("return-fee").defValue(true)
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
                                    "material", "BRICK",
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
                    .key("show-messages").defValue(true)
                    .back()
                .section("specials")
                    .key("dont-show-success-messages").defValue(false)
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
                    .section("bridge-egg")
                        .key("distance").defValue(30.0)
                        .key("material").defValue("GLASS")
                        .key("delay").defValue(5)
                        .back()
                    .section("popup-tower")
                        .key("material").defValue("WOOL")
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
                        .key("damage").migrateOld("explosion").defValue(3)
                        .key("incendiary").defValue(true)
                        .key("damage-thrower").defValue(true)
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
                        .key("damage").defValue(4)
                        .back()
                    .back()
                .drop("tnt", "auto-ignite");


            generator.start()
                .section("sounds")
                    .key("bed_destroyed", "sound").migrateOld("on_bed_destroyed").defValue("entity.ender_dragon.growl")
                    .key("bed_destroyed", "volume").defValue(1)
                    .key("bed_destroyed", "pitch").defValue(1)
                    .key("my_bed_destroyed", "sound")
                        .migrateOld("bed_destroyed", "sound")
                        .preventOldKeyRemove()
                        .defValue("entity.ender_dragon.growl")
                    .key("my_bed_destroyed", "volume").defValue(1)
                    .key("my_bed_destroyed", "pitch").defValue(1)
                    .key("countdown", "sound").migrateOld("on_countdown").defValue("ui.button.click")
                    .key("countdown", "volume").defValue(1)
                    .key("countdown", "pitch").defValue(1)
                    .key("game_start", "sound").migrateOld("on_game_start").defValue("entity.player.levelup")
                    .key("game_start", "volume").defValue(1)
                    .key("game_start", "pitch").defValue(1)
                    .key("team_kill", "sound").migrateOld("on_team_kill").defValue("entity.player.levelup")
                    .key("team_kill", "volume").defValue(1)
                    .key("team_kill", "pitch").defValue(1)
                    .key("player_kill", "sound").migrateOld("on_player_kill").defValue("entity.generic.big_fall")
                    .key("player_kill", "volume").defValue(1)
                    .key("player_kill", "pitch").defValue(1)
                    .key("item_buy", "sound").migrateOld("on_item_buy").defValue("entity.item.pickup")
                    .key("item_buy", "volume").defValue(1)
                    .key("item_buy", "pitch").defValue(1)
                    .key("upgrade_buy", "sound").migrateOld("on_upgrade_buy").defValue("entity.experience_orb.pickup")
                    .key("upgrade_buy", "volume").defValue(1)
                    .key("upgrade_buy", "pitch").defValue(1)
                    .key("respawn_cooldown_wait", "sound").migrateOld("on_respawn_cooldown_wait").defValue("ui.button.click")
                    .key("respawn_cooldown_wait", "volume").defValue(1)
                    .key("respawn_cooldown_wait", "pitch").defValue(1)
                    .key("respawn_cooldown_done", "sound").migrateOld("on_respawn_cooldown_done").defValue("entity.player.levelup")
                    .key("respawn_cooldown_done", "volume").defValue(1)
                    .key("respawn_cooldown_done", "pitch").defValue(1)
                    .back()
                .section("game-effects")
                    .key("end").defValue(() -> Map.of(
                                "type", "Firework",
                                "power", 1,
                                "effects", List.of(
                                        Map.of(
                                                "type", "small_ball",
                                                "flicker", false,
                                                "trail", false,
                                                "colors", List.of(
                                                        "white"
                                                ),
                                                "fade-colors", List.of(
                                                        "white"
                                                )
                                        )
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
                    .key("xp-to-level")
                        .defValue(Map.of(
                                "any", 5000,
                                "100n + 1", 500,
                                "100n + 2", 1000,
                                "100n + 3", 2000,
                                "100n + 4", 3500
                        ))
                    .back()
                .section("database")
                    .key("host").defValue("localhost")
                    .key("port").defValue(3306)
                    .key("db").defValue("database")
                    .key("user").defValue("root")
                    .key("password").defValue("secret")
                    .key("table-prefix").defValue("bw_")
                    .key("type").defValue("mysql")
                    .key("driver").defValue("default")
                    .key("params").defValue(() -> {
                        var map = new HashMap<String, Object>();
                        map.put("useSSL", configurationNode.node("database", "useSSL").getBoolean(true));
                        if (configurationNode.node("database", "add-timezone-to-connection-string").getBoolean(true)) {
                            map.put("serverTimezone", configurationNode.node("database", "timezone-id").getString(TimeZone.getDefault().getID()));
                        }
                        map.put("autoReconnect", true);
                        map.put("cachePrepStmts", true);
                        map.put("prepStmtCacheSize", 250);
                        map.put("prepStmtCacheSqlLimit", 2048);

                        if (!configurationNode.node("database", "useSSL").virtual()) {
                            configurationNode.node("database").removeChild("useSSL");
                        }

                        if (!configurationNode.node("database", "add-timezone-to-connection-string").virtual()) {
                            configurationNode.node("database").removeChild("add-timezone-to-connection-string");
                        }

                        if (!configurationNode.node("database", "timezone-id").virtual()) {
                            configurationNode.node("database").removeChild("timezone-id");
                        }

                        return map;
                    })
                    .back()
                .section("bossbar")
                    .key("use-xp-bar").defValue(false)
                    .section("lobby")
                        .key("enabled").migrateOld("enable").defValue(true)
                        .key("color").defValue("YELLOW")
                        .key("division").migrateOldAbsoluteKey("style").remap(node -> {
                                var str = node.getString("");
                                try {
                                    if (str.startsWith("SEGMENTED_")) {
                                        node.set("NOTCHED_" + str.substring(10));
                                    } else if (str.equals("SOLID") || str.equals("PROGRESS")) {
                                        node.set("NO_DIVISION");
                                    }
                                } catch (SerializationException e) {
                                    e.printStackTrace();
                                }
                            }).defValue("NOTCHED_20")
                        .back()
                    .section("game")
                        .key("enabled").migrateOld("enable").defValue(true)
                        .key("color").defValue("GREEN")
                        .key("division").migrateOldAbsoluteKey("style").remap(node -> {
                            var str = node.getString("");
                            try {
                                if (str.startsWith("SEGMENTED_")) {
                                    node.set("NOTCHED_" + str.substring(10));
                                } else if (str.equals("SOLID") || str.equals("PROGRESS")) {
                                    node.set("NO_DIVISION");
                                }
                            } catch (SerializationException e) {
                                e.printStackTrace();
                            }
                        }).defValue("NOTCHED_20")
                        .back()
                    .back();

            if (!Server.isVersion(1, 9)) {
                generator.start().key("bossbar", "backend-entity").defValue("dragon");
                generator.start().key("bossbar", "allow-via-hooks").defValue(true);
            }

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
                    .key("player-win-run-immediately").defValue(() -> List.of("/example {player} 200"))
                    .key("player-end-game").defValue(() -> List.of("/example {player} {score}"))
                    .key("player-destroy-bed").defValue(() -> List.of("/example {player} {score}"))
                    .key("player-kill").defValue(() -> List.of("/example {player} 10"))
                    .key("player-final-kill").defValue(() -> List.of("/example {player} 10"))
                    .key("player-game-start").defValue(() -> List.of("/example {player} 10"))
                    .key("player-early-leave").defValue(() -> List.of("/example {player} {death} 10"))
                    .key("team-win").defValue(() -> List.of("/example {team} 10"))
                    .key("player-team-win").defValue(() -> List.of("/example {team} {death} 10"))
                    .key("game-start").defValue(() -> List.of("/example Hello World!"))
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
                    .key("explosions").defValue(false)
                    .key("blocks").defValue(List::of)
                    .back()
                .section("leaveshortcuts")
                    .key("enabled").defValue(false)
                    .key("list").defValue(() -> List.of("leave"))
                    .back()
                .section("main-lobby")
                    .key("enabled").migrateOldAbsoluteKey("mainlobby", "enabled").defValue(false)
                    .key("location").migrateOldAbsoluteKey("mainlobby", "location").defValue("")
                    .key("world").migrateOldAbsoluteKey("mainlobby", "world").defValue("")
                    .key("date-format").migrateOldAbsoluteKey("sidebar", "date-format").preventOldKeyRemove().defValue("YY/mm/dd")
                    .section("sidebar")
                        .key("enabled").migrateOldAbsoluteKey("sidebar", "main-lobby", "enabled").defValue(false)
                        .key("title").migrateOldAbsoluteKey("sidebar", "main-lobby", "title").defValue("<yellow><bold>BED WARS")
                        .key("content").migrateOldAbsoluteKey("sidebar", "main-lobby", "content").defValue(() -> List.of(
                                "<white>Your level: <level>",
                                "",
                                "<white>Progress: <aqua><current-progress><gray>/<green><goal>",
                                "<progress-bar>",
                                " ",
                                "<white>Total Kills: <green><kills>",
                                "<white>Total Wins: <green><wins>",
                                "",
                                "<white>K/D ratio: <green><kd-ratio>",
                                "",
                                "<yellow>screamingsandals.org"
                        ))
                        .back()
                    .section("custom-chat")
                        .key("enabled").defValue(false)
                        .key("format").defValue("<level-prefix> <name>: <message>")
                        .back()
                    .section("tab")
                        .key("enabled").defValue(false)
                        .key("header").defValue(() -> List.of(
                                "<yellow>Welcome <bold><name>",
                                "<red>Online players: <players>"
                        ))
                        .key("footer").defValue(() -> List.of(
                                "<yellow>www.screamingsandals.org"
                        ))
                        .back()
                    .back();

            generator.start()
                .key("turnOnExperimentalGroovyShop").defValue(false)
                .key("prevent-spectator-from-flying-away").migrateOldAbsoluteKey("preventSpectatorFlyingAway").defValue(false)
                .key("removePurchaseMessages").defValue(false)
                .key("removePurchaseFailedMessages").migrateOldAbsoluteKey("removePurchaseMessages").preventOldKeyRemove().defValue(false)
                .key("removeUpgradeMessages").migrateOldAbsoluteKey("removePurchaseMessages").preventOldKeyRemove().defValue(false)
                .key("disable-cake-eating").migrateOldAbsoluteKey("disableCakeEating").defValue(true)
                .key("disable-dragon-egg-teleport").migrateOldAbsoluteKey("disableDragonEggTeleport").defValue(true)
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
                            .key("charge").defValue("block.respawn_anchor.charge")
                            .key("used").defValue("block.glass.break")
                            .key("deplete").defValue("block.respawn_anchor.deplete")
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
                .section("permissions")
                    .section("join")
                        .key("key").defValue("bw.cmd.join")
                        .key("default")
                            .migrateOldAbsoluteKey("default-permissions", "join")
                            .defValue(true)
                        .back()
                    .section("join-group")
                        .key("key").defValue("bw.cmd.join-group")
                        .key("default")
                            .migrateOldAbsoluteKey("default-permissions", "join-group")
                            .defValue(true)
                        .back()
                    .section("leave")
                        .key("key").defValue("bw.cmd.leave")
                        .key("default")
                            .migrateOldAbsoluteKey("default-permissions", "leave")
                            .defValue(true)
                        .back()
                    .section("autojoin")
                        .key("key").defValue("bw.cmd.autojoin")
                        .key("default")
                            .migrateOldAbsoluteKey("default-permissions", "autojoin")
                            .defValue(true)
                        .back()
                    .section("list")
                        .key("key").defValue("bw.cmd.list")
                        .key("default")
                            .migrateOldAbsoluteKey("default-permissions", "list")
                            .defValue(true)
                        .back()
                    .section("rejoin")
                        .key("key").defValue("bw.cmd.rejoin")
                        .key("default")
                            .migrateOldAbsoluteKey("default-permissions", "rejoin")
                            .defValue(true)
                        .back()
                    .section("stats")
                        .key("key").defValue("bw.cmd.stats")
                        .key("default")
                            .migrateOldAbsoluteKey("default-permissions", "stats")
                            .defValue(true)
                        .back()
                    .section("leaderboard")
                        .key("key").defValue("bw.cmd.leaderboard")
                        .key("default")
                            .migrateOldAbsoluteKey("default-permissions", "leaderboard")
                            .defValue(true)
                        .back()
                    .section("party")
                        .key("key").defValue("bw.cmd.party")
                        .key("default")
                            .migrateOldAbsoluteKey("default-permissions", "party")
                            .defValue(true)
                        .back()
                    .section("gamesinv")
                        .key("key").defValue("bw.cmd.gamesinv")
                        .key("default")
                            .migrateOldAbsoluteKey("default-permissions", "gamesinv")
                            .defValue(true)
                        .back()
                    .section("admin")
                        .key("key").defValue("bw.admin")
                        .key("default").defValue(false)
                        .back()
                    .section("other-stats")
                        .key("key").defValue("bw.otherstats")
                        .key("default").defValue(false)
                        .back()
                    .section("all-join")
                        .key("key").defValue("bw.admin.alljoin")
                        .key("default").defValue(false)
                        .back()
                    .section("disable-all-join")
                        .key("key").defValue("bw.disable.joinall")
                        .key("default").defValue(false)
                        .back()
                    .section("start-item")
                        .key("key").defValue("bw.vip.startitem")
                        .key("default").defValue(false)
                        .back()
                    .section("force-join")
                        .key("key").defValue("bw.vip.forcejoin")
                        .key("default").defValue(false)
                        .back()
                    .section("bypass-flight")
                        .key("key").defValue("bw.bypass.flight")
                        .key("default").defValue(false)
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

    public void toMiniMessage(ConfigurationNode node) {
        try {
            if (node.isList()) {
                node.set(MiscUtils.toMiniMessage(node.getList(String.class)));
            } else {
                node.set(MiscUtils.toMiniMessage(node.getString()));
            }
        } catch (SerializationException ex) {
            ex.printStackTrace();
        }
    }

    public ItemStack readDefinedItem(String item, String def) {
        var node = node("items", item);
        if (!node.empty()) {
            var obj = node.raw();
            var builtItem = ItemStackFactory.build(obj);
            return builtItem != null ? builtItem : ItemStackFactory.getAir();
        }

        var builtItem = ItemStackFactory.build(def);
        return builtItem != null ? builtItem : ItemStackFactory.getAir();
    }
}
