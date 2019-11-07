package org.screamingsandals.bedwars.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.screamingsandals.bedwars.Main;
import org.screamingsandals.bedwars.utils.Permissions;
import org.screamingsandals.lib.screamingcommands.base.annotations.RegisterCommand;
import org.screamingsandals.lib.screamingcommands.base.interfaces.IBasicCommand;

import java.util.List;

/**
 * @author ScreamingSandals team
 */

@RegisterCommand(commandName = "bw")
public class BedWarsCommand implements IBasicCommand {

    public BedWarsCommand(Main main) {
    }

    @Override
    public boolean onPlayerCommand(Player player, List<String> list) {
        return false;
    }

    @Override
    public boolean onConsoleCommand(ConsoleCommandSender consoleCommandSender, List<String> list) {
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, List<String> list) {
        return null;
    }

    @Override
    public String getPermission() {
        return Permissions.BASE_PERMISSION.permission;
    }

    @Override
    public String getDescription() {
        return "BedWars Base Command";
    }

    @Override
    public String getUsage() {
        return "/bw";
    }

    @Override
    public String getInvalidUsageMessage() {
        return "NNNN";
    }
}
