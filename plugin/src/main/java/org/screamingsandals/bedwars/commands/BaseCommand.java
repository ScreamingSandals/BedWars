package org.screamingsandals.bedwars.commands;

import org.bukkit.command.ConsoleCommandSender;
import org.screamingsandals.bedwars.Main;
import org.bukkit.command.CommandSender;
import org.screamingsandals.lib.debug.Debug;

import java.util.Arrays;
import java.util.Collections;
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
    public static final List<String> ALL_JOIN_PERMISSION =  Arrays.asList("misat11.bw.admin.alljoin", "bw.admin.alljoin");

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
