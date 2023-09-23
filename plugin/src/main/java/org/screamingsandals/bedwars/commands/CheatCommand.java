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

package org.screamingsandals.bedwars.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.screamingsandals.bedwars.Main;
import org.screamingsandals.bedwars.api.RunningTeam;
import org.screamingsandals.bedwars.api.Team;
import org.screamingsandals.bedwars.api.game.GameStatus;
import org.screamingsandals.bedwars.game.CurrentTeam;
import org.screamingsandals.bedwars.game.Game;
import org.screamingsandals.bedwars.game.GamePlayer;
import org.screamingsandals.bedwars.game.ItemSpawnerType;
import org.screamingsandals.bedwars.utils.FakeDeath;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.screamingsandals.bedwars.lib.lang.I18n.i18n;

public class CheatCommand extends BaseCommand {

    public CheatCommand(String commandName, boolean allowConsole) {
        super(commandName, ADMIN_PERMISSION, allowConsole, false);
    }

    @Override
    public boolean execute(CommandSender sender, List<String> args) {
        if (!Main.getConfigurator().config.getBoolean("enable-cheat-command-for-admins")) {
            sender.sendMessage(i18n("cheat_disabled"));
            return true;
        }

        int offset = isConsoleCommand() ? 1 : 0;

        Game game;
        Player defaultPlayer;
        if (offset == 1) {
            if (args.isEmpty()) {
                sender.sendMessage(i18n("unknown_usage"));
                return true;
            }

            game = Main.getGame(args.get(0));

            if (game == null) {
                sender.sendMessage(i18n("no_arena_found"));
                return true;
            }

            defaultPlayer = null;
        } else {
            Player player = (Player) sender;
            if (!Main.isPlayerInGame(player)) {
                sender.sendMessage(i18n("you_arent_in_game"));
                return true;
            }
            game = Main.getPlayerGameProfile(player).getGame();
            defaultPlayer = player;
        }


        if (args.size() >= offset + 1) {
            if (args.get(offset).equalsIgnoreCase("startemptygame") && offset != 1) { // not allowed for console
                if (game.getStatus() == GameStatus.WAITING) {
                    game.forceGameToStart = true;
                    sender.sendMessage(i18n("game_forced"));
                } else {
                    sender.sendMessage(i18n("cheat_not_waiting"));
                }
                return true;
            }

            switch (args.get(offset).toLowerCase()) {
                case "give":
                    {
                        if (game.getStatus() != GameStatus.RUNNING) {
                            sender.sendMessage(i18n("cheat_game_not_running"));
                            return true;
                        }

                        if (args.size() < offset + 2) {
                            sender.sendMessage(i18n("unknown_usage"));
                            return true;
                        }
                        String resource = args.get(offset + 1);
                        ItemSpawnerType type = Main.getSpawnerType(resource);
                        if (type == null) {
                            sender.sendMessage(i18n("admin_command_invalid_spawner_type"));
                            return true;
                        }
                        int stack = offset + 1;
                        if (args.size() > offset + 2) {
                            try {
                                stack = Integer.parseInt(args.get(offset + 2));
                            } catch (Throwable ignored) {}
                        }
                        Player player1 = defaultPlayer;
                        if (args.size() > offset + 3) {
                            player1 = Bukkit.getPlayer(args.get(offset + 3));
                            if (player1 == null || !game.isPlayerInAnyTeam(player1)) {
                                sender.sendMessage(i18n("cheat_invalid_player"));
                                return true;
                            }
                        } else if (defaultPlayer == null) {
                            sender.sendMessage(i18n("unknown_usage"));
                            return true;
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
                        if (game.getStatus() != GameStatus.RUNNING) {
                            sender.sendMessage(i18n("cheat_game_not_running"));
                            return true;
                        }

                        Player player1 = defaultPlayer;
                        if (args.size() > offset + 1) {
                            player1 = Bukkit.getPlayer(args.get(offset + 1));
                            if (player1 == null || !game.isPlayerInAnyTeam(player1)) {
                                sender.sendMessage(i18n("cheat_invalid_player"));
                                return true;
                            }
                        } else if (defaultPlayer == null) {
                            sender.sendMessage(i18n("unknown_usage"));
                            return true;
                        }
                        GamePlayer gamePlayer = Main.getPlayerGameProfile(player1);
                        if (gamePlayer.isSpectator) {
                            sender.sendMessage(i18n("cheat_invalid_player"));
                            return true;
                        }
                        if (Main.getConfigurator().config.getBoolean("allow-fake-death")) {
                            FakeDeath.die(gamePlayer);
                        } else {
                            player1.setHealth(0);
                        }
                        sender.sendMessage(i18n("cheat_received_kill").replace("%player%", player1.getName()));
                    }
                    break;
                case "destroybed":
                    {
                        if (game.getStatus() != GameStatus.RUNNING) {
                            sender.sendMessage(i18n("cheat_game_not_running"));
                            return true;
                        }

                        if (args.size() < offset + 2) {
                            sender.sendMessage(i18n("unknown_usage"));
                            return true;
                        }
                        String name = args.get(offset + 1);
                        Team team1 = game.getTeamFromName(name);
                        if (team1 == null) {
                            sender.sendMessage(i18n("admin_command_team_is_not_exists"));
                            return true;
                        }
                        CurrentTeam currentTeam = game.getCurrentTeamFromTeam(team1);
                        if (currentTeam == null) {
                            sender.sendMessage(i18n("team_not_in_game").replace("%name%", name));
                            return true;
                        }

                        if (!currentTeam.isBed) {
                            sender.sendMessage(i18n("team_bed_is_already_destroyed").replace("%name%", name));
                            return true;
                        }

                        game.targetBlockExplode(currentTeam);

                        sender.sendMessage(i18n("cheat_received_target_block_destroy").replace("%name%", name));
                    }
                    break;
                case "destroyallbeds":
                    {
                        if (game.getStatus() != GameStatus.RUNNING) {
                            sender.sendMessage(i18n("cheat_game_not_running"));
                            return true;
                        }

                        for (RunningTeam team : game.getRunningTeams()) {
                            if (team.isTargetBlockExists()) {
                                game.targetBlockExplode(team);
                            }
                        }

                        sender.sendMessage(i18n("cheat_received_target_blocks_destroy"));
                    }
                    break;
                case "jointeam":
                    {
                        if (defaultPlayer == null) { // does not currently work with console
                            sender.sendMessage(i18n("cheat_please_provide_valid_cheat_type"));
                            return true;
                        }

                        if (args.size() >= offset + 2) {
                            String name = args.get(offset + 1);
                            Team team1 = game.getTeamFromName(name);
                            if (team1 == null) {
                                sender.sendMessage(i18n("admin_command_team_is_not_exists"));
                                return true;
                            }

                            if (game.getStatus() == GameStatus.WAITING) {
                                game.selectPlayerTeam(defaultPlayer, team1, true, true, false);
                            } else {
                                if (game.getCurrentTeamFromTeam(team1) == null) {
                                    sender.sendMessage(i18n("team_not_in_game").replace("%name%", name));
                                    return true;
                                }

                                game.selectPlayerTeam(defaultPlayer, team1, true, false, true);

                                GamePlayer gP = Main.getPlayerGameProfile(defaultPlayer);
                                CurrentTeam team = game.getPlayerTeam(gP);
                                if (team != null) {
                                    if (gP.isSpectator) {
                                        game.makePlayerFromSpectator(gP);
                                    } else {
                                        gP.teleport(team.getTeamSpawn());
                                    }
                                }
                            }
                        } else {
                            GamePlayer gP = Main.getPlayerGameProfile(defaultPlayer);
                            if (game.getStatus() == GameStatus.WAITING) {
                                game.joinRandomTeam(gP, true, true, false);
                            } else {
                                game.joinRandomTeam(gP, true, false, true);

                                CurrentTeam team = game.getPlayerTeam(gP);
                                if (team != null) {
                                    if (gP.isSpectator) {
                                        game.makePlayerFromSpectator(gP);
                                    } else {
                                        gP.teleport(team.getTeamSpawn());
                                    }
                                }
                            }
                        }
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

        int offset = isConsoleCommand() ? 1 : 0;

        if (args.size() == 1 && offset == 1) {
            completion.addAll(Main.getGameNames());
            return;
        }

        if (args.size() == offset + 1) {
            if (offset == 1) {
                completion.addAll(Arrays.asList("give", "kill", "destroybed", "destroyallbeds"));
            } else {
                completion.addAll(Arrays.asList("give", "kill", "startemptygame", "destroybed", "destroyallbeds", "jointeam"));
            }
            return;
        }

        Game game;
        if (offset == 1) {
            game = Main.getGame(args.get(0));
        } else {
            game = (Game) Main.getInstance().getGameOfPlayer((Player) sender);
        }

        if (game != null) {
            if (args.size() > offset + 1 && args.get(offset).equals("give")) {
                if (args.size() == offset + 2) {
                    completion.addAll(Main.getAllSpawnerTypes());
                } else if (args.size() == offset + 3) {
                    completion.addAll(Arrays.asList("1", "2", "4", "8", "16", "32", "64"));
                } else if (args.size() == offset + 4) {
                    completion.addAll(game.getConnectedPlayers().stream().map(Player::getName).collect(Collectors.toList()));
                }
            }
            if (args.size() > offset + 1 && args.get(offset).equals("kill")) {
                if (args.size() == offset + 2) {
                    completion.addAll(game.getConnectedPlayers().stream().map(Player::getName).collect(Collectors.toList()));
                }
            }
            if (args.size() > offset + 1 && (args.get(offset).equalsIgnoreCase("destroybed") || args.get(offset).equalsIgnoreCase("jointeam"))) {
                if (args.size() == offset + 2) {
                    completion.addAll(game.getRunningTeams().stream().map(Team::getName).collect(Collectors.toList()));
                }
            }
        }
    }

}
