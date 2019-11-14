package org.screamingsandals.bedwars.commands;

import org.bukkit.command.Command;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.screamingsandals.bedwars.Main;
import org.screamingsandals.bedwars.utils.Permissions;
import org.screamingsandals.lib.screamingcommands.base.annotations.RegisterCommand;
import org.screamingsandals.lib.screamingcommands.base.interfaces.IBasicCommand;

import java.util.List;

import static misat11.lib.lang.I.mpr;

/**
 * @author ScreamingSandals team
 */

@RegisterCommand(commandName = "bw")
public class BedWarsCommand implements IBasicCommand {

    public BedWarsCommand(Main main) {
    }

    @Override
    public boolean onPlayerCommand(Player player, List<String> list) {
        mpr("plugin.version_info").replace("%version%", Main.getVersion()).send(player);
        return true;
    }

    @Override
    public boolean onConsoleCommand(ConsoleCommandSender consoleCommandSender, List<String> list) {
        mpr("plugin.version_info").replace("%version%", Main.getVersion()).send(consoleCommandSender);
        return true;
    }

    @Override
    public void onPlayerTabComplete(Player player, Command command, List<String> completion, List<String> args) {
    }

    @Override
    public void onConsoleTabComplete(ConsoleCommandSender consoleCommandSender, Command command, List<String> completion, List<String> args) {
    }


    @Override
    public String getPermission() {
        return Permissions.BASE.permission;
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
