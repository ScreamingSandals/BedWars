package org.screamingsandals.bedwars.commands;

import org.screamingsandals.bedwars.Main;
import org.bukkit.command.CommandSender;

import java.util.List;

public abstract class BaseCommand {

    public static final String ADMIN_PERMISSION = "misat11.bw.admin";
    public static final String OTHER_STATS_PERMISSION = "misat11.bw.otherstats";

    private String name;
    private String permission;
    private boolean allowConsole;

    protected BaseCommand(String name, String permission, boolean allowConsole) {
        this.name = name.toLowerCase();
        this.permission = permission;
        this.allowConsole = allowConsole;
        Main.getCommands().put(this.name, this);
    }

    public String getName() {
        return this.name;
    }

    public boolean isConsoleCommand() {
        return this.allowConsole;
    }

    public String getPermission() {
        return this.permission;
    }

    public abstract boolean execute(CommandSender sender, List<String> args);

    public abstract void completeTab(List<String> completion, CommandSender sender, List<String> args);

    public boolean hasPermission(CommandSender sender) {
        if (permission == null || "".equals(permission)) {
            return true; // There's no permissions required
        }

        return sender.hasPermission(permission);
    }

}
