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

import org.bukkit.command.ConsoleCommandSender;
import org.screamingsandals.bedwars.Main;
import org.bukkit.command.CommandSender;

import java.util.Arrays;
import java.util.List;

public abstract class BaseCommand {

    public static final List<String> ADMIN_PERMISSION = Arrays.asList("misat11.bw.admin", "bw.admin");
    public static final List<String> OTHER_STATS_PERMISSION =  Arrays.asList("misat11.bw.otherstats", "bw.otherstats");

    public static final List<String> JOIN_PERMISSION =  Arrays.asList("misat11.bw.cmd.join", "bw.cmd.join");
    public static final List<String> LEAVE_PERMISSION =  Arrays.asList("misat11.bw.cmd.leave", "bw.cmd.leave");
    public static final List<String> AUTOJOIN_PERMISSION =  Arrays.asList("misat11.bw.cmd.autojoin", "bw.cmd.autojoin");
    public static final List<String> LIST_PERMISSION =  Arrays.asList("misat11.bw.cmd.list", "bw.cmd.list");
    public static final List<String> REJOIN_PERMISSION =  Arrays.asList("misat11.bw.cmd.rejoin", "bw.cmd.rejoin");
    public static final List<String> STATS_PERMISSION =  Arrays.asList("misat11.bw.cmd.stats", "bw.cmd.stats");
    public static final List<String> LEADERBOARD_PERMISSION =  Arrays.asList("misat11.bw.cmd.leaderboard", "bw.cmd.leaderboard");
    public static final List<String> ALL_JOIN_PERMISSION =  Arrays.asList("misat11.bw.admin.alljoin", "bw.admin.alljoin");
    public static final List<String> PARTY_PERMISSION =  Arrays.asList("misat11.bw.cmd.party", "bw.cmd.party");

    private String name;
    private List<String> permissions;
    private boolean allowConsole;
    private boolean defaultAllowed;

    protected BaseCommand(String name, List<String> permissions, boolean allowConsole, boolean defaultAllowed) {
        this.name = name.toLowerCase();
        this.permissions = permissions;
        this.allowConsole = allowConsole;
        this.defaultAllowed = defaultAllowed;
        Main.getCommands().put(this.name, this);
    }

    public String getName() {
        return this.name;
    }

    public boolean isConsoleCommand() {
        return this.allowConsole;
    }

    public List<String> getPossiblePermissions() {
        return this.permissions;
    }

    public abstract boolean execute(CommandSender sender, List<String> args);

    public abstract void completeTab(List<String> completion, CommandSender sender, List<String> args);

    public boolean isDefaultAllowed() {
        return this.defaultAllowed;
    }

    public boolean hasPermission(CommandSender sender) {
        return hasPermission(sender, permissions, defaultAllowed);
    }

    public static boolean hasPermission(CommandSender sender, List<String> permissions, boolean defaultAllowed) {
        if (permissions == null || permissions.isEmpty() || sender instanceof ConsoleCommandSender || sender.isOp()) {
            return true;
        }

        for (String permission : permissions) {
            if (sender.isPermissionSet(permission)) {
                return sender.hasPermission(permission);
            }
        }

        return defaultAllowed;
    }

}
