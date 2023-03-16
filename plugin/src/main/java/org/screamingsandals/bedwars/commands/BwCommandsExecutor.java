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

import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;
import org.screamingsandals.bedwars.Main;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.screamingsandals.bedwars.lib.lang.I18n.i18n;

public class BwCommandsExecutor implements CommandExecutor, TabCompleter {

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String alias, String[] args) {
        List<String> completionList = new ArrayList<>();
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (args.length == 1) {
                for (BaseCommand c : Main.getCommands().values()) {
                    if (c.hasPermission(player)) {
                        completionList.add(c.getName());
                    }
                }
            } else if (args.length > 1) {
                ArrayList<String> arguments = new ArrayList<>(Arrays.asList(args));
                arguments.remove(0);
                BaseCommand bCommand = Main.getCommands().get(args[0]);
                if (bCommand != null) {
                    if (bCommand.hasPermission(player)) {
                        bCommand.completeTab(completionList, sender, arguments);
                    }
                }
            }
        }
        List<String> finalCompletionList = new ArrayList<>();
        StringUtil.copyPartialMatches(args[args.length - 1], completionList, finalCompletionList);
        return finalCompletionList;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (args.length == 0) {
            args = new String[]{"help"};
        }

        String command = args[0];
        ArrayList<String> arguments = new ArrayList<>(Arrays.asList(args));
        arguments.remove(0);

        BaseCommand bCommand = Main.getCommands().get(command.toLowerCase());

        if (bCommand == null) {
            sender.sendMessage(i18n("unknown_command"));
            return true;
        }

        if (sender instanceof ConsoleCommandSender) {
            if (!bCommand.isConsoleCommand()) {
                sender.sendMessage("§cConsole can't use this command!");
                return true;
            }
        }

        if (!bCommand.hasPermission(sender)) {
            sender.sendMessage(i18n("no_permissions"));
            return true;
        }

        boolean result = bCommand.execute(sender, arguments);

        if (!result) {
            sender.sendMessage(i18n("unknown_usage"));
        }

        return true;
    }

}
