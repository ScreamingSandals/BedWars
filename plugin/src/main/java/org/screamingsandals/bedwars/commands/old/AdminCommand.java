package org.screamingsandals.bedwars.commands.old;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.WeatherType;
import org.bukkit.boss.BarColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.screamingsandals.bedwars.Main;
import org.screamingsandals.bedwars.api.ArenaTime;
import org.screamingsandals.bedwars.game.*;
import java.util.*;

import static org.screamingsandals.bedwars.lib.lang.I18n.i18n;

public class AdminCommand{

    public void completeTab(List<String> completion, CommandSender sender, List<String> args) {
        if (args.size() == 1) {
            completion.addAll(Main.getGameNames());
            for (String arena : gc.keySet()) {
                if (!completion.contains(arena)) {
                    completion.add(arena);
                }
            }
        } else if (args.size() == 2) {
            completion.addAll(Arrays.asList("add", "lobby", "spec", "pos1", "pos2", "pausecountdown", "team", "spawner",
                    "time", "store", "save", "remove", "edit", "jointeam", "minplayers", "info", "config", "arenatime",
                    "arenaweather", "lobbybossbarcolor", "gamebossbarcolor", "postgamewaiting", "customprefix"));
        } else if (args.get(1).equalsIgnoreCase("pausecountdown") && args.size() == 3) {
            completion.addAll(Arrays.asList("30", "60"));
        } else if (args.get(1).equalsIgnoreCase("time") && args.size() == 3) {
            completion.addAll(Arrays.asList("180", "300", "600"));
        } else if (args.get(1).equalsIgnoreCase("minplayers") && args.size() == 3) {
            completion.addAll(Arrays.asList("2", "3", "4", "5"));
        } else if (args.get(1).equalsIgnoreCase("info") && args.size() == 3) {
            completion.addAll(Arrays.asList("base", "stores", "spawners", "teams", "config"));
        } else if (args.get(1).equalsIgnoreCase("arenatime") && args.size() == 3) {
            for (ArenaTime type : ArenaTime.values()) {
                completion.add(type.name());
            }
        } else if ((args.get(1).equalsIgnoreCase("lobbybossbarcolor")
                || args.get(1).equalsIgnoreCase("gamebossbarcolor")) && args.size() == 3) {
            try {
                completion.add("default");
                for (BarColor type : BarColor.values()) {
                    completion.add(type.name());
                }
            } catch (Throwable ignored) {
            }
        } else if (args.get(1).equalsIgnoreCase("arenaweather") && args.size() == 3) {
            completion.add("default");
            for (WeatherType type : WeatherType.values()) {
                completion.add(type.name());
            }
        } else if (args.get(1).equalsIgnoreCase("store")) {
            if (args.size() == 3) {
                completion.addAll(Arrays.asList("add", "remove", "type", "child", "adult"));
            }
            if (args.size() == 4 && args.get(2).equalsIgnoreCase("type")) {
                for (EntityType type : EntityType.values()) {
                    if (type.isAlive()) {
                        if (type == EntityType.PLAYER) {
                            completion.add(type.name() + ":");
                        } else {
                            completion.add(type.name());
                        }
                    }
                }
            }
            if (args.size() == 4 && args.get(2).equalsIgnoreCase("add")) {
                completion.addAll(Arrays.asList("Villager_shop", "Dealer", "Seller", "&a&lVillager_shop", "&4Barter"));
            }
            if (args.size() == 5 && args.get(2).equalsIgnoreCase("add")) {
                // TODO scan files for this :D
            }
            if (args.size() == 6 && args.get(2).equalsIgnoreCase("add")) {
                completion.addAll(Arrays.asList("true", "false"));
            }
        } else if (args.get(1).equalsIgnoreCase("config")) {
            if (args.size() == 3) {
                if (gc.containsKey(args.get(0))) {
                    completion.addAll(gc.get(args.get(0)).getGame().getConfigurationContainer().getRegisteredKeys());
                }
            }
            if (args.size() == 4) {
                completion.addAll(Arrays.asList("true", "false", "inherit")); // this is not necessary right
            }
        } else if (args.get(1).equalsIgnoreCase("spawner")) {
            if (args.size() == 3) {
                completion.addAll(Arrays.asList("add", "remove", "reset"));
            }
            if (args.get(2).equalsIgnoreCase("add")) {
                if (args.size() == 4) {
                    completion.addAll(Main.getAllSpawnerTypes());
                }
                if (args.size() == 5) {
                    completion.addAll(Arrays.asList("false", "true"));
                }
                if (args.size() == 6) {
                    completion.addAll(Collections.singletonList("1"));
                }
                if (args.size() == 8) {
                    if (gc.containsKey(args.get(0))) {
                        for (Team t : gc.get(args.get(0)).getGame().getTeams()) {
                            completion.add(t.name);
                        }
                    }
                }
                if (args.size() == 8 || args.size() == 9) {
                    completion.addAll(Arrays.asList("5", "10", "20"));
                }
            }
        } else if (args.get(1).equalsIgnoreCase("team")) {
            if (args.size() == 3) {
                completion.addAll(Arrays.asList("add", "color", "maxplayers", "spawn", "bed", "remove"));
            }
            if (args.size() == 4) {
                if (gc.containsKey(args.get(0))) {
                    for (Team t : gc.get(args.get(0)).getGame().getTeams()) {
                        completion.add(t.name);
                    }
                }
            }
            if (args.size() == 5) {
                if (args.get(2).equalsIgnoreCase("add") || args.get(2).equalsIgnoreCase("color")) {
                    for (TeamColor en : TeamColor.values()) {
                        completion.add(en.toString());
                    }
                } else if (args.get(2).equalsIgnoreCase("maxplayers")) {
                    completion.addAll(Arrays.asList("1", "2", "4", "8"));
                }
            }
            if (args.size() == 6 && args.get(2).equalsIgnoreCase("add")) {
                completion.addAll(Arrays.asList("1", "2", "4", "8"));
            }
        } else if (args.get(1).equalsIgnoreCase("jointeam")) {
            if (gc.containsKey(args.get(0))) {
                for (Team t : gc.get(args.get(0)).getGame().getTeams()) {
                    completion.add(t.name);
                }
            }
        }
    }

}
