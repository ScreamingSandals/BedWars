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

package org.screamingsandals.bedwars.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.screamingsandals.bedwars.Main;
import org.screamingsandals.bedwars.api.game.GameStatus;
import org.screamingsandals.bedwars.game.Game;
import org.screamingsandals.bedwars.game.GamePlayer;
import org.screamingsandals.bedwars.game.ItemSpawnerType;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.screamingsandals.bedwars.lib.lang.I18n.i18n;

public class CheatCommand extends BaseCommand {

    public CheatCommand() {
        super("cheat", ADMIN_PERMISSION, false, false);
    }

    @Override
    public boolean execute(CommandSender sender, List<String> args) {
        if (!Main.getConfigurator().config.getBoolean("enable-cheat-command-for-admins")) {
            sender.sendMessage(i18n("cheat_disabled"));
            return true;
        }

        if (args.size() >= 1) {
            Player player = (Player) sender;
            if (!Main.isPlayerInGame(player)) {
                sender.sendMessage(i18n("you_arent_in_game"));
                return true;
            }
            Game game = Main.getPlayerGameProfile(player).getGame();
            if (args.get(0).equalsIgnoreCase("startemptygame")) {
                if (game.getStatus() == GameStatus.WAITING) {
                    game.forceGameToStart = true;
                    sender.sendMessage(i18n("game_forced"));
                } else {
                    sender.sendMessage(i18n("cheat_not_waiting"));
                }
                return true;
            }
            if (game.getStatus() != GameStatus.RUNNING) {
                sender.sendMessage(i18n("cheat_game_not_running"));
                return true;
            }

            switch (args.get(0).toLowerCase()) {
                case "give":
                    {
                        if (args.size() < 2) {
                            sender.sendMessage(i18n("unknown_usage"));
                            return true;
                        }
                        String resource = args.get(1);
                        ItemSpawnerType type = Main.getSpawnerType(resource);
                        if (type == null) {
                            sender.sendMessage(i18n("admin_command_invalid_spawner_type"));
                            return true;
                        }
                        int stack = 1;
                        if (args.size() > 2) {
                            try {
                                stack = Integer.parseInt(args.get(2));
                            } catch (Throwable ignored) {}
                        }
                        Player player1 = player;
                        if (args.size() > 3) {
                            player1 = Bukkit.getPlayer(args.get(3));
                            if (player1 == null || !game.isPlayerInAnyTeam(player1)) {
                                sender.sendMessage(i18n("cheat_invalid_player"));
                                return true;
                            }
                        }
                        GamePlayer gamePlayer = Main.getPlayerGameProfile(player1);
                        if (gamePlayer.isSpectator) {
                            sender.sendMessage(i18n("cheat_invalid_player"));
                            return true;
                        }
                        Map<Integer, ItemStack> map = player1.getInventory().addItem(type.getStack(stack));
                        Player finalPlayer = player1;
                        map.forEach((integer, itemStack) -> finalPlayer.getLocation().getWorld().dropItem(finalPlayer.getLocation(), itemStack));
                        sender.sendMessage(i18n("cheat_received_give").replace("%player%", player1.getName()).replace("%amount%", String.valueOf(stack)).replace("%resource%", type.getItemName()));
                    }
                    break;
                case "kill":
                    {
                        Player player1 = player;
                        if (args.size() > 1) {
                            player1 = Bukkit.getPlayer(args.get(1));
                            if (player1 == null || !game.isPlayerInAnyTeam(player1)) {
                                sender.sendMessage(i18n("cheat_invalid_player"));
                                return true;
                            }
                        }
                        GamePlayer gamePlayer = Main.getPlayerGameProfile(player1);
                        if (gamePlayer.isSpectator) {
                            sender.sendMessage(i18n("cheat_invalid_player"));
                            return true;
                        }
                        player1.setHealth(0);
                        sender.sendMessage(i18n("cheat_received_kill").replace("%player%", player1.getName()));
                    }
                    break;
                default:
                    sender.sendMessage(i18n("cheat_please_provide_valid_cheat_type"));
            }
        } else {
            sender.sendMessage(i18n("cheat_please_provide_valid_cheat_type"));
        }
        return true;
    }

    @Override
    public void completeTab(List<String> completion, CommandSender sender, List<String> args) {
        if (!Main.getConfigurator().config.getBoolean("enable-cheat-command-for-admins")) {
            return;
        }

        if (args.size() == 1) {
            completion.addAll(Arrays.asList("give", "kill", "startemptygame"));
        }
        if (Main.isPlayerInGame((Player) sender)) {
            if (args.size() > 1 && args.get(0).equals("give")) {
                GamePlayer gPlayer = Main.getPlayerGameProfile((Player) sender);
                if (args.size() == 2) {
                    completion.addAll(Main.getAllSpawnerTypes());
                } else if (args.size() == 3) {
                    completion.addAll(Arrays.asList("1", "2", "4", "8", "16", "32", "64"));
                } else if (args.size() == 4) {
                    completion.addAll(gPlayer.getGame().getConnectedPlayers().stream().map(Player::getName).collect(Collectors.toList()));
                }
            }
            if (args.size() > 1 && args.get(0).equals("kill")) {
                GamePlayer gPlayer = Main.getPlayerGameProfile((Player) sender);
                if (args.size() == 2) {
                    completion.addAll(gPlayer.getGame().getConnectedPlayers().stream().map(Player::getName).collect(Collectors.toList()));
                }
            }
        }
    }

}
